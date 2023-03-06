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
        switch (type){
            case "空调": {
                if (brand == null) return Result.error().setData("mes", "空调需要品牌");
                LambdaQueryWrapper<Aircon> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Aircon::getRoomId, roomId)
                        .eq(Aircon::getSmallPos, smallPos)
                        .eq(Aircon::getBrand, brand);
                if (airconService.list(wrapper).size() > 0)
                    return Result.error().setData("mes", "本房间的" + smallPos + "已有其他电器");
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
                        .eq(Light::getSmallPos, smallPos)
                        .eq(Light::getBrand, brand);
                if (lightService.list(wrapper).size() > 0)
                    return Result.error().setData("mes", "本房间的" + smallPos + "已有其他电器");
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
                        .eq(Lock::getSmallPos, smallPos)
                        .eq(Lock::getBrand, brand);
                if (lockService.list(wrapper).size() > 0)
                    return Result.error().setData("mes", "本房间的" + smallPos + "已有其他电器");
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
                if (otherService.list(wrapper).size() > 0)
                    return Result.error().setData("mes", "本房间的" + smallPos + "已有其他电器");
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
    @DeleteMapping("deleteDevice")
    @ApiOperation("通过id删除某电器")
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
}
