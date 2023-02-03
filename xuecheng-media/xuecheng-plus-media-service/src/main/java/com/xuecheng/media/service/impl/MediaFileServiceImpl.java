package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadMediaFilesDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @description TODO
 * @author Mr.M
 * @date 2022/9/10 8:58
 * @version 1.0
 */
 @Component
public class MediaFileServiceImpl implements MediaFileService {

  @Autowired
  MediaFilesMapper mediaFilesMapper;

  @Autowired
 MediaProcessMapper mediaProcessMapper;


  @Override
  public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  queryWrapper.like(StringUtils.isNotBlank(queryMediaParamsDto.getFileType()),MediaFiles::getFileType,queryMediaParamsDto.getFileType());
  queryWrapper.like(StringUtils.isNotBlank(queryMediaParamsDto.getFilename()),MediaFiles::getFilename,queryMediaParamsDto.getFilename());
  queryWrapper.eq(StringUtils.isNotBlank(queryMediaParamsDto.getAuditStatus()),MediaFiles::getAuditStatus,queryMediaParamsDto.getAuditStatus());
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }

 @Override
 public UploadMediaFilesDto uploadFile(Long companyId, UploadFileParamDto uploadFileParamDto, byte[] bytes, String folder, String objectName) {
  //预留文件系统的接口：

  //得到文件的md5值：
  String md5code = DigestUtils.md5DigestAsHex(bytes);

  if(StringUtils.isBlank(folder)){
   //自动生成目录：
   folder="E:/joker/data/xuecheng/media/";
  }
  String fileName=uploadFileParamDto.getFilename();
  if(StringUtils.isBlank(objectName)){
   //使用md5值为objectName
   objectName=fileName;
  }
  //------------------------
  //文件类型：
  String fileType=uploadFileParamDto.getFileType();
  String bucket="";
  if(fileType.equals("001001")){
   bucket="mediafile";
  }else bucket="video";
  //上传到数据库：
  MediaFiles mediaFiles=addMediaFilesToDb(md5code,companyId,uploadFileParamDto,bucket,folder,objectName);
  UploadMediaFilesDto dto=new UploadMediaFilesDto();
  BeanUtils.copyProperties(mediaFiles,dto);
  return dto;
 }

 @Override
 public RestResponse<Boolean> checkFile(String fileMd5) {
  MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
  if(mediaFiles!=null){
   String bucket=mediaFiles.getBucket();
   //存储目录：
   String fillPath=mediaFiles.getFilePath();
   //读取文件路径：
   File file = new File(fillPath);
   if(file.exists()){//文件存在：
    return RestResponse.success(true);
   }
  }
  return RestResponse.success(false);
 }

 @Override
 public RestResponse<Boolean> checkchunk(String fileMd5, int chunkIndex) {
  File file=new File("E:\\joker\\data\\xuecheng\\media\\"+fileMd5+"-"+chunkIndex);
  if(file.exists()){
   return RestResponse.success(true);
  }
  return RestResponse.success(false);
 }

 @Override
 public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) throws IOException {

  File fileDir=new File("E:\\joker\\data\\xuecheng\\media\\"+fileMd5);
  if(!fileDir.exists()){
    fileDir.mkdir();
  }
  File file=new File("E:\\joker\\data\\xuecheng\\media\\"+fileMd5+"\\"+chunk);
  if(!file.exists()){
   file.createNewFile();
  }
  FileOutputStream fls=new FileOutputStream(file);
  fls.write(bytes);
  fls.close();
  return RestResponse.success(true);
 }

 @Override
 public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamDto uploadFileParamDto) throws IOException {
  File[] chunkFiles=checkchunkStatus(fileMd5,chunkTotal);
  String fileName=uploadFileParamDto.getFilename();
  //合并分块的文件名：
  String url="E:\\joker\\data\\xuecheng\\media\\";
  File margeFile=new File(url+fileName);
  if(!margeFile.exists()){
   margeFile.createNewFile();
  }
  byte[]b=new byte[1024];
  FileOutputStream fileOutputStream=new FileOutputStream(margeFile);
  for(File chunkFile: chunkFiles){
   //分块文件：
    FileInputStream fileInputStream=new FileInputStream(chunkFile);
    int len=-1;
    while((len=fileInputStream.read(b))!=-1){
     //向合并的文件写入：
     fileOutputStream.write(b,0,len);
    }
    fileInputStream.close();
  }
  fileOutputStream.close();

  //信息拷贝到dto:
  uploadFileParamDto.setFileSize(margeFile.length());
  //存入数据库：
  MediaFiles mediaFiles=addMediaFilesToDb(fileMd5,companyId,uploadFileParamDto,"video",url,fileName);
  //删除临时文件：
  for(File file1:chunkFiles){
    file1.delete();
  }
  File file=new File(url+fileMd5);
  file.delete();

  return RestResponse.success();
 }

 @Override
 public MediaFiles getFileById(String fileId) {
 return mediaFilesMapper.selectById(fileId);
 }

 @Override
 @Transactional
 public void deleteMediaFile(String fileId) {
  MediaFiles mediaFile = mediaFilesMapper.selectById(fileId);

  int res=mediaFilesMapper.deleteById(fileId);
  if(res<=0){
   xueChengException.cast("删除失败");
  }
  File file=new File(mediaFile.getUrl());
  file.delete();
 }

 //验证所有分块是否上传完毕：
 private File[] checkchunkStatus(String fileMd5,int chunkTotal){
   File[] files=new File[chunkTotal];
   for(int i=0;i<chunkTotal;i++){
     File chunkFile=new File("E:\\joker\\data\\xuecheng\\media\\"+fileMd5+"\\"+i);
     if(chunkFile.exists()){
      files[i]=chunkFile;
     }else{
      xueChengException.cast("分块不完整");
     }
   }
   return files;
 }

 //图片存入数据库事务：
 @Transactional(rollbackFor = {xueChengException.class})
 public MediaFiles addMediaFilesToDb(String md5code,Long companyId, UploadFileParamDto uploadFileParamDto,String bucket,String folder, String objectName){
  //保存到数据库：
  //封装数据：
   MediaFiles mediaFiles =new MediaFiles();
   BeanUtils.copyProperties(uploadFileParamDto,mediaFiles);
   mediaFiles.setId(md5code);
   mediaFiles.setFileId(md5code);
   mediaFiles.setCompanyId(companyId);
   mediaFiles.setFilename(uploadFileParamDto.getFilename());
   mediaFiles.setBucket(bucket);
   mediaFiles.setUrl(folder+objectName);
   mediaFiles.setFilePath(objectName);
   mediaFiles.setCreateDate(new Date());
   mediaFiles.setStatus("1");
   mediaFiles.setAuditStatus("002003");
   //写入：
   int res=mediaFilesMapper.insert(mediaFiles);
   if(res<=0){
    int result = mediaFilesMapper.updateById(mediaFiles);
    if(result<=0 ||res<=0){
     xueChengException.cast("插入或者修改媒资数据库失败");
    }
   }
   //如果为avi格式:
   String extension=objectName.substring(objectName.lastIndexOf("."));
   //转换编码为mp4:
   if(extension.equalsIgnoreCase(".avi")){
    MediaProcess mediaProcess=new MediaProcess();
    BeanUtils.copyProperties(mediaFiles,mediaProcess);
    mediaProcessMapper.insert(mediaProcess);
    //更新url为空：
    mediaFiles.setUrl(null);
    mediaFilesMapper.updateById(mediaFiles);
   }
   return mediaFiles;
 }


}
