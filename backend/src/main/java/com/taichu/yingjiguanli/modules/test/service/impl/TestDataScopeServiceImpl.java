package com.taichu.yingjiguanli.modules.test.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.taichu.yingjiguanli.common.BusinessException;
import com.taichu.yingjiguanli.common.datascope.DataScopeHelper;
import com.taichu.yingjiguanli.common.datascope.DataScopeType;
import com.taichu.yingjiguanli.modules.sys.entity.SysDept;
import com.taichu.yingjiguanli.modules.sys.entity.SysRole;
import com.taichu.yingjiguanli.modules.sys.entity.SysUser;
import com.taichu.yingjiguanli.modules.sys.repository.SysDeptRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysRoleRepository;
import com.taichu.yingjiguanli.modules.sys.repository.SysUserRepository;
import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeDTO;
import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeQueryDTO;
import com.taichu.yingjiguanli.modules.test.entity.TestDataScope;
import com.taichu.yingjiguanli.modules.test.repository.TestDataScopeRepository;
import com.taichu.yingjiguanli.modules.test.service.TestDataScopeService;
import com.taichu.yingjiguanli.modules.test.vo.TestDataScopeVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限测试服务实现
 * 核心功能：演示如何使用 DataScopeHelper 实现数据权限过滤
 *
 * @author CX
 * @since 2026-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestDataScopeServiceImpl implements TestDataScopeService {

    private final TestDataScopeRepository testDataScopeRepository;
    private final SysUserRepository userRepository;
    private final SysDeptRepository deptRepository;
    private final SysRoleRepository roleRepository;
    private final DataScopeHelper dataScopeHelper;

    /**
     * 分页查询 (应用数据权限过滤)
     * 这是数据权限的核心实现
     */
    @Override
    public Page<TestDataScopeVO> findPage(TestDataScopeQueryDTO query) {
        Long userId = StpUtil.getLoginIdAsLong();
        DataScopeInfo scopeInfo = getCurrentDataScopeInfo();
        log.info("数据权限查询: userId={}, dataScope={}, desc={}",
                userId, scopeInfo.getDataScope(), scopeInfo.getDataScopeDesc());

        // 构建查询条件
        Specification<TestDataScope> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 业务查询条件
            if (query.getTitle() != null && !query.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + query.getTitle() + "%"));
            }
            if (query.getDeptId() != null) {
                predicates.add(cb.equal(root.get("deptId"), query.getDeptId()));
            }
            if (query.getCreateBy() != null) {
                predicates.add(cb.equal(root.get("createBy"), query.getCreateBy()));
            }

            // ============================================================
            // 【核心】数据权限过滤
            // 使用 DataScopeHelper 构建数据权限条件
            // deptId: 部门字段名
            // createBy: 创建人字段名
            // ============================================================
            Predicate dataScopePredicate = dataScopeHelper.buildDataScopePredicate(
                    root, cb, "deptId", "createBy");
            if (dataScopePredicate != null) {
                predicates.add(dataScopePredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页参数
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() - 1 : 0;
        int size = query.getSize() != null && query.getSize() > 0 ? query.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));

        // 执行查询
        Page<TestDataScope> pageData = testDataScopeRepository.findAll(spec, pageRequest);

        // 转换为 VO
        return pageData.map(this::convertToVO);
    }

    /**
     * 获取当前用户数据权限信息
     */
    @Override
    public DataScopeInfo getCurrentDataScopeInfo() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 获取用户信息
        SysUser user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new DataScopeInfo(DataScopeType.DATA_SCOPE_SELF.getCode(),
                    DataScopeType.DATA_SCOPE_SELF.getDesc(), userId, null, Collections.emptySet());
        }

        // 获取用户角色
        List<SysRole> roles = roleRepository.findRolesByUserId(userId);
        if (roles.isEmpty()) {
            return new DataScopeInfo(DataScopeType.DATA_SCOPE_SELF.getCode(),
                    DataScopeType.DATA_SCOPE_SELF.getDesc(), userId, user.getDeptId(), Collections.emptySet());
        }

        // 找到最小的数据权限范围（权限最大）
        int minDataScope = roles.stream()
                .map(SysRole::getDataScope)
                .filter(Objects::nonNull)
                .min(Integer::compareTo)
                .orElse(DataScopeType.DATA_SCOPE_SELF.getCode());

        DataScopeType scopeType = DataScopeType.fromCode(minDataScope);

        // 收集自定义部门
        Set<Long> customDeptIds = new HashSet<>();
        if (minDataScope == DataScopeType.DATA_SCOPE_CUSTOM.getCode()) {
            for (SysRole role : roles) {
                if (role.getDataScope() != null && role.getDataScope() == DataScopeType.DATA_SCOPE_CUSTOM.getCode()) {
                    customDeptIds.addAll(role.getDepts().stream()
                            .map(SysDept::getId)
                            .collect(Collectors.toSet()));
                }
            }
        }

        return new DataScopeInfo(scopeType.getCode(), scopeType.getDesc(), userId, user.getDeptId(), customDeptIds);
    }

    /**
     * 根据ID查询
     */
    @Override
    public TestDataScopeVO findById(Long id) {
        TestDataScope entity = testDataScopeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "数据不存在"));

        // 检查数据权限
        checkDataPermission(entity);

        return convertToVO(entity);
    }

    /**
     * 新增
     */
    @Override
    @Transactional
    public TestDataScopeVO create(TestDataScopeDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        TestDataScope entity = new TestDataScope();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setDeptId(user.getDeptId());
        entity.setCreateBy(userId);
        entity.setCreateByName(user.getRealName());

        // 获取部门名称
        if (user.getDeptId() != null) {
            deptRepository.findById(user.getDeptId())
                    .ifPresent(dept -> entity.setDeptName(dept.getDeptName()));
        }

        TestDataScope saved = testDataScopeRepository.save(entity);
        log.info("新增测试数据: id={}, title={}, deptId={}, createBy={}",
                saved.getId(), saved.getTitle(), saved.getDeptId(), saved.getCreateBy());

        return convertToVO(saved);
    }

    /**
     * 更新
     */
    @Override
    @Transactional
    public TestDataScopeVO update(Long id, TestDataScopeDTO dto) {
        TestDataScope entity = testDataScopeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "数据不存在"));

        // 检查数据权限
        checkDataPermission(entity);

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());

        TestDataScope saved = testDataScopeRepository.save(entity);
        log.info("更新测试数据: id={}, title={}", saved.getId(), saved.getTitle());

        return convertToVO(saved);
    }

    /**
     * 删除
     */
    @Override
    @Transactional
    public void delete(Long id) {
        TestDataScope entity = testDataScopeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "数据不存在"));

        // 检查数据权限
        checkDataPermission(entity);

        testDataScopeRepository.delete(entity);
        log.info("删除测试数据: id={}", id);
    }

    /**
     * 检查当前用户是否有权限操作该数据
     */
    private void checkDataPermission(TestDataScope entity) {
        // 如果有全部权限，直接返回
        if (dataScopeHelper.hasAllDataScope()) {
            return;
        }

        // 获取可访问的部门ID
        Set<Long> accessibleDeptIds = dataScopeHelper.getAccessibleDeptIds();

        // 如果是仅本人权限
        if (dataScopeHelper.isSelfDataScopeOnly()) {
            Long userId = StpUtil.getLoginIdAsLong();
            if (entity.getCreateBy() != null && entity.getCreateBy().equals(userId)) {
                return;
            }
            throw new BusinessException(403, "没有权限操作此数据");
        }

        // 检查部门权限
        if (accessibleDeptIds != null && entity.getDeptId() != null) {
            if (accessibleDeptIds.contains(entity.getDeptId())) {
                return;
            }
        }

        throw new BusinessException(403, "没有权限操作此数据");
    }

    /**
     * 实体转 VO
     */
    private TestDataScopeVO convertToVO(TestDataScope entity) {
        TestDataScopeVO vo = new TestDataScopeVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setDeptId(entity.getDeptId());
        vo.setDeptName(entity.getDeptName());
        vo.setCreateBy(entity.getCreateBy());
        vo.setCreateByName(entity.getCreateByName());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 数据权限信息 (用于前端展示)
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class DataScopeInfo {
        private int dataScope;
        private String dataScopeDesc;
        private Long userId;
        private Long deptId;
        private Set<Long> customDeptIds;

        public String getDataScopeDesc() {
            return dataScopeDesc;
        }
    }
}
