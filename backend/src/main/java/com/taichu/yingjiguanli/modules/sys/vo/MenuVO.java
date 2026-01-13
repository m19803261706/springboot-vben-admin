package com.taichu.yingjiguanli.modules.sys.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单视图对象
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
     * 父菜单ID (0为顶级菜单)
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型 (0目录 1菜单 2按钮)
     */
    private Integer menuType;

    /**
     * 菜单类型描述
     */
    private String menuTypeDesc;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否可见 (0隐藏 1显示)
     */
    private Integer visible;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status;

    /**
     * 子菜单列表
     */
    private List<MenuVO> children = new ArrayList<>();

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 菜单类型描述
     */
    public static String getMenuTypeDesc(Integer menuType) {
        if (menuType == null) return "";
        return switch (menuType) {
            case 0 -> "目录";
            case 1 -> "菜单";
            case 2 -> "按钮";
            default -> "";
        };
    }
}
