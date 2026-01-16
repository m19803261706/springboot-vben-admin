package com.taichu.yingjiguanli.modules.test.dto;

import lombok.Data;

/**
 * 数据权限测试 - 查询 DTO
 *
 * @author CX
 * @since 2026-01-16
 */
@Data
public class TestDataScopeQueryDTO {

    /**
     * 标题关键词
     */
    private String title;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 每页条数
     */
    private Integer size = 10;
}
