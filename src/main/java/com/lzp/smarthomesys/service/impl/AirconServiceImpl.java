package com.lzp.smarthomesys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzp.smarthomesys.entity.Aircon;
import com.lzp.smarthomesys.entity.IDevice;
import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.entity.Room;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.mapper.AirconMapper;
import com.lzp.smarthomesys.mapper.LogMapper;
import com.lzp.smarthomesys.mapper.RoomMapper;
import com.lzp.smarthomesys.service.IAirconService;
import com.lzp.smarthomesys.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

/**
 * <p>
 *  服务实现类,这里主要就是对于数据库的操作,并不是涉及到发送命令
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Service
public class AirconServiceImpl extends ServiceImpl<AirconMapper, Aircon> implements IAirconService {

    @Resource
    private AirconMapper airconMapper;

    @Resource
    private RoomMapper roomMapper;

    /**
     * 通过标识开启空调
     * @param id 空调标识
     */
    @Override
    public void on(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setState(1);
        airconMapper.updateById(aircon);
    }

    /**
     * 通过标识关闭空调
     * @param id 空调标识
     */
    @Override
    public void off(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setState(0);
        airconMapper.updateById(aircon);
    }

    /**
     * 实现接口,实际上也就是通过设备id获取设备对象
     * @param id 设备标识
     * @return Result
     */
    @Override
    public IDevice myGetById(String id) {
        return airconMapper.selectById(id);
    }

    /**
     * 通过设备标识设置自动模式
     * @param id 设备标识
     */
    @Override
    public void modeAuto(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("自动");
        airconMapper.updateById(aircon);
    }

    /**
     * 通过标识设置制冷模式
     * @param id 标识
     */
    @Override
    public void modeCool(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("制冷");
        airconMapper.updateById(aircon);
    }

    /**
     * 通过标识设置制热模式
     * @param id 标识
     */
    @Override
    public void modeHot(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("制热");
        airconMapper.updateById(aircon);
    }

    /**
     * 通过标识开启空调干燥模式,这里干燥模式也就是通风模式
     * @param id 标识
     */
    @Override
    public void modeDry(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("通风");
        airconMapper.updateById(aircon);
    }

    /**
     * 通过空调标识开启节能模式
     * @param id 空调标识
     */
    @Override
    public void modeEcono(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("节能");
        airconMapper.updateById(aircon);
    }

    /**
     * 通过id设置空调的温度
     * @param id 空调标识
     * @param temperature 空调温度
     */
    @Override
    public void temper(String id, String temperature) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setTemper(Integer.parseInt(temperature));
        airconMapper.updateById(aircon);
    }

    /**
     * 通过id设置风速
     * @param id 空调标识
     * @param speed 空调风速
     */
    @Override
    public void windSpeed(String id, String speed) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setWindSpeed(speed);
        airconMapper.updateById(aircon);
    }
}
