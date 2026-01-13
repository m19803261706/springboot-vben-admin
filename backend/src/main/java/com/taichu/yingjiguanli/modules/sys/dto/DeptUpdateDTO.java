package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门更新请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class DeptUpdateDTO {

    /**
     * 父部门ID (0为顶级部门)
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String deptName;

    /**
     * 负责人
     */
    @Size(max = 50, message = "负责人名称长度不能超过50个字符")
    private String leader;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 (0禁用 1启用)
     */
    private Integer status;
}
