package com.xuecheng.system.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class filterCors{

    @Bean
    public CorsFilter getCorsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许方法跨域：
        corsConfiguration.addAllowedMethod("*");
        //允许源跨域：
        corsConfiguration.addAllowedOrigin("*");
        //允许请求头跨域
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsFilter(configurationSource);
    }
}
