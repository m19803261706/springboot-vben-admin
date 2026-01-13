package com.taichu.yingjiguanli.modules.sys.entity;

import com.taichu.yingjiguanli.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role", indexes = {
        @Index(name = "idx_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_code", columnNames = "role_code")
})
@Comment("角色表")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 50)
    @Comment("角色名称")
    private String roleName;

    /**
     * 角色编码
     */
    @Column(name = "role_code", nullable = false, length = 50)
    @Comment("角色编码")
    private String roleCode;

    /**
     * 数据权限范围
     * 1-全部数据 2-本部门数据 3-本部门及下级 4-仅本人数据 5-自定义
     */
    @Column(name = "data_scope")
    @Comment("数据权限范围 (1全部 2本部门 3本部门及下级 4仅本人 5自定义)")
    private Integer dataScope = 1;

    /**
     * 排序
     */
    @Column
    @Comment("排序")
    private Integer sort = 0;

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
     * 备注
     */
    @Column(length = 255)
    @Comment("备注")
    private String remark;

    /**
     * 角色菜单关联 (多对多)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_role_menu",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private Set<SysMenu> menus = new HashSet<>();

    /**
     * 角色部门关联 - 数据权限 (多对多)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "sys_role_dept",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "dept_id")
    )
    private Set<SysDept> depts = new HashSet<>();
}
