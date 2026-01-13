package com.taichu.yingjiguanli.modules.auth.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由响应 VO (Vben Admin 格式)
 * 符合 Vben Admin 的 RouteRecordStringComponent 类型定义
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RouteVO {

    /**
     * 路由名称 (唯一标识)
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径 (字符串格式)
     * - "BasicLayout" 表示使用基础布局
     * - "/sys/user/index" 表示页面组件路径
     */
    private String component;

    /**
     * 重定向路径
     */
    private String redirect;

    /**
     * 路由元信息
     */
    private RouteMeta meta;

    /**
     * 子路由
     */
    private List<RouteVO> children = new ArrayList<>();

    /**
     * 路由元信息
     */
    @Data
    public static class RouteMeta {
        /**
         * 标题
         */
        private String title;

        /**
         * 图标
         */
        private String icon;

        /**
         * 排序 (用于菜单排序)
         */
        private Integer order;

        /**
         * 是否在菜单中隐藏
         */
        private Boolean hideInMenu;

        /**
         * 是否开启 KeepAlive 缓存
         */
        private Boolean keepAlive;

        /**
         * 权限标识
         */
        private List<String> authority;

        /**
         * 是否固定标签页
         */
        private Boolean affixTab;

        /**
         * 外链地址
         */
        private String link;

        /**
         * 是否忽略权限检查
         */
        private Boolean ignoreAccess;
    }
}
