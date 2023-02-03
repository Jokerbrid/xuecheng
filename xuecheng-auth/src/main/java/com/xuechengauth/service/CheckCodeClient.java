package com.xuechengauth.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/***
 * 搜索服务远程接口：
 *@author jokerBird
 *@Data create 2023-02-01 13:19
*/
@Component
@FeignClient(value = "xuecheng-checkcode")
public interface CheckCodeClient {

    @PostMapping(value = "/verify")
     boolean verify(@RequestParam("key")String key,@RequestParam("code") String code);
}
