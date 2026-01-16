package com.taichu.yingjiguanli.modules.test.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据权限测试 - 视图对象
 *
 * @author CX
 * @since 2026-01-16
 */
@Data
public class TestDataScopeVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 所属部门ID
     */
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建人姓名
     */
    private String createByName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
