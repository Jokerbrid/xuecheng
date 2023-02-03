package com.xuecheng.context.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.context.model.po.CourseTeacher;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 Mapper 接口
 * </p>
 *
 * @author joker
 * @since 2023-01-30
 */

@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {



}
