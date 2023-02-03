package com.xuecheng.pay.job;

import com.google.gson.Gson;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.pay.config.PayNotifyConfig;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class payJobTask extends MessageProcessAbstract {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MqMessageService mqMessageService;

    //接收消息：
    @RabbitListener(queues = PayNotifyConfig.pay_notify_reply_queue)
    public void receive(String message){
        Gson gson=new Gson();
        MqMessage mqMessage = gson.fromJson(message, MqMessage.class);
        //
        log.debug("接收支付结果回复:{}",message);
        //
        mqMessageService.completed(mqMessage.getId());
    }


    @XxlJob("NotifyPayResultJobHandler")
    public void NotifyPayResultJobHandler(){
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("分片编号：{},分片数：{}",shardIndex,shardTotal);
        process(shardIndex,shardTotal, PayNotifyConfig.message_type,5,60);
    }

    @Override
    public boolean execute(MqMessage mqMessage) {
        System.out.println(mqMessage);
        log.debug("开始执行：{}",mqMessage.toString());
        send(mqMessage);
        return false;
    }

    public void send(MqMessage message){

        //message转json:
        String msg=new Gson().toJson(message);
        //发送到交换机：
        rabbitTemplate.convertAndSend(PayNotifyConfig.pay_notify_exchange_fanout,"",msg);
    }
}
