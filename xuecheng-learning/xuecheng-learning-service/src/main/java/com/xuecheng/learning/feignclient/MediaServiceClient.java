package com.xuecheng.learning.feignclient;

import com.xuecheng.base.model.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "xuecheng-media-service",fallbackFactory = MediaServiceClientFallbackFactory.class)
public interface MediaServiceClient {

    @GetMapping("/preview/{fileId}")
    public RestResponse<String> previewByFileId(@PathVariable String fileId);
}
