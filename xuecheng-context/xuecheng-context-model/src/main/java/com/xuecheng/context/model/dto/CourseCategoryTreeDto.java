package com.xuecheng.context.model.dto;

import com.xuecheng.context.model.po.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List  childrenTreeNodes;

}
