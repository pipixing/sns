package com.pipixing.sns.controller;

import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventProducer;
import com.pipixing.sns.async.EventType;
import com.pipixing.sns.model.Comment;
import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.model.HostHolder;
import com.pipixing.sns.service.CommentService;
import com.pipixing.sns.service.LikeService;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;

    @PostMapping(value = "/like")
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId){
        try {
            if(hostHolder.getUser()==null)
                return SnsUtil.getJsonString(999);
            Comment comment = commentService.getCommentById(commentId);
            //点赞之后发站内信给用户
            eventProducer.pushEventToMQ(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
            .setEntityType(EntityType.ENTITY_COMMENT).setEntityId(commentId).setEntityOwner(comment.getUserId())
            .setExts("questionId",String.valueOf(comment.getEntityId())));
            long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
            return SnsUtil.getJsonString(0,String.valueOf(likeCount));
        }catch (Exception e){
            logger.error("like失败"+e.getMessage());
        }
        return SnsUtil.getJsonString(1,"like失败");
    }

    @PostMapping(value = "/dislike")
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser()==null)
            return SnsUtil.getJsonString(999);
        Comment comment = commentService.getCommentById(commentId);
        long likeCount = likeService.dislike(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);
        return SnsUtil.getJsonString(0,String.valueOf(likeCount));

    }
}
