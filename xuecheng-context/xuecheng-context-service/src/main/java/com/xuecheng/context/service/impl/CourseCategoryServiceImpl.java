package com.xuecheng.context.service.impl;


import com.xuecheng.context.dao.CourseCategoryMapper;
import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@Component
@Slf4j

public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper categoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodesButtom(String id) {
        //查询数据库得到课程分类：
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = categoryMapper.selectTreeNodesButtom(id);
        //最终返回的list：
        List<CourseCategoryTreeDto> categoryTreeDtos=new ArrayList<>();
        HashMap<String,CourseCategoryTreeDto> map=new HashMap<>();
        courseCategoryTreeDtos.stream().forEach(item->{
//            System.out.println(item);
            //将全部结果写入map
            map.put(item.getId(),item);
            //将根节点以下的节点放入list:
            if(item.getParentid().equals(id)){
                categoryTreeDtos.add(item);
            }
            CourseCategoryTreeDto courseCategoryTreeDto=map.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                if(courseCategoryTreeDto.getChildrenTreeNodes()==null){
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList());
                }
                //向节点的下级节点list加入节点：
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}
