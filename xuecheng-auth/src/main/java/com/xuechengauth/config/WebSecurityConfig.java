package com.xuechengauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    DaoAuthenticationProvideCustom daoAuthenticationProvideCustom;

    //配置认证管理：
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    //配置编码：
    @Bean
    public PasswordEncoder passwordEncoder() {
        //密文：
        return new BCryptPasswordEncoder();
    }

    //配置安全拦截机制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/r/**").permitAll()//访问/r开始的请求需要认证通过
                .anyRequest().authenticated()//其它请求全部放行
                .and()
                .formLogin().successForwardUrl("/login-success")
                .and().logout().logoutUrl("/logout");//登录成功跳转到/login-success
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //配置修改后的认证提供类
        auth.authenticationProvider(daoAuthenticationProvideCustom);
    }
}
