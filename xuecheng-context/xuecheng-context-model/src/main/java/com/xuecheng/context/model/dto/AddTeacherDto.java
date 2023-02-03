package com.xuecheng.context.model.dto;

import lombok.Data;

@Data
public class AddTeacherDto {
    private Long courseId;
    private String introduction;
    private String position;
    private String teacherName;

    private String photograph;
}
