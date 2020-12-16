package com.mooc.distributedsession.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        //账号密码正确
        session.setAttribute("login_user", username);
        return "登录成功";
    }


    @GetMapping("/info")
    public String info(HttpSession session) {
        return "当前登录的是:" + session.getAttribute("login_user");
    }

    @GetMapping("/loginWithToken")
    public String loginWithToken(@RequestParam String username,
                                 @RequestParam String password) {
        //账号密码正确
        String key = "token_" + UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(key, username, 36000, TimeUnit.SECONDS);
        return key;
    }


    @GetMapping("/infoWithToken")
    public String infoWithToken(@RequestParam String token) {
        return "当前登录的是:" + stringRedisTemplate.opsForValue().get(token);
    }
}
