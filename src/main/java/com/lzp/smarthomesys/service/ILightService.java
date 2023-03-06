package com.lzp.smarthomesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzp.smarthomesys.entity.Light;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
public interface ILightService extends IService<Light>, IDeviceService {

    void on(String id);

    void off(String id);

    void intensity(String id, String value);
}
