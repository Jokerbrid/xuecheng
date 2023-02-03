package com.xuecheng.learning.api;

import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
public class LearningController {
    @Autowired
    LearningService learningService;


    @GetMapping("/open/learn/getvedio/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getVedio(@PathVariable Long courseId,
                                         @PathVariable Long teachplanId,
                                         @PathVariable String mediaId){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null){
            xueChengException.cast("请登录后再进行操作");
        }
        String userId = user.getId();
        //获取视频资源：
        learningService.getVideo(userId,courseId,teachplanId,mediaId);

        return null;
    }

}
