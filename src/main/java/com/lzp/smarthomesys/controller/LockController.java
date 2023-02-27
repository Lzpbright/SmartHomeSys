package com.lzp.smarthomesys.controller;

import com.lzp.smarthomesys.service.impl.LightServiceImpl;
import com.lzp.smarthomesys.service.impl.LockServiceImpl;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.service.impl.RoomServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lock")
public class LockController {

    @Resource
    LockServiceImpl lockService;

    @Value("${onenet.device_id}")
    String deviceId;

    @Value("${onenet.timeout}")
    String timeout;

    @Resource
    LogServiceImpl logService;

    @Resource
    RoomServiceImpl roomService;
}
