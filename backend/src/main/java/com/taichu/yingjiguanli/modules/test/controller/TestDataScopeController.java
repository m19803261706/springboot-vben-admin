package com.taichu.yingjiguanli.modules.test.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeDTO;
import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeQueryDTO;
import com.taichu.yingjiguanli.modules.test.service.TestDataScopeService;
import com.taichu.yingjiguanli.modules.test.service.impl.TestDataScopeServiceImpl;
import com.taichu.yingjiguanli.modules.test.vo.TestDataScopeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限测试控制器
 * 用于测试不同数据权限范围的效果
 *
 * @author CX
 * @since 2026-01-16
 */
@RestController
@RequestMapping("/api/test/data-scope")
@RequiredArgsConstructor
@Tag(name = "数据权限测试")
public class TestDataScopeController {

    private final TestDataScopeService testDataScopeService;

    /**
     * 分页查询 (自动应用数据权限过滤)
     */
    @GetMapping
    @SaCheckPermission("test:dataScope:list")
    @Operation(summary = "分页查询", description = "根据当前用户数据权限过滤数据")
    public ApiResponse<Map<String, Object>> list(TestDataScopeQueryDTO query) {
        Page<TestDataScopeVO> page = testDataScopeService.findPage(query);
        TestDataScopeServiceImpl.DataScopeInfo scopeInfo = testDataScopeService.getCurrentDataScopeInfo();

        // 返回数据和当前用户的数据权限信息
        Map<String, Object> result = new HashMap<>();
        result.put("content", page.getContent());
        result.put("total", page.getTotalElements());
        result.put("page", page.getNumber() + 1);
        result.put("size", page.getSize());
        result.put("dataScope", Map.of(
                "type", scopeInfo.getDataScope(),
                "desc", scopeInfo.getDataScopeDesc(),
                "userId", scopeInfo.getUserId(),
                "deptId", scopeInfo.getDeptId() != null ? scopeInfo.getDeptId() : "",
                "customDeptIds", scopeInfo.getCustomDeptIds()
        ));

        return ApiResponse.success(result);
    }

    /**
     * 获取当前用户数据权限信息
     */
    @GetMapping("/scope-info")
    @SaCheckPermission("test:dataScope:list")
    @Operation(summary = "获取数据权限信息", description = "获取当前用户的数据权限类型和范围")
    public ApiResponse<TestDataScopeServiceImpl.DataScopeInfo> getScopeInfo() {
        return ApiResponse.success(testDataScopeService.getCurrentDataScopeInfo());
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    @SaCheckPermission("test:dataScope:list")
    @Operation(summary = "查询详情")
    public ApiResponse<TestDataScopeVO> getById(@PathVariable Long id) {
        return ApiResponse.success(testDataScopeService.findById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @SaCheckPermission("test:dataScope:add")
    @Operation(summary = "新增", description = "新增时自动设置当前用户部门和创建人")
    public ApiResponse<TestDataScopeVO> create(@Valid @RequestBody TestDataScopeDTO dto) {
        return ApiResponse.success(testDataScopeService.create(dto));
    }

    /**
     * 更新
     */
    @PutMapping("/{id}")
    @SaCheckPermission("test:dataScope:edit")
    @Operation(summary = "更新", description = "更新时检查数据权限")
    public ApiResponse<TestDataScopeVO> update(@PathVariable Long id, @Valid @RequestBody TestDataScopeDTO dto) {
        return ApiResponse.success(testDataScopeService.update(id, dto));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("test:dataScope:delete")
    @Operation(summary = "删除", description = "删除时检查数据权限")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        testDataScopeService.delete(id);
        return ApiResponse.success();
    }
}
