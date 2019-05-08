package com.pipixing.sns.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.pipixing.sns.async.EventHandler;
import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventType;
import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.model.Feed;
import com.pipixing.sns.model.Question;
import com.pipixing.sns.model.User;
import com.pipixing.sns.service.FeedService;
import com.pipixing.sns.service.FollowService;
import com.pipixing.sns.service.QuestionService;
import com.pipixing.sns.service.UserService;
import com.pipixing.sns.utils.JedisAdapter;
import com.pipixing.sns.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel eventModel){
        Map<String,String> map = new HashMap<>();
        User actor = userService.getUser(eventModel.getActorId());
        if(actor==null)
            return null;
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHead_url());
        map.put("userName", actor.getName());
        //只关注followee发表评论和关注新闻问题的动态，关注User则不算
        if(eventModel.getType()==EventType.COMMENT||
                (eventModel.getType()==EventType.FOLLOW&&eventModel.getEntityType()== EntityType.ENTITY_QUESTION)){
            Question question = questionService.selectQuestion(eventModel.getEntityId());
            if (question == null) {
                return null;
            }
            // 往map里装问题信息
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandler(EventModel eventModel){
        Feed feed = new Feed();
        //feed代指的是一种行为，行为只能由一个触发者来触发
        feed.setType(eventModel.getType().getValue()).setCreateDate(new Date())
                .setUserId(eventModel.getActorId()).setData(buildFeedData(eventModel));
        if(feed.getData()==null)
            return;
        //把生成的新鲜事推入到MySQL,用于实现拉的功能
        feedService.addFeed(feed);
        //获取所有的粉丝
        List<Integer> followers = followService.getFollowerList(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        // 给未登录用户也推送
        followers.add(0);
        // 给所有粉丝推事件
        for (int follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            System.out.println("chuxianle");
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes(){return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});}
}
