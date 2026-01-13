package com.taichu.yingjiguanli.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.taichu.yingjiguanli.common.ApiResponse;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.MenuCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.MenuUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.service.SysMenuService;
import com.taichu.yingjiguanli.modules.sys.vo.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author CX
 * @since 2026-01-13
 */
@RestController
@RequestMapping("/api/sys/menu")
@RequiredArgsConstructor
@SaCheckLogin
@Tag(name = "菜单管理", description = "菜单 CRUD 接口")
public class SysMenuController {

    private final SysMenuService menuService;

    /**
     * 查询菜单树
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @SaCheckPermission("sys:menu:list")
    @Operation(summary = "查询菜单树")
    public ApiResponse<List<MenuVO>> tree() {
        return ApiResponse.success(menuService.findTree());
    }

    /**
     * 查询所有菜单列表 (平铺)
     *
     * @return 菜单列表
     */
    @GetMapping("/list")
    @SaCheckPermission("sys:menu:list")
    @Operation(summary = "查询所有菜单列表")
    public ApiResponse<List<MenuVO>> list() {
        return ApiResponse.success(menuService.findAll());
    }

    /**
     * 查询菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}")
    @SaCheckPermission("sys:menu:list")
    @Operation(summary = "查询菜单详情")
    public ApiResponse<MenuVO> getById(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        return ApiResponse.success(menuService.findById(id)
                .orElseThrow(() -> new BusinessException(404, "菜单不存在")));
    }

    /**
     * 新增菜单
     *
     * @param dto 创建请求
     * @return 创建的菜单
     */
    @PostMapping
    @SaCheckPermission("sys:menu:add")
    @Operation(summary = "新增菜单")
    public ApiResponse<MenuVO> create(@Valid @RequestBody MenuCreateDTO dto) {
        return ApiResponse.success("菜单创建成功", menuService.create(dto));
    }

    /**
     * 更新菜单
     *
     * @param id  菜单ID
     * @param dto 更新请求
     * @return 更新后的菜单
     */
    @PutMapping("/{id}")
    @SaCheckPermission("sys:menu:edit")
    @Operation(summary = "更新菜单")
    public ApiResponse<MenuVO> update(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @Valid @RequestBody MenuUpdateDTO dto) {
        return ApiResponse.success("菜单更新成功", menuService.update(id, dto));
    }

    /**
     * 删除菜单 (级联删除子菜单)
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("sys:menu:delete")
    @Operation(summary = "删除菜单")
    public ApiResponse<Void> delete(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.delete(id);
        return ApiResponse.success("菜单删除成功", null);
    }
}
