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

    public List<Question> selectLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    // 添加问题
    public int addQuestion(Question question) {
        // 这里question.getId()就是存进数据库后对应的Id了
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }
}