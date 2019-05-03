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
import org.springframework.web.bind.annotation.RequestParam;
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
        //在消息页展示跟所有朋友之间最新的一条信息
        List<Message> messages = messageService.getMessageList(user.getId(),0,10);
        for(Message message:messages)
        {
            ViewObject vo = new ViewObject();
            int targetId = (message.getFromId()==user.getId()? message.getToId():message.getFromId());
            vo.set("user",userService.getUser(targetId));
            //那条最新的信息
            vo.set("message",message);
            //跟每个朋友的未都信息数量
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

    @GetMapping(value = "/msg/detail")
    public String getMessageDetail(Model model, @RequestParam("conversationId") String conversationId){
        try {
            User user = hostHolder.getUser();
            if(user==null)
                return "redirect:/reglogin";
            List<ViewObject> vos = new ArrayList<>();
            //将两人之间的所有信息往来通过conversationId全部去过来
            List<Message> messages = messageService.getMessageListDetail(conversationId,0,10);
            //如果看完未看过的站内信，则对当前登陆者更新对此朋友未看站内信数量
            messageService.hasReadMessage(user.getId(),conversationId,1);
            for(Message message:messages)
            {
                ViewObject vo = new ViewObject();
                vo.set("user",userService.getUser(message.getFromId()));
                vo.set("message",message);
                vos.add(vo);
            }
            model.addAttribute("messages",vos);
        }catch (Exception e){
            logger.error("detail页面存在错误"+e.getMessage());
        }
        return "letterDetail";
    }

}
