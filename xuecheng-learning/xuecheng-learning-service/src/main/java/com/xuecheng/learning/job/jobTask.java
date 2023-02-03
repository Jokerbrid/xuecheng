package com.xuecheng.learning.job;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class jobTask extends MessageProcessAbstract {
    @Override
    public boolean execute(MqMessage mqMessage) {
        return false;
    }
}
