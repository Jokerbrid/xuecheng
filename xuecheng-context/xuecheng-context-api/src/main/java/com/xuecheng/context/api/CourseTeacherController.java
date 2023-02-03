package com.xuecheng.context.api;

import com.xuecheng.context.dao.CourseTeacherMapper;
import com.xuecheng.context.model.dto.AddTeacherDto;
import com.xuecheng.context.model.dto.EditTeacherDto;
import com.xuecheng.context.model.po.CourseTeacher;
import com.xuecheng.context.model.po.Teachplan;
import com.xuecheng.context.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> list(@PathVariable Long courseId){
        return courseTeacherService.listByCourseId(courseId);
    }
    @PostMapping("/courseTeacher")
    public void CreateCourseTeacher(@RequestBody EditTeacherDto dto){
        courseTeacherService.createTeacher(dto);
    }

}
