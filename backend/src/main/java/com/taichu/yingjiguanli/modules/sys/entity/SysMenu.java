package com.taichu.yingjiguanli.modules.sys.entity;

import com.taichu.yingjiguanli.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单实体类
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_menu", indexes = {
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Comment("菜单表")
public class SysMenu extends BaseEntity {

    /**
     * 父菜单ID (0为顶级菜单)
     */
    @Column(name = "parent_id")
    @Comment("父菜单ID (0为顶级)")
    private Long parentId = 0L;

    /**
     * 菜单名称
     */
    @Column(name = "menu_name", nullable = false, length = 50)
    @Comment("菜单名称")
    private String menuName;

    /**
     * 菜单类型 (0目录 1菜单 2按钮)
     */
    @Column(name = "menu_type", nullable = false)
    @Comment("菜单类型 (0目录 1菜单 2按钮)")
    private Integer menuType;

    /**
     * 路由路径
     */
    @Column(length = 255)
    @Comment("路由路径")
    private String path;

    /**
     * 组件路径
     */
    @Column(length = 255)
    @Comment("组件路径")
    private String component;

    /**
     * 权限标识
     */
    @Column(length = 100)
    @Comment("权限标识")
    private String permission;

    /**
     * 图标
     */
    @Column(length = 50)
    @Comment("图标")
    private String icon;

    /**
     * 排序
     */
    @Column
    @Comment("排序")
    private Integer sort = 0;

    /**
     * 是否可见 (0隐藏 1显示)
     */
    @Column
    @Comment("是否可见 (0隐藏 1显示)")
    private Integer visible = 1;

    /**
     * 状态 (0禁用 1启用)
     */
    @Column
    @Comment("状态 (0禁用 1启用)")
    private Integer status = 1;

    /**
     * 子菜单列表 (非数据库字段)
     */
    @Transient
    private List<SysMenu> children = new ArrayList<>();

    /**
     * 菜单类型枚举
     */
    public interface MenuType {
        /** 目录 */
        int DIRECTORY = 0;
        /** 菜单 */
        int MENU = 1;
        /** 按钮 */
        int BUTTON = 2;
    }
}
