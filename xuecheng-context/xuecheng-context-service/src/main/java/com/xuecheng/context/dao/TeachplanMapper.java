package com.xuecheng.context.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.model.po.Teachplan;
import com.xuecheng.context.model.po.TeachplanMedia;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author joker
 * @since 2023-01-28
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    @Select("select * from teachplan where parentid=0 and course_id=#{courseId} ORDER BY orderby;")
    @Results({
            @Result(property = "id",column = "id"),

            @Result(property = "teachplanTreeNodes",javaType = List.class,column = "id",many=@Many(
                    select = "com.xuecheng.context.dao.TeachplanMapper.selectByParentId"
            ))
    })
    List<TeachplanDto> getTreeNodes(Long courseId);


    @Select("select * from teachplan where parentid=#{id} order by orderby;")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "teachplanMedia",javaType = TeachplanMedia.class,column ="id",one=@One(
                    select = "com.xuecheng.context.dao.TeachplanMediaMapper.getMedia"
            ))
    })

    List<TeachplanDto> selectByParentId(Long id);

}
