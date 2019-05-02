package com.pipixing.sns.service;

import com.pipixing.sns.dao.QuestionDAO;
import com.pipixing.sns.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;

    //确定每页到底要展示多少个问题
    public List<Question> selectLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    // 添加问题
    public int addQuestion(Question question) {
        //用Spring自带的html过滤包先进行html语句过滤
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        //使用自己设计的敏感词过滤函数进行过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        // 这里question.getId()就是存进数据库后对应的Id了
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    // 选择问题
    public Question selectQuestion(int id){
        return questionDAO.selectQuestionById(id);
    }

    // 更新每个问题的评论数
    public int updateCommentCount(int questionId,int count){
        return questionDAO.updateCommentCount(questionId,count);
    }
}