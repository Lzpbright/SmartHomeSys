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
 *  服务实现类
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

    @Override
    public void on(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setState(1);
        airconMapper.updateById(aircon);
    }

    @Override
    public void off(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setState(0);
        airconMapper.updateById(aircon);
    }

    @Override
    public IDevice myGetById(String id) {
        return airconMapper.selectById(id);
    }

    @Override
    public void modeAuto(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("自动");
        airconMapper.updateById(aircon);
    }

    @Override
    public void modeCool(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("制冷");
        airconMapper.updateById(aircon);
    }

    @Override
    public void modeHot(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("制热");
        airconMapper.updateById(aircon);
    }

    @Override
    public void modeDry(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("除湿");
        airconMapper.updateById(aircon);
    }

    @Override
    public void modeEcono(String id) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setMode("节能");
        airconMapper.updateById(aircon);
    }

    @Override
    public void temper(String id, String temperature) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setTemper(Integer.parseInt(temperature));
        airconMapper.updateById(aircon);
    }

    @Override
    public void windSpeed(String id, String speed) {
        Aircon aircon = airconMapper.selectById(id);
        Room room = roomMapper.selectById(aircon.getRoomId());
        // aircon 对象
        aircon.setWindSpeed(speed);
        airconMapper.updateById(aircon);
    }
}
