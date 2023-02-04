package com.xuecheng.checkcode.controller;

import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.CheckCodeService;
import com.xuecheng.checkcode.service.impl.MemoryCheckCodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码服务接口
 * @date 2022/9/29 18:39
 */

@RestController
public class CheckCodeController {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MemoryCheckCodeStore memoryCheckCodeStore;

    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        //图片验证码：
        CheckCodeService service = applicationContext.getBean(checkCodeParamsDto.getCheckCodeType() + "CheckCodeService", CheckCodeService.class);
        return service.generate(checkCodeParamsDto);
    }


    @PostMapping(value = "/verify")
    public Boolean verify( String key,@RequestParam String code){

        if(key==null){
            //说明是sms验证码/邮箱验证码：
            CheckCodeService service = applicationContext.getBean("mailCheckCodeService", CheckCodeService.class);
            return service.verify(null, code);


        }
        //图片验证码：
        CheckCodeService service = applicationContext.getBean("picCheckCodeService", CheckCodeService.class);
        Boolean isSuccess = service.verify(key,code);
        return isSuccess;
    }
    @PostMapping(value = "/mail")
    public void generateMailCheckCode( CheckCodeParamsDto checkCodeParamsDto){
        //图片验证码：
        CheckCodeService service = applicationContext.getBean(checkCodeParamsDto.getCheckCodeType() + "CheckCodeService", CheckCodeService.class);
        service.generate(checkCodeParamsDto);
    }
}
