package com.taichu.yingjiguanli.modules.sys.entity;

import com.taichu.yingjiguanli.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体类
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_dept", indexes = {
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Comment("部门表")
public class SysDept extends BaseEntity {

    /**
     * 父部门ID (0为顶级部门)
     */
    @Column(name = "parent_id")
    @Comment("父部门ID (0为顶级)")
    private Long parentId = 0L;

    /**
     * 部门名称
     */
    @Column(name = "dept_name", nullable = false, length = 50)
    @Comment("部门名称")
    private String deptName;

    /**
     * 负责人
     */
    @Column(length = 50)
    @Comment("负责人")
    private String leader;

    /**
     * 联系电话
     */
    @Column(length = 20)
    @Comment("联系电话")
    private String phone;

    /**
     * 排序
     */
    @Column
    @Comment("排序")
    private Integer sort = 0;

    /**
     * 状态 (0禁用 1启用)
     */
    @Column
    @Comment("状态 (0禁用 1启用)")
    private Integer status = 1;

    /**
     * 删除标记 (0正常 1删除)
     */
    @Column(name = "del_flag")
    @Comment("删除标记 (0正常 1删除)")
    private Integer delFlag = 0;

    /**
     * 子部门列表 (非数据库字段)
     */
    @Transient
    private List<SysDept> children = new ArrayList<>();
}
