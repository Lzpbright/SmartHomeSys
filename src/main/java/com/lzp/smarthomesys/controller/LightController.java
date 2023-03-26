package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.Light;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LightServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
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
@RequestMapping("/light")
@Api("灯泡控制器")
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
        if (lightService.getById(id) == null) return Result.error().setData("mes", "没有标识为" + id + "的灯泡");
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
        if (lightService.getById(id) == null) return Result.error().setData("mes", "没有标识为" + id + "的灯泡");
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
    @ApiOperation("设置亮度")
    public Result intensity(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                            @ApiParam(value = "目标亮度（0~100）", required = true) @RequestParam("value") String value){
        if (lightService.getById(id) == null) return Result.error().setData("mes", "没有标识为" + id + "的灯泡");
        if (Integer.parseInt(value) >= 0 && Integer.parseInt(value) <= 100){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SET_INTENSITY_.getCmdValue() + String.format("%03d", Integer.parseInt(value)) + "_" + id, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Light light = lightService.getById(id);
            if (light.getState() == 1) {
                if (error.equals("succ")) {
                    lightService.intensity(id, value);
                    logService.saveCmdLog(lightService.getById(id), CmdEnum.LIGHT_SET_INTENSITY_.getCmdDesc() + "为" + String.format("%03d", Integer.parseInt(value)));
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
     * @return Result
     */
    @PutMapping("/modify")
    @ApiOperation("修改灯泡基本信息（这里将颜色修改删去了）")
    public Result modify(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "额定功率") @RequestParam(value = "power", required = false) String power,
                         @ApiParam(value = "灯泡种类") @RequestParam(value = "kind", required = false) String kind){
        if (lightService.getById(id) == null) return Result.error().setData("mes", "没有标识为" + id + "的灯泡");
        if (lightService.getById(id) != null){
            Light light = new Light();
            light.setId(id);
            if (power != null) light.setPower(power);
            if (kind != null) light.setKind(kind);
            lightService.updateById(light);
            return Result.success().setData("mes", "修改成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + id + "的灯泡");
        }
    }


    /**
     * 通过标识设计灯泡颜色
     * @param id 标识
     * @param red 红色
     * @param green 绿色
     * @param blue 蓝色
     * @return Result
     */
    @PutMapping("/color")
    @ApiOperation("通过标识设置灯泡颜色")
    public Result color(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id,
                        @ApiParam(value = "r[0~255]", required = true) @RequestParam("red") String red,
                        @ApiParam(value = "g[0~255]", required = true) @RequestParam("green") String green,
                        @ApiParam(value = "b[0~255]", required = true) @RequestParam("blue") String blue){
        Light light = lightService.getById(id);
        if (light != null){
            try {
                int redInt = Integer.parseInt(red);
                int greenInt = Integer.parseInt(green) ;
                int blueInt = Integer.parseInt(blue);
                red = String.format("%03d", redInt);
                green = String.format("%03d", greenInt);
                blue = String.format("%03d", blueInt);
                String rgb = "RGB(" + red + ", " + green + ", " + blue +")";
                if (lightService.getById(id).getColor().equals(rgb)) return Result.success().setData("mes", "rgb没有发生变化");
                if (redInt < 0 || redInt > 255) return Result.error().setData("mes", "red应该为0~255, 实际为:" + red);
                if (greenInt < 0 || greenInt > 255) return Result.error().setData("mes", "green应该为0~255, 实际为:" + green);
                if (blueInt < 0 || blueInt > 255) return Result.error().setData("mes", "blue应该为0~255, 实际为:" + blue);
                DeviceUtils.sendCmd(deviceId, CmdEnum.LIGHT_SET_COLOR_.getCmdValue() + red + "_" + green + "_" + blue + "_" + id, timeout);
                lightService.color(id, rgb);
                logService.saveCmdLog(light, CmdEnum.LIGHT_SET_COLOR_.getCmdDesc() + "为" + rgb);
                return Result.success().setData("mes", "已成功设置灯泡颜色为" + rgb);
            }catch (Exception e){
                return Result.error().setData("mes", "red, green, blue必须要是整数");
            }
        }else {
            return Result.error().setData("mes", "没有找到标识为" + id + "的灯泡");
        }
    }

    /**
     * 通过灯泡标识获取灯泡信息
     * @param id 标识
     * @return Result
     */
    @GetMapping("/getById")
    @ApiOperation("通过灯泡标识获取灯泡信息")
    public Result getById(@ApiParam(value = "灯泡标识", required = true) @RequestParam("id") String id){
        Light light = lightService.getById(id);
        return Result.success().setData("light", light);
    }
}
