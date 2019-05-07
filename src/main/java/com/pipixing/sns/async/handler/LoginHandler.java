package com.pipixing.sns.async.handler;

import com.pipixing.sns.async.EventHandler;
import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

//可以实现一个检测异地登陆，并异步发送邮件给注册邮箱的操作，待扩展
@Component
public class LoginHandler implements EventHandler {

    @Override
    public void doHandler(EventModel eventModel){

    }

    @Override
    public List<EventType> getSupportEventTypes(){return Arrays.asList(EventType.LOGIN);
    }
}
