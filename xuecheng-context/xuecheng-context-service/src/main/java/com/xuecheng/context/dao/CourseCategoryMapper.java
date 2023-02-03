package com.xuecheng.context.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author joker
 * @since 2023-01-20
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {


    //根据id,向上找父节点：
    @Select("with recursive t1 as (\n" +
            " select * from course_category p where id=#{id}\n " +
            "union all\n" +
            "select t.* from course_category t inner join t1 on t1.parentid = t.id\n" +
            ")\n" +
            "select * from t1 order by t1.id, t1.orderby;")
    List<CourseCategoryTreeDto> selectTreeNodes(String id);


    //根据id,向下找子节点：
    @Select("with recursive t1 as (\n" +
            "select * from course_category p where id=#{id}\n" +
            "union all\n" +
            "select t.* from course_category t inner join t1 on t1.id = t.parentid\n" +
            ")select * from t1 order by t1.id, t1.orderby;")
    List<CourseCategoryTreeDto> selectTreeNodesButtom(String id);
}
