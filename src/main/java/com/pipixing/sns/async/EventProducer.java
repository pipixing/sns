package com.pipixing.sns.async;

import com.alibaba.fastjson.JSONObject;
import com.pipixing.sns.utils.JedisAdapter;
import com.pipixing.sns.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean pushEventToMQ(EventModel eventModel){
        try {
            String eventQueueKey = RedisKeyUtil.getEventQueueKey();
            String json = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(eventQueueKey,json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
