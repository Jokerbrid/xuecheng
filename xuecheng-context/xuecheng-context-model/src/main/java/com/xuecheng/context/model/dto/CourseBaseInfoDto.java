package com.xuecheng.context.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class CourseBaseInfoDto {
    //结果Dto
    private Long id;
    private Long companyId;
    private String name;
    private String users;
    private String mt;
    private String mtName;
    private String st;
    private String stName;
    private String grade;
    private String teachmode;
    private String description;
    private String pic;
    private Date createDate;
    private Date changeDate;
    private String createPeople;
    private String changePeople;
    private String auditStatus;
    private String coursePubId;
    private Float price;
    private String charge;
}
