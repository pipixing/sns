package com.pipixing.sns.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pipixing.sns.utils.JedisAdapter;
import com.pipixing.sns.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //通过Map实现最简单的消息分发
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    //获得当前队列里的EventModel
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //找到EventHandler所有的实现类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //每个EventHandler所能支持的EventType种类
                List<EventType> EventTypes = entry.getValue().getSupportEventTypes();
                for (EventType eventType : EventTypes) {
                    if (!config.containsKey(eventType))
                        config.put(eventType, new ArrayList<>());
                    //将每种EventType与其被支持的EventHandler联系起来
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        //开始多线程，线程会一直从队列里取EventModel
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //从MQ里取Event
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0, key);
                    //解析取出的Event
                    for (String message : messages) {
                        //第一个为key值
                        if (message.equals(key))
                            continue;
                        //Json串转EventModel类型
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            System.out.println("无法处理的事件" + eventModel.getType());
                            logger.error("出现无法处理的事件");
                            continue;
                        }
                        //找到对应的EventType处理类型Handler列表，按顺序进行事件的处理
                        for (EventHandler handler : config.get(eventModel.getType()))
                            handler.doHandler(eventModel);
                    }
                }
            }
        });
        //别忘了开启线程
        thread.start();


    }


}
