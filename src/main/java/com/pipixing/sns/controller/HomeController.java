package com.pipixing.sns.controller;
import com.pipixing.sns.model.Question;
import com.pipixing.sns.model.User;
import com.pipixing.sns.model.ViewObject;
import com.pipixing.sns.service.QuestionService;
import com.pipixing.sns.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/", "/index"},method = {RequestMethod.GET,RequestMethod.POST})
    public String home(Model model, @RequestParam(value = "pop",defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"},method = {RequestMethod.GET,RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
//        // 显示关注和被关注列表
//        User user = userService.getUser(userId);
//        ViewObject vo = new ViewObject();
//        vo.set("user", user);
//        vo.set("commentCount", commentService.getUserCommentCount(userId));
//        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
//        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
//        if (hostHolder.getUser() != null) {
//            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), userId, EntityType.ENTITY_USER));
//        } else {
//            vo.set("followed", false);
//        }
//        model.addAttribute("profileUser", vo);
//        model.addAttribute("vos", getQuestions(userId, 0, 10));
//        return "profile";
        return "index";
    }

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.selectLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            //问题关注的数量
            //vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUser_id()));
            vos.add(vo);
        }
        return vos;
    }
}
