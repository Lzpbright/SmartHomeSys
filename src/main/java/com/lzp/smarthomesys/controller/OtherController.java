package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Other;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.OtherServiceImpl;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

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
@RequestMapping("/other")
public class OtherController {

    @Resource
    OtherServiceImpl otherService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    LogServiceImpl logService;

    @Resource
    RoomServiceImpl roomService;

    @GetMapping("/on")
    @ApiOperation("开启其他电器(硬件方尚未实现)")
    public Result on(@ApiParam(value = "其他电器标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_ON.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Other other = otherService.getById(id);
            Room room = roomService.getById(other.getRoomId());
            // other 对象
            other.setState(1);
            otherService.updateById(other);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + other.getSmallPos() + "|电器: 其他电器|电器标识: " + other.getId();
            String action = CmdEnum.OTHER_SWITCH_ON.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    @GetMapping("/off")
    @ApiOperation("关闭其他电器(硬件方尚未实现)")
    public Result off(@ApiParam(value = "其他电器标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_OFF.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Other other = otherService.getById(id);
            Room room = roomService.getById(other.getRoomId());
            // light 对象
            other.setState(0);
            otherService.updateById(other);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + other.getSmallPos() + "|电器: 其他电器|电器标识: " + other.getId();
            String action = CmdEnum.OTHER_SWITCH_OFF.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }
}
