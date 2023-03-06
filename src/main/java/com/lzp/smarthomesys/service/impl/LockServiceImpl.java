package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.mapper.LockMapper;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.IDeviceService;
import com.lzp.smarthomesys.service.ILockService;
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
public class LockServiceImpl extends ServiceImpl<LockMapper, Lock> implements ILockService {

    @Resource
    private LockMapper lockMapper;

    @Resource
    private RoomMapper roomMapper;


    @Override
    public void on(String id) {
        Lock lock = lockMapper.selectById(id);
        Room room = roomMapper.selectById(lock.getRoomId());
        // aircon 对象
        lock.setState(1);
        lockMapper.updateById(lock);
    }

    @Override
    public void off(String id) {
        Lock lock = lockMapper.selectById(id);
        Room room = roomMapper.selectById(lock.getRoomId());
        // aircon 对象
        lock.setState(0);
        lockMapper.updateById(lock);
    }

    @Override
    public IDevice myGetById(String id) {
        return lockMapper.selectById(id);
    }
}
