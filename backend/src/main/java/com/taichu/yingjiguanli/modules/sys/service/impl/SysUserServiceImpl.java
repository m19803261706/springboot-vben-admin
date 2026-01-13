package com.taichu.yingjiguanli.modules.sys.service.impl;

import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.common.annotation.DataScope;
import com.taichu.yingjiguanli.common.datascope.DataScopeHelper;
import com.taichu.yingjiguanli.modules.sys.dto.*;
import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysUserRepository;
import com.taichu.yingjiguanli.modules.sys.service.SysUserService;
import com.taichu.yingjiguanli.modules.sys.vo.UserVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author CX
 * @since 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final SysDeptRepository deptRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataScopeHelper dataScopeHelper;

    @Override
    @Transactional
    public UserVO create(UserCreateDTO dto) {
        log.info("创建用户: username={}", dto.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsernameAndDelFlag(dto.getUsername(), 0)) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setDeptId(dto.getDeptId());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setDelFlag(0);

        // 设置角色
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<SysRole> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        // 保存用户
        SysUser savedUser = userRepository.save(user);
        log.info("用户创建成功: id={}", savedUser.getId());

        return convertToVO(savedUser);
    }

    @Override
    @Transactional
    public UserVO update(Long id, UserUpdateDTO dto) {
        log.info("更新用户: id={}", id);

        // 查找用户
        SysUser user = userRepository.findById(id)
                .filter(u -> u.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 更新属性
        if (StringUtils.hasText(dto.getRealName())) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getDeptId() != null) {
            user.setDeptId(dto.getDeptId());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        // 更新角色
        if (dto.getRoleIds() != null) {
            Set<SysRole> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        // 保存用户
        SysUser savedUser = userRepository.save(user);
        log.info("用户更新成功: id={}", id);

        return convertToVO(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除用户: id={}", id);

        // 检查用户是否存在
        SysUser user = userRepository.findById(id)
                .filter(u -> u.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 禁止删除 admin 用户
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除管理员用户");
        }

        // 软删除
        userRepository.softDelete(id);
        log.info("用户删除成功: id={}", id);
    }

    @Override
    public Optional<UserVO> findById(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getDelFlag() == 0)
                .map(this::convertToVO);
    }

    @Override
    @DataScope(deptAlias = "", userAlias = "")
    public Page<UserVO> findPage(UserQueryDTO query) {
        log.info("分页查询用户: {}", query);

        // 构建查询条件
        Specification<SysUser> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 未删除
            predicates.add(cb.equal(root.get("delFlag"), 0));

            // 用户名模糊搜索
            if (StringUtils.hasText(query.getUsername())) {
                predicates.add(cb.like(root.get("username"), "%" + query.getUsername() + "%"));
            }

            // 真实姓名模糊搜索
            if (StringUtils.hasText(query.getRealName())) {
                predicates.add(cb.like(root.get("realName"), "%" + query.getRealName() + "%"));
            }

            // 手机号模糊搜索
            if (StringUtils.hasText(query.getPhone())) {
                predicates.add(cb.like(root.get("phone"), "%" + query.getPhone() + "%"));
            }

            // 部门筛选
            if (query.getDeptId() != null) {
                predicates.add(cb.equal(root.get("deptId"), query.getDeptId()));
            }

            // 状态筛选
            if (query.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), query.getStatus()));
            }

            // 数据权限过滤
            Predicate dataScopePredicate = dataScopeHelper.buildDataScopePredicate(
                    root, cb, "deptId", "id");
            if (dataScopePredicate != null) {
                predicates.add(dataScopePredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                query.getPage() != null ? query.getPage() : 0,
                query.getSize() != null ? query.getSize() : 10,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<SysUser> page = userRepository.findAll(spec, pageRequest);

        return page.map(this::convertToVO);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, ResetPasswordDTO dto) {
        log.info("重置用户密码: id={}", id);

        // 检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new BusinessException(404, "用户不存在");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        // 更新密码
        userRepository.updatePassword(id, encodedPassword);
        log.info("用户密码重置成功: id={}", id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UserStatusDTO dto) {
        log.info("更新用户状态: id={}, status={}", id, dto.getStatus());

        // 检查用户是否存在
        SysUser user = userRepository.findById(id)
                .filter(u -> u.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 禁止禁用 admin 用户
        if ("admin".equals(user.getUsername()) && dto.getStatus() == 0) {
            throw new BusinessException("不能禁用管理员用户");
        }

        // 更新状态
        userRepository.updateStatus(id, dto.getStatus());
        log.info("用户状态更新成功: id={}, status={}", id, dto.getStatus());
    }

    @Override
    @Transactional
    public void assignRoles(Long id, UserRoleDTO dto) {
        log.info("分配用户角色: id={}, roleIds={}", id, dto.getRoleIds());

        // 查找用户
        SysUser user = userRepository.findById(id)
                .filter(u -> u.getDelFlag() == 0)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 获取角色
        Set<SysRole> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));

        // 验证所有角色都存在
        if (roles.size() != dto.getRoleIds().size()) {
            throw new BusinessException("部分角色不存在");
        }

        // 更新用户角色
        user.setRoles(roles);
        userRepository.save(user);
        log.info("用户角色分配成功: id={}", id);
    }

    @Override
    public Optional<SysUser> findByUsername(String username) {
        return userRepository.findByUsernameAndDelFlag(username, 0);
    }

    /**
     * 将用户实体转换为视图对象
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setDeptId(user.getDeptId());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());

        // 设置部门名称
        if (user.getDeptId() != null) {
            deptRepository.findById(user.getDeptId())
                    .ifPresent(dept -> vo.setDeptName(dept.getDeptName()));
        }

        // 设置角色列表
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<UserVO.RoleVO> roleVOs = user.getRoles().stream()
                    .map(role -> {
                        UserVO.RoleVO roleVO = new UserVO.RoleVO();
                        roleVO.setId(role.getId());
                        roleVO.setRoleName(role.getRoleName());
                        roleVO.setRoleCode(role.getRoleCode());
                        return roleVO;
                    })
                    .collect(Collectors.toList());
            vo.setRoles(roleVOs);
        } else {
            vo.setRoles(Collections.emptyList());
        }

        return vo;
    }
}
