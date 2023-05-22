package com.lzp.smarthomesys.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DeviceUtils {

    /**
     * use：给设备下发命令
     * @param deviceId 设备id
     * @param cmd 命令
     * @param timeout 等待设备在线时间，单位为秒
     * @return 返回值
     */
    public static String sendCmd(String deviceId, String cmd, String timeout){
        String url = "http://api.heclouds.com/cmds?device_id=" + deviceId + "&timeout=" + timeout;

        Map<String, String> headers = new HashMap<>();
        try {
            headers.put("Authorization", TokenUtils.getOneNetToken());
        }catch (Exception e){
            log.info("没有获取到Authorization", e);
        }
        Map<String, String> params = new HashMap<>();
        log.info("发送命令：" + cmd);
        params.put("data", cmd);
        return HttpUtils.sendPost(url, headers, params);
    }

    /**
     * 添加设备
     * @param deviceName 设备名称
     * @param deviceDesc 设备描述
     * @return 返回值
     */
    public static String addDevice(String deviceName, String deviceDesc){
        String url = "http://api.heclouds.com/devices";
        Map<String, String> headers = new HashMap<>();
        try {
            headers.put("Authorization", TokenUtils.getOneNetToken());
        }catch (Exception e){
            log.info("没有获取到Authorization", e);
        }
        Map<String, String> params = new HashMap<>();
        params.put("title", deviceName);
        params.put("desc", deviceDesc);
        return HttpUtils.sendPostJson(url, headers, params);
    }
}
