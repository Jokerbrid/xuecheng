package com.xuecheng.learning.feignclient;


import com.xuecheng.context.model.po.CoursePublish;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("xuecheng-context")
public interface ContentServiceClient {

    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable Long courseId);
}
