package com.taichu.yingjiguanli.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.DeptCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.DeptUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.service.SysDeptService;
import com.taichu.yingjiguanli.modules.sys.vo.DeptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api/sys/dept")
@RequiredArgsConstructor
@SaCheckLogin
@Tag(name = "部门管理", description = "部门 CRUD 接口")
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 查询部门树
     *
     * @return 部门树
     */
    @GetMapping("/tree")
    @SaCheckPermission("sys:dept:list")
    @Operation(summary = "查询部门树")
    public ApiResponse<List<DeptVO>> tree() {
        return ApiResponse.success(deptService.findTree());
    }

    /**
     * 查询所有部门列表 (平铺)
     *
     * @return 部门列表
     */
    @GetMapping("/list")
    @SaCheckPermission("sys:dept:list")
    @Operation(summary = "查询所有部门列表")
    public ApiResponse<List<DeptVO>> list() {
        return ApiResponse.success(deptService.findAll());
    }

    /**
     * 查询部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:dept:list")
    @Operation(summary = "查询部门详情")
    public ApiResponse<DeptVO> getById(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        return ApiResponse.success(deptService.findById(id)
                .orElseThrow(() -> new BusinessException(404, "部门不存在")));
    }

    /**
     * 新增部门
     *
     * @param dto 创建请求
     * @return 创建的部门
     */
    @PostMapping
    @SaCheckPermission("sys:dept:add")
    @Operation(summary = "新增部门")
    public ApiResponse<DeptVO> create(@Valid @RequestBody DeptCreateDTO dto) {
        return ApiResponse.success("部门创建成功", deptService.create(dto));
    }

    /**
     * 更新部门
     *
     * @param id  部门ID
     * @param dto 更新请求
     * @return 更新后的部门
     */
    @PutMapping("/{id}")
    @SaCheckPermission("sys:dept:edit")
    @Operation(summary = "更新部门")
    public ApiResponse<DeptVO> update(
            @Parameter(description = "部门ID") @PathVariable Long id,
            @Valid @RequestBody DeptUpdateDTO dto) {
        return ApiResponse.success("部门更新成功", deptService.update(id, dto));
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:dept:delete")
    @Operation(summary = "删除部门")
    public ApiResponse<Void> delete(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        deptService.delete(id);
        return ApiResponse.success("部门删除成功", null);
    }
}
