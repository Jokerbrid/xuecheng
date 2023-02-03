package com.xuecheng.context.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.context.dao.CourseTeacherMapper;
import com.xuecheng.context.model.dto.AddTeacherDto;
import com.xuecheng.context.model.dto.EditTeacherDto;
import com.xuecheng.context.model.po.CourseTeacher;
import com.xuecheng.context.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacher> listByCourseId(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public CourseTeacher createTeacher( EditTeacherDto editTeacherDto) {
        //封装属性
        CourseTeacher courseTeacher=new CourseTeacher();
        BeanUtils.copyProperties(editTeacherDto,courseTeacher);
        CourseTeacher result = courseTeacherMapper.selectById(courseTeacher);
        if(result==null) {
            courseTeacher.setCreateDate(new Date());
            //插入表中
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                xueChengException.cast("该教师信息已经存在");
            }
        }else{
            //更新数据库：
            courseTeacherMapper.updateById(courseTeacher);
        }
        return courseTeacher;
    }
}
