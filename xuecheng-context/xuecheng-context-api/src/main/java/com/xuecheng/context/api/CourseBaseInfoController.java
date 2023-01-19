package com.xuecheng.context.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.service.CourseBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/***
 *
 */
@RestController
public class CourseBaseInfoController {
    /**
     * 查询课程相关信息：
     */
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamDto queryCourseParamDto){
        //调用service获取数据：
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params,queryCourseParamDto);
        return courseBasePageResult;
    }
}
