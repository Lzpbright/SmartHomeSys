package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.IDevice;
import com.lzp.smarthomesys.entity.Light;
import com.lzp.smarthomesys.mapper.LightMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.ILightService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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


    /**
     * 通过标识开启灯泡
     * @param id 标识
     */
    @Override
    public void on(String id) {
        Light light = lightMapper.selectById(id);
        // light 对象
        light.setState(1);
        lightMapper.updateById(light);
    }

    /**
     * 通过标识关闭灯泡
     * @param id 标识
     */
    @Override
    public void off(String id) {
        Light light = lightMapper.selectById(id);
        // light 对象
        light.setState(0);
        lightMapper.updateById(light);
    }

    /**
     * 仅仅就是实现接口,通过标识获取对象
     * @param id 标识
     * @return IDevice
     */
    @Override
    public IDevice myGetById(String id) {
        return lightMapper.selectById(id);
    }

    /**
     * 通过标识设置灯泡亮度
     * @param id 标识
     * @param value 亮度
     */
    @Override
    public void intensity(String id, String value) {
        Light light = lightMapper.selectById(id);
        // light 对象
        light.setIntensity(Integer.parseInt(value));
        lightMapper.updateById(light);
    }

    /**
     * 通过标识设置灯泡颜色
     * @param id 灯泡标识
     * @param rgb 一个val对应一个颜色
     */
    public void color(String id, String rgb){
        Light light = lightMapper.selectById(id);
        light.setColor(rgb);
        lightMapper.updateById(light);
    }
}
