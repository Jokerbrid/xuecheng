package com.xuecheng.checkcode.service.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.AbstractCheckCodeService;
import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service("mailCheckCodeService")
public class MailCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {

    @Resource(name="NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name="UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name="MemoryCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto, 4, "$xcKey$", 60);
        String key = generate.getKey();
        String code = generate.getCode();
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        checkCodeResultDto.setCode(code);

        //将验证码存放至redis并设置60s的存货时间：
        redisTemplate.opsForValue().set("check_key"+code,code,60, TimeUnit.SECONDS);

        //发送到指定邮箱：
        send(checkCodeParamsDto.getEmail(),code);

        return checkCodeResultDto;

    }
    @Value("${spring.mail.username}")
    private  String fromMail;
    @Value("${spring.mail.nickname}")
    private  String nickName;
    @Autowired
    private JavaMailSender javaMailSender;

    public void send(String email,String code) {
        String message="您的验证码为："+code+"\n";

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(nickName+'<'+fromMail+'>');
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("测试邮箱功能");
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public boolean verify(String key, String code) {

        String check = (String) redisTemplate.opsForValue().getAndDelete("check_key" + code);

        if(StringUtils.equals(check,code)){
            return true;
        }
        return false;
    }
}
