package com.lzp.smarthomesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.service.impl.*;
import com.lzp.smarthomesys.tasks.ScheduleTask;
import com.lzp.smarthomesys.utils.Base64Utils;
import com.lzp.smarthomesys.utils.EMailUtils;
import com.lzp.smarthomesys.utils.Result;
import com.lzp.smarthomesys.utils.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-24
 */
@RestController
@RequestMapping("/user")
@Api("用户控制器")
public class UserController {

    @Resource
    UserServiceImpl userService;

    @Resource
    RoomServiceImpl roomService;

    @Resource
    LockServiceImpl lockService;

    @Resource
    AirconServiceImpl airconService;

    @Resource
    LightServiceImpl lightService;

    @Resource
    OtherServiceImpl otherService;

    @Resource
    private ScheduleTask scheduleTask;


    public final Map<String, String> authCodes = new HashMap<>();

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @return Result
     */
    @ApiOperation("发送验证码")
    @GetMapping("/sendMail")
    public Result sendMail(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email){
        Random rand = new Random();
        int randomNum = rand.nextInt(100000); // 生成0-99999的随机整数
        String authCode = String.format("%05d", randomNum); // 转换为5位的字符串
        try {
            authCodes.put(email, authCode);
            EMailUtils.send("智家验证码", email, "<div style=\"text-align: center\"><h1>您的验证码为</h1><h2  style=\"color: blue\">" + authCode + "</h2></div>", true);
        }catch (Exception e){
             return Result.error().setData("mes", "邮箱或服务器错误");
        }
        return Result.success().setData("mes", "发送成功!");
    }

    /**
     * 用户注册
     * @param email 用户邮箱
     * @param password 用户密码
     * @param authCode 邮箱验证码
     * @return Result
     */
    @ApiOperation("注册")
    @GetMapping("/register")
    public Result register(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email,
                           @ApiParam(value = "密码", required = true) @RequestParam("password") String password,
                           @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (userService.getOne(wrapper) == null) {
            if (authCode.equals(authCodes.get(email))) {
                authCodes.remove(email);
                User user = new User();
                user.setEmail(email).setPassword(Base64Utils.encodeText(password));
                userService.save(user);

                // 默认房间
                String userId = userService.getOne(wrapper).getId();
                roomService.save(new Room().setUserId(userId).setPosition("客厅"));
                roomService.save(new Room().setUserId(userId).setPosition("厨房"));
                roomService.save(new Room().setUserId(userId).setPosition("卧室"));
                roomService.save(new Room().setUserId(userId).setPosition("卫生间"));

                // 默认客厅门锁
                Lock lock = new Lock();
                List<Room> keting = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, userId).eq(Room::getPosition, "客厅"));
                lock.setRoomId(keting.get(0).getId());
                lock.setBrand("鹏哥电器");
                lock.setSmallPos("大门门锁");
                lockService.save(lock);

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

    /**
     * 用户登录
     * @param email 用户邮箱
     * @param password 用户密码
     * @return Result
     */
    @ApiOperation("用户登录")
    @GetMapping("/login")
    public Result login(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email,
                        @ApiParam(value = "密码", required = true) @RequestParam("password") String password){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email).eq(User::getPassword, Base64Utils.encodeText(password));
        User user = userService.getOne(wrapper);
        if (user == null){
            return Result.error().setData("mes", "账号或密码错误");
        }
        else {
            scheduleTask.setEmail(email);
            return Result.success().setData("user", user);
        }
    }

    /**
     * 用户信息修改
     * @param id 用户标识
     * @param teleNumber 用户手机号
     * @param nickName 用户昵称
     * @param sex 用户性别
     * @param location 用户位置
     * @return Result
     */
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

        userService.updateById(user);
        return Result.success().setData("mes", "修改成功");
    }

    /**
     * 用户密码修改
     * @param id 用户标识
     * @param newPassword 新密码
     * @param authCode 授权码
     * @return Result
     */
    @ApiOperation("用户密码修改")
    @GetMapping("/modifyPassword")
    public Result modify(@ApiParam(value = "用户标识", required = true) @RequestParam("id") String id,
                         @ApiParam(value = "新密码", required = true) @RequestParam(value = "newPassword") String newPassword,
                         @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        User user = userService.getById(id);
        if (user != null) {
            String email = user.getEmail();
            if (authCode.equals(authCodes.get(email))) {
                authCodes.remove(email);
                user.setPassword(Base64Utils.encodeText(newPassword));
                userService.updateById(user);
                return Result.success().setData("mes", "修改成功");
            } else {
                return Result.error().setData("mes", "验证码不匹配");
            }
        }
        else{
            return Result.error().setData("mes", "查无此人");
        }
    }

    /**
     * 用户密码修改（通过邮箱）
     * @param email 邮箱
     * @param newPassword 新密码
     * @param authCode 授权码
     * @return Result
     */
    @ApiOperation("用户密码修改（通过邮箱）")
    @GetMapping("/modifyPasswordByEmail")
    public Result modifyPasswordByEmail(@ApiParam(value = "邮箱", required = true) @RequestParam("email") String email,
                         @ApiParam(value = "新密码", required = true) @RequestParam(value = "newPassword") String newPassword,
                         @ApiParam(value = "验证码", required = true) @RequestParam("authCode") String authCode){
        List<User> users = userService.list(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (users.size() >= 1) {
            if (authCode.equals(authCodes.get(email))) {
                authCodes.remove(email);
                User user = users.get(0);
                user.setPassword(Base64Utils.encodeText(newPassword));
                userService.updateById(user);
                return Result.success().setData("mes", "修改成功");
            } else {
                return Result.error().setData("mes", "验证码不匹配");
            }
        }
        else{
            return Result.error().setData("mes", "查无此人");
        }
    }

    /**
     * 通过用户标识获取用户信息
     * @param id 用户标识
     * @return Result
     */
    @ApiOperation("标识获取用户信息")
    @GetMapping("/getById")
    public Result modify(@ApiParam(value = "用户标识", required = true) @RequestParam("id") String id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.success().setData("user", user);
        }
        else {
            return Result.error().setData("mes", "没有查到该用户");
        }
    }

    /**
     * 上传用户头像
     * @param id 用户id
     * @param file 用户头像文件
     * @return Result
     * @throws IOException 异常
     */
    @PostMapping("/uploadIcon")
    @ApiOperation("上传头像[头像文件小于5M]")
    public Result uploads(@ApiParam(value = "用户标识", required = true) @RequestParam("id") String id,
                          @ApiParam(value = "头像[png, jpg, jpeg, bmp, svg, icon类型]", required = true) @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null) {
            List<String> fileTypes = Arrays.asList("png", "jpg", "jpeg", "bmp", "svg", "icon");
            String suffix = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf('.') + 1)
                    .toLowerCase();
            // 判断文件类型
            if (fileTypes.contains(suffix)) {
                // 判断文件大小
                double fileSize = file.getSize();
                if (fileSize / (1024 * 1024) > 5) {
                    return Result.error().setData("mes", "图像不能大于5M");
                } else {
                    String url = UploadUtils.uploads(file);
                    User user = new User();
                    user.setId(id).setUserIcon(url);
                    userService.updateById(user);
                    return Result.success().setData("url", url);
                }
            }else {
                return Result.error().setData("mes", "图像格式要为[png, jpg, jpeg, bmp, svg");
            }
        }
        else {
            return Result.error().setData("mes", "没有选择图片");
        }
    }

    @GetMapping("/getDevicesNum")
    @ApiOperation("获取用户电器的数目")
    public Result getDevicesNum(@ApiParam(value = "用户标识", required = true) @RequestParam("userId") String userId){
        int nums = 0;
        // 判断用户是否存在
        User user = userService.getById(userId);
        if (user != null){
            // 通过用户获取用户所有房间
            List<Room> rooms = roomService.list(new LambdaQueryWrapper<Room>().eq(Room::getUserId, userId));
            // 遍历所有房间,获取房间里面的电器
            for (Room room:rooms){
                // 空调
                nums += airconService.list(new LambdaQueryWrapper<Aircon>().eq(Aircon::getRoomId, room.getId())).size();
                nums += lightService.list(new LambdaQueryWrapper<Light>().eq(Light::getRoomId, room.getId())).size();
                nums += lockService.list(new LambdaQueryWrapper<Lock>().eq(Lock::getRoomId, room.getId())).size();
                nums += otherService.list(new LambdaQueryWrapper<Other>().eq(Other::getRoomId, room.getId())).size();
            }
            return Result.success().setData("devicesNum", nums);
        }else {
            return Result.error().setData("mes", "没有找到标识为" + userId + "用户");
        }
    }

    /**
     * 通过标识获取所有用户
     * @return Result
     */
    @GetMapping("/list")
    @ApiOperation("获取所有用户")
    public Result list(){
        return Result.success().setData("mes", userService.list());
    }
}
