package com.xuecheng.messagesdk.service;

import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public abstract class MessageProcessAbstract  {

    @Autowired
    MqMessageService mqMessageService;


    //执行任务内容：
    public abstract boolean execute(MqMessage mqMessage);

    //扫描消息表,多线程执行任务：
    public void process(int shardIndex, int shardTotal,String messageType,int count,long timeout){
        try {
            List<MqMessage> messagesList=mqMessageService.getMessageList(shardIndex,shardTotal,messageType,count);
            if(messagesList==null){
                return;
            }
            //
            int size=messagesList.size();
            if(size==0){
                return;
            }
            //创建线程池：
            ExecutorService threadPool= Executors.newFixedThreadPool(size);
            //计数器:
            CountDownLatch countDownLatch=new CountDownLatch(size);
            //
            messagesList.forEach(message->{
                threadPool.execute(()->{
                    log.debug("开始任务：{}",message);
                    try {
                        //处理任务：

                        boolean result=execute(message);
                        if(result){
                            log.debug("任务执行成功:{}",message);
                            //更新任务状态，删除消息表记录，添加到历史表：
                            int completed=mqMessageService.completed(message.getId());
                            if(completed>0){
                                log.debug("任务执行成功：{}",message);
                            }else{
                                log.debug("任务执行失败：{}",message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.debug("任务出现异常：{},任务：{}",e.getMessage(),message);
                    }
                    //计数
                    countDownLatch.countDown();
                    log.debug("结束任务：{}",message);
                });
            });
            //设置等待时间:
            countDownLatch.await(timeout, TimeUnit.SECONDS);
            System.out.println("结束...");
        } catch (InterruptedException e) {
            e.printStackTrace();
            xueChengException.cast(CommonError.UNKNOWN_ERROR);
        }


    }

}
