package com.xuecheng.context.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.model.dto.AddCourseDto;
import com.xuecheng.context.model.dto.CourseBaseInfoDto;
import com.xuecheng.context.model.dto.EditCourseDto;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.model.po.CourseMarket;

public interface CourseBaseInfoService {

    //分页查询所有的课程基本表信息
    PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamDto queryCourseParamDto);

    //插入课程基本表的信息
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto);

    //根据课程id查询课程信息：
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    //修改课程的信息：
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);


    //保存并下一步：
    int saveCourseMarket(CourseMarket courseMarket);
}
