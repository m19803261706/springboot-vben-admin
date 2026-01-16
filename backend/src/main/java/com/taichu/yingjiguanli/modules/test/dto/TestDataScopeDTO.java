package com.taichu.yingjiguanli.modules.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 数据权限测试 - 创建/更新 DTO
 *
 * @author CX
 * @since 2026-01-16
 */
@Data
public class TestDataScopeDTO {

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 内容
     */
    @Size(max = 500, message = "内容长度不能超过500个字符")
    private String content;
}
