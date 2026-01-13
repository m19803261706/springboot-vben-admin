package com.taichu.yingjiguanli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 * 提供密码加密器等安全相关 Bean
 *
 * @author CX
 * @since 2026-01-13
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码加密器
     * 使用 BCrypt 算法进行密码加密
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
