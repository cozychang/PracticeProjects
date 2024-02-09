package com.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})//规定范围：会捕获有这两个注解的类的异常
@ResponseBody//将输出对象转为JSON传给前端
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //打印错误信息
        log.error(ex.getMessage());

        return R.error("添加重复，失败");
    }


    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException ex){
        //打印错误信息
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
