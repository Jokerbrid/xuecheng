package com.xuecheng.context.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.context.dao.TeachplanMapper;
import com.xuecheng.context.dao.TeachplanMediaMapper;
import com.xuecheng.context.model.dto.BindTeachplanMediaDto;
import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.model.dto.SaveTeachplanDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.model.po.Teachplan;
import com.xuecheng.context.model.po.TeachplanMedia;
import com.xuecheng.context.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> queryTreeNodes(Long courseId) {
        return teachplanMapper.getTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto dto) {
        Long id=dto.getId();
        //修改课程计划：
        if(id!=null){
            Teachplan teachplan=teachplanMapper.selectById(id);
            BeanUtils.copyProperties(dto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
         //取出同父级的数量，并将新插入的计划排序到其后面。
            int count=getTeachCount(dto.getCourseId(),dto.getParentid());
            Teachplan teachplan=new Teachplan();
            teachplan.setOrderby(count+1);
            BeanUtils.copyProperties(dto,teachplan);
            teachplanMapper.insert(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long id) {
        teachplanMapper.deleteById(id);
    }

    @Override
    public void moveupTeachplan(Long techplanId) {
        //拿到当前的计划：
        Teachplan left = teachplanMapper.selectById(techplanId);
        //获取其上一个计划：
        LambdaQueryWrapper<Teachplan> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId,left.getCourseId())
                .eq(Teachplan::getGrade,left.getGrade())
                .eq(Teachplan::getParentid,left.getParentid())
                .eq(Teachplan::getOrderby,left.getOrderby()-1);
        Teachplan right = teachplanMapper.selectOne(wrapper);
        //交换序号：
        swapTeachplan(left,right);

    }

    @Override
    public void movedownTeachplan(Long teachplanId) {
        //拿到当前的计划：
        Teachplan left = teachplanMapper.selectById(teachplanId);
        //获取其上一个计划：
        LambdaQueryWrapper<Teachplan> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId,left.getCourseId())
                .eq(Teachplan::getGrade,left.getGrade())
                .eq(Teachplan::getParentid,left.getParentid())
                .eq(Teachplan::getOrderby,left.getOrderby()+1);
        Teachplan right = teachplanMapper.selectOne(wrapper);
        //交换序号：
        swapTeachplan(left,right);
    }

    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //根据教学计划id,查询教学计划表：
        Long teachplanId=bindTeachplanMediaDto.getTeachplanId();

        //查询是否存在教学计划：
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            xueChengException.cast("教学计划不存在");
        }
        Integer grade=teachplan.getGrade();
        if(grade!=2){
            xueChengException.cast("只允许二级教学计划绑定媒资文件");
        }
        //根据表单拿到课程id:
        Long courseId=teachplan.getCourseId();
        //删除原先该计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));
        //添加新的绑定计划：
        TeachplanMedia teachplanMedia=new TeachplanMedia();
        //
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setCreateDate(new Date());

        //插入课程计划媒资表：
        teachplanMediaMapper.insert(teachplanMedia);
        //返回结果：
        return teachplanMedia;
    }

    @Override
    public void delAassociationMedia(Long teachplanId, String mediaId) {
        teachplanMediaMapper.delete(
                new LambdaQueryWrapper<TeachplanMedia>()
                    .eq(TeachplanMedia::getTeachplanId,teachplanId)
                    .eq(TeachplanMedia::getMediaId,mediaId));
    }

    //用于查询同级的计划数量
    private int getTeachCount(long courseId,long parentid){
        LambdaQueryWrapper<Teachplan> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Teachplan::getCourseId,courseId);
        wrapper.eq(Teachplan::getParentid,parentid);
        Integer count=teachplanMapper.selectCount(wrapper);
        return count;
    }

    private void swapTeachplan(Teachplan left,Teachplan right){
        if(left==null || right==null){
            return ;
        }
        Integer orderby_left = left.getOrderby();
        Integer orderby_right = right.getOrderby();
        left.setOrderby(orderby_right);
        right.setOrderby(orderby_left);
        teachplanMapper.updateById(left);
        teachplanMapper.updateById(right);
        log.debug("课程计划交换位置，left:{},right:{}",left.getId(),right.getId());
    }

}
