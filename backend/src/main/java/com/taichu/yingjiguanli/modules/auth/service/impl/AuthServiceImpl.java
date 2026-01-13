package com.taichu.yingjiguanli.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.auth.dto.LoginDTO;
import com.taichu.yingjiguanli.modules.auth.service.AuthService;
import com.taichu.yingjiguanli.modules.auth.vo.LoginVO;
import com.taichu.yingjiguanli.modules.auth.vo.MenuVO;
import com.taichu.yingjiguanli.modules.auth.vo.UserInfoVO;
import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import com.taichu.yingjiguanli.modules.sys.entity.SysMenu;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysMenuRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysMenuRepository menuRepository;
    private final SysDeptRepository deptRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        log.info("用户登录: {}", dto.getUsername());

        // 1. 查询用户
        SysUser user = userRepository.findByUsernameAndDelFlag(dto.getUsername(), 0)
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        // 2. 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.warn("用户 {} 密码验证失败", dto.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 3. 检查状态
        if (user.getStatus() != 1) {
            log.warn("用户 {} 已被禁用", dto.getUsername());
            throw new BusinessException(403, "账号已被禁用");
        }

        // 4. 执行登录
        StpUtil.login(user.getId());

        log.info("用户 {} 登录成功, userId={}", dto.getUsername(), user.getId());

        // 5. 返回 Token 信息
        return LoginVO.builder()
                .accessToken(StpUtil.getTokenValue())
                .tokenType("Bearer")
                .expiresIn(StpUtil.getTokenTimeout())
                .build();
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("用户登出, userId={}", userId);
            StpUtil.logout();
        }
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取用户信息, userId={}", userId);

        // 查询用户
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 查询部门
        String deptName = null;
        if (user.getDeptId() != null) {
            deptName = deptRepository.findById(user.getDeptId())
                    .map(SysDept::getDeptName)
                    .orElse(null);
        }

        // 查询角色和权限
        List<String> roles = roleRepository.findRoleCodesByUserId(userId);
        List<String> permissions = menuRepository.findPermissionsByUserId(userId);

        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .deptId(user.getDeptId())
                .deptName(deptName)
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    /**
     * 获取当前用户菜单
     */
    @Override
    public List<MenuVO> getCurrentUserMenus() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取用户菜单, userId={}", userId);

        // 查询用户菜单 (不含按钮)
        List<SysMenu> menus = menuRepository.findMenusByUserId(userId);

        // 转换为 VO 并构建树形结构
        List<MenuVO> menuVOList = menus.stream()
                .map(this::convertToMenuVO)
                .collect(Collectors.toList());

        return buildMenuTree(menuVOList, 0L);
    }

    /**
     * 获取当前用户权限列表
     */
    @Override
    public List<String> getCurrentUserPermissions() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取用户权限列表, userId={}", userId);
        return menuRepository.findPermissionsByUserId(userId);
    }

    /**
     * 转换菜单实体为 VO
     */
    private MenuVO convertToMenuVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getMenuName());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setType(menu.getMenuType());
        vo.setPermission(menu.getPermission());
        vo.setVisible(menu.getVisible() == 1);
        return vo;
    }

    /**
     * 构建菜单树
     *
     * @param menus    菜单列表
     * @param parentId 父菜单ID
     * @return 菜单树
     */
    private List<MenuVO> buildMenuTree(List<MenuVO> menus, Long parentId) {
        // 按父ID分组
        Map<Long, List<MenuVO>> menuMap = menus.stream()
                .collect(Collectors.groupingBy(MenuVO::getParentId));

        // 递归构建树
        return buildTree(menuMap, parentId);
    }

    /**
     * 递归构建树形结构
     */
    private List<MenuVO> buildTree(Map<Long, List<MenuVO>> menuMap, Long parentId) {
        List<MenuVO> result = new ArrayList<>();
        List<MenuVO> children = menuMap.get(parentId);

        if (children != null) {
            for (MenuVO menu : children) {
                menu.setChildren(buildTree(menuMap, menu.getId()));
                result.add(menu);
            }
            // 按排序字段排序
            result.sort((a, b) -> {
                Integer sortA = a.getSort() != null ? a.getSort() : 0;
                Integer sortB = b.getSort() != null ? b.getSort() : 0;
                return sortA.compareTo(sortB);
            });
        }

        return result;
    }
}
