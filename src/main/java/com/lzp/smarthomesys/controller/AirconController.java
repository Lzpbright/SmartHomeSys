package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.Aircon;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.AirconServiceImpl;
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
 *  空调控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@RestController
@RequestMapping("/aircon")
@Api("空调控制器")
public class AirconController {

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    AirconServiceImpl airconService;

    @Resource
    LogServiceImpl logService;

    /**
     * 开启
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/on")
    @ApiOperation("开启本空调")
    public Result on(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_ON.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            airconService.on(id);
            logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_SWITCH_ON.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    /**
     * 关闭
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/off")
    @ApiOperation("关闭本空调")
    public Result off(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_OFF.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            airconService.off(id);
            logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_SWITCH_OFF.getCmdDesc());
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }


    /**
     * 自动模式
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/modeAuto")
    @ApiOperation("自动模式")
    public Result modeAuto(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_AUTO.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (airconService.getById(id).getState() == 1) {
            if (error.equals("succ")) {
                airconService.modeAuto(id);
                logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_MODE_AUTO.getCmdDesc());
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    /**
     * 智能模式
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/modeCool")
    @ApiOperation("制冷模式")
    public Result modeCool(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_COOL.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (airconService.getById(id).getState() == 1) {
            if (error.equals("succ")) {
                airconService.modeCool(id);
                logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_MODE_COOL.getCmdDesc());
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    /**
     * 制热模式
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/modeHot")
    @ApiOperation("制热模式")
    public Result modeHot(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_HOT.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (airconService.getById(id).getState() == 1) {
            if (error.equals("succ")) {
                airconService.modeHot(id);
                logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_MODE_HOT.getCmdDesc());
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    /**
     * 通风模式
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/modeDry")
    @ApiOperation("通风模式")
    public Result modeDry(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_DRY.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (airconService.getById(id).getState() == 1) {
            if (error.equals("succ")) {
                airconService.modeDry(id);
                logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_MODE_DRY.getCmdDesc());
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    /**
     * 节能模式
     * @param id 空调id
     * @return Result
     */
    @GetMapping("/modeEcono")
    @ApiOperation("节能模式")
    public Result modeEcono(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id) {
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_Econo.getCmdValue() + "_" + id, timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (airconService.getById(id).getState() == 1) {
            if (error.equals("succ")) {
                airconService.modeEcono(id);
                logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_MODE_Econo.getCmdDesc());
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else{
            return Result.error().setData("mes", "空调未开启");
        }
    }

    /**
     * 设置温度
     * @param id 空调id
     * @param temperature 目标温度
     * @return Result
     */
    @GetMapping("/temper")
    @ApiOperation("设置温度")
    public Result temper(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "目标温度（16~30）", required = true) @RequestParam("temperature") String temperature){
        if (Integer.parseInt(temperature) >= 16 && Integer.parseInt(temperature) <= 30){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_TEMP_.getCmdValue() + temperature + "_" + id, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Aircon aircon = airconService.getById(id);
            if (aircon == null){
                return Result.error().setData("mes", "没有找到id为" + aircon + "的空调");
            }else {
                if (aircon.getState() == 1) {
                    if (error.equals("succ")) {
                        airconService.temper(id, temperature);
                        logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_SET_TEMP_.getCmdDesc() + "为" + temperature);
                        return Result.success().setData("res", res);
                    } else {
                        return Result.error().setData("res", res);
                    }
                } else {
                    return Result.error().setData("mes", "空调未开启");
                }
            }
        }else {
            return Result.error().setData("mes", "温度范围为16~30摄氏度");
        }
    }


    /**
     * 设置风速
     * @param id 空调id
     * @param speed 目标风速
     * @return Result
     */
    @GetMapping("/windSpeed")
    @ApiOperation("设置风速")
    public Result windSpeed(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "目标风速（0~4,其中0是自动风速,1,2,3,4是对应几档风）", required = true) @RequestParam("speed") String speed){
        if (Integer.parseInt(speed) >= 0 && Integer.parseInt(speed) <= 4){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_FUN_.getCmdValue() + speed + "_" + id, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Aircon aircon = airconService.getById(id);
            if (aircon.getState() == 1) {
                if (error.equals("succ")) {
                    airconService.windSpeed(id, speed);
                    logService.saveCmdLog(airconService.getById(id), CmdEnum.AIR_SWITCH_ON.getCmdDesc() + "为" + speed);
                    return Result.success().setData("res", res);
                } else {
                    return Result.error().setData("res", res);
                }
            }
            else {
                return Result.error().setData("mes", "空调未开启");
            }
        }else {
            return Result.error().setData("mes", "风速范围为0~4");
        }
    }

    /**
     * 修改空调基本信息
     * @param id 空调标识
     * @param power 修改的功率
     * @return Result
     */
    @PutMapping("modify")
    @ApiOperation("修改空调基本信息[所说的基本信息是那种不是必要的信息，这里表现的就是功率，其他信息不允许修改仅仅允许删除之后增加]")
    public Result modify(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "额定功率单位为w") @RequestParam(value = "power", required = false) String power){
        if (power != null) {
            if (airconService.getById(id) != null) {
                Aircon aircon = new Aircon();
                aircon.setId(id).setPower(power);
                airconService.updateById(aircon);
                return Result.success().setData("mes", "修改成功");
            }else {
                return Result.error().setData("mes", "没有找到标识为" + id + "的空调");
            }
        }
        else {
            return Result.error().setData("mes", "没有任何修改");
        }
    }
}
