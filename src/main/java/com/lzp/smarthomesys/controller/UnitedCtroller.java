package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.utils.HttpUtils;
import com.lzp.smarthomesys.utils.Result;
import com.lzp.smarthomesys.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/united")
@Api("奇奇怪怪控制器")
public class UnitedCtroller {

    @Value("${onenet.device_id}")
    private String device_id;

    @GetMapping("/getTemAndHum")
    @ApiOperation("获取客厅温度和湿度[之后可能会要通过放假id获取,不过目前就单纯获取]")
    public Result getTemAndHum(){
        String url = "http://api.heclouds.com/devices/" + device_id + "/datastreams";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", TokenUtils.getOneNetToken());
        String res = HttpUtils.sendGet(url, headers, new HashMap<>());
        JSONObject resJson = JSON.parseObject(res);
        if (resJson.get("error").equals("succ")){
            JSONArray jsonArray = resJson.getJSONArray("data");
            Map<String, Object> result = new HashMap<>();
            result.put("温度", jsonArray.getJSONObject(0).get("current_value"));
            result.put("湿度", jsonArray.getJSONObject(1).get("current_value"));
            return Result.success().setData("res", result);
        }else {
            return Result.error().setData("mes", "获取失败，硬件方问题");
        }
    }
}
