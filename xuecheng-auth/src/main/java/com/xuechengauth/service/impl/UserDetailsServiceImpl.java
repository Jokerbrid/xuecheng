package com.xuechengauth.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xuechengauth.domain.XcMenu;
import com.xuechengauth.mapper.XcMenuMapper;
import com.xuechengauth.mapper.XcUserMapper;
import com.xuechengauth.model.dto.AuthParamsDto;
import com.xuechengauth.model.po.XcUserExt;
import com.xuechengauth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/***
 *
 * 认证身份信息的service
 */

@Component
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcMenuMapper xcMenuMapper;
    @Autowired
    ApplicationContext applicationContext;



    /***
     *
     * @param jsonUser   json格式的数据
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String jsonUser) throws UsernameNotFoundException {
        //修改验证，多种验证方式：
        Gson gson=new Gson();
        AuthParamsDto authParamsDto=null;
        try {
            authParamsDto=gson.fromJson(jsonUser,AuthParamsDto.class);
        } catch (JsonSyntaxException e) {
            log.info("认证请求不符合");
            throw new RuntimeException("认证请求的参数格式错误");
        }
        //认证方法：
        String authType= authParamsDto.getAuthType();
        //根据不同的方法名，拿到不同的认证类：
        AuthService authService=applicationContext.getBean(authType+"_authService",AuthService.class);
        //开始认证：
        XcUserExt user = authService.execute(authParamsDto);


        return getUserPrincipal(user);
    }

    //优化：
    public UserDetails getUserPrincipal(XcUserExt user){
        Gson gson=new Gson();
        //设置用户权限：
        List<XcMenu> xcMenus=xcMenuMapper.selectPermissionsByUserId(user.getId());
        //
        List<String> permissions=new ArrayList<>();
        //如果用户没有授权，那么设置默认的无作用权限避免框架报错。
        if(xcMenus==null){
            permissions.add("default");
        }else{
            xcMenus.forEach(menu->{
                permissions.add(menu.getCode());
            });
        }
        user.setPermissions(permissions);

        String password= user.getPassword();
        //token中不存放密码保证数据安全:
        user.setPassword(null);
        //将user转化为json:
        String userString=gson.toJson(user);
        //将授权信息封装成字符串数组：
        String[] authorities=permissions.toArray(new String[0]);
        //创建UserDetails对象：
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        //返回结果,注入到spring-security框架进行认证：
        return userDetails;
    }
}
