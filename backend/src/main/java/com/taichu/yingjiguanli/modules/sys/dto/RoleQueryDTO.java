package com.taichu.yingjiguanli.modules.sys.dto;

import lombok.Data;

/**
 * 角色查询请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class RoleQueryDTO {

    /**
     * 角色名称 (模糊搜索)
     */
    private String roleName;

    /**
     * 角色编码 (模糊搜索)
     */
    private String roleCode;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status;

    /**
     * 页码 (从0开始)
     */
    private Integer page = 0;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
