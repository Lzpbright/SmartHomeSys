package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
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


    @GetMapping("/on")
    @ApiOperation("开启门锁")
    public Result on(@ApiParam(value = "门锁标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LOCK_SWITCH_ON.getCmdValue(), timeout);
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

    @GetMapping("/off")
    @ApiOperation("关闭门锁")
    public Result off(@ApiParam(value = "门锁标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LOCK_SWITCH_OFF.getCmdValue(), timeout);
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
}
