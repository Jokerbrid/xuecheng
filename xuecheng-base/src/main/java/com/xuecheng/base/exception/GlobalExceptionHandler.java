package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//定义全局异常处理器：
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //根据不同异常，返回不同的response的json消息给前端：
    @ResponseBody
    @ExceptionHandler(xueChengException.class)
    public RestErrorResponse customException(xueChengException e){
        log.error("[系统异常]{}",e.getErrorMessage(),e);
        return new RestErrorResponse(e.getErrorMessage());
    }
    //
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public RestErrorResponse exception(Exception e){
        log.error("[系统异常]{}",e.getMessage(),e);
        if(e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("没有操作权限");
        }
        return new RestErrorResponse(CommonError.PARAMS_ERROR.getErrorMessage());
    }

}
