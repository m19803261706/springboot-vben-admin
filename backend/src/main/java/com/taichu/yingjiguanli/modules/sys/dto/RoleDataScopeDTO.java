package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色数据权限设置请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RoleDataScopeDTO {

    /**
     * 数据权限范围
     * 1-全部数据 2-本部门数据 3-本部门及下级 4-仅本人数据 5-自定义
     */
    @NotNull(message = "数据权限范围不能为空")
    @Min(value = 1, message = "数据权限范围值必须在1-5之间")
    @Max(value = 5, message = "数据权限范围值必须在1-5之间")
    private Integer dataScope;

    /**
     * 部门ID列表 (数据权限为自定义时使用)
     */
    private List<Long> deptIds;
}
