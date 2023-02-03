package com.xuecheng.context.job;

import com.xuecheng.context.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;


@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;



    public static final String MESSAGE_TYPE="course_publish";
    @XxlJob("CoursePublishJobHandler")
    public void CoursePublishJobHandler(){
        int shardIndex= XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("shardIndex:{},shardTotal:{}",shardIndex,shardTotal);
        process(shardIndex,shardTotal,MESSAGE_TYPE,5,60);

    }


    //执行器： 异步执行多个与课程发布相关的任务。
    @Override
    public boolean execute(MqMessage mqMessage) {
        String businessKey1= mqMessage.getBusinessKey1();
        long courseId=Integer.parseInt(businessKey1);
        //课程静态化：
        generateCourseHtml(mqMessage,courseId);
        //课程缓存：

        //课程索引：


        return true;
    }

    //课程静态化代码：
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        //
        Long id=mqMessage.getId();
        //从抽象类中拿到service;
        MqMessageService mqMessageService=this.getMqMessageService();
        //
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne==1){
            return;
        }
        //生成静态化页面：
        log.info("生成静态页面");
        File file=coursePublishService.generateCourseHtml(courseId);
        //上传：
        if(file!=null){
            System.out.println(file.getPath());
            coursePublishService.uploadCourseHtml(courseId,file);
        }

        //保存第一阶段：
        mqMessageService.completedStageOne(id);
        log.info("生成页面完成");
    }

    //将课程信息缓存至redis:
    public void saveCourseCache(MqMessage mqMessage, long courseId){

    }
    //将课程索引信息：
    public void saveCourseIndex(MqMessage mqMessage,long courseId){

    }

}
