package com.taichu.yingjiguanli.modules.sys.service;

import com.taichu.yingjiguanli.modules.sys.dto.MenuCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.MenuUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.vo.MenuVO;

import java.util.List;
import java.util.Optional;

/**
 * 菜单服务接口
 *
 * @author CX
 * @since 2026-01-13
 */
public interface SysMenuService {

    /**
     * 创建菜单
     *
     * @param dto 创建请求
     * @return 菜单视图对象
     */
    MenuVO create(MenuCreateDTO dto);

    /**
     * 更新菜单
     *
     * @param id  菜单ID
     * @param dto 更新请求
     * @return 菜单视图对象
     */
    MenuVO update(Long id, MenuUpdateDTO dto);

    /**
     * 删除菜单 (级联删除子菜单)
     *
     * @param id 菜单ID
     */
    void delete(Long id);

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单视图对象
     */
    Optional<MenuVO> findById(Long id);

    /**
     * 查询菜单树
     *
     * @return 菜单树
     */
    List<MenuVO> findTree();

    /**
     * 查询所有菜单列表 (平铺)
     *
     * @return 菜单列表
     */
    List<MenuVO> findAll();

    /**
     * 根据用户ID查询权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> findPermissionsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单树 (用于前端动态菜单)
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuVO> findUserMenuTree(Long userId);
}
