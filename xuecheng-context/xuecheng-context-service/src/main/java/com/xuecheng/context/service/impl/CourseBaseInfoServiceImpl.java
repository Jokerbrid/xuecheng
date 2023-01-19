package com.xuecheng.context.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.dao.CourseBaseMapper;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper cBM;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamDto queryCourseParamDto) {
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();

        //模糊查询 name有关的信息：
        //like '%name%'
        wrapper.like(StringUtils.isNotEmpty(queryCourseParamDto.getName()),CourseBase::getName,queryCourseParamDto.getName());
        //审核状态： where status=' '
        wrapper.eq(StringUtils.isNotEmpty(queryCourseParamDto.getAudistate()),CourseBase::getAudistate,queryCourseParamDto.getAudistate());
        //根据课程发布状态：

        //分页：
        Page<CourseBase> page=new Page<>(params.getPage(), params.getPageSize());
        Page<CourseBase> pageResult = cBM.selectPage(page, wrapper);
        List<CourseBase> items =  pageResult.getRecords();
        long total = pageResult.getTotal();
        //返回数据：
       return  new PageResult<CourseBase>(items,total, params.getPage(), params.getPageSize());

    }
}
