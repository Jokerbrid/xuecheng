package com.xuecheng.context.service;

import com.xuecheng.context.model.po.CoursePublish;

import java.io.File;

public interface CoursePublishService {

    void commitAudit(Long companyId,Long courseId);

    void publish(Long companyId,Long courseId);

    void OffLine(Long companyId,Long courseId);

    //静态化：
    public File generateCourseHtml(Long courseId);
    //上传静态化页面：
    void uploadCourseHtml(Long courseId,File file);

//    CoursePublish getCoursePublish(Long courseId);

    //从缓存中获取：
    CoursePublish getCoursePublishCache(Long courseId);

}
