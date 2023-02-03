package com.xuecheng.context.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.model.dto.AddCourseDto;
import com.xuecheng.context.model.dto.CourseBaseInfoDto;
import com.xuecheng.context.model.dto.EditCourseDto;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.service.CourseBaseInfoService;
import com.xuecheng.context.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/***
 *
 */
@Slf4j
@RestController
public class CourseBaseInfoController {
    /**
     * 查询课程相关信息：
     */
    @Autowired
    CourseBaseInfoService courseBaseInfoService;


    //设置授权：
//    @PreAuthorize("hasAuthority('xc_teachmanager_course')")
    //查询所有课程列表名api:
    @PostMapping("/course/list")
    public PageResult<CourseBase> listD(PageParams params, @RequestBody QueryCourseParamDto queryCourseParamDto){
        //取出用户身份：
        SecurityUtil.XcUser user=SecurityUtil.getUser();
        //
        String companyId=user.getCompanyId();

        //调用service获取数据：
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(params,queryCourseParamDto);

        return courseBasePageResult;
    }
    //新建课程api：
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){
        //机构id
//        Long companyId=1232141425L;
        //从token中获取机构id:
        SecurityUtil.XcUser user= SecurityUtil.getUser();
       Long companyId= Long.valueOf(user.getCompanyId());
        //返回：
        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);
    }
    //根据课程id查询课程信息api:
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto queryCourseBase(@PathVariable Long courseId){
        //取出当前用户身份：
        //通过工具类取出身份的完整信息：
        SecurityUtil.XcUser user= SecurityUtil.getUser();
        //
        String companyId= user.getCompanyId();
//        System.out.println(companyId);


        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }
    //修改课程的api:
    @PutMapping("/course")
    public  CourseBaseInfoDto updateCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
//       //机构id: 先采用硬编码实现：
//        Long companyId=1232141425L;
        //从token中获取公司id:
        SecurityUtil.XcUser user= SecurityUtil.getUser();
        //
        Long companyId= Long.valueOf(user.getCompanyId());
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }
}
