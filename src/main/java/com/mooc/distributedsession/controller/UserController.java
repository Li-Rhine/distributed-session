package com.mooc.distributedsession.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
public class UserController {

    private static final String JWT_KEY = "imooc";

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


    @GetMapping("/loginWithJwt")
    public String loginWithJwt(@RequestParam String username,
                               @RequestParam String password) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        String token = JWT.create()
                .withClaim("login_user", username)
                .withClaim("id", 1)
                // 过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(algorithm);
        return token;
    }

    @GetMapping("/infoWithJwt")
    public String infoWithJwt(@RequestHeader String token) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .build(); //Reusable verifier instance
        // 如果过期会抛异常
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("login_user").asString();
        }catch (TokenExpiredException e) {
            // Token过期
        }catch (JWTDecodeException e) {
            // 解码失败，token错误
        }
        return "error";
    }
}
