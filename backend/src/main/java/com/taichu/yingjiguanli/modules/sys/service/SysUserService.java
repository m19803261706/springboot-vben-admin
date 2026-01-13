package com.taichu.yingjiguanli.modules.sys.service;

import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.vo.UserVO;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * 用户服务接口
 *
 * @author CX
 * @since 2026-01-13
 */
public interface SysUserService {

    /**
     * 创建用户
     *
     * @param dto 创建请求
     * @return 用户视图对象
     */
    UserVO create(UserCreateDTO dto);

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 更新请求
     * @return 用户视图对象
     */
    UserVO update(Long id, UserUpdateDTO dto);

    /**
     * 删除用户 (软删除)
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户视图对象
     */
    Optional<UserVO> findById(Long id);

    /**
     * 分页查询用户
     *
     * @param query 查询条件
     * @return 分页结果
     */
    Page<UserVO> findPage(UserQueryDTO query);

    /**
     * 重置密码
     *
     * @param id  用户ID
     * @param dto 重置密码请求
     */
    void resetPassword(Long id, ResetPasswordDTO dto);

    /**
     * 更新用户状态
     *
     * @param id  用户ID
     * @param dto 状态更新请求
     */
    void updateStatus(Long id, UserStatusDTO dto);

    /**
     * 分配角色
     *
     * @param id  用户ID
     * @param dto 角色分配请求
     */
    void assignRoles(Long id, UserRoleDTO dto);

    /**
     * 根据用户名查询用户实体
     *
     * @param username 用户名
     * @return 用户实体
     */
    Optional<SysUser> findByUsername(String username);
}
