package com.lzp.smarthomesys.controller;

import com.lzp.smarthomesys.entity.Log;
import com.lzp.smarthomesys.service.impl.LogServiceImpl;
import com.lzp.smarthomesys.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    LogServiceImpl logService;

    /**
     * 获取所有命令发送日志
     * @return Result
     */
    @GetMapping("/getAll")
    @ApiOperation("获取所有日志")
    public Result getAll(){
        List<Log> list = new ArrayList<>();
        try {
            list = logService.list();
        }catch (Exception e){
            return Result.error().setData("mes", e);
        }
        return Result.success().setData("allLog", list);
    }
}
