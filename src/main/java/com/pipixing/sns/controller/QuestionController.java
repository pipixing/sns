package com.pipixing.sns.controller;

import com.pipixing.sns.model.HostHolder;
import com.pipixing.sns.model.Question;
import com.pipixing.sns.service.QuestionService;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

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

}
