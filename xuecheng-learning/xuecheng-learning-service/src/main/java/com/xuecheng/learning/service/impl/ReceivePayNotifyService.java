package com.xuecheng.learning.service.impl;

import com.google.gson.Gson;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReceivePayNotifyService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MyCourseTablesService myCourseTablesService;

    @RabbitListener(queues = PayNotifyConfig.choose_course_pay_notify_queue)
    public void receive(String message){
        Gson gson=new Gson();
        //从消息队列中拿到消息：
        MqMessage mqMessage = gson.fromJson(message, MqMessage.class);
        log.debug("学习中心服务接收支付结果:{}",mqMessage);

        //消息类型：
        String messageType = mqMessage.getMessageType();
        //订单类型：
        String businessKey2 = mqMessage.getBusinessKey2();
        //
        if(PayNotifyConfig.message_type.equals(messageType)&&
        "602001".equals(businessKey2)){
            //选课id:
            String chooseCourseId = mqMessage.getBusinessKey1();
            //添加选课：
            boolean res=myCourseTablesService.saveChooseCourseStatus(chooseCourseId);
            if(res){
                //回复：
                send(mqMessage);
            }
        }
    }

    public void send(MqMessage message){
        String msg=new Gson().toJson(message);
        rabbitTemplate.convertAndSend(PayNotifyConfig.pay_notify_reply_queue,msg);
    }

}
