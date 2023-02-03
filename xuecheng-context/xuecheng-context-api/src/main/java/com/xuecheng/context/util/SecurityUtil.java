package com.xuecheng.context.util;


import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Date;

@Slf4j
public class SecurityUtil {


    //将jwt的用户信息取出的工具类：
    public static XcUser getUser() {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principalObj instanceof String){
            //取出用户身份信息：
            String principal = principalObj.toString();
            //将json装成对象：
            XcUser xcUser= new Gson().fromJson(principal, XcUser.class);
            return xcUser;
        }
        return null;
    }
    @Data
    public static class XcUser implements Serializable{
        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        /**
         * 微信unionid
         */
        private String wxUnionid;

        /**
         * 昵称
         */
        private String nickname;

        private String name;

        /**
         * 头像
         */
        private String userpic;

        private String companyId;

        private String utype;

        private Date birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private Date createTime;

        private Date updateTime;


    }
}
