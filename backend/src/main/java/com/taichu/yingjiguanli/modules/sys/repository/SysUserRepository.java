package com.taichu.yingjiguanli.modules.sys.repository;

import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 *
 * @author CX
 * @since 2026-01-13
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<SysUser> findByUsernameAndDelFlag(String username, Integer delFlag);

    /**
     * 根据用户名查询用户 (包含角色信息)
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Query("SELECT u FROM SysUser u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.delFlag = 0")
    Optional<SysUser> findByUsernameWithRoles(String username);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsernameAndDelFlag(String username, Integer delFlag);

    /**
     * 检查用户名是否存在 (排除指定ID)
     *
     * @param username 用户名
     * @param id       排除的用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM SysUser u WHERE u.username = :username AND u.id != :id AND u.delFlag = 0")
    boolean existsByUsernameAndIdNot(String username, Long id);

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 状态
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE SysUser u SET u.status = :status WHERE u.id = :id")
    int updateStatus(Long id, Integer status);

    /**
     * 更新用户密码
     *
     * @param id       用户ID
     * @param password 新密码
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE SysUser u SET u.password = :password WHERE u.id = :id")
    int updatePassword(Long id, String password);

    /**
     * 软删除用户
     *
     * @param id 用户ID
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE SysUser u SET u.delFlag = 1 WHERE u.id = :id")
    int softDelete(Long id);
}
