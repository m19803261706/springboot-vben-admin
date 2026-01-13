package com.taichu.yingjiguanli.common.datascope;

import cn.dev33.satoken.stp.StpUtil;
import com.taichu.yingjiguanli.common.annotation.DataScope;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限 AOP 切面
 * 根据用户角色的数据权限范围，生成 SQL 过滤条件
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysDeptRepository deptRepository;

    /**
     * 前置通知：在方法执行前生成数据权限 SQL
     *
     * @param joinPoint 切点
     * @param dataScope 数据权限注解
     */
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint joinPoint, DataScope dataScope) {
        // 清除之前的数据权限条件
        DataScopeContext.clear();

        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            log.debug("用户未登录，跳过数据权限过滤");
            return;
        }

        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 获取用户信息
        SysUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("用户不存在: userId={}", userId);
            return;
        }

        // 获取用户所有角色
        List<SysRole> roles = roleRepository.findRolesByUserId(userId);
        if (roles.isEmpty()) {
            log.debug("用户无角色，跳过数据权限过滤: userId={}", userId);
            return;
        }

        // 生成数据权限 SQL
        String sql = buildDataScopeSql(user, roles, dataScope);
        if (StringUtils.hasText(sql)) {
            DataScopeContext.setDataScopeSql(sql);
            log.debug("数据权限SQL: {}", sql);
        }
    }

    /**
     * 后置通知：清除数据权限上下文
     */
    @After("@annotation(com.taichu.yingjiguanli.common.annotation.DataScope)")
    public void doAfter() {
        DataScopeContext.clear();
    }

    /**
     * 构建数据权限 SQL 条件
     *
     * @param user      当前用户
     * @param roles     用户角色列表
     * @param dataScope 数据权限注解
     * @return SQL 条件片段
     */
    private String buildDataScopeSql(SysUser user, List<SysRole> roles, DataScope dataScope) {
        StringBuilder sql = new StringBuilder();
        Set<String> conditions = new HashSet<>();

        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();
        String deptIdColumn = dataScope.deptIdColumn();
        String userIdColumn = dataScope.userIdColumn();

        // 遍历角色，获取数据权限范围
        for (SysRole role : roles) {
            Integer scopeType = role.getDataScope();
            if (scopeType == null) {
                scopeType = DataScopeType.DATA_SCOPE_ALL.getCode();
            }

            DataScopeType dataScopeType = DataScopeType.fromCode(scopeType);

            switch (dataScopeType) {
                case DATA_SCOPE_ALL:
                    // 全部数据权限，无需添加条件
                    return "";

                case DATA_SCOPE_DEPT:
                    // 本部门数据
                    if (StringUtils.hasText(deptAlias)) {
                        conditions.add(String.format("%s.%s = %d",
                                deptAlias, deptIdColumn, user.getDeptId()));
                    } else {
                        conditions.add(String.format("%s = %d",
                                deptIdColumn, user.getDeptId()));
                    }
                    break;

                case DATA_SCOPE_DEPT_AND_CHILD:
                    // 本部门及下级部门
                    List<Long> deptIds = deptRepository.findDeptAndChildrenIds(user.getDeptId());
                    if (!deptIds.isEmpty()) {
                        String deptIdsStr = deptIds.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        if (StringUtils.hasText(deptAlias)) {
                            conditions.add(String.format("%s.%s IN (%s)",
                                    deptAlias, deptIdColumn, deptIdsStr));
                        } else {
                            conditions.add(String.format("%s IN (%s)",
                                    deptIdColumn, deptIdsStr));
                        }
                    }
                    break;

                case DATA_SCOPE_SELF:
                    // 仅本人数据
                    if (StringUtils.hasText(userAlias)) {
                        conditions.add(String.format("%s.%s = %d",
                                userAlias, userIdColumn, user.getId()));
                    } else {
                        conditions.add(String.format("%s = %d",
                                userIdColumn, user.getId()));
                    }
                    break;

                case DATA_SCOPE_CUSTOM:
                    // 自定义部门数据权限
                    Set<Long> customDeptIds = role.getDepts().stream()
                            .map(dept -> dept.getId())
                            .collect(Collectors.toSet());
                    if (!customDeptIds.isEmpty()) {
                        String customDeptIdsStr = customDeptIds.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","));
                        if (StringUtils.hasText(deptAlias)) {
                            conditions.add(String.format("%s.%s IN (%s)",
                                    deptAlias, deptIdColumn, customDeptIdsStr));
                        } else {
                            conditions.add(String.format("%s IN (%s)",
                                    deptIdColumn, customDeptIdsStr));
                        }
                    }
                    break;

                default:
                    break;
            }
        }

        // 合并所有条件 (使用 OR)
        if (!conditions.isEmpty()) {
            sql.append(" AND (");
            sql.append(String.join(" OR ", conditions));
            sql.append(")");
        }

        return sql.toString();
    }
}
