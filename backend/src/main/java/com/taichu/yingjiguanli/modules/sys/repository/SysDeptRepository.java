package com.taichu.yingjiguanli.modules.sys.repository;

import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门数据访问接口
 *
 * @author CX
 * @since 2026-01-13
 */
@Repository
public interface SysDeptRepository extends JpaRepository<SysDept, Long>, JpaSpecificationExecutor<SysDept> {

    /**
     * 查询所有未删除的部门
     *
     * @return 部门列表
     */
    List<SysDept> findByDelFlagOrderBySortAsc(Integer delFlag);

    /**
     * 根据父ID查询子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    List<SysDept> findByParentIdAndDelFlagOrderBySortAsc(Long parentId, Integer delFlag);

    /**
     * 统计部门下的用户数量
     *
     * @param deptId 部门ID
     * @return 用户数量
     */
    @Query("SELECT COUNT(u) FROM SysUser u WHERE u.deptId = :deptId AND u.delFlag = 0")
    long countUsersByDeptId(Long deptId);

    /**
     * 查询部门及其所有下级部门ID
     *
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    @Query(value = """
            WITH RECURSIVE dept_tree AS (
                SELECT id FROM sys_dept WHERE id = :deptId AND del_flag = 0
                UNION ALL
                SELECT d.id FROM sys_dept d
                INNER JOIN dept_tree dt ON d.parent_id = dt.id
                WHERE d.del_flag = 0
            )
            SELECT id FROM dept_tree
            """, nativeQuery = true)
    List<Long> findDeptAndChildrenIds(Long deptId);
}
