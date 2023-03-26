package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.OtherServiceImpl;
import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
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
@RequestMapping("/other")
@Api("其他电器控制器")
public class OtherController {

    @Resource
    OtherServiceImpl otherService;

    @Resource
    LogServiceImpl logService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    /**
     * 开启其他电器
     * @param id 其他电器标识
     * @return Result
     */
    @GetMapping("/on")
    @ApiOperation("开启其他电器(硬件方尚未实现)")
    public Result on(@ApiParam(value = "其他电器标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_ON.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            otherService.on(id);
            logService.saveCmdLog(otherService.getById(id), CmdEnum.OTHER_SWITCH_ON.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    /**
     * 关闭其他电器
     * @param id 其他电器标识
     * @return Result
     */
    @GetMapping("/off")
    @ApiOperation("关闭其他电器(硬件方尚未实现)")
    public Result off(@ApiParam(value = "其他电器标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.OTHER_SWITCH_OFF.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            otherService.off(id);
            logService.saveCmdLog(otherService.getById(id), CmdEnum.OTHER_SWITCH_OFF.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }
}
