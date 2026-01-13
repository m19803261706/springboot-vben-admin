package com.taichu.yingjiguanli.modules.sys.repository;

import com.taichu.yingjiguanli.modules.sys.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单数据访问接口
 *
 * @author CX
 * @since 2026-01-13
 */
@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long>, JpaSpecificationExecutor<SysMenu> {

    /**
     * 查询所有启用的菜单
     *
     * @return 菜单列表
     */
    List<SysMenu> findByStatusOrderBySortAsc(Integer status);

    /**
     * 根据父ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    List<SysMenu> findByParentIdAndStatusOrderBySortAsc(Long parentId, Integer status);

    /**
     * 查询用户拥有的权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    @Query("""
            SELECT DISTINCT m.permission FROM SysMenu m
            INNER JOIN SysRole r ON m MEMBER OF r.menus
            INNER JOIN SysUser u ON r MEMBER OF u.roles
            WHERE u.id = :userId
            AND m.status = 1
            AND m.permission IS NOT NULL
            AND m.permission != ''
            AND r.status = 1
            AND r.delFlag = 0
            """)
    List<String> findPermissionsByUserId(Long userId);

    /**
     * 查询用户拥有的菜单列表 (不含按钮)
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Query("""
            SELECT DISTINCT m FROM SysMenu m
            INNER JOIN SysRole r ON m MEMBER OF r.menus
            INNER JOIN SysUser u ON r MEMBER OF u.roles
            WHERE u.id = :userId
            AND m.menuType != 2
            AND m.status = 1
            AND r.status = 1
            AND r.delFlag = 0
            ORDER BY m.sort ASC
            """)
    List<SysMenu> findMenusByUserId(Long userId);

    /**
     * 查询角色拥有的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Query("""
            SELECT m.id FROM SysMenu m
            INNER JOIN SysRole r ON m MEMBER OF r.menus
            WHERE r.id = :roleId AND m.status = 1
            """)
    List<Long> findMenuIdsByRoleId(Long roleId);

    /**
     * 统计子菜单数量
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    long countByParentId(Long parentId);

    /**
     * 查询所有菜单 (包含目录、菜单、按钮)
     *
     * @return 菜单列表
     */
    @Query("SELECT m FROM SysMenu m ORDER BY m.sort ASC")
    List<SysMenu> findAllOrderBySort();
}
