package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单分配请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RoleMenuDTO {

    /**
     * 菜单ID列表
     */
    @NotNull(message = "菜单列表不能为空")
    private List<Long> menuIds;
}
