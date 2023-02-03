package com.xuechengauth.controller;


import com.xuechengauth.domain.XcUser;
import com.xuechengauth.mapper.XcUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginController {
    @Autowired
    XcUserMapper xcUserMapper;

    @RequestMapping("/login-success")
    public String loginSuccess(){
        return "login_success";
    }

    @RequestMapping("/logout")
    public String logout(){
        return "logout";
    }


    //校验接口：

    @RequestMapping("/user/{id}")
    public XcUser login(@PathVariable String id){
        XcUser xcUser = xcUserMapper.selectById(id);
        return xcUser;
    }
    @RequestMapping("/r/r1")
    @PreAuthorize("hasAuthority('p1')")//设置访问权限：只有p1权限的用户才能访问：
    public String r1(){
        return "访问资源";
    }
    @RequestMapping("/r/r2")
    public String r2(){
        return "访问资源";
    }


}
