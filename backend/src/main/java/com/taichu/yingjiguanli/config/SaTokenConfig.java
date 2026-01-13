package com.taichu.yingjiguanli.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 * 配置路由拦截规则
 *
 * @author CX
 * @since 2026-01-13
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     * 配置需要登录校验的路由
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定拦截路由
            SaRouter.match("/**")
                    // 排除登录接口
                    .notMatch("/api/auth/login")
                    // 排除 Swagger 文档
                    .notMatch("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html")
                    // 排除健康检查
                    .notMatch("/api/health", "/actuator/**")
                    // 排除静态资源
                    .notMatch("/static/**", "/favicon.ico", "/error")
                    // 校验登录
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
