package com.pipixing.sns.controller;

import com.pipixing.sns.model.HostHolder;
import com.pipixing.sns.model.Message;
import com.pipixing.sns.model.User;
import com.pipixing.sns.model.ViewObject;
import com.pipixing.sns.service.MessageService;
import com.pipixing.sns.service.UserService;
import com.pipixing.sns.utils.SnsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @GetMapping(value = "/msg/list")
    public String getMessageList(Model model){
        User user = hostHolder.getUser();
        if(user == null)
            return "redirect:/reglogin";
        List<ViewObject> vos = new ArrayList<>();
        List<Message> messages = messageService.getMessageList(user.getId(),0,10);
        for(Message message:messages)
        {
            ViewObject vo = new ViewObject();
            int targetId = (message.getFromId()==user.getId()? message.getToId():message.getFromId());
            vo.set("user",userService.getUser(targetId));
            vo.set("message",message);
            vo.set("unread",messageService.getUnreadConversationCount(user.getId(),message.getConversationId()));
            vos.add(vo);
        }
        model.addAttribute("conversations",vos);
        return "letter";

    }

    @PostMapping(value = "/msg/addMessage")
    @ResponseBody
    public String addMessage(String toName,String content){
        try {
            if(hostHolder.getUser()==null)
                return SnsUtil.getJsonString(999,"未登录");
            //检查此人是否存在
            User user = userService.selectUserByName(toName);
            if(user==null)
                return SnsUtil.getJsonString(1,"此用户不存在");
            //发送消息
            int fromId = hostHolder.getUser().getId();
            int toId = user.getId();
            Message message = new Message();
            message.setFromId(fromId);
            message.setToId(toId);
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setConversationId(fromId<toId? String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
            messageService.addMessage(message);
            return SnsUtil.getJsonString(0);
        }catch (Exception e){
            logger.error("发送消息失败："+ e.getMessage());
            return SnsUtil.getJsonString(1,"发送消息失败");
        }
    }

}
