package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 角色创建请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RoleCreateDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "角色编码必须以字母开头，只能包含字母、数字和下划线")
    private String roleCode;

    /**
     * 数据权限范围
     * 1-全部数据 2-本部门数据 3-本部门及下级 4-仅本人数据 5-自定义
     */
    private Integer dataScope = 1;

    /**
     * 排序
     */
    private Integer sort = 0;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status = 1;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    private String remark;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;

    /**
     * 部门ID列表 (数据权限为自定义时使用)
     */
    private List<Long> deptIds;
}
