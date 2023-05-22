package com.lzp.smarthomesys.tasks;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.entity.ScenePlan;
import com.lzp.smarthomesys.service.impl.ScenePlanServiceImpl;
import com.lzp.smarthomesys.service.impl.SceneServiceImpl;
import com.lzp.smarthomesys.utils.EMailUtils;
import com.lzp.smarthomesys.utils.HttpUtils;
import com.lzp.smarthomesys.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ScheduleTask {

    @Resource
    private ScenePlanServiceImpl scenePlanService;

    @Resource
    private SceneServiceImpl sceneService;

    @Value("${onenet.device_id}")
    private String device_id;

    private List<ScenePlan> scenePlans = new ArrayList<>();

    private String nowTimeOut;

    private String email;

    private int times = 3;


    @PostConstruct
    private void init(){
        // 初始化全局变量
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long miles = System.currentTimeMillis();
        nowTimeOut = sdf.format(miles);
        updateScenePlans();
    }

    /**
     * 监测什么时候要执行莫任务
     */
    @Async("myThreadPool")
    @Scheduled(cron = "0/1 * * * * ?")
    public void check(){

        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long miles = System.currentTimeMillis();
        String nowTime = sdf.format(miles);
        nowTimeOut = nowTime;
        long todayMiles = Time.valueOf(nowTime).getTime();
//        log.info("----------------------------------------");
//        log.info(scenePlans.toString());
        // 获取星期
        LocalDate localDate = LocalDate.now();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int week = dayOfWeek.getValue();

        // 遍历计划列表
        for (ScenePlan scenePlan:scenePlans){
            // 获取场景计划的星期
            String daysStr = scenePlan.getWeekChoose();
            // 如果是单次的或者这个星期存在于这里面的一个星期则表示有计划任务
            if (daysStr.contains("0") || daysStr.contains(String.valueOf(week))){
                // 对今天有计划的就可进行是否到点判断
                // 获取开始和结束时间
                String startTime = scenePlan.getStartAt();
                String endTime = scenePlan.getEndAt();

                // 将时间转换为long
                long startLong = Long.MIN_VALUE;
                long endLong = Long.MIN_VALUE;
                if (startTime != null) startLong = Time.valueOf(startTime).getTime();
                if (endTime != null) endLong = Time.valueOf(endTime).getTime();

//                log.info("start: " + startLong + ", todayMiles: " + todayMiles + ", Math.abs(todayMiles - startLong): " + Math.abs(todayMiles - startLong));
//                log.info("end: " + endLong + ", todayMiles: " + todayMiles + ", Math.abs(todayMiles - endLong): " + Math.abs(todayMiles - endLong));

                // 如果目前时间已经和开始时间十分接近，就可以激发事件，防止事务执行导致跳过,这里选择500毫秒间隔就可以激发,,,实际上相减只会等于0,因为是以秒为单位
                if (Math.abs(todayMiles - startLong) <= 500){
                    sceneService.on(scenePlan.getSceneId());
                    log.info("周" + week + "场景" + scenePlan.getId() + "开启, 设定的开启时间为" + startTime + ", 当前时间为" + nowTime);
                    if (daysStr.contains("0") && endLong == Long.MIN_VALUE){
                        scenePlan.setState(0);
                        scenePlanService.updateById(scenePlan);
                        log.info("单次场景计划" + scenePlan.getId() + "状态设置为关闭, 设定的开启时间为" + startTime + ", 当前时间为" + nowTime);
                    }
                    updateScenePlans();
                }
                if (Math.abs(todayMiles - endLong) <= 500){
                    sceneService.off(scenePlan.getSceneId());
                    log.info("周" + week + "场景" + scenePlan.getId() + "关闭, 设定的关闭时间为" + endTime + ", 当前时间为" + nowTime);
                    if (daysStr.contains("0")) {
                        scenePlan.setState(0);
                        scenePlanService.updateById(scenePlan);
                        log.info("单次场景计划" + scenePlan.getId() + "状态设置为关闭, 设定的关闭时间为" + endTime + ", 当前时间为" + nowTime);
                    }
                    updateScenePlans();
                }
            }
        }
    }

    /**
     * 更新定时任务中的scenePlans
     */
    public void updateScenePlans(){
        scenePlans = scenePlanService.list(
                new LambdaQueryWrapper<ScenePlan>()
                .eq(ScenePlan::getState, 1)
                .and(i -> i.gt(ScenePlan::getStartAt, nowTimeOut)
                        .or()
                        .gt(ScenePlan::getEndAt, nowTimeOut)
                ));
    }

    /**
     * 监测是否触发了烟雾报警器，如果烟雾报警被触发，则发送邮件提醒用户
     */
    @Async("myThreadPool")
    @Scheduled(cron = "0/5 * * * * ?")
    public void fire(){
        String url = "http://api.heclouds.com/devices/" + device_id + "/datastreams" + "/smoke";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", TokenUtils.getOneNetToken());
        String res = HttpUtils.sendGet(url, headers, new HashMap<>());
        JSONObject resJson = JSONObject.parseObject(res);
        if (resJson.get("error").equals("succ")){
            JSONObject jsonObject = resJson.getJSONObject("data");
            String current_value = jsonObject.get("current_value").toString();
//            log.info(current_value);
            if (current_value.equals("1") || times != 3){
                if (email != null && times > 0){
                    EMailUtils.send("火灾报警！！！", email, "<div style=\"text-align: center\"><h1>智家提示您</h1><h2  style=\"color: red\">房间着火！！请立马查看监控进行确认</h2></div>", true);
                    times--;
                }
            }else {
                times = 3;
            }
        }else {
            log.info("获取数据流失败！！！");
        }
    }

    /**
     * 设置当前邮箱
     */
    public void setEmail(String email){
        this.email = email;
    }
}
