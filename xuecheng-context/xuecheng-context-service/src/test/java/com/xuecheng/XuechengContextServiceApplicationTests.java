package com.xuecheng;

import com.xuecheng.context.dao.CourseBaseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XuechengContextServiceApplicationTests {

    @Autowired
    CourseBaseMapper cBm;
    @Test
    void contextLoads() {
        System.out.println(cBm.selectList(null));
    }

}
