package com.lzp.smarthomesys.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

// 返回编码
public enum ResultCode {
    // 成功20000,以及错误20001是一般来说为了和前端统一的方式
    SUCCESS(20000, "成功"),
    ERROR(20001, "失败");

    /**
     * 返回结果构造函数
     * @param resultValue 返回结果
     * @param resultDesc 返回结果描述
     */
    ResultCode(Integer resultValue, String resultDesc){
        this.resultValue = resultValue;
        this.resultDesc = resultDesc;
    }

    @EnumValue
    private final int resultValue;
    private final String resultDesc;

    public Integer getResultValue() {
        return resultValue;
    }

    public String getResultDesc() {
        return resultDesc;
    }
}
