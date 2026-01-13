package com.taichu.yingjiguanli.modules.sys.dto;

import lombok.Data;

/**
 * 用户查询请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class UserQueryDTO {

    /**
     * 用户名 (模糊搜索)
     */
    private String username;

    /**
     * 真实姓名 (模糊搜索)
     */
    private String realName;

    /**
     * 手机号 (模糊搜索)
     */
    private String phone;

    /**
     * 部门ID
     */
    private Long deptId;

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
