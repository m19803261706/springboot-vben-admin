package com.taichu.yingjiguanli.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taichu.yingjiguanli.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user", indexes = {
        @Index(name = "idx_dept_id", columnList = "dept_id"),
        @Index(name = "idx_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_username", columnNames = "username")
})
@Comment("用户表")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    @Column(nullable = false, length = 50)
    @Comment("用户名")
    private String username;

    /**
     * 密码 (BCrypt加密)
     */
    @JsonIgnore
    @Column(nullable = false, length = 100)
    @Comment("密码")
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    @Comment("真实姓名")
    private String realName;

    /**
     * 手机号
     */
    @Column(length = 20)
    @Comment("手机号")
    private String phone;

    /**
     * 邮箱
     */
    @Column(length = 100)
    @Comment("邮箱")
    private String email;

    /**
     * 头像URL
     */
    @Column(length = 255)
    @Comment("头像URL")
    private String avatar;

    /**
     * 部门ID
     */
    @Column(name = "dept_id")
    @Comment("部门ID")
    private Long deptId;

    /**
     * 状态 (0禁用 1启用)
     */
    @Column
    @Comment("状态 (0禁用 1启用)")
    private Integer status = 1;

    /**
     * 删除标记 (0正常 1删除)
     */
    @Column(name = "del_flag")
    @Comment("删除标记 (0正常 1删除)")
    private Integer delFlag = 0;

    /**
     * 用户角色关联 (多对多)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<SysRole> roles = new HashSet<>();

    /**
     * 部门信息 (非数据库字段)
     */
    @Transient
    private SysDept dept;
}
