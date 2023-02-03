package com.xuechengauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuechengauth.domain.XcUser;
import com.xuechengauth.mapper.XcUserMapper;
import com.xuechengauth.model.dto.AuthParamsDto;
import com.xuechengauth.model.po.XcUserExt;
import com.xuechengauth.service.AuthService;
import com.xuechengauth.service.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/***
 *账户密码认证服务
 *
 *@author jokerBird
 *@Data create 2023-02-01 11:23
*/

@Service("password_authService")
@Slf4j
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Resource
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //增加验证码校验：
        String checkcode= authParamsDto.getCheckCode();
        String checkcodkey= authParamsDto.getCheckCodeKey();
        if(StringUtils.isBlank(checkcodkey)||StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");
        }
        boolean verify= checkCodeClient.verify(checkcodkey,checkcode);
        if(!verify){
            throw new RuntimeException("验证码输入错误");
        }

        String username=authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(xcUser==null){
            //返回空结果：
            throw new RuntimeException("账户不存在");
        }
        XcUserExt xcUserExt=new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);

        //校验密码：
        // 数据库存放密文：
        String passwordDB=xcUserExt.getPassword();
        //提交的是明文：
        String passwordForm=authParamsDto.getPassword();
        boolean matches=passwordEncoder.matches(passwordForm,passwordDB);
        if(!matches){
            throw new RuntimeException("账户或密码错误");
        }
        return xcUserExt;
    }
}
