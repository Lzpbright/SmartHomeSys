package com.lzp.smarthomesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.entity.User;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import com.lzp.smarthomesys.service.impl.UserServiceImpl;
import com.lzp.smarthomesys.utils.Base64Utils;
import com.lzp.smarthomesys.utils.EMailUtils;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-24
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@Api("用户控制器")
public class UserController {

    @Resource
    UserServiceImpl service;

    @Resource
    RoomServiceImpl roomService;


    private Map<String, String> authCodes = new HashMap<>();

    @ApiOperation("发送验证码")
    @GetMapping("/sendMail")
    public Result sendMail(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email){
        Random rand = new Random();
        int randomNum = rand.nextInt(100000); // 生成0-99999的随机整数
        String authCode = String.format("%05d", randomNum); // 转换为5位的字符串
        try {
            authCodes.put(email, authCode);
            EMailUtils.send("注册验证码", email, authCode, true);
        }catch (Exception e){
             return Result.error().setData("mes", "邮箱或服务器错误");
        }
        return Result.success().setData("mes", "发送成功!");
    }

    @ApiOperation("注册")
    @GetMapping("/register")
    public Result register(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email,
                           @ApiParam(value = "密码", required = true) @RequestParam("password") String password,
                           @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (service.getOne(wrapper) == null) {
            if (authCode.equals(authCodes.get(email))) {
                authCodes.remove(email);
                User user = new User();
                user.setEmail(email).setPassword(Base64Utils.encode(password));
                service.save(user);
                // 默认房间
                String userId = service.getOne(wrapper).getId();
                roomService.save(new Room().setUserId(userId).setPosition("客厅"));                roomService.save(new Room().setUserId(userId).setPosition("客厅"));
                roomService.save(new Room().setUserId(userId).setPosition("卧室"));
                roomService.save(new Room().setUserId(userId).setPosition("厨房"));
                roomService.save(new Room().setUserId(userId).setPosition("卫生间"));
                roomService.save(new Room().setUserId(userId).setPosition("阳台"));
                return Result.success().setData("mes", "注册成功!");
            } else {
                return Result.error().setData("mes", "验证码不匹配");
            }
        }
        else {
            authCodes.remove(email);
            return Result.error().setData("mes", "邮箱已注册!无法重复注册");
        }
    }

    @ApiOperation("用户登录")
    @GetMapping("/login")
    public Result login(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email,
                        @ApiParam(value = "密码", required = true) @RequestParam("password") String password){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email).eq(User::getPassword, Base64Utils.encode(password));
        User user = service.getOne(wrapper);
        if (user == null){
            return Result.error().setData("mes", "账号或密码错误");
        }
        else {
            return Result.success().setData("user", user);
        }
    }

    @ApiOperation("用户信息修改")
    @GetMapping("/modify")
    public Result modify(@ApiParam(value = "用户标识") @RequestParam("id") String id,
                         @ApiParam(value = "电话号码") @RequestParam(value = "teleNumber", required = false) String teleNumber,
                         @ApiParam(value = "昵称") @RequestParam(value = "nickName", required = false) String nickName,
                         @ApiParam(value = "性别") @RequestParam(value = "sex", required = false) String sex,
                         @ApiParam(value = "位置") @RequestParam(value = "location", required = false) String location){
        User user = new User();
        user.setId(id);
        user.setTeleNumber(Objects.equals(teleNumber, "") ? "暂未设置" : teleNumber);
        user.setNickname(Objects.equals(nickName, "") ? "暂未设置" : nickName);
        user.setSex(Objects.equals(sex, "") ? "暂未设置" : sex);
        user.setLocation(Objects.equals(location, "") ? "暂未设置" : location);

        service.updateById(user);
        return Result.success().setData("mes", "修改成功");
    }

    @ApiOperation("用户密码修改")
    @GetMapping("/modifyPassword")
    public Result modify(@ApiParam(value = "用户标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "新密码", required = true) @RequestParam(value = "newPassword") String newPassword,
                         @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        User u = service.getById(id);
        String email = u.getEmail();
        if (Objects.equals(authCodes.get(email), authCode)){
            authCodes.remove(email);
            User user = new User();
            user.setId(id);
            user.setPassword(Base64Utils.encode(newPassword));
            service.updateById(user);
            return Result.success().setData("mes", "修改成功");
        }else {
            return Result.error().setData("mes", "验证码不匹配");
        }
    }

    @ApiOperation("标识获取用户信息")
    @GetMapping("/getById")
    public Result modify(@ApiParam(value = "用户标识", required = true) @RequestParam("id") String id) {
        User user = service.getById(id);
        if (user != null) {
            return Result.success().setData("user", user);
        }
        else {
            return Result.error().setData("mes", "没有查到该用户");
        }
    }
}
