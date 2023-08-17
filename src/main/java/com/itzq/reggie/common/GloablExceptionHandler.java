package com.itzq.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//全局的异常处理器
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//返回JSON
@ResponseBody
@Slf4j
public class GloablExceptionHandler {
    //这个注解在一个地方集中处理控制器中的所有异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String>exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //如果异常信息包含"Duplicate entry"
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);

        }

        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String>exceptionHandler(CustomException ex){
        log.error(ex.getMessage());
        //如果异常信息包含"Duplicate entry"
        return R.error(ex.getMessage());
    }
}
