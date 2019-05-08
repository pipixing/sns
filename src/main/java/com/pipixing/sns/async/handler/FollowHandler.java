package com.pipixing.sns.async.handler;

import com.pipixing.sns.async.EventHandler;
import com.pipixing.sns.async.EventModel;
import com.pipixing.sns.async.EventType;
import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.model.Message;
import com.pipixing.sns.model.User;
import com.pipixing.sns.service.MessageService;
import com.pipixing.sns.service.UserService;
import com.pipixing.sns.utils.SnsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class FollowHandler implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;

    @Override
    public void doHandler(EventModel eventModel) {
        //发送相对应的站内信给被关注的用户，或者是问题的提出者；
        User user = userService.getUser(eventModel.getActorId());
        int fromId = SnsUtil.SYSTEMCONTROLLER_USERID;
        int toId = eventModel.getEntityOwner();
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        if(eventModel.getEntityType()== EntityType.ENTITY_QUESTION)
            message.setContent("用户" + user.getName() +
                    "关注了你的问题，http://127.0.0.1/question/" + eventModel.getEntityId());
        else if(eventModel.getEntityType()==EntityType.ENTITY_USER)
            message.setContent("用户" + user.getName() +
                    "关注了你，http://127.0.0.1/user/" + eventModel.getActorId());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FELLOW);
    }
}
