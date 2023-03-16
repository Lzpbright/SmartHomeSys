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
@RestController
@RequestMapping("/light")
public class LightController {

    @Resource
    LightServiceImpl lightService;

    @Resource
    LogServiceImpl logService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    /**
     * 开启灯泡
     * @param id 灯泡标识
     * @return Result
     */
    @GetMapping("/on")
    @ApiOperation("开启灯泡")
    public Result on(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_ON.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            lightService.on(id);
            logService.saveCmdLog(lightService.getById(id), CmdEnum.LIGHT_SWITCH_ON.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    /**
     * 关闭灯泡
     * @param id id
     * @return result
     */
    @GetMapping("/off")
    @ApiOperation("关闭灯泡")
    public Result off(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SWITCH_OFF.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            lightService.off(id);
            logService.saveCmdLog(lightService.getById(id), CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    /**
     * 设置灯泡亮度
     * @param id 灯泡标识
     * @param value 预设计亮度
     * @return Result
     */
    @GetMapping("/intensity")
    @ApiOperation("设置亮度(硬件方尚未实现)")
    public Result intensity(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                            @ApiParam(value = "目标亮度（0~100）", required = true) @RequestParam("value") String value){
        if (Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 100){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SET_INTENSITY_.getCmdValue() + value + "_" + id, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Light light = lightService.getById(id);
            if (light.getState() == 1) {
                if (error.equals("succ")) {
                    lightService.intensity(id, value);
                    logService.saveCmdLog(lightService.getById(id), CmdEnum.LIGHT_SET_INTENSITY_.getCmdDesc() + "为" + value);
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

    /**
     * 修改灯泡基本信息
     * @param id 灯泡标识
     * @param power 额定功率
     * @param kind 灯泡类别
     * @param color 灯泡颜色
     * @return Result
     */
    @PutMapping("/modify")
    @ApiOperation("修改灯泡基本信息")
    public Result modify(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "额定功率") @RequestParam(value = "power", required = false) String power,
                         @ApiParam(value = "灯泡种类") @RequestParam(value = "kind", required = false) String kind,
                         @ApiParam(value = "灯泡颜色") @RequestParam(value = "color", required = false) String color){
        if (lightService.getById(id) != null){
            Light light = new Light();
            light.setId(id);
            if (power != null) light.setPower(power);
            if (kind != null) light.setKind(kind);
            if (color != null) light.setColor(color);
            lightService.updateById(light);
            return Result.success().setData("mes", "修改成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + id + "的灯泡");
        }
    }
}
