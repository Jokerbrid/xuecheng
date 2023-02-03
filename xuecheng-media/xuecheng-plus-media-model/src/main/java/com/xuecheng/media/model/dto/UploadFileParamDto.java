package com.xuecheng.media.model.dto;

import lombok.Data;

@Data
public class UploadFileParamDto {

    private String filename;

    private String contentType;

    private String fileType;

    private Long fileSize;

    private String tags;

    //上传人
    private String username;

    //备注
    private String remark;


}
