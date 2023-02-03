package com.xuecheng.pay.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit //通过yml文件,自动装配cachingConnectionFactory连接rabbitmq
public class PayNotifyConfig {

    //定义交换机：
    public static final String pay_notify_exchange_fanout="pay_notify_exchange_fanout";
    //定义支付结果通知队列
    public static final String choose_course_pay_notify_queue="choose_course_pay_notify_queue";
    //定义支付结果反馈队列：
    public static final String pay_notify_reply_queue="pay_notify_reply_queue";

    //定义支付结果通知消息类型：
    public static final String message_type="payresult_notify";

    //声明exchange
    @Bean(pay_notify_exchange_fanout)
    public FanoutExchange pay_notify_exchange_fanout(){
        //
        return new FanoutExchange(pay_notify_exchange_fanout,true,false);
    }
    //声明queue
    @Bean(choose_course_pay_notify_queue)
    public Queue course_publish_queue(){
        //
        return new Queue(choose_course_pay_notify_queue,true,false,false);
    }
    //声明结果反馈队列：
    @Bean(pay_notify_reply_queue)
    public Queue msg_result_queue(){
        //
        return new Queue(pay_notify_reply_queue,true,false,false);
    }

    //交换机和队列绑定：
    @Bean
    public Binding bing_course_publish_queue(@Qualifier(choose_course_pay_notify_queue) Queue queue,
                                             @Qualifier(pay_notify_exchange_fanout) FanoutExchange exchange){
        return BindingBuilder.bind(queue).to(exchange);
    }
}
