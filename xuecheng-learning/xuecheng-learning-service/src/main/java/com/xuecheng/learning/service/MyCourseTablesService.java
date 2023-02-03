package com.xuecheng.learning.service;


import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTableDto;
import com.xuecheng.learning.model.po.XcCourseTables;

public interface MyCourseTablesService {


    //根据用户id和课程id增加选课信息：
    XcChooseCourseDto addChooseCourse(String userId,Long courseId);


    //判断学习资格：
    XcCourseTableDto getLearningStatus(String userId,Long courseId);

    boolean saveChooseCourseStatus(String chooseCourseId);


    //分页查询课程表：
    PageResult<XcCourseTables> mycourseTables(MyCourseTableParams params);
}
