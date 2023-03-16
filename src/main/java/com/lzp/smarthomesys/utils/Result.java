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

    /**
     * 返回成功的结果
     * @return Result
     */
    public static Result success(){
        return new Result()
                .setIsSuccess(true)
                .setCode(ResultCode.SUCCESS.getResultValue())
                .setMessage("操作成功")
                .setData(new HashMap<>());
    }

    /**
     * 返回失败的结果
     * @return Result
     */
    public static Result error(){
        return new Result()
                .setIsSuccess(false)
                .setCode(ResultCode.ERROR.getResultValue())
                .setMessage("操作失败")
                .setData(new HashMap<>());
    }

    /**
     * 设置Result里面的内容
     * @param data 数据
     * @return Result
     */
    public Result setData(HashMap<String, Object> data){
        this.data = data;
        return this;
    }

    /**
     * 设置Result里面的内容
     * @param key 键
     * @param value 值
     * @return Result
     */
    public Result setData(String key, Object value){
        this.data.put(key, value);
        return this;
    }
}
