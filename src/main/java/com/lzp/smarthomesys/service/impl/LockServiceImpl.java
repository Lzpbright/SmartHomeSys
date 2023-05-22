package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.mapper.LockMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.ILockService;
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
public class LockServiceImpl extends ServiceImpl<LockMapper, Lock> implements ILockService {

    @Resource
    private LockMapper lockMapper;

    @Resource
    private RoomMapper roomMapper;


    /**
     * 通过标识开启门锁
     * @param id 标识
     */
    @Override
    public void on(String id) {
        Lock lock = lockMapper.selectById(id);
        // aircon 对象
        lock.setState(1);
        lockMapper.updateById(lock);
    }

    /**
     * 通过标识关闭门锁
     * @param id 标识
     */
    @Override
    public void off(String id) {
        Lock lock = lockMapper.selectById(id);
        // aircon 对象
        lock.setState(0);
        lockMapper.updateById(lock);
    }

    @Override
    public void setPwd(String id, String pwd) {
        Lock lock = lockMapper.selectById(id);
        // aircon 对象
        lock.setPassword(pwd);
        lockMapper.updateById(lock);
    }


    /**
     * 实现接口,通过id获取对象
     * @param id 标识
     * @return IDevice
     */
    @Override
    public IDevice myGetById(String id) {
        return lockMapper.selectById(id);
    }
}
