package com.lzp.smarthomesys.exception;

import com.lzp.smarthomesys.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    /**
//     * 全局异常处理
//     * @param e 异常
//     * @return Result
//     */
//    @ExceptionHandler(Exception.class)
//    public Result ExceptionHandler(Exception e){
//        return Result.except().setData("异常信息", e.getMessage());
//    }
}