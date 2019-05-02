package com.pipixing.sns.service;

import com.pipixing.sns.dao.CommentDAO;
import com.pipixing.sns.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public List<Comment> getCommentByEntityId(int entityId,int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public void removeComment(int entityId,int entityType,int entityStatus){
        commentDAO.updateStatus(entityId,entityType,entityStatus);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

}
