package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单更新请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class MenuUpdateDTO {

    /**
     * 父菜单ID (0为顶级菜单)
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 菜单类型 (0目录 1菜单 2按钮)
     */
    @Min(value = 0, message = "菜单类型值必须在0-2之间")
    @Max(value = 2, message = "菜单类型值必须在0-2之间")
    private Integer menuType;

    /**
     * 路由路径
     */
    @Size(max = 255, message = "路由路径长度不能超过255个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 255, message = "组件路径长度不能超过255个字符")
    private String component;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    /**
     * 图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
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
}
