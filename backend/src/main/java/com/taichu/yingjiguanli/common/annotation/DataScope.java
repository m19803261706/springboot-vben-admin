package com.taichu.yingjiguanli.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 用于方法级别，标记该方法需要进行数据权限过滤
 *
 * @author CX
 * @since 2026-01-13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    String deptAlias() default "";

    /**
     * 用户表的别名
     */
    String userAlias() default "";

    /**
     * 部门ID字段名
     */
    String deptIdColumn() default "dept_id";

    /**
     * 用户ID字段名
     */
    String userIdColumn() default "create_by";
}
