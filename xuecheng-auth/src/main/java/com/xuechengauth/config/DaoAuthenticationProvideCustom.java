package com.xuechengauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;


@Component
public class DaoAuthenticationProvideCustom extends DaoAuthenticationProvider {
    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService){
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        //不进行Spring-security框架的密码对比：
        //super.additionalAuthenticationChecks(userDetails, authentication);
    }

}
