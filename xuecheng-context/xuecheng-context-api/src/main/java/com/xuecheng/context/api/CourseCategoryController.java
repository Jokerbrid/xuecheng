package com.xuecheng.context.api;


import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.model.po.CourseCategory;
import com.xuecheng.context.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j

public class CourseCategoryController {

    @Autowired
    CourseCategoryService categoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodeButtom(@RequestParam("id") String id){
        return categoryService.queryTreeNodesButtom(id);
    }
}
