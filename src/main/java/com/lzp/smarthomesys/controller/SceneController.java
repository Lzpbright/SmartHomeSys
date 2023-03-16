package com.lzp.smarthomesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.service.impl.*;
import com.lzp.smarthomesys.utils.Result;
import com.lzp.smarthomesys.utils.SceneUtils;
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
@RequestMapping("/scene")
@Api("场景控制器")
public class SceneController {

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    SceneServiceImpl sceneService;

    @Resource
    RoomServiceImpl roomService;

    @Resource
    UserServiceImpl userService;

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


    /**
     * 通过房间标识获取所有场景
     * @param roomId 房间标识
     * @return Result
     */
    @GetMapping("/getByRoomId")
    @ApiOperation("用房间标识获取场景")
    public Result getByRoomId(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scene::getRoomId, roomId);
        List<Scene> scenes = sceneService.list(wrapper);
        return Result.success().setData("scenes", scenes);
    }

    /**
     * 添加场景
     * @param roomId 房间标识
     * @param intro 房间备注
     * @param appliance 场景电器
     * @return Result
     */
    @GetMapping("/add")
    @ApiOperation("添加场景")
    public Result add(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                           @ApiParam(value = "场景介绍", required = true) @RequestParam("intro") String intro,
                           @ApiParam(value = "相关电器(格式:X(灯泡),A(灯泡标识);Y(门锁),B(门锁标识)...【!!电器类型仅有空调、灯泡、门锁、其他】,表示该场景开启A,B,C三电器, 其他电器均关闭)")
                               @RequestParam(value = "appliance", required = false) String appliance){
        Room room = roomService.getById(roomId);
        if (room != null) {
            LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Scene::getIntro, intro).eq(Scene::getRoomId, roomId);
            List<Scene> list = sceneService.list(wrapper);
            if (list.size() == 0) {
                Scene scene = new Scene();
                scene.setRoomId(roomId);
                scene.setIntro(intro);
                appliance = appliance.replace(" ", "");
                scene.setAppliance(appliance);
                sceneService.save(scene);
                return Result.success().setData("mes", "添加成功");
            } else {
                return Result.error().setData("mes", "同一房间不能存在相同场景名");
            }
        }else{
            return Result.error().setData("mes", "房间不存在");
        }
    }

    /**
     * 修改场景或者修改场景电器
     * @param id 场景标识
     * @param intro 场景名字
     * @param appliance 场景电器
     * @return Result
     */
    @GetMapping("/modify")
    @ApiOperation("修改或增加电器")
    public Result modify(@ApiParam(value = "场景标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "场景介绍（就是场景的名字）") @RequestParam(value = "intro", required = false) String intro,
                         @ApiParam(value = "相关电器(格式:X(灯泡),A(灯泡标识);Y(门锁),B(门锁标识)...【!!电器类型仅有空调、灯泡、门锁、其他】,表示该场景开启A,B,C三电器, 其他电器均关闭)") @RequestParam(value = "appliance", required = false) String appliance){
        Scene scene = new Scene();
        scene.setId(id);
        if (!Objects.equals(intro, "")) {
            scene.setIntro(intro);
        }
        appliance = appliance.replace(" ", "");
        scene.setAppliance(appliance);
        sceneService.updateById(scene);
        return Result.success().setData("mes", "修改成功");
    }

    /**
     * 通过标识删除场景
     * @param id 场景标识
     * @return Result
     */
    @DeleteMapping("delete")
    @ApiOperation("通过标识删除场景")
    public Result delete(@ApiParam(value = "场景标识", required = true) @RequestParam("id") String id){
        Scene scene = sceneService.getById(id);
        if (scene != null){
            sceneService.removeById(id);
            return Result.success().setData("mes", "已删除id为" + id + "的场景");
        }else{
            return Result.error().setData("mes", "没有找到id为" + id + "的场景");
        }
    }

    /**
     * 获取场景电器
     * @param id 场景id
     * @return Result
     */
    @GetMapping("/devices")
    @ApiOperation("获取场景所有电器")
    public Result modify(@ApiParam(value = "场景标识", required = true) @RequestParam(value = "id") String id){
        Scene scene = sceneService.getById(id);
        String appliance = scene.getAppliance();

        String[]all = appliance.split(";");

        List<String> devices = new ArrayList<>();

        for (String temp : all) {
            String[] single = temp.split(",");
            devices.add(single[0] + ":" + single[1]);
        }
        return Result.success().setData("devices", devices);
    }

    /**
     * 通过场景标识开启场景
     * @param id 场景标识
     * @return Result
     */
    @GetMapping("/on")
    @ApiOperation("开启本场景")
    public Result on(@ApiParam(value = "场景标识", required = true) @RequestParam(value = "id") String id){
        Scene scene = sceneService.getById(id);
        if (scene != null) {
            if (scene.getState() == 0) {
                // 获取userId,从而获得user所有的电器
                Room theRoom = roomService.getById(scene.getRoomId());
                User user = userService.getById(theRoom.getUserId());
                String userId = user.getId();

                // 一些所需中间变量
                String appliances = scene.getAppliance();

                // 遍历用户所有的电器并进行响应操作
                LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
                roomWrapper.eq(Room::getUserId, userId);
                List<Room> rooms = roomService.list(roomWrapper);

                Map<Object, Object> result = new HashMap<>();
                for (Room room : rooms) {
                    LambdaQueryWrapper<Aircon> airconWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Light> lightWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Lock> lockWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Other> otherWrapper = new LambdaQueryWrapper<>();
                    airconWrapper.eq(Aircon::getRoomId, room.getId());
                    lightWrapper.eq(Light::getRoomId, room.getId());
                    lockWrapper.eq(Lock::getRoomId, room.getId());
                    otherWrapper.eq(Other::getRoomId, room.getId());
                    List<Aircon> airconList = airconService.list(airconWrapper);
                    List<Light> lightList = lightService.list(lightWrapper);
                    List<Lock> lockList = lockService.list(lockWrapper);
                    List<Other> otherList = otherService.list(otherWrapper);

                    List<IDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(airconList);
                    deviceList.addAll(lightList);
                    deviceList.addAll(lockList);
                    deviceList.addAll(otherList);
                    result.put("房间" + room.getId(), SceneUtils.onOrOff(id, deviceList, appliances, "开启"));
                }
                result.put("场景电器", appliances);
                scene.setState(1);
                sceneService.updateById(scene);
                return Result.success().setData("result", result);
            } else {
                return Result.success().setData("mes", "标识为" + id + "的场景已开");
            }
        }else{
            return Result.error().setData("mes", "没有找到标识为" + id + "的场景");
        }
    }

    /**
     * 通过哦场景标识关闭场景
     * @param id 场景标识
     * @return Result
     */
    @GetMapping("/off")
    @ApiOperation("关闭本场景")
    public Result off(@ApiParam(value = "场景标识", required = true) @RequestParam(value = "id") String id){
        Scene scene = sceneService.getById(id);
        if (scene != null) {
            if (scene.getState() == 1) {
                // 获取userId,从而获得user所有的电器
                Room theRoom = roomService.getById(scene.getRoomId());
                User user = userService.getById(theRoom.getUserId());
                String userId = user.getId();

                // 一些所需中间变量
                String appliances = scene.getAppliance();

                // 遍历用户所有的电器并进行响应操作
                LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
                roomWrapper.eq(Room::getUserId, userId);
                List<Room> rooms = roomService.list(roomWrapper);

                Map<Object, Object> result = new HashMap<>();
                for (Room room : rooms) {
                    LambdaQueryWrapper<Aircon> airconWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Light> lightWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Lock> lockWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Other> otherWrapper = new LambdaQueryWrapper<>();
                    airconWrapper.eq(Aircon::getRoomId, room.getId());
                    lightWrapper.eq(Light::getRoomId, room.getId());
                    lockWrapper.eq(Lock::getRoomId, room.getId());
                    otherWrapper.eq(Other::getRoomId, room.getId());
                    List<Aircon> airconList = airconService.list(airconWrapper);
                    List<Light> lightList = lightService.list(lightWrapper);
                    List<Lock> lockList = lockService.list(lockWrapper);
                    List<Other> otherList = otherService.list(otherWrapper);

                    List<IDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(airconList);
                    deviceList.addAll(lightList);
                    deviceList.addAll(lockList);
                    deviceList.addAll(otherList);
                    result.put("房间" + room.getId(), SceneUtils.onOrOff(id, deviceList, appliances, "关闭"));
                }
                result.put("场景电器", appliances);
                scene.setState(0);
                sceneService.updateById(scene);
                return Result.success().setData("result", result);
            } else {
                return Result.success().setData("mes", "标识为" + id + "的场景已关");
            }
        }else{
            return Result.error().setData("mes", "没有找到标识为" + id + "的场景");
        }
    }
}
