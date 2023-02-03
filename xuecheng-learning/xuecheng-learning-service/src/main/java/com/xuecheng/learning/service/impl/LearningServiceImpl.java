package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.context.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTableDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LearningServiceImpl implements LearningService {
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //查询课程信息：
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if(coursePublish==null){
            xueChengException.cast("课程信息不存在");
        }
        if(StringUtils.isBlank(userId)){
            //如果登录：
            XcCourseTableDto xcCourseTableDto = myCourseTablesService.getLearningStatus(userId, courseId);
            //学习资格状态：
            String learningStatus = xcCourseTableDto.getLearningStatus();
            //正常学习：
            if("702001".equals(learningStatus)){
                return mediaServiceClient.previewByFileId(mediaId);
            }else if("702003".equals(learningStatus)){
                RestResponse.validfail("您的选课已过期需要申请续期或重新支付");
            }
        }
        //未登录：
        String charge = coursePublish.getCharge();
        if("201000".equals(charge)){
            //免费：
            return mediaServiceClient.previewByFileId(mediaId);
        }
        return RestResponse.validfail("请购买课程后继续学习");
    }
}
