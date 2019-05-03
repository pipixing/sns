package com.pipixing.sns.controller;

import com.pipixing.sns.model.Comment;
import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.model.HostHolder;
import com.pipixing.sns.service.CommentService;
import com.pipixing.sns.service.QuestionService;
import com.pipixing.sns.service.SensitiveService;
import com.pipixing.sns.service.UserService;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import javax.swing.text.html.parser.Entity;
import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    QuestionService questionService;

    @PostMapping(value = "/addComment")
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveService.filter(content);
            Comment comment = new Comment();
            if(hostHolder.getUser()!=null){
                comment.setUserId(hostHolder.getUser().getId());
            }
            else{
                comment.setUserId(SnsUtil.ANONYMOUS_USERID);
            }
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            //获取某个问题的评论数
            int count = commentService.getCommentCount(comment.getEntityId(),EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(comment.getEntityId(),count);
        }catch (Exception e){
            logger.error("增加评论失败："+e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
