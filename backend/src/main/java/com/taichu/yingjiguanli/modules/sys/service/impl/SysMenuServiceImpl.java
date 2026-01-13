package com.taichu.yingjiguanli.modules.sys.service.impl;

import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.modules.sys.dto.MenuCreateDTO;
import com.taichu.yingjiguanli.modules.sys.dto.MenuUpdateDTO;
import com.taichu.yingjiguanli.modules.sys.entity.SysMenu;
import com.taichu.yingjiguanli.modules.sys.repository.SysMenuRepository;
import com.taichu.yingjiguanli.modules.sys.service.SysMenuService;
import com.taichu.yingjiguanli.modules.sys.vo.MenuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuRepository menuRepository;

    @Override
    @Transactional
    public MenuVO create(MenuCreateDTO dto) {
        log.info("创建菜单: menuName={}", dto.getMenuName());

        // 创建菜单实体
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setMenuName(dto.getMenuName());
        menu.setMenuType(dto.getMenuType());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setPermission(dto.getPermission());
        menu.setIcon(dto.getIcon());
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 验证父菜单
        if (menu.getParentId() != 0) {
            menuRepository.findById(menu.getParentId())
                    .orElseThrow(() -> new BusinessException(404, "父菜单不存在"));
        }

        // 保存菜单
        SysMenu savedMenu = menuRepository.save(menu);
        log.info("菜单创建成功: id={}", savedMenu.getId());

        return convertToVO(savedMenu);
    }

    @Override
    @Transactional
    public MenuVO update(Long id, MenuUpdateDTO dto) {
        log.info("更新菜单: id={}", id);

        // 查找菜单
        SysMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "菜单不存在"));

        // 更新属性
        if (dto.getParentId() != null) {
            // 验证不能将自己设为父菜单
            if (dto.getParentId().equals(id)) {
                throw new BusinessException("不能将自己设为父菜单");
            }
            // 验证父菜单存在
            if (dto.getParentId() != 0) {
                menuRepository.findById(dto.getParentId())
                        .orElseThrow(() -> new BusinessException(404, "父菜单不存在"));
            }
            menu.setParentId(dto.getParentId());
        }
        if (StringUtils.hasText(dto.getMenuName())) {
            menu.setMenuName(dto.getMenuName());
        }
        if (dto.getMenuType() != null) {
            menu.setMenuType(dto.getMenuType());
        }
        if (dto.getPath() != null) {
            menu.setPath(dto.getPath());
        }
        if (dto.getComponent() != null) {
            menu.setComponent(dto.getComponent());
        }
        if (dto.getPermission() != null) {
            menu.setPermission(dto.getPermission());
        }
        if (dto.getIcon() != null) {
            menu.setIcon(dto.getIcon());
        }
        if (dto.getSort() != null) {
            menu.setSort(dto.getSort());
        }
        if (dto.getVisible() != null) {
            menu.setVisible(dto.getVisible());
        }
        if (dto.getStatus() != null) {
            menu.setStatus(dto.getStatus());
        }

        // 保存菜单
        SysMenu savedMenu = menuRepository.save(menu);
        log.info("菜单更新成功: id={}", id);

        return convertToVO(savedMenu);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除菜单: id={}", id);

        // 检查菜单是否存在
        SysMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "菜单不存在"));

        // 级联删除子菜单
        deleteChildrenRecursive(id);

        // 删除当前菜单
        menuRepository.delete(menu);
        log.info("菜单删除成功: id={}", id);
    }

    /**
     * 递归删除子菜单
     *
     * @param parentId 父菜单ID
     */
    private void deleteChildrenRecursive(Long parentId) {
        List<SysMenu> children = menuRepository.findByParentIdAndStatusOrderBySortAsc(parentId, 1);
        for (SysMenu child : children) {
            // 先删除子节点的子节点
            deleteChildrenRecursive(child.getId());
            // 再删除子节点
            menuRepository.delete(child);
            log.info("删除子菜单: id={}", child.getId());
        }
    }

    @Override
    public Optional<MenuVO> findById(Long id) {
        return menuRepository.findById(id).map(this::convertToVO);
    }

    @Override
    public List<MenuVO> findTree() {
        // 查询所有菜单
        List<SysMenu> allMenus = menuRepository.findAllOrderBySort();

        // 转换为 VO
        List<MenuVO> allVOs = allMenus.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public List<MenuVO> findAll() {
        List<SysMenu> allMenus = menuRepository.findAllOrderBySort();
        return allMenus.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findPermissionsByUserId(Long userId) {
        return menuRepository.findPermissionsByUserId(userId);
    }

    @Override
    public List<MenuVO> findUserMenuTree(Long userId) {
        // 查询用户拥有的菜单 (不含按钮)
        List<SysMenu> userMenus = menuRepository.findMenusByUserId(userId);

        // 转换为 VO
        List<MenuVO> allVOs = userMenus.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    /**
     * 构建菜单树
     *
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID
     * @return 树形菜单列表
     */
    private List<MenuVO> buildTree(List<MenuVO> allMenus, Long parentId) {
        List<MenuVO> result = new ArrayList<>();

        for (MenuVO menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                // 递归获取子菜单
                List<MenuVO> children = buildTree(allMenus, menu.getId());
                menu.setChildren(children);
                result.add(menu);
            }
        }

        // 按 sort 排序
        result.sort(Comparator.comparingInt(m -> m.getSort() != null ? m.getSort() : 0));

        return result;
    }

    /**
     * 将菜单实体转换为视图对象
     *
     * @param menu 菜单实体
     * @return 菜单视图对象
     */
    private MenuVO convertToVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        vo.setId(menu.getId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setMenuType(menu.getMenuType());
        vo.setMenuTypeDesc(MenuVO.getMenuTypeDesc(menu.getMenuType()));
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setPermission(menu.getPermission());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setCreatedAt(menu.getCreateTime());
        vo.setUpdatedAt(menu.getUpdateTime());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
