package com.xuechengauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

@Configuration
public class TokenConfig {

    private String SIGNING_KEY="mq123";//签名密钥；



    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();//使用内存存储令牌.
    }


    //token服务
    @Bean(name = "AuthorizationServerTokenServicesCustom")
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services=new DefaultTokenServices();
        services.setTokenStore(tokenStore());//注入令牌存储策略
        services.setSupportRefreshToken(true);//支持刷新令牌


        //配置令牌增强器：(生成JWT)
        TokenEnhancerChain tokenEnhancerChain=new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter()));

        services.setTokenEnhancer(tokenEnhancerChain);//设置token链。


        services.setAccessTokenValiditySeconds(60*60*2);//设置令牌默认有效时间 2小时
        services.setRefreshTokenValiditySeconds(3*24*60*60);//刷新令牌默认有效时间 3天
        return services;
    }


    //注册转换器：
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter=new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }
}
