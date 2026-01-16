package com.taichu.yingjiguanli.modules.test.service;

import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeDTO;
import com.taichu.yingjiguanli.modules.test.dto.TestDataScopeQueryDTO;
import com.taichu.yingjiguanli.modules.test.service.impl.TestDataScopeServiceImpl;
import com.taichu.yingjiguanli.modules.test.vo.TestDataScopeVO;
import org.springframework.data.domain.Page;

/**
 * 数据权限测试服务接口
 *
 * @author CX
 * @since 2026-01-16
 */
public interface TestDataScopeService {

    /**
     * 分页查询 (应用数据权限过滤)
     *
     * @param query 查询条件
     * @return 分页数据
     */
    Page<TestDataScopeVO> findPage(TestDataScopeQueryDTO query);

    /**
     * 获取当前用户数据权限信息
     *
     * @return 数据权限信息
     */
    TestDataScopeServiceImpl.DataScopeInfo getCurrentDataScopeInfo();

    /**
     * 根据ID查询
     *
     * @param id 主键ID
     * @return 详情
     */
    TestDataScopeVO findById(Long id);

    /**
     * 新增
     *
     * @param dto 创建数据
     * @return 创建结果
     */
    TestDataScopeVO create(TestDataScopeDTO dto);

    /**
     * 更新
     *
     * @param id  主键ID
     * @param dto 更新数据
     * @return 更新结果
     */
    TestDataScopeVO update(Long id, TestDataScopeDTO dto);

    /**
     * 删除
     *
     * @param id 主键ID
     */
    void delete(Long id);
}
