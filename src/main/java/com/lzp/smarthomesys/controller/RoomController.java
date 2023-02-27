package com.lzp.smarthomesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.service.impl.*;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ApiOperation("通过用户标识获取其所有房间")
    @GetMapping("/getByUserId")
    public Result getByUserId(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId){
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Room::getUserId, userId);
        List<Room> rooms = roomService.list(wrapper);
        return Result.success().setData("rooms", rooms);
    }

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


    @ApiOperation("给用户增加房间")
    @GetMapping("/addRoom")
    public Result addRoom(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String usrId,
                          @ApiParam(value = "房间位置（例如三楼卧室）", required = true) @RequestParam("position") String position,
                          @ApiParam(value = "房间备注") @RequestParam(value = "remarks", required = false) String remarks){
        Room room = new Room();
        room.setUserId(usrId);
        room.setPosition(position);
        room.setRemarks(remarks);
        roomService.save(room);
        return Result.success().setData("mes", "添加成功");
    }
}
