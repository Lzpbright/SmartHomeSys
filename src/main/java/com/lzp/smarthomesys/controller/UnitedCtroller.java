package com.lzp.smarthomesys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/united")
@Api("奇奇怪怪控制器")
public class UnitedCtroller {

    @Value("${onenet.device_id}")
    private String device_id;

    @Resource
    private FileUtils fileUtils;

    @Value("${weather.key}")
    private String weatherKey;

    private JSONObject cityCodes = new JSONObject();

    /**
     * 获取用户客厅湿度和温度
     * @return Result
     */
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

    /**
     * 获取全国名字
     * @return Result
     */
    @GetMapping("/getCityName")
    @ApiOperation("获取全国城市名字")
    public Result getCityName(){
        if (cityCodes.size() == 0) {
            String content = fileUtils.readFile("src/main/resources/appFiles/cityCode.json");
            cityCodes = JSONObject.parseObject(content);
        }
        Set<String> cityNames = cityCodes.keySet();
        String[] cityNamesArray = cityNames.toArray(new String[0]);
        return Result.success().setData("城市名字", cityNamesArray);
    }

    /**
     * 通过城市名字获取城市天气
     * @param cityName 城市名字
     * @return Result
     */
    @GetMapping("/getWeather")
    @ApiOperation("通过城市名字获取城市天气, 返回可能为空，该地点没有温度")
    public Result getWeather(@ApiParam(value = "城市名字", required = true) @RequestParam(value = "cityName") String cityName){
        if (cityCodes.size() == 0) {
            String content = fileUtils.readFile("src/main/resources/appFiles/cityCode.json");
            cityCodes = JSONObject.parseObject(content);
        }
        if (cityCodes.get(cityName) == null){
            return Result.error().setData("mes", "城市名字不存在，请查看前一个接口获取得到的城市");
        }
        else {
            String code = cityCodes.get(cityName).toString();
            String url = "https://restapi.amap.com/v3/weather/weatherInfo?key=" + weatherKey + "&city=" + code;
            String res = HttpUtils.sendGet(url, null, null);
            JSONObject resJson = JSONObject.parseObject(res);
            if (resJson.get("info").equals("OK")){
                Map<String, String> result = new HashMap<>();
                JSONArray resArray = resJson.getJSONArray("lives");
                if (resArray.getJSONObject(0) != null) {
                    JSONObject livesObject = resArray.getJSONObject(0);
                    result.put("省份", livesObject.get("province").toString()); // 由于不是所有城市都有，所以注释，直辖市没有省份
                    result.put("城市", livesObject.get("city").toString());
                    result.put("天气", livesObject.get("weather").toString());
                    result.put("温度", livesObject.get("temperature").toString());
                    result.put("风向", livesObject.get("winddirection").toString());
                    result.put("风力", livesObject.get("windpower").toString());
                    result.put("湿度", livesObject.get("humidity").toString());
                    result.put("时间", livesObject.get("reporttime").toString());
                }else {
                    result.put("省份", "本地暂无数据");
                    result.put("城市", "本地暂无数据");
                    result.put("天气", "本地暂无数据");
                    result.put("温度", "本地暂无数据");
                    result.put("风向", "本地暂无数据");
                    result.put("风力", "本地暂无数据");
                    result.put("湿度", "本地暂无数据");
                    result.put("时间", "本地暂无数据");
                }
                return Result.success().setData("res", result);
            }else {
                return Result.error().setData("mes", "天气接口出问题啦。。。");
            }
        }
    }

    /**
     * 发送邮箱
     * @param subject 主题
     * @param to 发送对象
     * @param content 内容
     * @param isHtml 是否html
     * @return Result
     */
    @GetMapping("/sendMail")
    @ApiOperation("发送邮件")
    public Result sendMail(@ApiParam(value = "主题", required = true) @RequestParam(value = "subject") String subject,
                           @ApiParam(value = "发送对象", required = true) @RequestParam(value = "to") String to,
                           @ApiParam(value = "内容", required = true) @RequestParam(value = "content") String content,
                           @ApiParam(value = "是否html", required = true) @RequestParam(value = "isHtml") String isHtml){
        EMailUtils.send(subject, to, content, Boolean.parseBoolean(isHtml));
        return Result.success().setData("mes", "success");
    }
}
