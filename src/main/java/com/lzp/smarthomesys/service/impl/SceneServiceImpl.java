package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.mapper.*;
import com.lzp.smarthomesys.service.ISceneService;
import com.lzp.smarthomesys.utils.Result;
import com.lzp.smarthomesys.utils.SceneUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements ISceneService {

    @Resource
    private AirconMapper airconMapper;

    @Resource
    private LightMapper lightMapper;

    @Resource
    private LockMapper lockMapper;

    @Resource
    private OtherMapper otherMapper;

    @Resource
    private SceneMapper sceneMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Result on(String id) {
        Scene scene = sceneMapper.selectById(id);
        if (scene != null) {
            if (scene.getState() == 0) {
                // 获取userId,从而获得user所有的电器
                Room theRoom = roomMapper.selectById(scene.getRoomId());
                User user = userMapper.selectById(theRoom.getUserId());
                String userId = user.getId();

                // 一些所需中间变量
                String appliances = scene.getAppliance();

                // 遍历用户所有的电器并进行响应操作
                LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
                roomWrapper.eq(Room::getUserId, userId);
                List<Room> rooms = roomMapper.selectList(roomWrapper);

                Map<Object, Object> result = new HashMap<>();
                for (Room room : rooms) {
                    LambdaQueryWrapper<Aircon> airconWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Light> lightWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Lock> lockWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Other> otherWrapper = new LambdaQueryWrapper<>();
                    airconWrapper.eq(Aircon::getRoomId, room.getId());
                    lightWrapper.eq(Light::getRoomId, room.getId());
                    lockWrapper.eq(Lock::getRoomId, room.getId());
                    otherWrapper.eq(Other::getRoomId, room.getId());
                    List<Aircon> airconList = airconMapper.selectList(airconWrapper);
                    List<Light> lightList = lightMapper.selectList(lightWrapper);
                    List<Lock> lockList = lockMapper.selectList(lockWrapper);
                    List<Other> otherList = otherMapper.selectList(otherWrapper);

                    List<IDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(airconList);
                    deviceList.addAll(lightList);
                    deviceList.addAll(lockList);
                    deviceList.addAll(otherList);
                    result.put("房间" + room.getId(), SceneUtils.onOrOff(deviceList, appliances, "开启"));
                }
                result.put("场景电器", appliances);
                scene.setState(1);
                sceneMapper.updateById(scene);
                return Result.success().setData("result", result);
            } else {
                return Result.success().setData("mes", "标识为" + id + "的场景已开");
            }
        }else{
            return Result.error().setData("mes", "没有找到标识为" + id + "的场景");
        }
    }

    @Override
    public Result off(String id) {
        Scene scene = sceneMapper.selectById(id);
        if (scene != null) {
            if (scene.getState() == 1) {
                // 获取userId,从而获得user所有的电器
                Room theRoom = roomMapper.selectById(scene.getRoomId());
                User user = userMapper.selectById(theRoom.getUserId());
                String userId = user.getId();

                // 一些所需中间变量
                String appliances = scene.getAppliance();

                // 遍历用户所有的电器并进行响应操作
                LambdaQueryWrapper<Room> roomWrapper = new LambdaQueryWrapper<>();
                roomWrapper.eq(Room::getUserId, userId);
                List<Room> rooms = roomMapper.selectList(roomWrapper);

                Map<Object, Object> result = new HashMap<>();
                for (Room room : rooms) {
                    LambdaQueryWrapper<Aircon> airconWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Light> lightWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Lock> lockWrapper = new LambdaQueryWrapper<>();
                    LambdaQueryWrapper<Other> otherWrapper = new LambdaQueryWrapper<>();
                    airconWrapper.eq(Aircon::getRoomId, room.getId());
                    lightWrapper.eq(Light::getRoomId, room.getId());
                    lockWrapper.eq(Lock::getRoomId, room.getId());
                    otherWrapper.eq(Other::getRoomId, room.getId());
                    List<Aircon> airconList = airconMapper.selectList(airconWrapper);
                    List<Light> lightList = lightMapper.selectList(lightWrapper);
                    List<Lock> lockList = lockMapper.selectList(lockWrapper);
                    List<Other> otherList = otherMapper.selectList(otherWrapper);

                    List<IDevice> deviceList = new ArrayList<>();
                    deviceList.addAll(airconList);
                    deviceList.addAll(lightList);
                    deviceList.addAll(lockList);
                    deviceList.addAll(otherList);
                    result.put("房间" + room.getId(), SceneUtils.onOrOff(deviceList, appliances, "关闭"));
                }
                result.put("场景电器", appliances);
                scene.setState(0);
                sceneMapper.updateById(scene);
                return Result.success().setData("result", result);
            } else {
                return Result.success().setData("mes", "标识为" + id + "的场景已关");
            }
        }else{
            return Result.error().setData("mes", "没有找到标识为" + id + "的场景");
        }
    }
}
