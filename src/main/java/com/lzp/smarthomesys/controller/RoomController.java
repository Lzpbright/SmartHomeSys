package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.*;
import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@RestController
@Api("房间控制器")
@RequestMapping("/room")
public class RoomController {

    @Resource
    RoomServiceImpl roomService;

    @Resource
    AirconServiceImpl airconService;

    @Resource
    LightServiceImpl lightService;

    @Resource
    LockServiceImpl lockService;

    @Resource
    OtherServiceImpl otherService;

    @Resource
    LogServiceImpl logService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    /**
     * 通过用户标识获取其所有房间
     * @param userId 用户id
     * @return Result
     */
    @ApiOperation("通过用户标识获取其所有房间")
    @GetMapping("/getByUserId")
    public Result getByUserId(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId){
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getUserId, userId);
        List<Room> rooms = roomService.list(wrapper);
        return Result.success().setData("rooms", rooms);
    }

    /**
     * 获取本房间所有电器
     * @param roomId 房间id
     * @return Result
     */
    @ApiOperation("获取本房间所有电器")
    @GetMapping("/getDevices")
    public Result getDevices(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        Room room = roomService.getById(roomId);
        if (room != null) {
            Map<String, Object> deviceMap = new HashMap<>();
            Map<String, Object> resultMap = new HashMap<>();
            List<Aircon> aircons = airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            List<Light> lights = lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
            List<Lock> locks = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, roomId));
            List<Other> others = otherService.list(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, roomId));
            deviceMap.put("aircons", aircons);
            deviceMap.put("lights", lights);
            deviceMap.put("locks", locks);
            deviceMap.put("others", others);
            int devicesNum = aircons.size() + lights.size() + locks.size() + others.size();
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
    @ApiOperation("通过标识删除某电器")
    public Result deleteDevice(@ApiParam(value = "设备类别[选择：空调、灯泡、门锁、其他]", required = true) @RequestParam("type") String type,
                               @ApiParam(value = "电器设备标识", required = true) @RequestParam("deviceId") String deviceId){
        switch (type){
            case "空调": {
                if (airconService.getById(deviceId) != null) {
                    airconService.removeById(deviceId);
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的空调");
                }else{
                    return Result.error().setData("mes", "没有找到" + deviceId + "的空调");
                }
            }
            case "灯泡": {
                if (lightService.getById(deviceId) != null) {
                    lightService.removeById(deviceId);
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的灯泡");
                }else{
                    return Result.error().setData("mes", "没有找到标识为" + deviceId + "的灯泡");
                }
            }
            case "门锁": {
                if (lockService.getById(deviceId) != null) {
                    lockService.removeById(deviceId);
                    return Result.success().setData("mes", "已删除标识为" + deviceId + "的门锁");
                }else {
                    return Result.error().setData("mes", "没有找到标识为" + deviceId + "的门锁");
                }
            }
            case "其他": {
                if (otherService.getById(deviceId) != null) {
                    otherService.removeById(deviceId);
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
    public Result allOn(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                        @ApiParam(value = "选择0[关闭]或1[开启]", required = true) @RequestParam("op") Integer op){
        // 默认是关闭命令
        Integer state = 1;
        String cmdStr = CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc();;
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
                        DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_ON.getCmdValue() + "_" + light.getId(), timeout);
                        lightService.on(light.getId());
                    }
                    else {
                        DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_ON.getCmdValue() + "_" + light.getId(), timeout);
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
    @ApiOperation("通过标识删除房间以及房间里面所有电器")
    public Result deleteRoom(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        Room room = roomService.getById(roomId);
        if (room != null){
            // 删除房间所有电器
            airconService.remove(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, roomId));
            lightService.remove(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, roomId));
            lockService.remove(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, roomId));
            otherService.remove(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, roomId));
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

}
