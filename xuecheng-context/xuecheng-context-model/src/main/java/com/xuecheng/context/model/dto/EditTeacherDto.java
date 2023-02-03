package com.xuecheng.context.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EditTeacherDto extends AddTeacherDto{
    private Date CreateDate;
    private Long id;
}
