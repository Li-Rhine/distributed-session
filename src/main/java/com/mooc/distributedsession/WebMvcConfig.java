package com.mooc.distributedsession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: 拦截器使用
 * @author lidahai
 * @date 2020/12/29 21:08
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginIntercepter loginIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntercepter)
                .addPathPatterns("/user/address")
                .addPathPatterns("/user/infoWithJwt"); // /user/**

//        registry.addInterceptor(loginIntercepter)
//                .addPathPatterns("/user/**")    // 未登录的都会被拦截
//                .excludePathPatterns("/user/login");
    }
}
