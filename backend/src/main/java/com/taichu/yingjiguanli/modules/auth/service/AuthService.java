package com.taichu.yingjiguanli.modules.auth.service;

import com.taichu.yingjiguanli.modules.auth.dto.LoginDTO;
import com.taichu.yingjiguanli.modules.auth.vo.LoginVO;
import com.taichu.yingjiguanli.modules.auth.vo.MenuVO;
import com.taichu.yingjiguanli.modules.auth.vo.RouteVO;
import com.taichu.yingjiguanli.modules.auth.vo.UserInfoVO;

import java.util.List;

/**
 * 认证服务接口
 *
 * @author CX
 * @since 2026-01-13
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return Token 信息
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 获取当前用户菜单
     *
     * @return 菜单树
     */
    List<MenuVO> getCurrentUserMenus();

    /**
     * 获取当前用户路由 (Vben Admin 格式)
     *
     * @return 路由树
     */
    List<RouteVO> getCurrentUserRoutes();

    /**
     * 获取当前用户权限列表
     *
     * @return 权限标识列表
     */
    List<String> getCurrentUserPermissions();
}
