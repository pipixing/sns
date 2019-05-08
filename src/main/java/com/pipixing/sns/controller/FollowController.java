package com.pipixing.sns.controller;

import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventProducer;
import com.pipixing.sns.async.EventType;
import com.pipixing.sns.model.*;
import com.pipixing.sns.service.CommentService;
import com.pipixing.sns.service.FollowService;
import com.pipixing.sns.service.QuestionService;
import com.pipixing.sns.service.UserService;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FollowController {
    public static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    @PostMapping(value = "/followUser")
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        try {
            if (hostHolder.getUser() == null)
                return SnsUtil.getJsonString(999);
            if(hostHolder.getUser().getId()==userId)
                return SnsUtil.getJsonString(1,"自己不能关注自己哟");
            boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

            //如果关注则发站内信给对应User
            eventProducer.pushEventToMQ(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwner(userId));
            //返回当前用户关注的总的人数
            return SnsUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

        } catch (Exception e) {
            logger.error("follow用户失败：" + e.getMessage());
        }
        return SnsUtil.getJsonString(1,"follow用户失败");
    }

    @PostMapping(value = "/unfollowUser")
    @ResponseBody
    public String unFollowUser(@RequestParam("userId") int userId) {
        try {
            if (hostHolder.getUser() == null)
                return SnsUtil.getJsonString(999);
            boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

            eventProducer.pushEventToMQ(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwner(userId));
            //返回当前用户关注的总的人数
            return SnsUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

        } catch (Exception e) {
            logger.error("unfollow用户失败：" + e.getMessage());
        }
        return SnsUtil.getJsonString(1,"unfollow用户失败");
    }

    @PostMapping(value = "/followQuestion")
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        try {
            if (hostHolder.getUser() == null)
                return SnsUtil.getJsonString(999);
            Question question = questionService.selectQuestion(questionId);
            if(question==null)
                return SnsUtil.getJsonString(1,"question不存在");

            //如果关注则发站内信给对应User
            boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

            eventProducer.pushEventToMQ(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwner(question.getUser_id()));
            //返回当前用户关注的总的人数
            return SnsUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION)));

        } catch (Exception e) {
            logger.error("follow问题失败：" + e.getMessage());
        }
        return SnsUtil.getJsonString(1,"follow问题失败");
    }

    @PostMapping(value = "/unfollowQuestion")
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        try {
            if (hostHolder.getUser() == null)
                return SnsUtil.getJsonString(999);
            Question question = questionService.selectQuestion(questionId);
            if(question==null)
                return SnsUtil.getJsonString(1,"question不存在");
            boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

            eventProducer.pushEventToMQ(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                    .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwner(question.getUser_id()));
            //返回当前用户关注的总的人数
            return SnsUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION)));

        } catch (Exception e) {
            logger.error("unfollow问题失败：" + e.getMessage());
        }
        return SnsUtil.getJsonString(1,"unfollow问题失败");
    }

    //粉丝页面
    @GetMapping(value = "/user/{userId}/followers")
    public String followerPage(Model model,@PathVariable("userId") int userId){
        List<Integer> followerIds = followService.getFollowerList(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            //如果登陆状态下会跟查看用户比较是否有共同关注
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }
    //关注用户页面
    @GetMapping(value = "/user/{userId}/followees")
    public String followeePage(Model model,@PathVariable("userId") int userId){
        List<Integer> followeeIds = followService.getFolloweeList(userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            //设置评论数
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
