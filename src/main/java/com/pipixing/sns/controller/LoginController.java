package com.pipixing.sns.controller;

import com.pipixing.sns.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path={"/reg"},method = {RequestMethod.POST})
    public String reg(Model model, String username, String password,
                      @RequestParam(value = "next",required = false) String  next,
                      @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                      HttpServletResponse response){
        try {
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                // 在同一应用服务器内共享cookie
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                // ticket下发到客户端（浏览器）存储
                response.addCookie(cookie);
                // 当读取到的next字段不为空的话跳转
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("msg","服务器错误");
            return "login";
        }
    }
    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String register(Model model, @RequestParam(value = "next", required = false) String next) {
        // 把next参数放在view里
        model.addAttribute("next", next);
        return "login";
    }

    //登陆
    @RequestMapping(path = {"/login"},method = {RequestMethod.POST})
    public String login(Model model, String username, String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                // 在同一应用服务器内共享cookie
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                // ticket下发到客户端（浏览器）存储
                response.addCookie(cookie);
                // 当读取到的next字段不为空的话跳转
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch (Exception e) {
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }

    @GetMapping(value = "/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/"; // 这一步会再次执行ticket拦截器，所以首页没有个人登陆显示了
    }

}
