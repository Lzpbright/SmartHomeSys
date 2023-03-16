package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.ILogService;
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
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {

    @Resource
    private LogMapper logMapper;

    @Resource
    private RoomMapper roomMapper;

    /**
     * 对设备发送的命令加入日志
     * @param device 设备
     * @param action 动作
     */
    @Override
    public void saveCmdLog(IDevice device, String action) {
        Room room = roomMapper.selectById(device.getRoomId());
        String type = "未知";
        if (device.getClass() == Aircon.class){
            type = "空调";
        } else if (device.getClass() == Light.class) {
            type = "灯泡";
        } else if (device.getClass() == Lock.class) {
            type = "门锁";
        } else if (device.getClass() == Other.class) {
            type = "其他";
        }
        // log 对象
        String time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis());
        String userId = room.getUserId();
        String target = "房间: " + room.getPosition() + "|具体位置: "
                + device.getSmallPos() + "|电器: " + type + "|电器标识: " + device.getId();
        Log cmdLog = new Log();
        cmdLog.setTime(time).setUserId(userId).setTarget(target).setAction(action);
        logMapper.insert(cmdLog);
    }
}
