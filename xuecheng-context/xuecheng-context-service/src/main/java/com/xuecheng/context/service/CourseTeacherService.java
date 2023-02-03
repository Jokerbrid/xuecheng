package com.xuecheng.context.service;

import com.xuecheng.context.model.dto.AddTeacherDto;
import com.xuecheng.context.model.dto.EditTeacherDto;
import com.xuecheng.context.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {


    List<CourseTeacher> listByCourseId(Long courseId);

    CourseTeacher createTeacher(EditTeacherDto editTeacherDto);
}
