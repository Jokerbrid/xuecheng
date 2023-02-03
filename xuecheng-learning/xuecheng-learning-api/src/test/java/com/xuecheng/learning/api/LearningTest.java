package com.xuecheng.learning.api;

import com.xuecheng.learning.feignclient.ContentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LearningTest {
    @Autowired
    ContentServiceClient contentServiceClient;

    @Test
    void contextLoads() {
        System.out.println(contentServiceClient.getCoursePublish(2L));
    }

}
