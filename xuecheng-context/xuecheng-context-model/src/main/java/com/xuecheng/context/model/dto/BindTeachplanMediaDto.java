package com.xuecheng.context.model.dto;

import lombok.Data;

/***
 * 用于绑定媒资和教学资源的dto：
 */
@Data
public class BindTeachplanMediaDto {

    private String fileName;

    private String mediaId;

    private Long teachplanId;
}
