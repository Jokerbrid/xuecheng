package com.xuecheng.context.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryCourseParamDto {

    private String courseName;

    private  String auditStatus;

    private  String publishStatus;

}
