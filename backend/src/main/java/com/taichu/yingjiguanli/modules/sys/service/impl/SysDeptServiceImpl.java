package com.taichu.yingjiguanli.modules.sys.service.impl;

import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.DeptCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.DeptUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.service.SysDeptService;
import com.taichu.yingjiguanli.modules.sys.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptRepository deptRepository;

    @Override
    @Transactional
    public DeptVO create(DeptCreateDTO dto) {
        log.info("创建部门: deptName={}", dto.getDeptName());

        // 创建部门实体
        SysDept dept = new SysDept();
        dept.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        dept.setDeptName(dto.getDeptName());
        dept.setLeader(dto.getLeader());
        dept.setPhone(dto.getPhone());
        dept.setSort(dto.getSort() != null ? dto.getSort() : 0);
        dept.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 验证父部门
        if (dept.getParentId() != 0) {
            SysDept parentDept = deptRepository.findById(dept.getParentId())
                    .orElseThrow(() -> new BusinessException(404, "父部门不存在"));
            if (parentDept.getDelFlag() == 1) {
                throw new BusinessException("父部门已被删除");
            }
        }

        // 保存部门
        SysDept savedDept = deptRepository.save(dept);
        log.info("部门创建成功: id={}", savedDept.getId());

        return convertToVO(savedDept);
    }

    @Override
    @Transactional
    public DeptVO update(Long id, DeptUpdateDTO dto) {
        log.info("更新部门: id={}", id);

        // 查找部门
        SysDept dept = deptRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "部门不存在"));

        if (dept.getDelFlag() == 1) {
            throw new BusinessException("部门已被删除");
        }

        // 更新属性
        if (dto.getParentId() != null) {
            // 验证不能将自己设为父部门
            if (dto.getParentId().equals(id)) {
                throw new BusinessException("不能将自己设为父部门");
            }
            // 验证不能将子部门设为父部门
            List<Long> childrenIds = deptRepository.findDeptAndChildrenIds(id);
            if (childrenIds.contains(dto.getParentId())) {
                throw new BusinessException("不能将子部门设为父部门");
            }
            // 验证父部门存在
            if (dto.getParentId() != 0) {
                SysDept parentDept = deptRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new BusinessException(404, "父部门不存在"));
                if (parentDept.getDelFlag() == 1) {
                    throw new BusinessException("父部门已被删除");
                }
            }
            dept.setParentId(dto.getParentId());
        }
        if (StringUtils.hasText(dto.getDeptName())) {
            dept.setDeptName(dto.getDeptName());
        }
        if (dto.getLeader() != null) {
            dept.setLeader(dto.getLeader());
        }
        if (dto.getPhone() != null) {
            dept.setPhone(dto.getPhone());
        }
        if (dto.getSort() != null) {
            dept.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            dept.setStatus(dto.getStatus());
        }

        // 保存部门
        SysDept savedDept = deptRepository.save(dept);
        log.info("部门更新成功: id={}", id);

        return convertToVO(savedDept);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除部门: id={}", id);

        // 检查部门是否存在
        SysDept dept = deptRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "部门不存在"));

        if (dept.getDelFlag() == 1) {
            throw new BusinessException("部门已被删除");
        }

        // 检查是否有子部门
        List<SysDept> children = deptRepository.findByParentIdAndDelFlagOrderBySortAsc(id, 0);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子部门，不能删除");
        }

        // 检查是否有用户
        long userCount = deptRepository.countUsersByDeptId(id);
        if (userCount > 0) {
            throw new BusinessException("部门下存在用户，不能删除");
        }

        // 软删除
        dept.setDelFlag(1);
        deptRepository.save(dept);
        log.info("部门删除成功: id={}", id);
    }

    @Override
    public Optional<DeptVO> findById(Long id) {
        return deptRepository.findById(id)
                .filter(dept -> dept.getDelFlag() == 0)
                .map(this::convertToVO);
    }

    @Override
    public List<DeptVO> findTree() {
        // 查询所有未删除的部门
        List<SysDept> allDepts = deptRepository.findByDelFlagOrderBySortAsc(0);

        // 转换为 VO
        List<DeptVO> allVOs = allDepts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public List<DeptVO> findAll() {
        List<SysDept> allDepts = deptRepository.findByDelFlagOrderBySortAsc(0);
        return allDepts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findDeptAndChildrenIds(Long deptId) {
        return deptRepository.findDeptAndChildrenIds(deptId);
    }

    /**
     * 构建部门树
     *
     * @param allDepts 所有部门列表
     * @param parentId 父部门ID
     * @return 树形部门列表
     */
    private List<DeptVO> buildTree(List<DeptVO> allDepts, Long parentId) {
        List<DeptVO> result = new ArrayList<>();

        for (DeptVO dept : allDepts) {
            if (Objects.equals(dept.getParentId(), parentId)) {
                // 递归获取子部门
                List<DeptVO> children = buildTree(allDepts, dept.getId());
                dept.setChildren(children);
                result.add(dept);
            }
        }

        // 按 sort 排序
        result.sort(Comparator.comparingInt(d -> d.getSort() != null ? d.getSort() : 0));

        return result;
    }

    /**
     * 将部门实体转换为视图对象
     *
     * @param dept 部门实体
     * @return 部门视图对象
     */
    private DeptVO convertToVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        vo.setId(dept.getId());
        vo.setParentId(dept.getParentId());
        vo.setDeptName(dept.getDeptName());
        vo.setLeader(dept.getLeader());
        vo.setPhone(dept.getPhone());
        vo.setSort(dept.getSort());
        vo.setStatus(dept.getStatus());
        vo.setCreatedAt(dept.getCreateTime());
        vo.setUpdatedAt(dept.getUpdateTime());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
