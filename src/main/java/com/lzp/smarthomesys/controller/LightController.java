package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.Light;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LightServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
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
@RequestMapping("/light")
public class LightController {

    @Resource
    LightServiceImpl lightService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    LogServiceImpl logService;

    @Resource
    RoomServiceImpl roomService;

    @GetMapping("/on")
    @ApiOperation("开启灯泡(硬件方尚未实现)")
    public Result on(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_ON.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Light light = lightService.getById(id);
            Room room = roomService.getById(light.getRoomId());
            // light 对象
            light.setState(1);
            lightService.updateById(light);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + light.getSmallPos() + "|电器: 灯泡|电器标识: " + light.getId();
            String action = CmdEnum.LIGHT_SWITCH_ON.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    @GetMapping("/off")
    @ApiOperation("关闭灯泡(硬件方尚未实现)")
    public Result off(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_OFF.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Light light = lightService.getById(id);
            Room room = roomService.getById(light.getRoomId());
            // light 对象
            light.setState(0);
            lightService.updateById(light);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + light.getSmallPos() + "|电器: 灯泡|电器标识: " + light.getId();
            String action = CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    @GetMapping("/intensity")
    @ApiOperation("设置亮度(硬件方尚未实现)")
    public Result intensity(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                            @ApiParam(value = "目标亮度（0~100）", required = true) @RequestParam("value") String value){
        if (Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 100){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SET_INTENSITY_.getCmdValue() + value, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Light light = lightService.getById(id);
            if (light.getState() == 1) {
                if (error.equals("succ")) {
                    Room room = roomService.getById(light.getRoomId());
                    // light 对象
                    light.setIntensity(Integer.parseInt(value));
                    lightService.updateById(light);
                    // log 对象
                    String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                    String userId = room.getUserId();
                    String target = "房间: " + room.getPosition() + "|具体位置: "
                            + light.getSmallPos() + "|电器: 灯泡|电器标识: " + light.getId();
                    String action = CmdEnum.LIGHT_SET_INTENSITY_.getCmdDesc() + "为" + value;
                    Log cmdLog = new Log();
                    cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                    logService.save(cmdLog);
                    return Result.success().setData("res", res);
                } else {
                    return Result.error().setData("res", res);
                }
            }
            else {
                return Result.error().setData("mes", "灯泡未开启");
            }
        }else {
            return Result.error().setData("mes", "亮度范围为0~100");
        }
    }
}
