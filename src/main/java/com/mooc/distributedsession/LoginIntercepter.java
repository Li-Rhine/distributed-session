package com.mooc.distributedsession;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginIntercepter extends HandlerInterceptorAdapter {

    public static final String JWT_KEY = "imooc";
    public static final String JWT_TOKEN = "token";
    public static final String UID = "uid";
    public static final String LOGIN_USER = "login_user";

    /**
     * @description:  返回true表示不拦截，继续往下执行
     *                  返回false/抛异常，不再往下执行
     * @param:  * @param request
     * @param response
     * @param handler
     * @return: boolean
     * @author LENOVO
     * @date: 2020/12/29 20:40
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(JWT_TOKEN);
        if (StringUtils.isEmpty(token)) {
            throw new RuntimeException("token为空");
        }

        Algorithm algorithm = Algorithm.HMAC256(JWT_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .build(); //Reusable verifier instance
        // 如果过期会抛异常
        try {
            DecodedJWT jwt = verifier.verify(token);
            request.setAttribute(UID, jwt.getClaim(UID).asInt());
            request.setAttribute(LOGIN_USER, jwt.getClaim(LOGIN_USER).asString());
        }catch (TokenExpiredException e) {
            // Token过期
            throw new RuntimeException("token过期");
        }catch (JWTDecodeException e) {
            // 解码失败，token错误
            throw new RuntimeException("解码失败,token错误");
        }
        return true;
    }
}
