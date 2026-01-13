package com.taichu.yingjiguanli.modules.sys.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态更新请求 DTO
 *
 * @author CX
 * @since 2026-01-13
 */
@Data
public class UserStatusDTO {

    /**
     * 状态 (0禁用 1启用)
     */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值只能是0或1")
    @Max(value = 1, message = "状态值只能是0或1")
    private Integer status;
}
