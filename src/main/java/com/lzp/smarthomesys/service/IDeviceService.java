package com.lzp.smarthomesys.service;

import com.lzp.smarthomesys.entity.IDevice;
import org.springframework.stereotype.Component;


@Component
public interface IDeviceService{
    void on(String id);

    void off(String id);

    IDevice myGetById(String id);
}
