package com.xuecheng.context.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import org.springframework.stereotype.Service;

@Service
public interface CourseBaseInfoService {
    /**
     *
     * @param params
     * @param queryCourseParamDto
     * @return
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamDto queryCourseParamDto);
}
