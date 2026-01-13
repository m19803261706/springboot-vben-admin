package com.taichu.yingjiguanli.modules.sys.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门视图对象
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class DeptVO {

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID (0为顶级部门)
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status;

    /**
     * 子部门列表
     */
    private List<DeptVO> children = new ArrayList<>();

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
