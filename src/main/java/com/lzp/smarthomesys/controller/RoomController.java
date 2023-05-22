package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.*;
import com.lzp.smarthomesys.tasks.ScheduleTask;
import com.lzp.smarthomesys.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Slf4j
@RestController
@Api("房间控制器")
@RequestMapping("/room")
public class RoomController {

    @Resource
    private RoomServiceImpl roomService;

    @Resource
    private AirconServiceImpl airconService;

    @Resource
    private LightServiceImpl lightService;

    @Resource
    private LockServiceImpl lockService;

    @Resource
    private OtherServiceImpl otherService;

    @Resource
    private SceneServiceImpl sceneService;

    @Resource
    private LogServiceImpl logService;

    @Resource
    private FileUtils fileUtils;

    @Resource
    private ScenePlanServiceImpl scenePlanService;

    @Resource
    ScheduleTask scheduleTask;

    @Resource
    LightController lightController;

    @Value("${onenet.device_id}")
    private String deviceId;

    @Value("${onenet.timeout}")
    private String timeout;

    @Value("${aliyun.url}")
    private String audioUrl;

    @Value("${aliyun.apikey}")
    private String audioApikey;

    @Value("${aliyun.rate}")
    private String audioRate;

    @Value("${audio.actualPath}")
    private String actualPathAudio;

    private String aliyunToken;

    // 在类初始化的时候对其赋值
    @PostConstruct
    private void init() {
        aliyunToken = fileUtils.readAliyunToken();
    }

    /**
     * 通过用户标识获取其所有房间
     * @param userId 用户id
     * @return Result
     */
    @ApiOperation("通过用户标识获取其所有房间")
    @GetMapping("/getByUserId")
    public Result getByUserId(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId){
        List<Room> result = new ArrayList<>();
        List<Room> rooms = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, userId));
        for (Room room:rooms){
            int devicesNums = 0;
            devicesNums += airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, room.getId())).size();
            devicesNums += lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, room.getId())).size();
//            devicesNums += lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, room.getId())).size(); // 排除了门锁
            devicesNums += otherService.list(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, room.getId())).size();
            room.setDeviceNum(devicesNums);
            result.add(room);
        }
        return Result.success().setData("rooms", result);
    }

    /**
     * 获取本房间所有电器
     * @param roomId 房间id
     * @return Result
     */
    @ApiOperation("获取本房间所有电器（不包含门锁）")
    @GetMapping("/getDevices")
    public Result getDevices(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        Room room = roomService.getById(roomId);
        if (room != null) {
            Map<String, Object> deviceMap = new HashMap<>();
            Map<String, Object> resultMap = new HashMap<>();
            List<Aircon> aircons = airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            List<Light> lights = lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
//            List<Lock> locks = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, roomId)); // 排除了门锁
            List<Other> others = otherService.list(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, roomId));
            deviceMap.put("aircons", aircons);
            deviceMap.put("lights", lights);
//            deviceMap.put("locks", locks); // 排除了门锁
            deviceMap.put("others", others);
            int devicesNum = aircons.size() + lights.size() + others.size(); // + locks.size()排除了门锁
            resultMap.put("devices", deviceMap);
            resultMap.put("totalDevicesNum", devicesNum);
            return Result.success().setData("result", resultMap);
        }
        else {
            return Result.error().setData("mes", "查无此房间");
        }
    }


    /**
     * 给用户增加房间
     * @param usrId 用户id
     * @param position 位置
     * @param remarks 备注
     * @return Result
     */
    @ApiOperation("给用户增加房间")
    @GetMapping("/addRoom")
    public Result addRoom(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String usrId,
                          @ApiParam(value = "房间位置（例如三楼卧室）", required = true) @RequestParam("position") String position,
                          @ApiParam(value = "房间备注") @RequestParam(value = "remarks", required = false) String remarks){
        List<Room> rooms = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, usrId).eq(Room::getPosition, position));
        if (rooms.size() > 0){
            return Result.error().setData("mes", "已经存在" + position);
        }
        Room room = new Room();
        room.setUserId(usrId);
        room.setPosition(position);
        room.setRemarks(remarks);
        roomService.save(room);
        return Result.success().setData("mes", "添加成功");
    }

    /**
     * 房间增加设备
     * @param roomId 房间id
     * @param type 电器类型
     * @param smallPos 房间中的精确位置
     * @param brand 品牌
     * @return Result
     */
    @ApiOperation("房间增加设备")
    @GetMapping("/addDevice")
    public Result addDevice(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                            @ApiParam(value = "设备类别[选择：空调、灯泡、门锁、其他]", required = true) @RequestParam("type") String type,
                            @ApiParam(value = "更小位置[例如：电视右边]", required = true) @RequestParam("smallPos") String smallPos,
                            @ApiParam(value = "电器品牌[\"其他类别\"不需要品牌]") @RequestParam(value = "brand", required = false) String brand){
        Light lighttest = new Light();
        lighttest.setId("xxxx");
        lighttest.setKind("TCL");
        lighttest.setColor("RGB(143, 039, 039)");
        lighttest.setPower("未知");
        lighttest.setRoomId("xxx");
        lighttest.setIntensity(99);
        Result.success().setData("light", lighttest);


        // 判断该房间是否有其他电器
        Integer n1 = airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId).eq(Aircon::getSmallPos, smallPos)).size();
        Integer n2 = lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId).eq(Light::getSmallPos, smallPos)).size();
        Integer n3 = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, roomId).eq(Lock::getSmallPos, smallPos)).size();
        Integer n4 = otherService.list(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, roomId).eq(Other::getSmallPos, smallPos)).size();
        if (n1 + n2 + n3 + n4 > 0){
            return Result.error().setData("mes", "本房间的" + smallPos + "已有其他电器");
        }
        switch (type){
            case "空调": {
                if (brand == null) return Result.error().setData("mes", "空调需要品牌");
                LambdaQueryWrapper<Aircon> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Aircon::getRoomId, roomId)
                        .eq(Aircon::getSmallPos, smallPos);
                Aircon aircon = new Aircon();
                aircon.setRoomId(roomId);
                aircon.setSmallPos(smallPos);
                aircon.setBrand(brand);
                airconService.save(aircon);
                List<Aircon> aircons = airconService.list(wrapper);
                return Result.success().setData("aircon", aircons);
            }
            case "灯泡": {
                if (brand == null) return Result.error().setData("mes", "灯泡需要品牌");
                LambdaQueryWrapper<Light> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Light::getRoomId, roomId)
                        .eq(Light::getSmallPos, smallPos);
                Light light = new Light();
                light.setRoomId(roomId);
                light.setSmallPos(smallPos);
                light.setBrand(brand);
                lightService.save(light);
                List<Light> lights = lightService.list(wrapper);
                return Result.success().setData("light", lights);
            }
            case "门锁": {
                if (brand == null) return Result.error().setData("mes", "门锁需要品牌");
                LambdaQueryWrapper<Lock> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Lock::getRoomId, roomId)
                        .eq(Lock::getSmallPos, smallPos);
                Lock lock = new Lock();
                lock.setRoomId(roomId);
                lock.setSmallPos(smallPos);
                lock.setBrand(brand);
                lockService.save(lock);
                List<Lock> locks = lockService.list(wrapper);
                return Result.success().setData("lock", locks);
            }
            case "其他": {
                LambdaQueryWrapper<Other> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Other::getRoomId, roomId)
                        .eq(Other::getSmallPos, smallPos);
                Other other = new Other();
                other.setRoomId(roomId);
                other.setSmallPos(smallPos);
                otherService.save(other);
                List<Other> others = otherService.list(wrapper);
                return Result.success().setData("other", others);
            }
            default: {
                return Result.error().setData("mes", "设备类别应是以上提及类别");
            }
        }
    }

    /**
     * 通过id删除电器
     * @param type 电器类型
     * @param deviceId 设备id
     * @return Result
     */
    @DeleteMapping("/deleteDevice")
    @ApiOperation("通过标识删除某电器(场景中的电器也会同时删除，同时场景设置为关闭，相关的场景计划也会设置会关闭)")
    public Result deleteDevice(@ApiParam(value = "设备类别[选择：空调、灯泡、门锁、其他]", required = true) @RequestParam("type") String type,
                               @ApiParam(value = "电器设备标识", required = true) @RequestParam("deviceId") String deviceId){
        switch (type){
            case "空调": {
                if (airconService.getById(deviceId) != null) {
                    // 删除电器
                    airconService.removeById(deviceId);
                    // 修改场景的电器
                    LambdaQueryWrapper<Scene> sceneWrapper = new LambdaQueryWrapper<>();
                    sceneWrapper.like(Scene::getAppliance, "," + deviceId + ";");
                    List<Scene> scenes = sceneService.list(sceneWrapper);
                    for (Scene scene:scenes){
                        String appliance = scene.getAppliance().replace("空调," + deviceId + ";", "");
                        scene.setAppliance(appliance);
                        scene.setState(0);
                        sceneService.updateById(scene);
                        // 修改场景计划将其关闭，不要忘记更新定时任务中的集合了哦
                        LambdaQueryWrapper<ScenePlan> scenePlanWrapper = new LambdaQueryWrapper<>();
                        scenePlanWrapper.eq(ScenePlan::getSceneId, scene.getId());
                        List<ScenePlan> scenePlans = scenePlanService.list(scenePlanWrapper);
                        for (ScenePlan scenePlan:scenePlans){
                            scenePlan.setState(0);
                            scenePlanService.updateById(scenePlan);
                            scheduleTask.updateScenePlans();
                        }
                    }
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的空调");
                }else{
                    return Result.error().setData("mes", "没有找到" + deviceId + "的空调");
                }
            }
            case "灯泡": {
                if (lightService.getById(deviceId) != null) {
                    // 删除电器
                    lightService.removeById(deviceId);
                    // 修改场景的电器
                    LambdaQueryWrapper<Scene> sceneWrapper = new LambdaQueryWrapper<>();
                    sceneWrapper.like(Scene::getAppliance, "," + deviceId + ";");
                    List<Scene> scenes = sceneService.list(sceneWrapper);
                    for (Scene scene:scenes){
                        String appliance = scene.getAppliance().replace("灯泡," + deviceId + ";", "");
                        scene.setAppliance(appliance);
                        scene.setState(0);
                        sceneService.updateById(scene);
                        // 修改场景计划将其关闭，不要忘记更新定时任务中的集合了哦
                        LambdaQueryWrapper<ScenePlan> scenePlanWrapper = new LambdaQueryWrapper<>();
                        scenePlanWrapper.eq(ScenePlan::getSceneId, scene.getId());
                        List<ScenePlan> scenePlans = scenePlanService.list(scenePlanWrapper);
                        for (ScenePlan scenePlan:scenePlans){
                            scenePlan.setState(0);
                            scenePlanService.updateById(scenePlan);
                            scheduleTask.updateScenePlans();
                        }
                    }
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的灯泡");
                }else{
                    return Result.error().setData("mes", "没有找到标识为" + deviceId + "的灯泡");
                }
            }
            case "门锁": {
                if (lockService.getById(deviceId) != null) {
                    // 删除电器
                    lockService.removeById(deviceId);
                    // 修改场景的电器
                    LambdaQueryWrapper<Scene> sceneWrapper = new LambdaQueryWrapper<>();
                    sceneWrapper.like(Scene::getAppliance, "," + deviceId + ";");
                    List<Scene> scenes = sceneService.list(sceneWrapper);
                    for (Scene scene:scenes){
                        String appliance = scene.getAppliance().replace("门锁," + deviceId + ";", "");
                        scene.setAppliance(appliance);
                        scene.setState(0);
                        sceneService.updateById(scene);
                        // 修改场景计划将其关闭，不要忘记更新定时任务中的集合了哦
                        LambdaQueryWrapper<ScenePlan> scenePlanWrapper = new LambdaQueryWrapper<>();
                        scenePlanWrapper.eq(ScenePlan::getSceneId, scene.getId());
                        List<ScenePlan> scenePlans = scenePlanService.list(scenePlanWrapper);
                        for (ScenePlan scenePlan:scenePlans){
                            scenePlan.setState(0);
                            scenePlanService.updateById(scenePlan);
                            scheduleTask.updateScenePlans();
                        }
                    }
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的门锁");
                }else {
                    return Result.error().setData("mes", "没有找到标识为" + deviceId + "的门锁");
                }
            }
            case "其他": {
                if (otherService.getById(deviceId) != null) {
                    // 删除电器
                    otherService.removeById(deviceId);
                    // 修改场景的电器
                    LambdaQueryWrapper<Scene> sceneWrapper = new LambdaQueryWrapper<>();
                    sceneWrapper.like(Scene::getAppliance, "," + deviceId + ";");
                    List<Scene> scenes = sceneService.list(sceneWrapper);
                    for (Scene scene:scenes){
                        String appliance = scene.getAppliance().replace("其他," + deviceId + ";", "");
                        scene.setAppliance(appliance);
                        scene.setState(0);
                        sceneService.updateById(scene);
                        // 修改场景计划将其关闭，不要忘记更新定时任务中的集合了哦
                        LambdaQueryWrapper<ScenePlan> scenePlanWrapper = new LambdaQueryWrapper<>();
                        scenePlanWrapper.eq(ScenePlan::getSceneId, scene.getId());
                        List<ScenePlan> scenePlans = scenePlanService.list(scenePlanWrapper);
                        for (ScenePlan scenePlan:scenePlans){
                            scenePlan.setState(0);
                            scenePlanService.updateById(scenePlan);
                            scheduleTask.updateScenePlans();
                        }
                    }
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的其他电器");
                }else {
                    return Result.error().setData("mes", "没有找到标识为" + deviceId + "的其他电器");
                }
            }
            default: {
                return Result.error().setData("mes", "设备类别应是以上提及类别");
            }
        }
    }

    /**
     * 开启或者关闭本房间所有灯泡
     * @param roomId 房间id
     * @param op 操作
     * @return result
     */
    @PutMapping("/allLightsOnOrOff")
    @ApiOperation("开启或者关闭所有灯泡")
    public Result allLightsOnOrOff(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                        @ApiParam(value = "选择0[关闭]或1[开启]", required = true) @RequestParam("op") Integer op){
        // 默认是关闭命令
        Integer state = 1;
        String cmdStr = CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc();
        String onOrOff = "关闭";
        // 如果是开启命令则进行修改
        if (op == 1){
            state = 0;
            cmdStr = CmdEnum.LIGHT_SWITCH_ON.getCmdDesc();
            onOrOff = "开启";
        }else if (op != 0){
            return Result.error().setData("mes", "op请选择0或者1操作");
        }
        Room room = roomService.getById(roomId);
        if (room != null){
            LambdaQueryWrapper<Light> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Light::getRoomId, roomId);
            List<Light> lights = lightService.list(wrapper);
            List<Light> openedLights = new ArrayList<>();
            Map<String, List<Light>> result = new HashMap<>();
            result.put("所有灯泡", lights);
            for (Light light:lights){
                if (light.getState().equals(state)) {
                    openedLights.add(light);
                    if (op == 1) {
                        DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_ON.getCmdValue() + "_" + light.getId(), timeout);
                        lightService.on(light.getId());
                    }
                    else {
                        DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_OFF.getCmdValue() + "_" + light.getId(), timeout);
                        lightService.off(light.getId());
                    }
                    logService.saveCmdLog(light, cmdStr);
                }
            }
            result.put(onOrOff + "灯泡", openedLights);
            return Result.success().setData("result", result);
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }

    /**
     * 通过标识删除房间以及房间里面所有电器
     * @param roomId 房间id
     * @return Result
     */
    @DeleteMapping("/deleteRoom")
    @ApiOperation("通过标识删除房间, 房间里的电器, 房间里的场景, 以及相关场景计划")
    public Result deleteRoom(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        Room room = roomService.getById(roomId);
        if (room != null){
            // 删除房间所有电器
            airconService.remove(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            lightService.remove(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
            lockService.remove(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, roomId));
            otherService.remove(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, roomId));
            // 删除场景所有场景计划
            List<Scene> scenes = sceneService.list(new LambdaQueryWrapper<Scene>().eq(Scene::getRoomId, roomId));
            for (Scene scene:scenes){
                scenePlanService.remove(new LambdaQueryWrapper<ScenePlan>().eq(ScenePlan::getSceneId, scene.getId()));
            }
            // 删除房间所有场景
            sceneService.remove(new LambdaQueryWrapper<Scene>().eq(Scene::getRoomId, roomId));
            // 删除房间
            roomService.removeById(roomId);
            return Result.success().setData("mes", "删除标识为" + roomId + "的房间成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }


    /**
     * 开启房间所有空调并进行相应控制
     * @param roomId 房间id
     * @param onOrOff 选择是否开关
     * @param mode 模式
     * @param temperature 温度
     * @param speed 风速
     * @return Result
     */
    @PutMapping("allAirconsOps")
    @ApiOperation("开启房间所有空调并进行相应控制")
    public Result allAirconsOps (@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                                 @ApiParam(value = "开关:选择[0]关闭或[1]开启", required = true) @RequestParam("onOrOff") String onOrOff,
                                 @ApiParam(value = "模式:选择[自动],[制冷],[制热],[通风],[节能]", required = true) @RequestParam("mode") String mode,
                                 @ApiParam(value = "温度:输入{16~30}单位摄氏度", required = true) @RequestParam("temperature") String temperature,
                                 @ApiParam(value = "风速:输入{0~4}风速,0是自动风速,1,2,3,4是对应几档风", required = true) @RequestParam("speed") String speed){
        // 判断房间是否存在
        Room room = roomService.getById(roomId);
        if (room != null){
            // 找到本房间所有空调
            List<Aircon> aircons = airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            // 判断是开启还是关闭
            if (onOrOff.equals("0")){
                for (Aircon aircon:aircons){
                    if (aircon.getState() == 1){
                        // 向硬件端发送请求
                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_OFF.getCmdValue() + "_" + aircon.getId(), timeout);
                        airconService.off(aircon.getId());
                        logService.saveCmdLog(aircon, CmdEnum.AIR_SWITCH_OFF.getCmdDesc());
                    }
                }
                return Result.success().setData("mes", "供暖系统关闭成功");
            }else if (onOrOff.equals("1")){
                List<String> modes = Arrays.asList("自动", "制冷", "制热", "通风", "节能");
                // 判断模式是否正确
                if (modes.contains(mode)){
                    try{
                        if (Integer.parseInt(temperature) >= 16 && Integer.parseInt(temperature) <= 30){
                            if (Integer.parseInt(speed) >= 0 && Integer.parseInt(speed) <= 4){
                                for (Aircon aircon:aircons){
                                    // 首先全部开启该空调
                                    if (aircon.getState() == 0) {
                                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_ON.getCmdValue() + "_" + aircon.getId(), timeout);
                                        airconService.on(aircon.getId());
                                        logService.saveCmdLog(aircon, CmdEnum.AIR_SWITCH_ON.getCmdDesc());
                                    }
                                    // 设置空调模式
                                    if (!aircon.getMode().equals(mode)){
                                        switch (mode){
                                            case "自动": {
                                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_AUTO.getCmdValue() + "_" + aircon.getId(), timeout);
                                                airconService.modeAuto(aircon.getId());
                                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_AUTO.getCmdDesc());
                                                break;
                                            }
                                            case "制冷": {
                                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_COOL.getCmdValue() + "_" + aircon.getId(), timeout);
                                                airconService.modeCool(aircon.getId());
                                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_COOL.getCmdDesc());
                                                break;
                                            }
                                            case "制热": {
                                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_HOT.getCmdValue() + "_" + aircon.getId(), timeout);
                                                airconService.modeHot(aircon.getId());
                                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_HOT.getCmdDesc());
                                                break;
                                            }
                                            case "通风": {
                                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_DRY.getCmdValue() + "_" + aircon.getId(), timeout);
                                                airconService.modeDry(aircon.getId());
                                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_DRY.getCmdDesc());
                                                break;
                                            }
                                            case "节能": {
                                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_Econo.getCmdValue() + "_" + aircon.getId(), timeout);
                                                airconService.modeEcono(aircon.getId());
                                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_Econo.getCmdDesc());
                                                break;
                                            }
                                        }
                                    }
                                    // 设置空调温度
                                    if (aircon.getTemper() != Integer.parseInt(temperature)){
                                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_TEMP_.getCmdValue() + temperature + "_" + aircon.getId(), timeout);
                                        airconService.temper(aircon.getId(), temperature);
                                        logService.saveCmdLog(aircon, CmdEnum.AIR_SET_TEMP_.getCmdDesc() + "为" + temperature);
                                    }
                                    // 设置空调风速
                                    if (!aircon.getWindSpeed().equals(speed)){
                                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_FUN_.getCmdValue() + speed + "_" + aircon.getId(), timeout);
                                        airconService.windSpeed(aircon.getId(), speed);
                                        logService.saveCmdLog(aircon, CmdEnum.AIR_SET_FUN_.getCmdDesc() + "为" + speed);
                                    }
                                }
                                return Result.success().setData("mes", "供暖系统开启成功");
                            }else {
                                return Result.error().setData("mes", "风速范围0~4, 实际输入:" + speed);
                            }
                        }else {
                            return Result.error().setData("mes", "温度范围16~30, 实际输入:" + temperature);
                        }
                    }catch (Exception e){
                        return Result.error().setData("mes", "温度和风速必须输入数字");
                    }
                } else{
                  return Result.error().setData("mes", "空调模式仅仅是选择[自动],[制冷],[制热],[通风],[自动]" + ", 实际输入为:" + mode);
                }
            }else {
                return Result.error().setData("mes", "开关只能是0和1" + ", 实际为: " + onOrOff);
            }
        }else {
            return Result.error().setData("mes", "没有找到" + roomId + "的房间");
        }
    }

    /**
     * 通过房间标识统一操作房间灯泡
     * @param roomId 房间标识
     * @param onOrOff 开还是关
     * @param red 红色
     * @param green 绿色
     * @param blue 蓝色
     * @param value 亮度
     * @return Result
     */
    @ApiOperation("通过房间标识统一操作房间灯泡")
    @PutMapping("/allLightOps")
    public Result allLightOps(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                              @ApiParam(value = "开[1], 关[0]", required = true) @RequestParam("onOrOff") String onOrOff,
                              @ApiParam(value = "r[0~255]", required = true) @RequestParam("red") String red,
                              @ApiParam(value = "g[0~255]", required = true) @RequestParam("green") String green,
                              @ApiParam(value = "b[0~255]", required = true) @RequestParam("blue") String blue,
                              @ApiParam(value = "目标亮度[0~99]", required = true) @RequestParam("value") String value){
        // 判断房间是否存在
        Room room = roomService.getById(roomId);
        if (room != null){
            // 通过房间id获取房间所有的灯泡
            List<Light> lights = lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
            // 对这些灯泡进行控制
            // 控制开关
            Result res1 = allLightsOnOrOff(roomId, Integer.parseInt(onOrOff));
            JSONObject res1Json = (JSONObject) JSONObject.toJSON(res1);
            if (!res1Json.get("code").toString().equals("20000")){
                return res1;
            }
            for (Light light:lights){
                // 控制颜色
                Result res2 = lightController.color(light.getId(), red, green, blue);
                JSONObject res2Json = (JSONObject) JSONObject.toJSON(res2);
                if (!res2Json.get("code").toString().equals("20000")){
                    return res2;
                }
                // 控制亮度
                Result res3 = lightController.intensity(light.getId(), value);
                JSONObject res3Json = (JSONObject) JSONObject.toJSON(res3);
                if (!res3Json.get("code").toString().equals("20000")){
                    return res3;
                }
            }
            return Result.success().setData("mes", "成功设置!");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }

    /**
     * 语音识别
     * @param mp3File 语音识别的mp3文件
     * @return Result
     */
    @PostMapping("/audio")
    @ApiOperation("语音识别")
    public Result audio(@ApiParam(value = "mp3文件", required = true) @RequestParam("mp3File") MultipartFile mp3File) {
        if (mp3File != null) {
            // 使用UUID新建文件名防止生成的临时文件重复
            String fileName = String.valueOf(UUID.randomUUID());

            String mp3Path = actualPathAudio + "mp3";
            String pcmPath = actualPathAudio + "pcm";
            // 每天建立一个文件夹
            String time = (new SimpleDateFormat("yyyy/MM/dd")).format(System.currentTimeMillis());
            mp3Path += "/" + time + "/";
            pcmPath += "/" + time + "/";
            // 将文件进行转换为pcm同时发送到语音识别获得返回值
            try {
                // 判断是否存在本目录
                File file1 = new File(mp3Path + fileName + ".mp3");
                File file2 = new File(pcmPath + fileName + ".pcm");
                if (!file1.getParentFile().exists()) {
                    boolean a = file1.getParentFile().mkdirs();
                }
                if (!file2.getParentFile().exists()) {
                    boolean b = file2.getParentFile().mkdirs();
                }

                // 新建文件并将MP3文件保存在该路径下
                File file = new File(mp3Path, fileName + ".mp3");
                mp3File.transferTo(file);

                // 将该路径下面的mp3文件转换为pcm文件并保存下来
                AudioConvertUtils.mp3ToPcm(mp3Path + fileName + ".mp3", pcmPath + fileName + ".pcm");

                // 设置接口请求参数以及请求头和请求体
                Map<String, String> params = new HashMap<>();
                Map<String, String> headers = new HashMap<>();
                params.put("appkey", audioApikey);
                params.put("sample_rate", audioRate);
                headers.put("X-NLS-Token", aliyunToken);

                // 使用工具类发送请求
                String res = HttpUtils.sendPostAFile(audioUrl, params, headers, new File(pcmPath + fileName + ".pcm"));

                // 根据返回情况返回结果
                JSONObject resJson = JSON.parseObject(res);
                log.info(res);
                String status = resJson.get("status").toString();
                if (status.equals("20000000")){
                    return Result.success().setData("res", resJson.get("result"));
                }if (status.equals("40000001")){
                    String token = TokenUtils.getAliyunNlsToken();
                    fileUtils.modifyAliyunToken(token);
                    aliyunToken = token;
                    log.info("新的阿里云语音识别token: " + token);
                    return Result.error().setData("mes", "之前的token不存在或过期, 已重新获取token, 请重新说一次命令");
                }else {
                    return Result.error().setData("mes", resJson.get("message"));
                }
            }catch (Exception e){
                log.info("语音识别异常", e);
                return Result.error().setData("mes", "语音识别异常");
            }
        }
        else {
            return Result.error().setData("mes", "请上传文件");
        }
    }


    /**
     * 开启或者关闭本房间所有空调
     * @param roomId 房间id
     * @param op 操作
     * @return result
     */
    @PutMapping("/allAirsOnOrOff")
    @ApiOperation("语音识别-开启或者关闭所有空调")
    public Result allAirsOnOrOff(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                                 @ApiParam(value = "选择0[关闭]或1[开启]", required = true) @RequestParam("op") Integer op){
        // 默认是关闭命令
        Integer state = 1;
        String cmdStr = CmdEnum.AIR_SWITCH_OFF.getCmdDesc();
        String onOrOff = "关闭";
        // 如果是开启命令则进行修改
        if (op == 1){
            state = 0;
            cmdStr = CmdEnum.AIR_SWITCH_ON.getCmdDesc();
            onOrOff = "开启";
        }else if (op != 0){
            return Result.error().setData("mes", "op请选择0或者1操作");
        }
        Room room = roomService.getById(roomId);
        if (room != null){
            LambdaQueryWrapper<Aircon> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Aircon::getRoomId, roomId);
            List<Aircon> aircons = airconService.list(wrapper);
            List<Aircon> openedAirs = new ArrayList<>();
            Map<String, List<Aircon>> result = new HashMap<>();
            result.put("所有空调", aircons);
            for (Aircon aircon: aircons){
                if (aircon.getState().equals(state)) {
                    openedAirs.add(aircon);
                    if (op == 1) {
                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_ON.getCmdValue() + "_" + aircon.getId(), timeout);
                        airconService.on(aircon.getId());
                    }
                    else {
                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_OFF.getCmdValue() + "_" + aircon.getId(), timeout);
                        airconService.off(aircon.getId());
                    }
                    logService.saveCmdLog(aircon, cmdStr);
                }
            }
            result.put(onOrOff + "空调", openedAirs);
            return Result.success().setData("result", result);
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }

    /**
     * 语音识别-设置空调
     * @param roomId 房间标识
     * @param key 设置目标
     * @param value 设置目标的值
     * @return Result
     */
    @PutMapping("/allAirsSet")
    @ApiOperation("语音识别-设置空调, 对于没有开启的空调将自动将其开启")
    public Result allAirsSet(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                             @ApiParam(value = "选择[模式],[风速],[温度]", required = true) @RequestParam("key") String key,
                             @ApiParam(value = "选择[模式:[自动, 制冷, 制热, 通风, 节能], 风速:[0(自动风), 1, 2, 3, 4], 温度:[16~30]整数]", required = true) @RequestParam("value") String value){
        Room room = roomService.getById(roomId);
        if (room != null){
            List<Aircon> aircons = airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            // 将没有开启的空调全部开启
            for (Aircon aircon:aircons) {
                if (aircon.getState() == 1) continue;
                // 向硬件端发送请求
                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_ON.getCmdValue() + "_" + aircon.getId(), timeout);
                airconService.on(aircon.getId());
                logService.saveCmdLog(aircon, CmdEnum.AIR_SWITCH_ON.getCmdDesc());
            }
            // 判断选择的功能
            switch (key){
                case "模式":{
                    switch (value) {
                        case "自动": {
                            for (Aircon aircon:aircons){
                                if (aircon.getMode().equals("自动")) continue;
                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_AUTO.getCmdValue() + "_" + aircon.getId(), timeout);
                                airconService.modeAuto(aircon.getId());
                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_AUTO.getCmdDesc());
                            }
                            break;
                        }
                        case "制冷": {
                            for (Aircon aircon:aircons){
                                if (aircon.getMode().equals("制冷")) continue;
                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_COOL.getCmdValue() + "_" + aircon.getId(), timeout);
                                airconService.modeCool(aircon.getId());
                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_COOL.getCmdDesc());
                            }
                            break;
                        }
                        case "制热": {
                            for (Aircon aircon:aircons){
                                if (aircon.getMode().equals("制热")) continue;
                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_HOT.getCmdValue() + "_" + aircon.getId(), timeout);
                                airconService.modeHot(aircon.getId());
                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_HOT.getCmdDesc());
                            }
                            break;
                        }
                        case "通风": {
                            for (Aircon aircon:aircons){
                                if (aircon.getMode().equals("通风")) continue;
                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_DRY.getCmdValue() + "_" + aircon.getId(), timeout);
                                airconService.modeDry(aircon.getId());
                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_DRY.getCmdDesc());
                            }
                            break;
                        }
                        case "节能": {
                            for (Aircon aircon:aircons){
                                if (aircon.getMode().equals("节能")) continue;
                                DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_Econo.getCmdValue() + "_" + aircon.getId(), timeout);
                                airconService.modeEcono(aircon.getId());
                                logService.saveCmdLog(aircon, CmdEnum.AIR_MODE_Econo.getCmdDesc());
                            }
                            break;
                        }
                        default: {
                            return Result.error().setData("mes", "key应该为自动, 制冷, 制热, 通风, 节能, 实际为" + value);
                        }
                    }
                    break;
                }
                case "风速":{
                    List<String> speeds = Arrays.asList("0", "1", "2", "3", "4");
                    if (!speeds.contains(value)) return Result.error().setData("mes", "风速0~4, 实际为" + value);
                    for (Aircon aircon:aircons){
                        if (aircon.getWindSpeed().equals(value)) continue;
                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_FUN_.getCmdValue() + value + "_" + aircon.getId(), timeout);
                        airconService.windSpeed(aircon.getId(), value);
                        logService.saveCmdLog(aircon, CmdEnum.AIR_SET_FUN_.getCmdDesc() + "为" + value);
                    }
                    break;
                }
                case "温度":{
                    try {
//                        if (value.contains(".")) return Result.error().setData("mes", "温度必须是整数, 实际为" + value);
                        if (Integer.parseInt(value) < 16 || Integer.parseInt(value) > 30)
                            return Result.error().setData("mes", "温度16~30, 实际为" + value);
                    }catch (ConversionException e){
                        log.info("整数转换异常", e);
                        return Result.error().setData("mes", "温度必须是整数, 实际为" + value);
                    }
                    for (Aircon aircon:aircons){
                        if (String.valueOf(aircon.getTemper()).equals(value)) continue;
                        DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_TEMP_.getCmdValue() + value + "_" + aircon.getId(), timeout);
                        airconService.temper(aircon.getId(), value);
                        logService.saveCmdLog(aircon, CmdEnum.AIR_SET_TEMP_.getCmdDesc() + "为" + value);
                    }
                    break;
                }
                default:{
                    return Result.error().setData("mes", "key应该为模式, 风速, 温度. 实际为" + key);
                }
            }
            return Result.success().setData("mes", "设置房间" + room.getPosition() + "所有空调的" + key + "为" + value + "成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }

    /**
     * 语音识别-设置房间的所有灯泡亮度
     * @param roomId 房间标识
     * @param value 亮度的值
     * @return Result
     */
    @PutMapping("allLightSet")
    @ApiOperation("语音识别-设置房间的所有灯泡亮度")
    public Result allLightSet(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                              @ApiParam(value = "目标亮度[0~99]整数", required = true) @RequestParam("value") String value){
        Room room = roomService.getById(roomId);
        if (room != null){
            List<Light> lights = lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
            try {
                if (Integer.parseInt(value) < 0 || Integer.parseInt(value) > 99)
                    return Result.error().setData("mes", "亮度为0~99, 实际为" + value);
            }catch (ConversionException e) {
                return Result.error().setData("mes", "整数转换错误");
            }
            // 遍历所有灯泡将所有灯泡开启
            for (Light light:lights){
                if (light.getState() == 1) continue;
                DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_ON.getCmdValue() + "_" + light.getId(), timeout);
                lightService.on(light.getId());
                logService.saveCmdLog(light, CmdEnum.LIGHT_SWITCH_ON.getCmdDesc());
            }
            // 遍历所有灯泡设置灯泡亮度
            for (Light light:lights){
                if (light.getIntensity() == Integer.parseInt(value)) continue;
                DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SET_INTENSITY_.getCmdValue() + value + "_" + light.getId(), timeout);
                lightService.intensity(light.getId(), value);
                logService.saveCmdLog(light, CmdEnum.LIGHT_SET_INTENSITY_.getCmdDesc() + "为" + value);
            }
            return Result.success().setData("mes", "设置房间" + room.getPosition() + "所有灯泡的亮度为" + value + "成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + roomId + "的房间");
        }
    }
}
