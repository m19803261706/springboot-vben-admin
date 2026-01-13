package com.taichu.yingjiguanli.modules.sys.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

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
     * 状态 (0禁用 1启用)
     */
    private Integer status;

    /**
     * 角色列表
     */
    private List<RoleVO> roles;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 角色视图对象
     */
    @Data
    public static class RoleVO {
        /**
         * 角色ID
         */
        private Long id;

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 角色编码
         */
        private String roleCode;
    }
}
