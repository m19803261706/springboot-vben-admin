package com.taichu.yingjiguanli.controller;

import com.taichu.yingjiguanli.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api")
@Tag(name = "系统", description = "系统相关接口")
public class HealthController {

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("service", "yingjiguanli");
        data.put("version", "1.0.0");
        return ApiResponse.success(data);
    }

    /**
     * 首页
     *
     * @return 欢迎信息
     */
    @GetMapping("/")
    @Operation(summary = "首页", description = "API 首页")
    public ApiResponse<String> index() {
        return ApiResponse.success("欢迎使用应急管理系统 API");
    }
}
