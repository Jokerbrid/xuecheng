package com.xuechengauth.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuthParamsDto {
    private String username;
    private String password;
    private String cellphone;
    private String checkCode;//验证码

    private String checkCodeKey;

    private String authType;//验证类型  password 密码认证，sms 短信认证

    private Map<String ,Object> payload=new HashMap<>();//附加数据。


}
