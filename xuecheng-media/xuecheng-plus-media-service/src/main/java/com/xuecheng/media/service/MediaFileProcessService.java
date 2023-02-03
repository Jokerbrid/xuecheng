package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MediaFileProcessService {

    @Transactional
    public void saveProcessFinishStatus(int status,String fileId, String url,String errorMsg);


    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

}
