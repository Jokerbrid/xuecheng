package com.xuecheng.context.model.po;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_base")
public class CourseBase {
    @TableId
    private Integer id;

    private String company_id;

    private String company_name;

    private String name;

    private String users;

    private String classific;

    private String teachmode;

    private String createPeople;

    private String changePeople;

    private  String audistate;

    private String createDate;
}
