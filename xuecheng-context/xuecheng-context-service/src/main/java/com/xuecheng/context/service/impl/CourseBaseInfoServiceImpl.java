package com.xuecheng.context.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.context.dao.CourseBaseMapper;
import com.xuecheng.context.dao.CourseCategoryMapper;
import com.xuecheng.context.dao.CourseMarketMapper;
import com.xuecheng.context.model.dto.AddCourseDto;
import com.xuecheng.context.model.dto.CourseBaseInfoDto;
import com.xuecheng.context.model.dto.EditCourseDto;
import com.xuecheng.context.model.dto.QueryCourseParamDto;
import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.model.po.CourseCategory;
import com.xuecheng.context.model.po.CourseMarket;
import com.xuecheng.context.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Autowired
    CourseBaseMapper cBM;
    @Autowired
    CourseMarketMapper cMM;
    @Autowired
    CourseCategoryMapper CCM;
    @Autowired
    CourseMarketServiceImpl courseMarketService;

    @Override
    @Transactional(readOnly = true)
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamDto queryCourseParamDto) {
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();

        //模糊查询 name有关的信息：
        //like '%name%'
        wrapper.like(StringUtils.isNotBlank(queryCourseParamDto.getCourseName()),CourseBase::getName,queryCourseParamDto.getCourseName());
        //审核状态： where status=''
        wrapper.like(StringUtils.isNotBlank(queryCourseParamDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamDto.getAuditStatus());
        //发布状态：
        wrapper.like(StringUtils.isNotBlank(queryCourseParamDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamDto.getPublishStatus());

        //根据课程发布状态：

        //分页：
        Page<CourseBase> page=new Page<>(params.getPageNo(), params.getPageSize());
        Page<CourseBase> pageResult = cBM.selectPage(page, wrapper);
        List<CourseBase> items =  pageResult.getRecords();
        long total = pageResult.getTotal();
        //返回数据：
       return  new PageResult<CourseBase>(items,total, params.getPageNo(), params.getPageSize());

    }

    @Override
    //事务的完整性：
    @Transactional(rollbackFor = xueChengException.class)
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //判断非法条件：
        if(StringUtils.isBlank(dto.getName())){
            xueChengException.cast("课程名程为空");
        }
        //_____________________________

        //新增对象：
        CourseBase courseBaseNew= new CourseBase();
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态: 未提交
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态： 未发布
        courseBaseNew.setStatus("203001");
        //机构的id
        courseBaseNew.setCompanyId(companyId);
        //添加时间：
        courseBaseNew.setCreateDate(new Date());
        //插入表的基本信息:
        //___________________________________

        int insert=cBM.insert(courseBaseNew);
        //查询课程id:
        CourseBase getIdCourseBase=cBM.selectById(courseBaseNew);

        Long courseId=getIdCourseBase.getId();
        //课程营销信息：
        CourseMarket courseMarketNew=new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        //收费规则：
        String charge=dto.getCharge();
        //判断合法性：
        if(charge.equals("201001")){
            Float price=dto.getPrice();
            if(price==null||price.floatValue()<=0){
                xueChengException.cast("收费价格不能为空");
            }
        }
        //插入营销表的信息：
        int insert2=cMM.insert(courseMarketNew);

        //判断是否添加成功：
        if(insert<=0||insert2<=0){
            xueChengException.cast("新增课程失败");
        }
        //添加成功，返回：
        return getCourseBaseInfo(courseId);
    }

    @Override
    //根据课程id查询课程基本信息：
    @Transactional(readOnly = true)
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase=cBM.selectById(courseId);
        //根据课程id查询结果：
        LambdaQueryWrapper<CourseMarket> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseMarket::getId,courseId);
        CourseMarket courseMarket=cMM.selectOne(queryWrapper);
        //判断合法条件：
        if(courseBase == null){
            return null;
        }
        //
        CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);


        if(courseMarket!=null){
            courseBaseInfoDto.setPrice(courseMarket.getPrice());
            courseBaseInfoDto.setCharge(courseMarket.getCharge());
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
            //查询分类信息：
            CourseCategory courseCategoryBySt=CCM.selectById(courseBase.getSt());
            courseBaseInfoDto.setStName(courseCategoryBySt.getName());
            //
            CourseCategory courseCategoryByMs=CCM.selectById(courseBase.getMt());
            courseBaseInfoDto.setMtName(courseCategoryByMs.getName());
        }

        return courseBaseInfoDto;
    }


    //更新表：
    @Override
    @Transactional(rollbackFor = xueChengException.class)//回滚事务
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //课程id;
        Long courseId=dto.getId();
        CourseBase courseBaseUpdate=cBM.selectById(courseId);

        //
        if(!companyId.equals(courseBaseUpdate.getCompanyId())){
            xueChengException.cast("只允许修改本机构的课程");
        }

        BeanUtils.copyProperties(dto,courseBaseUpdate);
        //插入修改时间：
        courseBaseUpdate.setChangeDate(new Date());

        //更新课程基本表：
        int res1=cBM.updateById(courseBaseUpdate);
        if(res1<=0){
            xueChengException.cast("修改课程基本表失败");
        }

        //查询营销信息表：
        CourseMarket courseMarket=cMM.selectById(courseId);
        if(courseMarket==null){
            courseMarket=new CourseMarket();
        }
        //判断是否收费：
        String charge=dto.getCharge();
        if(charge.equals("201001")){
            Float price=dto.getPrice();
            if(price==null||price.floatValue()<0){
                xueChengException.cast("课程收费不能为空或者小于零");
            }
        }

        //将修改的内容拷贝到courseMarket;
        BeanUtils.copyProperties(dto,courseMarket);
        //插入或者更新营销表：
        int res2=saveCourseMarket(courseMarket);
        if(res2==-1){
            xueChengException.cast("课程营销表添加或者更新失败");
        }
        //返回更新结果:
        return getCourseBaseInfo(courseId);
    }

    @Override
    @Transactional(rollbackFor = xueChengException.class)//回滚事务
    public int saveCourseMarket(CourseMarket courseMarket) {
        String charge= courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            xueChengException.cast("请设置收费规则");
        }
        if(charge.equals("201001")){
            Float price=courseMarket.getPrice();
            if(price==null||price.floatValue()<0){
                xueChengException.cast("课程收费不能为空或者小于零");
            }
        }
        boolean b=courseMarketService.saveOrUpdate(courseMarket);
        return b?1:-1;
    }


}
