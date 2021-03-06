package com.pipixing.sns.controller;

import com.pipixing.sns.dao.CommentDAO;
import com.pipixing.sns.model.*;
import com.pipixing.sns.service.*;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    //增加一个问题
    @RequestMapping(path = "/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try {
            Question question =new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreated_date(new Date());
            question.setComment_count(0);
            if(hostHolder.getUser()==null){
                question.setUser_id(SnsUtil.ANONYMOUS_USERID);
            }else {
                question.setUser_id(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question)>0){
                return SnsUtil.getJsonString(0);
            }
        }catch (Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }
        return SnsUtil.getJsonString(1,"失败");
    }

    //问题的展示页面
    @GetMapping(value = "/question/{qid}")
    public String questionIndex(Model model, @PathVariable("qid") int id) {
        Question question = questionService.selectQuestion(id);
        model.addAttribute("question",question);
        List<Comment> commentList = commentService.getCommentByEntityId(id, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment:commentList){
            if(comment.getStatus()==1)
                continue;
            ViewObject vo = new ViewObject();
            if(hostHolder.getUser()==null)
                vo.set("liked", 0);
            else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getEntityId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            vo.set("comment",comment);
            vo.set("user",userService.getUser(comment.getUserId()));
            comments.add(vo);
        }

        model.addAttribute("comments",comments);
        // 获取关注的用户Id
        List<Integer> followUserIds = followService.getFollowerList(EntityType.ENTITY_QUESTION,id,0,10);
        model.addAttribute("followUsers", getFollowUsers(followUserIds));
        if(hostHolder.getUser()!=null)
            model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,id));
        else
            model.addAttribute("followed", false);

        return "detail";
    }

    private List<ViewObject> getFollowUsers(List<Integer> userIds){
        List<ViewObject> followUsers = new ArrayList<>();
        for (Integer userId : userIds) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHead_url());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        return followUsers;
    }
}
