package com.xuecheng.media.api;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamDto;
import com.xuecheng.media.model.dto.UploadMediaFilesDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */

@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;
    //上传图片：
    @PostMapping("/files")
    public PageResult<MediaFiles> list(@RequestParam(required = false) PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
  Long companyId = 1232141425L;
  if(pageParams!=null) {
      //拿到分页参数，执行分页服务：
      return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
  }
  //否则进行简单的模糊查询：
  return mediaFileService.queryMediaFiels(companyId,new PageParams(1,100),queryMediaParamsDto);
 }

    //上传视频：


    @PostMapping(value = "/upload/coursefile",consumes ={MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadMediaFilesDto upload(@RequestPart("filedata") MultipartFile file,
                                   @RequestParam(value = "folder",required = false) String folder,
                                   @RequestParam(value = "objectName",required = false) String objectName) throws IOException {
     //获取文件字节数组
     byte[] bytes=file.getBytes();
     UploadFileParamDto dto=new UploadFileParamDto();
    //获取头类型：
     dto.setContentType(file.getContentType());
    //
     if(dto.getContentType().indexOf("image")>=0){
         //是图片：
         dto.setFileType("001001");
     }else dto.setFileType("001003");

     dto.setFilename(file.getOriginalFilename());
     //
     dto.setFileSize(file.getSize());
     //判断是否为空
     boolean empty=file.isEmpty();
     //上传到文件路径：
     file.transferTo(new File("E:\\joker\\data\\xuecheng\\media\\"+file.getOriginalFilename()));

     //公司id采用硬编码：
     Long companyId=1232141425L;

     //调用服务：
     if(folder!=null){
         if(objectName!=null){
             return mediaFileService.uploadFile(companyId,dto,bytes,folder,objectName);
         }
         return mediaFileService.uploadFile(companyId,dto,bytes,folder,null);
     }
     return mediaFileService.uploadFile(companyId,dto,bytes,null,null);
 }


    //
    @PostMapping(value ="/upload/checkfile")
    public RestResponse<Boolean> cheackfile(@RequestParam("fileMd5")String fileMd5){

         return mediaFileService.checkFile(fileMd5);
    }
    //验证块：
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5")String fileMd5,
                                            @RequestParam("chunk")int chunk){

        return mediaFileService.checkchunk(fileMd5,chunk);
    }
    //上传块：
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws IOException {
        return mediaFileService.uploadChunk(fileMd5,chunk,file.getBytes());
    }
    //合并整体文件：
    @PostMapping("/upload/mergechunks")
    public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws IOException {
        Long companyId=1232141425L;
        UploadFileParamDto dto=new UploadFileParamDto();
        dto.setFilename(fileName);
        return mediaFileService.mergeChunks(companyId,fileMd5,chunkTotal,dto);
    }

    //预览图片视频：
    @GetMapping("/preview/{fileId}")
    public RestResponse<String> previewByFileId(@PathVariable String fileId){
        MediaFiles mediaFile = mediaFileService.getFileById(fileId);
        if(mediaFile==null || StringUtils.isBlank(mediaFile.getUrl())){
            xueChengException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFile.getUrl());

    }


    @DeleteMapping("/{fileId}")
    public void Delete(@PathVariable String fileId){
        mediaFileService.deleteMediaFile(fileId);
    }

}
