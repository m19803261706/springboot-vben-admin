package com.taichu.yingjiguanli.common.datascope;

import cn.dev33.satoken.stp.StpUtil;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysUserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限辅助类
 * 用于在 JPA Specification 中构建数据权限条件
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataScopeHelper {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysDeptRepository deptRepository;

    /**
     * 构建数据权限 Predicate
     *
     * @param root            实体根
     * @param criteriaBuilder 条件构建器
     * @param deptIdField     部门ID字段名
     * @param createByField   创建人字段名（用于仅本人数据权限）
     * @param <T>             实体类型
     * @return Predicate 条件，如果无权限限制则返回 null
     */
    public <T> Predicate buildDataScopePredicate(
            Root<T> root,
            CriteriaBuilder criteriaBuilder,
            String deptIdField,
            String createByField) {

        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            log.debug("用户未登录，跳过数据权限过滤");
            return null;
        }

        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 获取用户信息
        SysUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            return null;
        }

        // 获取用户所有角色
        List<SysRole> roles = roleRepository.findRolesByUserId(userId);
        if (roles.isEmpty()) {
            log.debug("用户无角色，跳过数据权限过滤: userId={}", userId);
            return null;
        }

        // 构建数据权限条件
        List<Predicate> predicates = new ArrayList<>();

        for (SysRole role : roles) {
            Integer scopeType = role.getDataScope();
            if (scopeType == null) {
                scopeType = DataScopeType.DATA_SCOPE_ALL.getCode();
            }

            DataScopeType dataScopeType = DataScopeType.fromCode(scopeType);

            switch (dataScopeType) {
                case DATA_SCOPE_ALL:
                    // 全部数据权限，返回 null 表示无限制
                    return null;

                case DATA_SCOPE_DEPT:
                    // 本部门数据
                    if (deptIdField != null && user.getDeptId() != null) {
                        predicates.add(criteriaBuilder.equal(root.get(deptIdField), user.getDeptId()));
                    }
                    break;

                case DATA_SCOPE_DEPT_AND_CHILD:
                    // 本部门及下级部门
                    if (deptIdField != null && user.getDeptId() != null) {
                        List<Long> deptIds = deptRepository.findDeptAndChildrenIds(user.getDeptId());
                        if (!deptIds.isEmpty()) {
                            predicates.add(root.get(deptIdField).in(deptIds));
                        }
                    }
                    break;

                case DATA_SCOPE_SELF:
                    // 仅本人数据
                    if (createByField != null) {
                        predicates.add(criteriaBuilder.equal(root.get(createByField), userId));
                    }
                    break;

                case DATA_SCOPE_CUSTOM:
                    // 自定义部门数据权限
                    if (deptIdField != null) {
                        Set<Long> customDeptIds = role.getDepts().stream()
                                .map(dept -> dept.getId())
                                .collect(Collectors.toSet());
                        if (!customDeptIds.isEmpty()) {
                            predicates.add(root.get(deptIdField).in(customDeptIds));
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        // 合并所有条件 (使用 OR)
        if (!predicates.isEmpty()) {
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        }

        return null;
    }

    /**
     * 获取当前用户可访问的部门ID列表
     *
     * @return 部门ID列表，如果是全部权限则返回 null
     */
    public Set<Long> getAccessibleDeptIds() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            return new HashSet<>();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new HashSet<>();
        }

        List<SysRole> roles = roleRepository.findRolesByUserId(userId);
        if (roles.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> accessibleDeptIds = new HashSet<>();

        for (SysRole role : roles) {
            Integer scopeType = role.getDataScope();
            if (scopeType == null) {
                scopeType = DataScopeType.DATA_SCOPE_ALL.getCode();
            }

            DataScopeType dataScopeType = DataScopeType.fromCode(scopeType);

            switch (dataScopeType) {
                case DATA_SCOPE_ALL:
                    // 全部数据权限，返回 null 表示无限制
                    return null;

                case DATA_SCOPE_DEPT:
                    if (user.getDeptId() != null) {
                        accessibleDeptIds.add(user.getDeptId());
                    }
                    break;

                case DATA_SCOPE_DEPT_AND_CHILD:
                    if (user.getDeptId() != null) {
                        List<Long> deptIds = deptRepository.findDeptAndChildrenIds(user.getDeptId());
                        accessibleDeptIds.addAll(deptIds);
                    }
                    break;

                case DATA_SCOPE_CUSTOM:
                    Set<Long> customDeptIds = role.getDepts().stream()
                            .map(dept -> dept.getId())
                            .collect(Collectors.toSet());
                    accessibleDeptIds.addAll(customDeptIds);
                    break;

                default:
                    break;
            }
        }

        return accessibleDeptIds;
    }

    /**
     * 检查当前用户是否有全部数据权限
     *
     * @return true 有全部权限，false 无
     */
    public boolean hasAllDataScope() {
        if (!StpUtil.isLogin()) {
            return false;
        }

        Long userId = StpUtil.getLoginIdAsLong();
        List<SysRole> roles = roleRepository.findRolesByUserId(userId);

        for (SysRole role : roles) {
            Integer scopeType = role.getDataScope();
            if (scopeType != null && scopeType == DataScopeType.DATA_SCOPE_ALL.getCode()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查当前用户是否仅有本人数据权限
     *
     * @return true 仅本人权限，false 有其他权限
     */
    public boolean isSelfDataScopeOnly() {
        if (!StpUtil.isLogin()) {
            return true;
        }

        Long userId = StpUtil.getLoginIdAsLong();
        List<SysRole> roles = roleRepository.findRolesByUserId(userId);

        if (roles.isEmpty()) {
            return true;
        }

        // 检查是否所有角色都是仅本人权限
        for (SysRole role : roles) {
            Integer scopeType = role.getDataScope();
            if (scopeType == null) {
                scopeType = DataScopeType.DATA_SCOPE_ALL.getCode();
            }
            if (scopeType != DataScopeType.DATA_SCOPE_SELF.getCode()) {
                return false;
            }
        }

        return true;
    }
}
