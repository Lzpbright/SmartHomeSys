package com.lzp.smarthomesys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzp.smarthomesys.entity.IDevice;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public interface IDeviceService{
    void on(String id);

    void off(String id);

    IDevice myGetById(String id);
}
