package com.taichu.yingjiguanli.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.service.SysUserService;
import com.taichu.yingjiguanli.modules.sys.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api/sys/user")
@RequiredArgsConstructor
@SaCheckLogin
@Tag(name = "用户管理", description = "用户 CRUD 接口")
public class SysUserController {

    private final SysUserService userService;

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @GetMapping
    @SaCheckPermission("sys:user:list")
    @Operation(summary = "分页查询用户列表")
    public ApiResponse<Page<UserVO>> list(UserQueryDTO query) {
        return ApiResponse.success(userService.findPage(query));
    }

    /**
     * 查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:list")
    @Operation(summary = "查询用户详情")
    public ApiResponse<UserVO> getById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return ApiResponse.success(userService.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在")));
    }

    /**
     * 新增用户
     *
     * @param dto 创建请求
     * @return 创建的用户
     */
    @PostMapping
    @SaCheckPermission("sys:user:add")
    @Operation(summary = "新增用户")
    public ApiResponse<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        return ApiResponse.success("用户创建成功", userService.create(dto));
    }

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 更新请求
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @SaCheckPermission("sys:user:edit")
    @Operation(summary = "更新用户")
    public ApiResponse<UserVO> update(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        return ApiResponse.success("用户更新成功", userService.update(id, dto));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:user:delete")
    @Operation(summary = "删除用户")
    public ApiResponse<Void> delete(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success("用户删除成功", null);
    }

    /**
     * 重置密码
     *
     * @param id  用户ID
     * @param dto 重置密码请求
     * @return 操作结果
     */
    @PutMapping("/{id}/reset-password")
    @SaCheckPermission("sys:user:edit")
    @Operation(summary = "重置用户密码")
    public ApiResponse<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody ResetPasswordDTO dto) {
        userService.resetPassword(id, dto);
        return ApiResponse.success("密码重置成功", null);
    }

    /**
     * 更新用户状态
     *
     * @param id  用户ID
     * @param dto 状态更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @SaCheckPermission("sys:user:edit")
    @Operation(summary = "更新用户状态")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserStatusDTO dto) {
        userService.updateStatus(id, dto);
        return ApiResponse.success("状态更新成功", null);
    }

    /**
     * 分配用户角色
     *
     * @param id  用户ID
     * @param dto 角色分配请求
     * @return 操作结果
     */
    @PutMapping("/{id}/role")
    @SaCheckPermission("sys:user:edit")
    @Operation(summary = "分配用户角色")
    public ApiResponse<Void> assignRoles(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserRoleDTO dto) {
        userService.assignRoles(id, dto);
        return ApiResponse.success("角色分配成功", null);
    }
}
