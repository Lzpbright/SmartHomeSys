package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.IDevice;
import com.lzp.smarthomesys.entity.Light;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.mapper.LightMapper;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.IDeviceService;
import com.lzp.smarthomesys.service.ILightService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Service
public class LightServiceImpl extends ServiceImpl<LightMapper, Light> implements ILightService {

    @Resource
    private LightMapper lightMapper;

    @Resource
    private RoomMapper roomMapper;


    @Override
    public void on(String id) {
        Light light = lightMapper.selectById(id);
        Room room = roomMapper.selectById(light.getRoomId());
        // light 对象
        light.setState(1);
        lightMapper.updateById(light);
    }

    @Override
    public void off(String id) {
        Light light = lightMapper.selectById(id);
        Room room = roomMapper.selectById(light.getRoomId());
        // light 对象
        light.setState(0);
        lightMapper.updateById(light);
    }

    @Override
    public IDevice myGetById(String id) {
        return lightMapper.selectById(id);
    }

    @Override
    public void intensity(String id, String value) {
        Light light = lightMapper.selectById(id);
        Room room = roomMapper.selectById(light.getRoomId());
        // light 对象
        light.setIntensity(Integer.parseInt(value));
        lightMapper.updateById(light);
    }
}
