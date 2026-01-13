package com.taichu.yingjiguanli.modules.auth.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 VO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@Builder
public class LoginVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * Token 类型
     */
    private String tokenType;

    /**
     * Token 有效期（秒）
     */
    private Long expiresIn;
}
