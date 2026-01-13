package com.taichu.yingjiguanli.modules.auth.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 用户信息响应 VO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@Builder
public class UserInfoVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 角色编码列表
     */
    private List<String> roles;

    /**
     * 权限标识列表
     */
    private List<String> permissions;
}
