package com.taichu.yingjiguanli.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.auth.dto.LoginDTO;
import com.taichu.yingjiguanli.modules.auth.service.AuthService;
import com.taichu.yingjiguanli.modules.auth.vo.LoginVO;
import com.taichu.yingjiguanli.modules.auth.vo.MenuVO;
import com.taichu.yingjiguanli.modules.auth.vo.RouteVO;
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

        // 补充缺失的父级菜单 (确保树形结构完整)
        menus = completeParentMenus(menus);

        // 转换为 VO 并构建树形结构
        List<MenuVO> menuVOList = menus.stream()
                .map(this::convertToMenuVO)
                .collect(Collectors.toList());

        return buildMenuTree(menuVOList, 0L);
    }

    /**
     * 获取当前用户路由 (Vben Admin 格式)
     */
    @Override
    public List<RouteVO> getCurrentUserRoutes() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取用户路由, userId={}", userId);

        // 查询用户菜单 (不含按钮)
        List<SysMenu> menus = menuRepository.findMenusByUserId(userId);

        // 补充缺失的父级菜单 (确保树形结构完整)
        menus = completeParentMenus(menus);

        // 转换为路由数据并构建树形结构
        List<RouteData> routeDataList = menus.stream()
                .map(this::convertToRouteData)
                .collect(Collectors.toList());

        return buildRouteTree(routeDataList, 0L);
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
     * 补充缺失的父级菜单
     * 确保菜单树形结构完整，每个菜单的父级菜单都存在
     *
     * @param menus 用户直接拥有权限的菜单列表
     * @return 包含所有父级菜单的完整菜单列表
     */
    private List<SysMenu> completeParentMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用 Map 存储已有的菜单，避免重复
        Map<Long, SysMenu> menuMap = menus.stream()
                .collect(Collectors.toMap(SysMenu::getId, m -> m, (m1, m2) -> m1));

        // 收集所有需要补充的父级菜单ID
        java.util.Set<Long> missingParentIds = new java.util.HashSet<>();
        for (SysMenu menu : menus) {
            Long parentId = menu.getParentId();
            // 遍历所有父级，直到根节点 (parentId = 0)
            while (parentId != null && parentId != 0 && !menuMap.containsKey(parentId)) {
                missingParentIds.add(parentId);
                // 查询父级菜单以获取其 parentId
                SysMenu parentMenu = menuRepository.findById(parentId).orElse(null);
                if (parentMenu != null) {
                    menuMap.put(parentMenu.getId(), parentMenu);
                    parentId = parentMenu.getParentId();
                } else {
                    break;
                }
            }
        }

        log.debug("补充父级菜单: 原有{}个, 补充{}个父级菜单",
                menus.size(), menuMap.size() - menus.size());

        return new ArrayList<>(menuMap.values());
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

    // ==================== 路由相关方法 (Vben Admin 格式) ====================

    /**
     * 路由数据 (包含菜单ID，用于构建树)
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class RouteData {
        private Long menuId;
        private Long parentId;
        private RouteVO route;
    }

    /**
     * 转换菜单实体为路由 VO (Vben Admin 格式)
     */
    private RouteData convertToRouteData(SysMenu menu) {
        RouteVO route = new RouteVO();

        // 生成路由名称
        String routeName = generateRouteName(menu);
        route.setName(routeName);

        // 设置路径
        route.setPath(menu.getPath() != null ? menu.getPath() : "/" + menu.getId());

        // 设置组件
        if (menu.getMenuType() == SysMenu.MenuType.DIRECTORY) {
            // 目录使用基础布局
            route.setComponent("BasicLayout");
        } else if (menu.getMenuType() == SysMenu.MenuType.MENU) {
            // 菜单使用具体组件
            String component = menu.getComponent();
            if (component != null && !component.isEmpty()) {
                // 确保组件路径格式正确
                if (!component.startsWith("/")) {
                    component = "/" + component;
                }
                route.setComponent(component);
            }
        }

        // 设置元信息
        RouteVO.RouteMeta meta = new RouteVO.RouteMeta();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setOrder(menu.getSort());
        meta.setHideInMenu(menu.getVisible() != null && menu.getVisible() == 0);
        meta.setKeepAlive(true); // 默认开启缓存

        // 设置权限标识
        if (menu.getPermission() != null && !menu.getPermission().isEmpty()) {
            meta.setAuthority(List.of(menu.getPermission()));
        }

        route.setMeta(meta);

        return new RouteData(menu.getId(), menu.getParentId(), route);
    }

    /**
     * 生成路由名称
     */
    private String generateRouteName(SysMenu menu) {
        // 使用路径生成路由名称
        if (menu.getPath() != null && !menu.getPath().isEmpty()) {
            String path = menu.getPath();
            // 移除开头的斜杠
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            // 将斜杠替换为驼峰命名
            StringBuilder sb = new StringBuilder();
            boolean capitalizeNext = false;
            for (char c : path.toCharArray()) {
                if (c == '/' || c == '-') {
                    capitalizeNext = true;
                } else {
                    if (capitalizeNext) {
                        sb.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else if (sb.length() == 0) {
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(c);
                    }
                }
            }
            return sb.toString();
        }
        // 使用菜单名称
        return "Menu" + menu.getId();
    }

    /**
     * 构建路由树
     *
     * @param routeDataList 路由数据列表
     * @param parentId      父菜单ID
     * @return 路由树
     */
    private List<RouteVO> buildRouteTree(List<RouteData> routeDataList, Long parentId) {
        // 按父ID分组
        Map<Long, List<RouteData>> routeMap = routeDataList.stream()
                .collect(Collectors.groupingBy(RouteData::getParentId));

        return buildRouteTreeRecursive(routeMap, parentId);
    }

    /**
     * 递归构建路由树
     */
    private List<RouteVO> buildRouteTreeRecursive(Map<Long, List<RouteData>> routeMap, Long parentId) {
        List<RouteVO> result = new ArrayList<>();
        List<RouteData> children = routeMap.get(parentId);

        if (children != null) {
            for (RouteData data : children) {
                RouteVO route = data.getRoute();
                // 递归获取子路由
                List<RouteVO> childRoutes = buildRouteTreeRecursive(routeMap, data.getMenuId());
                route.setChildren(childRoutes.isEmpty() ? null : childRoutes);

                // 如果有子路由且当前是目录（BasicLayout），设置 redirect 到第一个子路由
                if (!childRoutes.isEmpty() && "BasicLayout".equals(route.getComponent())) {
                    route.setRedirect(childRoutes.get(0).getPath());
                }

                result.add(route);
            }
            // 按排序字段排序
            result.sort((a, b) -> {
                Integer orderA = a.getMeta() != null && a.getMeta().getOrder() != null ? a.getMeta().getOrder() : 0;
                Integer orderB = b.getMeta() != null && b.getMeta().getOrder() != null ? b.getMeta().getOrder() : 0;
                return orderA.compareTo(orderB);
            });
        }

        return result;
    }
}
