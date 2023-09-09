package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 重复数据异常处理器（通过全局异常处理器，将重复异常sql错误以REST风格的形式显示给前端页面）
     */

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException sqlException){
        //java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'hwt' for key 'employee.idx_username'  重复用户名产生的错误

        //获取错误的消息
        String message = sqlException.getMessage();

        //判断是否为重复用户名的错误消息
        if(message.contains("Duplicate entry")){
            String[] m = message.split(" ");
            message = m[2] + MessageConstant.MESSAGE_EXISTS;
            return Result.success(message);
        }else{
            return Result.error(MessageConstant.ERROR_UNKNOW);
        }


    }

}
