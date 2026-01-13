package com.taichu.yingjiguanli.modules.sys.repository;

import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问接口
 *
 * @author CX
 * @since 2026-01-13
 */
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long>, JpaSpecificationExecutor<SysRole> {

    /**
     * 查询所有未删除的角色
     *
     * @return 角色列表
     */
    List<SysRole> findByDelFlagOrderBySortAsc(Integer delFlag);

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Optional<SysRole> findByRoleCodeAndDelFlag(String roleCode, Integer delFlag);

    /**
     * 检查角色编码是否存在
     *
     * @param roleCode 角色编码
     * @return 是否存在
     */
    boolean existsByRoleCodeAndDelFlag(String roleCode, Integer delFlag);

    /**
     * 检查角色编码是否存在 (排除指定ID)
     *
     * @param roleCode 角色编码
     * @param id       排除的角色ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(r) > 0 FROM SysRole r WHERE r.roleCode = :roleCode AND r.id != :id AND r.delFlag = 0")
    boolean existsByRoleCodeAndIdNot(String roleCode, Long id);

    /**
     * 查询用户拥有的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @Query("""
            SELECT r.roleCode FROM SysRole r
            INNER JOIN SysUser u ON r MEMBER OF u.roles
            WHERE u.id = :userId AND r.status = 1 AND r.delFlag = 0
            """)
    List<String> findRoleCodesByUserId(Long userId);

    /**
     * 查询用户拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("""
            SELECT r FROM SysRole r
            INNER JOIN SysUser u ON r MEMBER OF u.roles
            WHERE u.id = :userId AND r.delFlag = 0
            """)
    List<SysRole> findRolesByUserId(Long userId);

    /**
     * 软删除角色
     *
     * @param id 角色ID
     * @return 更新数量
     */
    @Modifying
    @Query("UPDATE SysRole r SET r.delFlag = 1 WHERE r.id = :id")
    int softDelete(Long id);
}
