package com.xuecheng.context.service;

import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CourseCategoryService {


    public List<CourseCategoryTreeDto> queryTreeNodesButtom(String id);
}
