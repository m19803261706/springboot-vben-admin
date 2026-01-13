package com.taichu.yingjiguanli.modules.sys.service;

import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.vo.RoleVO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务接口
 *
 * @author CX
 * @since 2026-01-13
 */
public interface SysRoleService {

    /**
     * 创建角色
     *
     * @param dto 创建请求
     * @return 角色视图对象
     */
    RoleVO create(RoleCreateDTO dto);

    /**
     * 更新角色
     *
     * @param id  角色ID
     * @param dto 更新请求
     * @return 角色视图对象
     */
    RoleVO update(Long id, RoleUpdateDTO dto);

    /**
     * 删除角色 (软删除)
     *
     * @param id 角色ID
     */
    void delete(Long id);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色视图对象
     */
    Optional<RoleVO> findById(Long id);

    /**
     * 分页查询角色
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<RoleVO> findPage(RoleQueryDTO query);

    /**
     * 查询所有角色 (下拉选择用)
     *
     * @return 角色列表
     */
    List<RoleVO> findAll();

    /**
     * 分配菜单权限
     *
     * @param id  角色ID
     * @param dto 菜单分配请求
     */
    void assignMenus(Long id, RoleMenuDTO dto);

    /**
     * 设置数据权限范围
     *
     * @param id  角色ID
     * @param dto 数据权限设置请求
     */
    void setDataScope(Long id, RoleDataScopeDTO dto);

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> findRoleCodesByUserId(Long userId);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> findRolesByUserId(Long userId);
}
