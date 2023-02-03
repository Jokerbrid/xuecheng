package com.xuecheng.context.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.context.model.po.TeachplanMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author joker
 * @since 2023-01-28
 */
@Mapper
public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {

    @Select("select * from teachplan_media where teachplan_id = #{id}")
    TeachplanMedia getMedia(Long id);
}
