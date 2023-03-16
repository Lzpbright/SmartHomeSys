package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.Lock;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LightServiceImpl;
import com.lzp.smarthomesys.service.impl.LockServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@RestController
@RequestMapping("/lock")
public class LockController {

    @Resource
    LockServiceImpl lockService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    LogServiceImpl logService;

    @Resource
    RoomServiceImpl roomService;


    /**
     * 开启门锁
     * @param id 门锁标识
     * @return Result
     */
    @GetMapping("/on")
    @ApiOperation("开启门锁")
    public Result on(@ApiParam(value = "门锁标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LOCK_SWITCH_ON.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            lockService.on(id);
            logService.saveCmdLog(lockService.getById(id), CmdEnum.LOCK_SWITCH_ON.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    /**
     * 关闭门锁
     * @param id 门锁标识
     * @return Result
     */
    @GetMapping("/off")
    @ApiOperation("关闭门锁")
    public Result off(@ApiParam(value = "门锁标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LOCK_SWITCH_OFF.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            lockService.off(id);
            logService.saveCmdLog(lockService.getById(id), CmdEnum.LOCK_SWITCH_OFF.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }


    /**
     * 通过用户标识获取门锁信息
     * @param userId 用户标识
     * @return Result
     */
    @GetMapping("/getByUserId")
    @ApiOperation("通过用户标识获取到门锁")
    public Result getByUserId(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId){
        List<Room> rooms = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, userId).eq(Room::getPosition, "客厅"));
        Room room = null;
        if (rooms.size() == 0){
            return Result.error().setData("mes", "用户没有客厅？");
        }else {
            room = rooms.get(0);
        }
        List<Lock> locks = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, room.getId()));
        return Result.success().setData("lock", locks);
    }
}
