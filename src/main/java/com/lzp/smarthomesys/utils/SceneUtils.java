package com.lzp.smarthomesys.utils;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.entity.*;
import com.lzp.smarthomesys.enums.CmdEnum;
import com.lzp.smarthomesys.service.IDeviceService;
import com.lzp.smarthomesys.service.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

@Component
public class SceneUtils {
    
    @Value("${onenet.device_id}")
    private String deviceId;
    
    @Value("${onenet.timeout}")
    private String timeout;
    
    @Resource
    private LogServiceImpl logService;

    @Resource
    private AirconServiceImpl airconService;

    @Resource
    private LightServiceImpl lightService;

    @Resource
    private LockServiceImpl lockService;

    @Resource
    private OtherServiceImpl otherService;
    
    private static SceneUtils sceneUtils;

    @PostConstruct
    private void init(){
        sceneUtils = this;
        sceneUtils.deviceId = this.deviceId;
        sceneUtils.timeout = this.timeout;
        sceneUtils.logService = this.logService;
    }

    /**
     * 开启或者关闭场景
     * @param deviceList 设备列表
     * @param appliances 场景电器
     * @param strategy 开或者关
     * @return Map<Object, Object>
     */
    public static Map<Object, Object> onOrOff(List<IDevice> deviceList, String appliances, String strategy){
        String deviceId = sceneUtils.deviceId;
        String timeout = sceneUtils.timeout;
        LogServiceImpl logService = sceneUtils.logService;

        List<String> beOn = new ArrayList<>();
        List<String> beOff = new ArrayList<>();
        List<String> except = new ArrayList<>();
        
        IDeviceService deviceService = null;
        String type = "未知";
        String cmd1 = "";
        String des1 = "";
        String cmd2 = "";
        String des2 = "";
        for (IDevice device:deviceList) {
            if (device.getClass() == Aircon.class) {
                deviceService = sceneUtils.airconService;
                type = "空调";
                if (Objects.equals(strategy, "开启")) {
                    cmd1 = CmdEnum.AIR_SWITCH_ON.getCmdValue();
                    des1 = CmdEnum.AIR_SWITCH_ON.getCmdDesc();
                    cmd2 = CmdEnum.AIR_SWITCH_OFF.getCmdValue();
                    des2 = CmdEnum.AIR_SWITCH_OFF.getCmdDesc();
                } else {
                    cmd1 = CmdEnum.AIR_SWITCH_OFF.getCmdValue();
                    des1 = CmdEnum.AIR_SWITCH_OFF.getCmdDesc();
                    cmd2 = CmdEnum.AIR_SWITCH_ON.getCmdValue();
                    des2 = CmdEnum.AIR_SWITCH_ON.getCmdDesc();
                }
            } else if (device.getClass() == Light.class) {
                deviceService = sceneUtils.lightService;
                type = "灯泡";
                if (Objects.equals(strategy, "开启")) {
                    cmd1 = CmdEnum.LIGHT_SWITCH_ON.getCmdValue();
                    des1 = CmdEnum.LIGHT_SWITCH_ON.getCmdDesc();
                    cmd2 = CmdEnum.LIGHT_SWITCH_OFF.getCmdValue();
                    des2 = CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc();
                } else {
                    cmd1 = CmdEnum.LIGHT_SWITCH_OFF.getCmdValue();
                    des1 = CmdEnum.LIGHT_SWITCH_OFF.getCmdDesc();
                    cmd2 = CmdEnum.LIGHT_SWITCH_ON.getCmdValue();
                    des2 = CmdEnum.LIGHT_SWITCH_ON.getCmdDesc();
                }
            } else if (device.getClass() == Lock.class) {
                deviceService = sceneUtils.lockService;
                type = "门锁";
                if (Objects.equals(strategy, "开启")) {
                    cmd1 = CmdEnum.LOCK_SWITCH_ON.getCmdValue();
                    des1 = CmdEnum.LOCK_SWITCH_ON.getCmdDesc();
                    cmd2 = CmdEnum.LOCK_SWITCH_OFF.getCmdValue();
                    des2 = CmdEnum.LOCK_SWITCH_OFF.getCmdDesc();
                } else {
                    cmd1 = CmdEnum.LOCK_SWITCH_OFF.getCmdValue();
                    des1 = CmdEnum.LOCK_SWITCH_OFF.getCmdDesc();
                    cmd2 = CmdEnum.LOCK_SWITCH_ON.getCmdValue();
                    des2 = CmdEnum.LOCK_SWITCH_ON.getCmdDesc();
                }
            } else if (device.getClass() == Other.class) {
                deviceService = sceneUtils.otherService;
                type = "其他";
                if (Objects.equals(strategy, "开启")) {
                    cmd1 = CmdEnum.OTHER_SWITCH_ON.getCmdValue();
                    des1 = CmdEnum.OTHER_SWITCH_ON.getCmdDesc();
                    cmd2 = CmdEnum.OTHER_SWITCH_OFF.getCmdValue();
                    des2 = CmdEnum.OTHER_SWITCH_OFF.getCmdDesc();
                } else {
                    cmd1 = CmdEnum.OTHER_SWITCH_OFF.getCmdValue();
                    des1 = CmdEnum.OTHER_SWITCH_OFF.getCmdDesc();
                    cmd2 = CmdEnum.OTHER_SWITCH_ON.getCmdValue();
                    des2 = CmdEnum.OTHER_SWITCH_ON.getCmdDesc();
                }
            }
            if (appliances == null) appliances = "";
            String temp = type + "," + device.getId();
            if (strategy == "开启") {
                if (appliances.contains(temp)) {
                    if (device.getState() == 0) {
                        String res = DeviceUtils.sendCmd(deviceId, cmd1 + "_" + device.getId(), timeout);
                        JSONObject resJson = JSONObject.parseObject(res);
                        Object error = resJson.get("error").toString();
                        if (error.equals("succ")) {
                            deviceService.on(device.getId());
                            logService.saveCmdLog(deviceService.myGetById(device.getId()), des1);
                            beOn.add(temp);
                        } else {
                            except.add("开启异常: " + temp);
                        }
                    }
                } else {
                    if (device.getState() == 1) {
                        String res = DeviceUtils.sendCmd(deviceId, cmd2 + "_" + device.getId(), timeout);
                        JSONObject resJson = JSONObject.parseObject(res);
                        Object error = resJson.get("error").toString();
                        if (error.equals("succ")) {
                            deviceService.off(device.getId());
                            logService.saveCmdLog(deviceService.myGetById(device.getId()), des2);
                            beOff.add(temp);
                        } else {
                            except.add("关闭异常: " + temp);
                        }
                    }
                }
            }else{
                if (appliances.contains(temp)){
                    if (device.getState() == 1){
                        String res = DeviceUtils.sendCmd(deviceId, cmd1 + "_" + device.getId(), timeout);
                        JSONObject resJson = JSONObject.parseObject(res);
                        Object error = resJson.get("error").toString();
                        if (error.equals("succ")) {
                            deviceService.off(device.getId());
                            logService.saveCmdLog(deviceService.myGetById(device.getId()), des1);
                            beOff.add(temp);
                        }else{
                            except.add("关闭异常: " + temp);
                        }
                    }
                }
            }
        }
        Map<Object, Object> res = new HashMap<>();
        res.put("开启了", beOn);
        res.put("关闭了", beOff);
        res.put("操作异常", except);
        return res;
    }
}
