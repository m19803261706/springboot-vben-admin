package com.taichu.yingjiguanli.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.service.SysRoleService;
import com.taichu.yingjiguanli.modules.sys.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api/sys/role")
@RequiredArgsConstructor
@SaCheckLogin
@Tag(name = "角色管理", description = "角色 CRUD 接口")
public class SysRoleController {

    private final SysRoleService roleService;

    /**
     * 分页查询角色列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping
    @SaCheckPermission("sys:role:list")
    @Operation(summary = "分页查询角色列表")
    public ApiResponse<Page<RoleVO>> list(RoleQueryDTO query) {
        return ApiResponse.success(roleService.findPage(query));
    }

    /**
     * 查询所有角色 (下拉选择用)
     *
     * @return 角色列表
     */
    @GetMapping("/all")
    @SaCheckPermission("sys:role:list")
    @Operation(summary = "查询所有角色")
    public ApiResponse<List<RoleVO>> listAll() {
        return ApiResponse.success(roleService.findAll());
    }

    /**
     * 查询角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:list")
    @Operation(summary = "查询角色详情")
    public ApiResponse<RoleVO> getById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        return ApiResponse.success(roleService.findById(id)
                .orElseThrow(() -> new BusinessException(404, "角色不存在")));
    }

    /**
     * 新增角色
     *
     * @param dto 创建请求
     * @return 创建的角色
     */
    @PostMapping
    @SaCheckPermission("sys:role:add")
    @Operation(summary = "新增角色")
    public ApiResponse<RoleVO> create(@Valid @RequestBody RoleCreateDTO dto) {
        return ApiResponse.success("角色创建成功", roleService.create(dto));
    }

    /**
     * 更新角色
     *
     * @param id  角色ID
     * @param dto 更新请求
     * @return 更新后的角色
     */
    @PutMapping("/{id}")
    @SaCheckPermission("sys:role:edit")
    @Operation(summary = "更新角色")
    public ApiResponse<RoleVO> update(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO dto) {
        return ApiResponse.success("角色更新成功", roleService.update(id, dto));
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:role:delete")
    @Operation(summary = "删除角色")
    public ApiResponse<Void> delete(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success("角色删除成功", null);
    }

    /**
     * 分配菜单权限
     *
     * @param id  角色ID
     * @param dto 菜单分配请求
     * @return 操作结果
     */
    @PutMapping("/{id}/menu")
    @SaCheckPermission("sys:role:edit")
    @Operation(summary = "分配菜单权限")
    public ApiResponse<Void> assignMenus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleMenuDTO dto) {
        roleService.assignMenus(id, dto);
        return ApiResponse.success("菜单权限分配成功", null);
    }

    /**
     * 设置数据权限范围
     *
     * @param id  角色ID
     * @param dto 数据权限设置请求
     * @return 操作结果
     */
    @PutMapping("/{id}/data-scope")
    @SaCheckPermission("sys:role:edit")
    @Operation(summary = "设置数据权限范围")
    public ApiResponse<Void> setDataScope(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleDataScopeDTO dto) {
        roleService.setDataScope(id, dto);
        return ApiResponse.success("数据权限设置成功", null);
    }
}
