package com.xuecheng.context.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.xuecheng.context.model.dto.CourseBaseInfoDto;
import com.xuecheng.context.model.dto.CoursePreviewDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.model.po.CoursePublish;
import com.xuecheng.context.service.CoursePublishService;
import com.xuecheng.context.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

@RestController
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;

    //提交审核：
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable Long courseId){
        //取出当前用户身份：
        //通过工具类取出身份的完整信息：
        SecurityUtil.XcUser user= SecurityUtil.getUser();
        //
        Long companyId= Long.valueOf(user.getCompanyId());

        coursePublishService.commitAudit(companyId,courseId);
    }

    //发布课程：
    @PostMapping("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable Long courseId){
        //公司id,用于验证：
        //从token中获取公司id:
        SecurityUtil.XcUser user= SecurityUtil.getUser();
        //
        Long companyId= Long.valueOf(user.getCompanyId());

        coursePublishService.publish(companyId,courseId);
    }

    //下架课程：
    @GetMapping("/courseoffline/{courseId}")
    public void courseOffLine(@PathVariable Long courseId){
        //公司id,用于验证：
        //从token中获取公司id:
        SecurityUtil.XcUser user= SecurityUtil.getUser();
        //
        Long companyId= Long.valueOf(user.getCompanyId());
        coursePublishService.OffLine(companyId,courseId);
    }

    //根据课程id,获取已发布的课程信息。
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublishTest(@PathVariable Long courseId){
        CoursePublish coursePublish = coursePublishService.getCoursePublishCache(courseId);
        if(coursePublish==null){
            return null;
        }
        //拿到课程发布状态：
        String status=coursePublish.getStatus();
        //如果状态为：已发布。
        if(status.equals("203002")){
            return coursePublish;
        }
        //否则返回空：
        return null;
    }

    //获取课程发布信息：
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable Long courseId){
        CoursePublish coursePublish = coursePublishService.getCoursePublishCache(courseId);
        if(coursePublish==null){
            return new CoursePreviewDto();
        }
        //课程基本信息：
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程计划：
        Gson gson=new Gson();
        Type listType=new TypeToken<List<TeachplanDto>>(){}.getType();
        List<TeachplanDto> teachplans=gson.fromJson(coursePublish.getTeachplan(),listType);
        CoursePreviewDto coursePreviewInfo = new CoursePreviewDto();
        coursePreviewInfo.setCourseBase(courseBaseInfoDto);
        coursePreviewInfo.setTeachplans(teachplans);

        return coursePreviewInfo;
    }
}
