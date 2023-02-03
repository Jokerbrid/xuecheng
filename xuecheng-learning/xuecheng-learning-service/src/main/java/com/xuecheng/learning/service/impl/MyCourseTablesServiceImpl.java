package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTableDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Component
public class MyCourseTablesServiceImpl implements MyCourseTablesService {
    @Autowired
    ContentServiceClient contentServiceClient;
    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;
    @Autowired
    XcCourseTablesMapper xcCourseTablesMapper;

    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //根据id查询到发布的课程信息：
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        //查看课程收费标准：
        String charge=coursePublish.getCharge();
        XcChooseCourse xcChooseCourse=null;
        if(charge.equals("201001")){
            //添加为收费课程：
            xcChooseCourse = addChargeCourse(userId, coursePublish);
        }else{
            //添加为免费课程：
            xcChooseCourse = addFreeCourse(userId, coursePublish);
        }
        XcChooseCourseDto xcChooseCourseDto=new XcChooseCourseDto();
        BeanUtils.copyProperties(xcChooseCourse,xcChooseCourseDto);
        //获取学习资格：
        XcCourseTableDto xcCourseTableDto = getLearningStatus(userId, courseId);
        xcChooseCourseDto.setLearningStatus(xcCourseTableDto.getLearningStatus());

        return xcChooseCourseDto;
    }

    //获取学习状态：
    @Override
    public XcCourseTableDto getLearningStatus(String userId, Long courseId) {
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if(xcCourseTables==null){
            XcCourseTableDto xcCourseTableDto=new XcCourseTableDto();
            xcCourseTableDto.setLearningStatus("702002");
            return xcCourseTableDto;
        }
        XcCourseTableDto xcCourseTableDto=new XcCourseTableDto();
        BeanUtils.copyProperties(xcCourseTables,xcCourseTableDto);
        //验证是否过期： 过期false,未过期true;
        boolean isExpires=xcCourseTables.getValidtimeEnd().after(new Date());
        if(isExpires){
            //没有过期,正常学习
            xcCourseTableDto.setLearningStatus("702001");
            return xcCourseTableDto;
        }else{
            //已过期：
            xcCourseTableDto.setLearningStatus("702003");
            return xcCourseTableDto;
        }
    }

    @Override
    public boolean saveChooseCourseStatus(String chooseCourseId) {
        //根据chooseCourseId 查询选课记录：
        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectById(chooseCourseId);
        if(xcChooseCourse==null){
            //收到支付结果通知，没有查询到选课记录。

            return false;
        }
        String status = xcChooseCourse.getStatus();
        if("701001".equals(status)){
            //添加到课程表中：
            addCourseTable(xcChooseCourse);
            return true;
        }
        //待支付状态:
        if("701002".equals(status)){
            //更新为选课成功：
            xcChooseCourse.setStatus("701001");
            int update = xcChooseCourseMapper.updateById(xcChooseCourse);
            if(update>0){
                //收到支付结果通知，处理成功：
                addCourseTable(xcChooseCourse);
                return true;
            }else{
                //处理失败：
                return false;
            }
        }
        return false;
    }

    @Override
    public PageResult<XcCourseTables> mycourseTables(MyCourseTableParams params) {
        Integer pageNo = params.getPageNo();
        Integer pageSize=8;
        //分页条件：
        Page<XcCourseTables> page=new Page<>(pageNo,pageSize);

        //查询当前用户的课程信息:
        LambdaQueryWrapper<XcCourseTables> wrapper=new LambdaQueryWrapper<>();
//        wrapper.eq(XcCourseTables::getUserId,params.getUserId());
        //分页查询：
        Page<XcCourseTables> pageResult = xcCourseTablesMapper.selectPage(page, wrapper);
        List<XcCourseTables> records = pageResult.getRecords();
        long total = pageResult.getTotal();
        return  new PageResult<XcCourseTables>(records, total, pageNo, pageSize);
    }

    //添加免费课程：
    @Transactional
    public XcChooseCourse addFreeCourse(String userId,CoursePublish coursePublish){
        LambdaQueryWrapper<XcChooseCourse> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(XcChooseCourse::getUserId,userId)
                .eq(XcChooseCourse::getCourseId,coursePublish.getId())
                .eq(XcChooseCourse::getOrderType,"700001")
                .eq(XcChooseCourse::getStatus,"701001");

        List<XcChooseCourse> xcChooseCourses=xcChooseCourseMapper.selectList(wrapper);
        if(xcChooseCourses!=null&&xcChooseCourses.size()>0){
            //查询出的多条结果取首条：
            return xcChooseCourses.get(0);
        }
        //无结果则添加数据：
        XcChooseCourse xcChooseCourse=new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(0f);//免费课程。
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        xcChooseCourse.setOrderType("700001");
        xcChooseCourse.setStatus("701001");
        xcChooseCourse.setValidDays(coursePublish.getValidDays());
        //
        Date curDate=new Date();
        long endTime=curDate.getTime();     //毫秒时间.
        endTime+=coursePublish.getValidDays()*24*60*60*1000;//计算验证的结束时间，
        xcChooseCourse.setCreateDate(curDate);
        xcChooseCourse.setValidtimeStart(curDate);
        xcChooseCourse.setValidtimeEnd(new Date(endTime));
        //添加到表中：
        xcChooseCourseMapper.insert(xcChooseCourse);


        return xcChooseCourse;
    }

    //添加付费课程：
    @Transactional
    public XcChooseCourse addChargeCourse(String userId,CoursePublish coursePublish){
        LambdaQueryWrapper<XcChooseCourse> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(XcChooseCourse::getUserId,userId)
                .eq(XcChooseCourse::getCourseId,coursePublish.getId())
                .eq(XcChooseCourse::getOrderType,"700002") //收费课程code
                .eq(XcChooseCourse::getStatus,"701002");    //待支付：

        List<XcChooseCourse> xcChooseCourses=xcChooseCourseMapper.selectList(wrapper);
        if(xcChooseCourses!=null&&xcChooseCourses.size()>0){
            //查询出的多条结果取首条：
            return xcChooseCourses.get(0);
        }
        //无结果则添加数据：
        XcChooseCourse xcChooseCourse=new XcChooseCourse();
        xcChooseCourse.setCourseId(coursePublish.getId());
        xcChooseCourse.setCourseName(coursePublish.getName());
        xcChooseCourse.setCoursePrice(coursePublish.getPrice());//收费价格
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursePublish.getCompanyId());
        xcChooseCourse.setOrderType("700002");
        xcChooseCourse.setStatus("701002");
        xcChooseCourse.setValidDays(coursePublish.getValidDays());
        //
        Date curDate=new Date();
        long endTime=curDate.getTime();
        endTime+=coursePublish.getValidDays()*24*60*60*1000;//计算验证的结束时间，
        xcChooseCourse.setCreateDate(curDate);
        xcChooseCourse.setValidtimeStart(curDate);
        xcChooseCourse.setValidtimeEnd(new Date(endTime));
        //添加到表中：
        xcChooseCourseMapper.insert(xcChooseCourse);

        return xcChooseCourse;
    }

    //添加到我的课程表：
    @Transactional
    public XcCourseTables addCourseTable(XcChooseCourse xcChooseCourse){
        //线课记录为完成且未完成可以添加课程到课程表
        String status=xcChooseCourse.getStatus();
        if(!"701001".equals(status)){
            xueChengException.cast("选课记录未完成,无法添加到课程表");
        }
        //查询课程表：
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if(xcCourseTables!=null){
            //数据库的时间：
            Date validtimeEnd = xcCourseTables.getValidtimeEnd();
            //if 数据库时间 after 当前时间
            if(validtimeEnd.after(xcChooseCourse.getValidtimeEnd())){
                //
                return xcCourseTables;

            }else{
                //更新我的课程表：
                xcCourseTables.setChooseCourseId(xcChooseCourse.getCourseId());
                xcCourseTables.setUpdateDate(new Date());
                xcCourseTables.setValidtimeStart(xcCourseTables.getValidtimeStart());
                xcCourseTables.setValidtimeEnd(xcCourseTables.getValidtimeEnd());
                xcCourseTables.setCourseType(xcChooseCourse.getOrderType());
                xcCourseTablesMapper.updateById(xcCourseTables);
                return xcCourseTables;
            }
        }
        //课程表中无选课，则添加：
        XcCourseTables xcCourseTablesNew=new XcCourseTables();
        xcCourseTablesNew.setChooseCourseId(xcChooseCourse.getCourseId());
        xcCourseTablesNew.setUserId(xcChooseCourse.getUserId());
        xcCourseTablesNew.setCourseId(xcChooseCourse.getId());
        xcCourseTablesNew.setCompanyId(xcChooseCourse.getCompanyId());
        xcCourseTablesNew.setCourseName(xcChooseCourse.getCourseName());
        xcCourseTablesNew.setCreateDate(new Date());
        xcCourseTablesNew.setValidtimeStart(xcChooseCourse.getValidtimeStart());
        xcCourseTablesNew.setValidtimeEnd(xcChooseCourse.getValidtimeEnd());
        xcCourseTablesNew.setCourseType(xcCourseTables.getCourseType());

        xcCourseTablesMapper.insert(xcCourseTablesNew);
        return xcCourseTablesNew;
    }
    //查询课程表信息：
    public XcCourseTables getXcCourseTables(String userId,Long courseId){
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(
                new LambdaQueryWrapper<XcCourseTables>()
                        .eq(XcCourseTables::getUserId,userId)
                        .eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;
    }
}
