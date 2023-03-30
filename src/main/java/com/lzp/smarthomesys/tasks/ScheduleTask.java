package com.lzp.smarthomesys.tasks;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzp.smarthomesys.controller.SceneController;
import com.lzp.smarthomesys.entity.ScenePlan;
import com.lzp.smarthomesys.service.impl.ScenePlanServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Component
@Slf4j
public class ScheduleTask {

    @Resource
    private ScenePlanServiceImpl scenePlanService;

    @Resource
    private SceneController sceneController;

    private List<ScenePlan> scenePlans = new ArrayList<>();

    private String nowTimeOut;


    @PostConstruct
    private void init(){
        // 初始化全局变量
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long miles = System.currentTimeMillis();
        nowTimeOut = sdf.format(miles);
        updateScenePlans();
    }

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
                    sceneController.on(scenePlan.getSceneId());
                    log.info("周" + week + "场景" + scenePlan.getId() + "开启, 设定的开启时间为" + startTime + ", 当前时间为" + nowTime);
                    if (daysStr.contains("0") && endLong == Long.MIN_VALUE){
                        scenePlan.setState(0);
                        scenePlanService.updateById(scenePlan);
                        log.info("单次场景计划" + scenePlan.getId() + "状态设置为关闭, 设定的开启时间为" + startTime + ", 当前时间为" + nowTime);
                    }
                    updateScenePlans();
                }
                if (Math.abs(todayMiles - endLong) <= 500){
                    sceneController.off(scenePlan.getSceneId());
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
}
