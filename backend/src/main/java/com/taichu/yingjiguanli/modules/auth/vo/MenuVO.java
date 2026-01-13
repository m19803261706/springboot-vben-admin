package com.taichu.yingjiguanli.modules.auth.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单响应 VO (前端路由格式)
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class MenuVO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 菜单类型 (0目录 1菜单 2按钮)
     */
    private Integer type;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 子菜单
     */
    private List<MenuVO> children = new ArrayList<>();
}
