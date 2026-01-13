package com.taichu.yingjiguanli.security;

import cn.dev33.satoken.stp.StpInterface;
import com.taichu.yingjiguanli.modules.sys.repository.SysMenuRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限认证接口实现
 * 用于获取用户的权限列表和角色列表
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysMenuRepository menuRepository;
    private final SysRoleRepository roleRepository;

    /**
     * 返回指定账号拥有的权限码集合
     *
     * @param loginId   登录用户ID
     * @param loginType 登录类型
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        log.debug("获取用户权限列表, userId={}", userId);
        // 从数据库查询用户权限
        return menuRepository.findPermissionsByUserId(userId);
    }

    /**
     * 返回指定账号拥有的角色标识集合
     *
     * @param loginId   登录用户ID
     * @param loginType 登录类型
     * @return 角色标识列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        log.debug("获取用户角色列表, userId={}", userId);
        // 从数据库查询用户角色
        return roleRepository.findRoleCodesByUserId(userId);
    }
}
