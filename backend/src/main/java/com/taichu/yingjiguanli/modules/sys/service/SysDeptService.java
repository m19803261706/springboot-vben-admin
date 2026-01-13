package com.taichu.yingjiguanli.modules.sys.service;

import com.taichu.yingjiguanli.modules.sys.dto.DeptCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.DeptUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.vo.DeptVO;

import java.util.List;
import java.util.Optional;

/**
 * 部门服务接口
 *
 * @author CX
 * @since 2026-01-13
 */
public interface SysDeptService {

    /**
     * 创建部门
     *
     * @param dto 创建请求
     * @return 部门视图对象
     */
    DeptVO create(DeptCreateDTO dto);

    /**
     * 更新部门
     *
     * @param id  部门ID
     * @param dto 更新请求
     * @return 部门视图对象
     */
    DeptVO update(Long id, DeptUpdateDTO dto);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void delete(Long id);

    /**
     * 根据ID查询部门
     *
     * @param id 部门ID
     * @return 部门视图对象
     */
    Optional<DeptVO> findById(Long id);

    /**
     * 查询部门树
     *
     * @return 部门树
     */
    List<DeptVO> findTree();

    /**
     * 查询所有部门列表 (平铺)
     *
     * @return 部门列表
     */
    List<DeptVO> findAll();

    /**
     * 获取部门及其所有下级部门ID列表
     *
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    List<Long> findDeptAndChildrenIds(Long deptId);
}
