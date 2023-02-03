package com.xuecheng.learning.api;


import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.learning.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j

public class MyCourseTablesController {
    @Autowired
    MyCourseTablesService myCourseTablesService;

    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId){

        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null){
            xueChengException.cast("请登录后继续选课");
        }
        String userId=user.getId();
        return myCourseTablesService.addChooseCourse(userId,courseId);
    }

    @GetMapping("/r/mycoursetables")
    public PageResult<XcCourseTables> mycourseTables(@RequestBody MyCourseTableParams params){

        return myCourseTablesService.mycourseTables(params);
    }
}
