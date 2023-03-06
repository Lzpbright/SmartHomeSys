package com.lzp.smarthomesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzp.smarthomesys.entity.Aircon;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
public interface IAirconService extends IService<Aircon>, IDeviceService {

    void on(String id);

    void off(String id);

    void modeAuto(String id);

    void modeCool(String id);

    void modeHot(String id);

    void modeDry(String id);

    void modeEcono(String id);

    void temper(String id, String temperature);

    void windSpeed(String id, String speed);
}
