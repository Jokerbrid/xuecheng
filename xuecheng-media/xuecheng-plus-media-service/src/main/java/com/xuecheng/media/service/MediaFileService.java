package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadMediaFilesDto;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.IOException;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


 /***
  * //上传文件通用接口：
  * @param companyId 机构id
  * @param uploadFileParamDto 文件信息
  * @param bytes   文件字节数组
  * @param folder
  * @param objectName 对象名
  * @return
  */
 UploadMediaFilesDto uploadFile(Long companyId, UploadFileParamDto uploadFileParamDto, byte[] bytes,String folder,String objectName);


 //检查文件是否存在：
 public RestResponse<Boolean> checkFile(String fileMd5);

 //查询分块存在：
 RestResponse<Boolean> checkchunk(String fileMd5,int chunkIndex);

 //
 RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes) throws IOException;


 RestResponse mergeChunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamDto uploadFileParamDto) throws IOException;


 MediaFiles getFileById(String fileId);

 void deleteMediaFile(String fileId);
}
