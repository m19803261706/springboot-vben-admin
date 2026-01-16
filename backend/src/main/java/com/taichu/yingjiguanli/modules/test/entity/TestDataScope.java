package com.taichu.yingjiguanli.modules.test.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 数据权限测试实体类
 * 用于测试不同数据权限范围的数据过滤效果
 *
 * @author CX
 * @since 2026-01-16
 */
@Data
@Entity
@Table(name = "test_data_scope", indexes = {
        @Index(name = "idx_dept_id", columnList = "dept_id"),
        @Index(name = "idx_create_by", columnList = "create_by")
})
@Comment("数据权限测试表")
public class TestDataScope {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("主键ID")
    private Long id;

    /**
     * 标题
     */
    @Column(nullable = false, length = 100)
    @Comment("标题")
    private String title;

    /**
     * 内容
     */
    @Column(length = 500)
    @Comment("内容")
    private String content;

    /**
     * 所属部门ID
     */
    @Column(name = "dept_id")
    @Comment("所属部门ID")
    private Long deptId;

    /**
     * 所属部门名称
     */
    @Column(name = "dept_name", length = 50)
    @Comment("所属部门名称")
    private String deptName;

    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    @Comment("创建人ID")
    private Long createBy;

    /**
     * 创建人姓名
     */
    @Column(name = "create_by_name", length = 50)
    @Comment("创建人姓名")
    private String createByName;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
