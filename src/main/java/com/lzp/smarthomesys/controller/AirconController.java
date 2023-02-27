package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.Aircon;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.AirconServiceImpl;
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
 *  空调控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aircon")
public class AirconController {

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    AirconServiceImpl service;

    @Resource
    LogServiceImpl logService;

    @Resource
    RoomServiceImpl roomService;

    @GetMapping("/on")
    @ApiOperation("开启本空调")
    public Result on(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_ON.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Aircon aircon = service.getById(id);
            Room room = roomService.getById(aircon.getRoomId());
            // aircon 对象
            aircon.setState(1);
            service.updateById(aircon);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
            String action = CmdEnum.AIR_SWITCH_ON.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }

    @GetMapping("/off")
    @ApiOperation("关闭本空调")
    public Result off(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SWITCH_OFF.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        if (error.equals("succ")) {
            Aircon aircon = service.getById(id);
            Room room = roomService.getById(aircon.getRoomId());
            // aircon 对象
            aircon.setState(0);
            service.updateById(aircon);
            // log 对象
            String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
            String userId = room.getUserId();
            String target = "房间: " + room.getPosition() + "|具体位置: "
                    + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
            String action = CmdEnum.AIR_SWITCH_OFF.getCmdDesc();
            Log cmdLog = new Log();
            cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
            logService.save(cmdLog);
            return Result.success().setData("res", res);
        }else {
            return Result.error().setData("res", res);
        }
    }


    @GetMapping("/modeAuto")
    @ApiOperation("自动模式")
    public Result modeAuto(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_AUTO.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        Aircon aircon = service.getById(id);
        if (aircon.getState() == 1) {
            if (error.equals("succ")) {
                Room room = roomService.getById(aircon.getRoomId());
                // aircon 对象
                aircon.setMode("自动");
                service.updateById(aircon);
                // log 对象
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                String userId = room.getUserId();
                String target = "房间: " + room.getPosition() + "|具体位置: "
                        + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                String action = CmdEnum.AIR_MODE_AUTO.getCmdDesc();
                Log cmdLog = new Log();
                cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                logService.save(cmdLog);
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    @GetMapping("/modeCool")
    @ApiOperation("制冷模式")
    public Result modeCool(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_COOL.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        Aircon aircon = service.getById(id);
        if (aircon.getState() == 1) {
            if (error.equals("succ")) {
                Room room = roomService.getById(aircon.getRoomId());
                // aircon 对象
                aircon.setMode("制冷");
                service.updateById(aircon);
                // log 对象
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                String userId = room.getUserId();
                String target = "房间: " + room.getPosition() + "|具体位置: "
                        + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                String action = CmdEnum.AIR_MODE_COOL.getCmdDesc();
                Log cmdLog = new Log();
                cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                logService.save(cmdLog);
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    @GetMapping("/modeHot")
    @ApiOperation("制热模式")
    public Result modeHot(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_HOT.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        Aircon aircon = service.getById(id);
        if (aircon.getState() == 1) {
            if (error.equals("succ")) {
                Room room = roomService.getById(aircon.getRoomId());
                // aircon 对象
                aircon.setMode("制热");
                service.updateById(aircon);
                // log 对象
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                String userId = room.getUserId();
                String target = "房间: " + room.getPosition() + "|具体位置: "
                        + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                String action = CmdEnum.AIR_MODE_HOT.getCmdDesc();
                Log cmdLog = new Log();
                cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                logService.save(cmdLog);
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    @GetMapping("/modeDry")
    @ApiOperation("除湿模式")
    public Result modeDry(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id){
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_HOT.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        Aircon aircon = service.getById(id);
        if (aircon.getState() == 1) {
            if (error.equals("succ")) {
                Room room = roomService.getById(aircon.getRoomId());
                // aircon 对象
                aircon.setMode("除湿");
                service.updateById(aircon);
                // log 对象
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                String userId = room.getUserId();
                String target = "房间: " + room.getPosition() + "|具体位置: "
                        + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                String action = CmdEnum.AIR_MODE_DRY.getCmdDesc();
                Log cmdLog = new Log();
                cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                logService.save(cmdLog);
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "空调未开启");
        }
    }

    @GetMapping("/modeEcono")
    @ApiOperation("节能模式")
    public Result modeEcono(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id) {
        String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_MODE_Econo.getCmdValue(), timeout);
        JSONObject resJson = JSONObject.parseObject(res);
        Object error = resJson.get("error").toString();
        Aircon aircon = service.getById(id);
        if (aircon.getState() == 1) {
            if (error.equals("succ")) {

                Room room = roomService.getById(aircon.getRoomId());
                // aircon 对象
                aircon.setMode("节能");
                service.updateById(aircon);
                // log 对象
                String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                String userId = room.getUserId();
                String target = "房间: " + room.getPosition() + "|具体位置: "
                        + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                String action = CmdEnum.AIR_MODE_Econo.getCmdDesc();
                Log cmdLog = new Log();
                cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                logService.save(cmdLog);
                return Result.success().setData("res", res);
            } else {
                return Result.error().setData("res", res);
            }
        }
        else{
            return Result.error().setData("mes", "空调未开启");
        }
    }

    @GetMapping("/temper")
    @ApiOperation("设置温度(硬件方尚有bug)")
    public Result temper(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "目标温度（16~30）", required = true) @RequestParam("temperature") String temperature){
        if (Integer.parseInt(temperature) >= 16 && Integer.parseInt(temperature) <= 30){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_TEMP_.getCmdValue() + temperature, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Aircon aircon = service.getById(id);
            if (aircon.getState() == 1) {
                if (error.equals("succ")) {
                    Room room = roomService.getById(aircon.getRoomId());
                    // aircon 对象
                    aircon.setTemper(Integer.parseInt(temperature));
                    service.updateById(aircon);
                    // log 对象
                    String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                    String userId = room.getUserId();
                    String target = "房间: " + room.getPosition() + "|具体位置: "
                            + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                    String action = CmdEnum.AIR_SET_TEMP_.getCmdDesc() + "为" + temperature;
                    Log cmdLog = new Log();
                    cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                    logService.save(cmdLog);
                    return Result.success().setData("res", res);
                } else {
                    return Result.error().setData("res", res);
                }
            }
            else {
                return Result.error().setData("mes", "空调未开启");
            }
        }else {
            return Result.error().setData("mes", "温度范围为16~30摄氏度");
        }
    }


    @GetMapping("/windSpeed")
    @ApiOperation("设置风速(硬件方尚有bug)")
    public Result windSpeed(@ApiParam(value = "空调标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "目标风速（0~4,其中0是自动风速,1,2,3,4是对应几档风）", required = true) @RequestParam("speed") String speed){
        if (Integer.parseInt(speed) >= 0 && Integer.parseInt(speed) <= 3){
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.AIR_SET_FUN_.getCmdValue() + speed, timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            Aircon aircon = service.getById(id);
            if (aircon.getState() == 1) {
                if (error.equals("succ")) {
                    Room room = roomService.getById(aircon.getRoomId());
                    // aircon 对象
                    aircon.setWindSpeed(speed);
                    service.updateById(aircon);
                    // log 对象
                    String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
                    String userId = room.getUserId();
                    String target = "房间: " + room.getPosition() + "|具体位置: "
                            + aircon.getSmallPos() + "|电器: 空调|电器标识: " + aircon.getId();
                    String action = CmdEnum.AIR_SET_FUN_.getCmdDesc() + "为" + speed;
                    Log cmdLog = new Log();
                    cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
                    logService.save(cmdLog);
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
}
