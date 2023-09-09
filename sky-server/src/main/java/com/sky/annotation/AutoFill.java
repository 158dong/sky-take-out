package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于AOP切入（每次insert或者update的时候都要进行更新修改时间，所以用AOP切面减少冗余代码）
 */

@Target(ElementType.METHOD)   //设置此注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)   //固定写法
public @interface AutoFill {
    //数据库操作类型：update  insert
    OperationType value();

}
