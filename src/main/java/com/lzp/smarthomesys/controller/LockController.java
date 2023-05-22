package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.Lock;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.entity.User;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.impl.LockServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import com.lzp.smarthomesys.service.impl.UserServiceImpl;
import com.lzp.smarthomesys.utils.Base64Utils;
import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.EMailUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
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
@Api("门锁控制器")
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

    @Resource
    UserController userController;

    @Resource
    UserServiceImpl userService;


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
        Room room;
        if (rooms.size() == 0){
            return Result.error().setData("mes", "用户没有客厅?");
        }else {
            room = rooms.get(0);
        }
        List<Lock> locks = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, room.getId()));
        return Result.success().setData("lock", locks);
    }

    /**
     * 判断是否由四位字符串数字组成
     * @param str 字符串
     * @return Boolean
     */
    public static boolean isNumber(String str){
        if (str.length() != 4) return false;
        for (int i = str.length(); --i >= 0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    @PostMapping("/setPwdByUserId")
    @ApiOperation("通过用户标识设置门锁密码，验证码通过UserController接口获取")
    public Result setPwdByUserId(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId,
                                 @ApiParam(value = "密码(必须四位数字)", required = true) @RequestParam("password") String password,
                                 @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        if (!isNumber(password)) {
            return Result.error().setData("mes", "密码的长度必须为4位数字组成的字符串");
        }
        User user = userService.getById(userId);
        if(user == null) return Result.error().setData("mes", "标识为" + userId + "的用户不存在");
        if (authCode.equals(userController.authCodes.get(user.getEmail()))) {
            userController.authCodes.remove(user.getEmail());
            List<Room> rooms = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, userId).eq(Room::getPosition, "客厅"));
            Room room;
            if (rooms.size() == 0) {
                return Result.error().setData("mes", "用户没有客厅?");
            } else {
                room = rooms.get(0);
            }
            List<Lock> locks = lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, room.getId()));
            String res = DeviceUtils.sendCmd(deviceId, CmdEnum.LOCK_PSW_RPL_.getCmdValue() + password + "_" + locks.get(0).getId(), timeout);
            JSONObject resJson = JSONObject.parseObject(res);
            Object error = resJson.get("error").toString();
            if (error.equals("succ")) {
                lockService.setPwd(locks.get(0).getId(), Base64Utils.encodeText(password));
                return Result.success().setData("mes", "设置成功");
            } else {
                return Result.error().setData("res", res);
            }
        }
        else {
            return Result.error().setData("mes", "验证码错误或不存在，请确认是否发送了验证码");
        }
    }

    @GetMapping("/exceptionTest")
    @ApiOperation("全局异常测试（测试用）")
    public Result exceptionTest(){
        int a = 1;
        int b= 0;
        System.out.println(a / b);
        return Result.success().setData("mes", "没有抛出异常");
    }
}
