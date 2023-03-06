package com.lzp.smarthomesys.utils;

import com.lzp.smarthomesys.enums.ResultCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

// 统一返回结果
@Data
@Accessors(chain = true)
public class Result {
    // 操作是否成功
    private Boolean isSuccess;
    // 状态码
    private Integer code;
    // 操作消息
    private String message;
    // 返回可能数据
    private Map<String, Object> data;

    // 成功的操作
    public static Result success(){
        return new Result()
                .setIsSuccess(true)
                .setCode(ResultCode.SUCCESS.getResultValue())
                .setMessage("操作成功")
                .setData(new HashMap<>());
    }

    // 失败的操作
    public static Result error(){
        return new Result()
                .setIsSuccess(false)
                .setCode(ResultCode.ERROR.getResultValue())
                .setMessage("操作失败")
                .setData(new HashMap<>());
    }

    public Result setData(HashMap<String, Object> data){
        this.data = data;
        return this;
    }

    public Result setData(String key, Object value){
        this.data.put(key, value);
        return this;
    }
}
