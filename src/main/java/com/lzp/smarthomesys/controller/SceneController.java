package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.entity.Scene;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import com.lzp.smarthomesys.service.impl.SceneServiceImpl;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/scene")
@Api("场景控制器")
public class SceneController {

    @Resource
    SceneServiceImpl service;

    @Resource
    RoomServiceImpl roomService;

    @GetMapping("/getByRoomId")
    @ApiOperation("用房间标识获取场景")
    public Result getByRoomId(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId){
        LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scene::getRoomId, roomId);
        List<Scene> scenes = service.list(wrapper);
        return Result.success().setData("scenes", scenes);
    }

    @GetMapping("/add")
    @ApiOperation("添加场景")
    public Result add(@ApiParam(value = "房间标识", required = true) @RequestParam("roomId") String roomId,
                           @ApiParam(value = "场景介绍", required = true) @RequestParam("intro") String intro,
                           @ApiParam(value = "相关电器(格式:X(light)A(light标识标识);Y(lock)B(lock标识),表示该场景开启A,B,C三电器, 其他电器均关闭)")
                               @RequestParam(value = "appliance", required = false) String appliance){
        Room room = roomService.getById(roomId);
        if (room != null) {
            LambdaQueryWrapper<Scene> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Scene::getIntro, intro).eq(Scene::getRoomId, roomId);
            List<Scene> list = service.list(wrapper);
            if (list.size() == 0) {
                Scene scene = new Scene();
                scene.setRoomId(roomId);
                scene.setIntro(intro);
                scene.setAppliance(appliance);
                service.save(scene);
                return Result.success().setData("mes", "添加成功");
            } else {
                return Result.error().setData("mes", "同一房间不能存在相同场景名");
            }
        }else{
            return Result.error().setData("mes", "房间不存在");
        }
    }

    @GetMapping("/modify")
    @ApiOperation("修改或增加电器")
    public Result modify(@ApiParam(value = "场景标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "场景介绍（就是场景的名字）") @RequestParam(value = "intro", required = false) String intro,
                         @ApiParam(value = "相关电器(格式:X(light)A(light标识标识);Y(lock)B(lock标识),表示该场景开启A,B,C三电器, 其他电器均关闭)") @RequestParam(value = "appliance", required = false) String appliance){
        Scene scene = new Scene();
        scene.setId(id);
        if (!Objects.equals(intro, "")) {
            scene.setIntro(intro);
        }
        scene.setAppliance(appliance);
        service.updateById(scene);
        return Result.success().setData("mes", "修改成功");
    }

    @GetMapping("/devices")
    @ApiOperation("获取场景所有电器")
    public Result modify(@ApiParam(value = "场景标识", required = true) @RequestParam(value = "id") String id){
        Scene scene = service.getById(id);
        String appliance = scene.getAppliance();

        String[] all = appliance.split(";");

        List<String> devices = new ArrayList<>();

        for (String temp : all) {
            String[] single = temp.split(",");
            devices.add(single[0] + ":" + single[1]);
        }
        return Result.success().setData("devices", devices);
    }
}
