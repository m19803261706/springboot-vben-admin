package com.taichu.yingjiguanli.modules.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.modules.auth.dto.LoginDTO;
import com.taichu.yingjiguanli.modules.auth.service.AuthService;
import com.taichu.yingjiguanli.modules.auth.vo.LoginVO;
import com.taichu.yingjiguanli.modules.auth.vo.MenuVO;
import com.taichu.yingjiguanli.modules.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 * 处理登录、登出、获取用户信息等认证相关接口
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、登出、用户信息等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return Token 信息
     */
    @SaIgnore
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名密码登录，返回访问令牌")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return ApiResponse.success(authService.login(dto));
    }

    /**
     * 用户登出
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录，使当前 Token 失效")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success();
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息，包括角色和权限")
    public ApiResponse<UserInfoVO> getUserInfo() {
        return ApiResponse.success(authService.getCurrentUserInfo());
    }

    /**
     * 获取当前用户菜单
     *
     * @return 菜单树
     */
    @GetMapping("/menus")
    @Operation(summary = "获取当前用户菜单", description = "获取当前登录用户的菜单树，用于前端动态路由")
    public ApiResponse<List<MenuVO>> getUserMenus() {
        return ApiResponse.success(authService.getCurrentUserMenus());
    }

    /**
     * 获取当前用户权限列表
     *
     * @return 权限标识列表
     */
    @GetMapping("/permissions")
    @Operation(summary = "获取当前用户权限", description = "获取当前登录用户的所有权限标识")
    public ApiResponse<List<String>> getUserPermissions() {
        return ApiResponse.success(authService.getCurrentUserPermissions());
    }
}
