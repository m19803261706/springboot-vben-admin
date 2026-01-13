package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class UserRoleDTO {

    /**
     * 角色ID列表
     */
    @NotEmpty(message = "角色列表不能为空")
    private List<Long> roleIds;
}
