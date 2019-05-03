package com.pipixing.sns.service;

import com.pipixing.sns.dao.MessageDAO;
import com.pipixing.sns.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    public List<Message> getMessageList(int userId,int offset,int limit){
        return messageDAO.getMessageList(userId,offset,limit);
    }

    public int getUnreadConversationCount(int userId,String conversationId){
        return messageDAO.getUnreadConversationCount(userId,conversationId);
    }

    public void addMessage(Message message){
        messageDAO.addMessage(message);
    }

    public List<Message> getMessageListDetail(String conversationId, int offset, int limit) {
        return messageDAO.getMessageListDetail(conversationId,offset,limit);
    }

    public void hasReadMessage(int toId,String conversationId,int hasRead){
        messageDAO.hasReadMessage(toId,conversationId,hasRead);
    }
}
