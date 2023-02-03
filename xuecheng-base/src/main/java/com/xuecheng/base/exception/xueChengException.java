package com.xuecheng.base.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class xueChengException extends RuntimeException {

    static final long serialVersionUID = 3034897190745766939L;
    private String errorMessage;

    public  static void cast(CommonError commonError){
        throw new xueChengException(commonError.getErrorMessage());
    }
    public static void cast(String errorMessage){
        throw new xueChengException(errorMessage);
    }

}
