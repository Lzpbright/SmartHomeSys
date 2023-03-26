package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.IDevice;
import com.lzp.smarthomesys.entity.Other;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.mapper.OtherMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.IOtherService;
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
public class OtherServiceImpl extends ServiceImpl<OtherMapper, Other> implements IOtherService {

    @Resource
    private OtherMapper otherMapper;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private LogMapper logMapper;

    /**
     * 通过标识开启其他电器
     * @param id 标识
     */
    @Override
    public void on(String id) {
        Other other = otherMapper.selectById(id);
        Room room = roomMapper.selectById(other.getRoomId());
        // other 对象
        other.setState(1);
        otherMapper.updateById(other);
    }

    /**
     * 通过标识关闭其他电器
     * @param id 标识
     */
    @Override
    public void off(String id) {
        Other other = otherMapper.selectById(id);
        Room room = roomMapper.selectById(other.getRoomId());
        // light 对象
        other.setState(0);
        otherMapper.updateById(other);
    }

    /**
     * 实现接口,通过id获取对象
     * @param id 标识
     * @return IDevice
     */
    @Override
    public IDevice myGetById(String id) {
        return otherMapper.selectById(id);
    }
}
