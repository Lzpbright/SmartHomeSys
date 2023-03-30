package com.lzp.smarthomesys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lzp.smarthomesys.entity.Scene;
import com.lzp.smarthomesys.entity.ScenePlan;
import com.lzp.smarthomesys.service.impl.ScenePlanServiceImpl;
import com.lzp.smarthomesys.service.impl.SceneServiceImpl;
import com.lzp.smarthomesys.tasks.ScheduleTask;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-03-26
 */
@RestController
@RequestMapping("/sceneplan")
@Api("场景定时计划控制器")
public class ScenePlanController {

    @Resource
    SceneServiceImpl sceneService;

    @Resource
    ScheduleTask scheduleTask;

    @Resource
    ScenePlanServiceImpl scenePlanService;

    @ApiOperation("通过场景表示获取该场景所有的定时计划")
    @GetMapping("/listBySceneId")
    public Result listBySceneId(@ApiParam(value = "场景标识", required = true) @RequestParam("sceneId") String sceneId){
        Scene scene = sceneService.getById(sceneId);
        if (scene != null){
            List<ScenePlan> scenePlans = scenePlanService.list(new LambdaQueryWrapper<ScenePlan>().eq(ScenePlan::getSceneId, sceneId));
            return Result.success().setData("scenePlans", scenePlans);
        }else {
            return Result.error().setData("mes", "没有找到标识为" + sceneId + "的场景");
        }
    }

    @ApiOperation("开启还是关闭该定时计划")
    @PutMapping("/onOrOff")
    public Result onOrOff(@ApiParam(value = "定时计划标识", required = true) @RequestParam("scenePlanId") String scenePlanId,
                          @ApiParam(value = "选择0[关闭]或1[开启]", required = true) @RequestParam("op") Integer op){
        ScenePlan scenePlan = scenePlanService.getById(scenePlanId);
        if (scenePlan != null){
            if (Objects.equals(scenePlan.getState(), op)){
                return Result.success().setData("mes", "没有进行任何操作");
            }else {
                scenePlan.setState(op);
                scenePlanService.updateById(scenePlan);
                scheduleTask.updateScenePlans();
                return Result.success().setData("mes", "操作成功");
            }
        }else {
            return Result.error().setData("mes", "没有找到标识为" + scenePlanId + "的场景计划");
        }
    }

    @ApiOperation("添加定时计划，添加之后会自动开启该计划, 启动时间和停止时间不能同时为空，如果单独只设置一个不为空，则在对应时间执行对应计划")
    @GetMapping("/add")
    public Result add(@ApiParam(value = "场景标识", required = true) @RequestParam("sceneId") String sceneId,
                         @ApiParam(value = "启动时间,格式[HH:mm]") @RequestParam(value = "startAt", required = false) String startAt,
                         @ApiParam(value = "停止时间,格式[HH:mm]") @RequestParam(value = "endAt", required = false) String endAt,
                         @ApiParam(value = "星期选择(0~7, 其中0表示不重复, 只是执行一次, 传入格式例如[1,2,3,]表示周1,2,3启用, [0,]表示不重复)", required = true) @RequestParam("weekChose") String weekChose){
        if (startAt == null && endAt == null) return Result.error().setData("mes", "启动时间和借书时间不能同时为空");
        if (sceneId == null || weekChose == null) return Result.error().setData("mes", "场景标识或星期选择有未填项");
        Scene scene = sceneService.getById(sceneId);
        if (scene != null){
            ScenePlan scenePlan = new ScenePlan();
            scenePlan.setSceneId(sceneId);
            scenePlan.setStartAt(startAt);
            scenePlan.setEndAt(endAt);
            scenePlan.setWeekChoose(weekChose);
            scenePlan.setState(1);
            scenePlanService.save(scenePlan);
            scheduleTask.updateScenePlans();
            return Result.success().setData("mes", "添加成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + sceneId + "的场景");
        }
    }

    @ApiOperation("修改该定时计划，设置完之后会自动开启该计划, 启动时间和停止时间不能同时为空，如果单独只设置一个不为空，则在对应时间执行对应计划. 前端进入设置界面之后获取该场景计划信息自动填写原来设置, 用户在此基础上修改")
    @PutMapping("/modify")
    public Result modify(@ApiParam(value = "定时计划标识", required = true) @RequestParam("scenePlanId") String scenePlanId,
                      @ApiParam(value = "启动时间,格式[HH:mm]") @RequestParam(value = "startAt", required = false) String startAt,
                      @ApiParam(value = "停止时间,格式[HH:mm]") @RequestParam(value = "endAt", required = false) String endAt,
                      @ApiParam(value = "星期选择(0~7, 其中0表示不重复, 只是执行一次, 传入格式例如[1,2,3,]表示周1,2,3启用, [0,]表示不重复)", required = true) @RequestParam("weekChose") String weekChose){
        if (startAt.equals("无") && endAt.equals("无")) return Result.error().setData("mes", "启动时间和结束时间不能同时为空");
        if (scenePlanId == null || weekChose == null) return Result.error().setData("mes", "场景计划标识或星期选择有未填项");
        ScenePlan scenePlan = scenePlanService.getById(scenePlanId);
        if (scenePlan != null){
            LambdaUpdateWrapper<ScenePlan> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(ScenePlan::getStartAt, startAt)
                    .set(ScenePlan::getEndAt, endAt)
                    .set(ScenePlan::getWeekChoose, weekChose)
                    .set(ScenePlan::getState, 1)
                    .eq(ScenePlan::getId, scenePlanId);
            scenePlanService.update(wrapper);
            scheduleTask.updateScenePlans();
            return Result.success().setData("mes", "修改标识为: " + scenePlanId + "成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + scenePlanId + "的场景计划");
        }
    }

    @ApiOperation("删除该定时计划")
    @DeleteMapping("/delete")
    public Result delete(@ApiParam(value = "定时计划标识", required = true) @RequestParam("scenePlanId") String scenePlanId){
        ScenePlan scenePlan = scenePlanService.getById(scenePlanId);
        if (scenePlan != null){
            scenePlanService.removeById(scenePlanId);
            scheduleTask.updateScenePlans();
            return Result.success().setData("mes", "设置成功");
        }else {
            return Result.error().setData("mes", "没有找到标识为" + scenePlanId + "的场景计划");
        }
    }
}
