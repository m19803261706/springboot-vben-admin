package com.taichu.yingjiguanli.modules.sys.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色视图对象
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RoleVO {

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

    /**
     * 数据权限范围
     * 1-全部数据 2-本部门数据 3-本部门及下级 4-仅本人数据 5-自定义
     */
    private Integer dataScope;

    /**
     * 数据权限描述
     */
    private String dataScopeDesc;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;

    /**
     * 部门ID列表 (数据权限为自定义时)
     */
    private List<Long> deptIds;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 数据权限范围描述
     */
    public static String getDataScopeDesc(Integer dataScope) {
        if (dataScope == null) return "";
        return switch (dataScope) {
            case 1 -> "全部数据";
            case 2 -> "本部门数据";
            case 3 -> "本部门及下级";
            case 4 -> "仅本人数据";
            case 5 -> "自定义";
            default -> "";
        };
    }
}
