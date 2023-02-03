package com.xuecheng.context.model.dto;

import com.xuecheng.context.model.po.CourseBase;
import com.xuecheng.context.model.po.CourseMarket;
import lombok.Data;

@Data
public class AddCourseDto {

    private String charge;

    private Float price;

    private Integer originalPrice;

    private String qq;
    private  String wechat;
    private  String phone;
    private  Integer validDays;

    private String mt;
    private String st;
    private String name;
    private String pic;

    private  String grade;
    private  String teachmode;

    private  String users;

    private  String tags;
    private String description;
    private String Objectives;

}
