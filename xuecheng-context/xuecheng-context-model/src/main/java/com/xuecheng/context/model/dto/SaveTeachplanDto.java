package com.xuecheng.context.model.dto;

import lombok.Data;

@Data

public class SaveTeachplanDto {

    private Long id;

    private Long courseId;
    private Long parentid;//
    private Integer grade; //层级
    private String pname;//课程计划名

    private String mediaType;

    //发布标识：
    private Long coursePubId;
    //是否支持提前阅览：

    private String isPreview;
}
