package com.xuecheng.media.jobHandler;

import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@Slf4j
public class VediolJob extends MessageProcessAbstract {

    @Autowired
    private MediaFileProcessService mediaFileProcessService;
    @Autowired
    private MediaFileService mediaFileService;


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void JobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);

        //一次任务取出2条记录进行处理：
        List<MediaProcess> mediaProcesses = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, 2);
        //
        int size=mediaProcesses.size();
        log.debug("取出待处理视频记录"+size+"条");
        if(size<=0)return;
        //开启线程池进行任务：
        ExecutorService threadPool= Executors.newFixedThreadPool(size);
        //
        mediaProcesses.forEach(mediaProcess -> {
            threadPool.execute(()->{
                String bucket= mediaProcess.getBucket();
                //存储路径：
                String filePath=mediaProcess.getFilePath();
                //原始md5值：
                String fileId=mediaProcess.getFileId();
                //原始文件名：
                String fileName=mediaProcess.getFilename();
                //原始视频文件：
                File file=new File(filePath);

                //处理视频并转换格式：使用工具类将avi->mp4;

                //将新格式的视频上传到指定目录：

                //保存数据，并将待处理记录删除，存入历史记录：
                mediaFileProcessService.saveProcessFinishStatus(2,fileId,null,null);
            });
        });
    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        return false;
    }
}
