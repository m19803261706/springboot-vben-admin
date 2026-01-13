package com.taichu.yingjiguanli.modules.sys.service.impl;

import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import com.taichu.yingjiguanli.modules.sys.entity.SysMenu;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysMenuRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.service.SysRoleService;
import com.taichu.yingjiguanli.modules.sys.vo.RoleVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;
    private final SysDeptRepository deptRepository;

    @Override
    @Transactional
    public RoleVO create(RoleCreateDTO dto) {
        log.info("创建角色: roleCode={}", dto.getRoleCode());

        // 检查角色编码是否已存在
        if (roleRepository.existsByRoleCodeAndDelFlag(dto.getRoleCode(), 0)) {
            throw new BusinessException("角色编码已存在");
        }

        // 创建角色实体
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setDataScope(dto.getDataScope() != null ? dto.getDataScope() : 1);
        role.setSort(dto.getSort() != null ? dto.getSort() : 0);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setRemark(dto.getRemark());
        role.setDelFlag(0);

        // 设置菜单
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            Set<SysMenu> menus = new HashSet<>(menuRepository.findAllById(dto.getMenuIds()));
            role.setMenus(menus);
        }

        // 设置数据权限部门 (自定义模式)
        if (dto.getDataScope() != null && dto.getDataScope() == 5
                && dto.getDeptIds() != null && !dto.getDeptIds().isEmpty()) {
            Set<SysDept> depts = new HashSet<>(deptRepository.findAllById(dto.getDeptIds()));
            role.setDepts(depts);
        }

        // 保存角色
        SysRole savedRole = roleRepository.save(role);
        log.info("角色创建成功: id={}", savedRole.getId());

        return convertToVO(savedRole);
    }

    @Override
    @Transactional
    public RoleVO update(Long id, RoleUpdateDTO dto) {
        log.info("更新角色: id={}", id);

        // 查找角色
        SysRole role = roleRepository.findById(id)
                .filter(r -> r.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        // 禁止修改 admin 角色编码
        if ("admin".equals(role.getRoleCode())) {
            log.warn("尝试修改管理员角色: id={}", id);
        }

        // 更新属性
        if (StringUtils.hasText(dto.getRoleName())) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getDataScope() != null) {
            role.setDataScope(dto.getDataScope());
        }
        if (dto.getSort() != null) {
            role.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            role.setRemark(dto.getRemark());
        }

        // 更新菜单
        if (dto.getMenuIds() != null) {
            Set<SysMenu> menus = new HashSet<>(menuRepository.findAllById(dto.getMenuIds()));
            role.setMenus(menus);
        }

        // 更新数据权限部门
        if (dto.getDeptIds() != null) {
            if (dto.getDataScope() != null && dto.getDataScope() == 5) {
                Set<SysDept> depts = new HashSet<>(deptRepository.findAllById(dto.getDeptIds()));
                role.setDepts(depts);
            } else {
                role.setDepts(new HashSet<>());
            }
        }

        // 保存角色
        SysRole savedRole = roleRepository.save(role);
        log.info("角色更新成功: id={}", id);

        return convertToVO(savedRole);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除角色: id={}", id);

        // 检查角色是否存在
        SysRole role = roleRepository.findById(id)
                .filter(r -> r.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        // 禁止删除 admin 角色
        if ("admin".equals(role.getRoleCode())) {
            throw new BusinessException("不能删除管理员角色");
        }

        // 软删除
        roleRepository.softDelete(id);
        log.info("角色删除成功: id={}", id);
    }

    @Override
    public Optional<RoleVO> findById(Long id) {
        return roleRepository.findById(id)
                .filter(r -> r.getDelFlag() == 0)
                .map(this::convertToVO);
    }

    @Override
    public Page<RoleVO> findPage(RoleQueryDTO query) {
        log.info("分页查询角色: {}", query);

        // 构建查询条件
        Specification<SysRole> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 未删除
            predicates.add(cb.equal(root.get("delFlag"), 0));

            // 角色名称模糊搜索
            if (StringUtils.hasText(query.getRoleName())) {
                predicates.add(cb.like(root.get("roleName"), "%" + query.getRoleName() + "%"));
            }

            // 角色编码模糊搜索
            if (StringUtils.hasText(query.getRoleCode())) {
                predicates.add(cb.like(root.get("roleCode"), "%" + query.getRoleCode() + "%"));
            }

            // 状态筛选
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                query.getPage() != null ? query.getPage() : 0,
                query.getSize() != null ? query.getSize() : 10,
                Sort.by(Sort.Direction.ASC, "sort")
        );

        Page<SysRole> page = roleRepository.findAll(spec, pageRequest);

        return page.map(this::convertToVO);
    }

    @Override
    public List<RoleVO> findAll() {
        List<SysRole> roles = roleRepository.findByDelFlagOrderBySortAsc(0);
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignMenus(Long id, RoleMenuDTO dto) {
        log.info("分配角色菜单: id={}, menuIds={}", id, dto.getMenuIds());

        // 查找角色
        SysRole role = roleRepository.findById(id)
                .filter(r -> r.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        // 获取菜单
        Set<SysMenu> menus = dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()
                ? new HashSet<>(menuRepository.findAllById(dto.getMenuIds()))
                : new HashSet<>();

        // 更新角色菜单
        role.setMenus(menus);
        roleRepository.save(role);
        log.info("角色菜单分配成功: id={}", id);
    }

    @Override
    @Transactional
    public void setDataScope(Long id, RoleDataScopeDTO dto) {
        log.info("设置角色数据权限: id={}, dataScope={}", id, dto.getDataScope());

        // 查找角色
        SysRole role = roleRepository.findById(id)
                .filter(r -> r.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        // 设置数据权限范围
        role.setDataScope(dto.getDataScope());

        // 设置自定义部门
        if (dto.getDataScope() == 5) {
            if (dto.getDeptIds() != null && !dto.getDeptIds().isEmpty()) {
                Set<SysDept> depts = new HashSet<>(deptRepository.findAllById(dto.getDeptIds()));
                role.setDepts(depts);
            }
        } else {
            // 非自定义模式，清空部门
            role.setDepts(new HashSet<>());
        }

        roleRepository.save(role);
        log.info("角色数据权限设置成功: id={}", id);
    }

    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        return roleRepository.findRoleCodesByUserId(userId);
    }

    @Override
    public List<SysRole> findRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    /**
     * 将角色实体转换为视图对象
     *
     * @param role 角色实体
     * @return 角色视图对象
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDataScope(role.getDataScope());
        vo.setDataScopeDesc(RoleVO.getDataScopeDesc(role.getDataScope()));
        vo.setSort(role.getSort());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreatedAt(role.getCreateTime());
        vo.setUpdatedAt(role.getUpdateTime());

        // 设置菜单ID列表
        if (role.getMenus() != null && !role.getMenus().isEmpty()) {
            List<Long> menuIds = role.getMenus().stream()
                    .map(SysMenu::getId)
                    .collect(Collectors.toList());
            vo.setMenuIds(menuIds);
        } else {
            vo.setMenuIds(Collections.emptyList());
        }

        // 设置部门ID列表
        if (role.getDepts() != null && !role.getDepts().isEmpty()) {
            List<Long> deptIds = role.getDepts().stream()
                    .map(SysDept::getId)
                    .collect(Collectors.toList());
            vo.setDeptIds(deptIds);
        } else {
            vo.setDeptIds(Collections.emptyList());
        }

        return vo;
    }
}
