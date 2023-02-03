package com.xuecheng.base.exception;


public enum CommonError {

    PARAMS_ERROR("非法参数"),
    UNKNOWN_ERROR("未知错误");

    private String errorMessage;
    public String getErrorMessage(){
        return errorMessage;
    }
    private CommonError(String errorMessage){
        this.errorMessage=errorMessage;
    }
}
