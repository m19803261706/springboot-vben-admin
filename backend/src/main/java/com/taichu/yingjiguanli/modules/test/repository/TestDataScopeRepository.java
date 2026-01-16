package com.taichu.yingjiguanli.modules.test.repository;

import com.taichu.yingjiguanli.modules.test.entity.TestDataScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 数据权限测试数据访问接口
 *
 * @author CX
 * @since 2026-01-16
 */
@Repository
public interface TestDataScopeRepository extends JpaRepository<TestDataScope, Long>, JpaSpecificationExecutor<TestDataScope> {
}
