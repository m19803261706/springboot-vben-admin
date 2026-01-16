/**
 * 数据权限测试 API
 * 对接后端 /api/test/data-scope 接口
 *
 * @author CX
 * @since 2026-01-16
 */
import { requestClient } from '#/api/request';

/**
 * 数据权限信息
 */
export interface DataScopeInfo {
  /** 数据权限类型 (1-5) */
  type: number;
  /** 数据权限描述 */
  desc: string;
  /** 当前用户ID */
  userId: number;
  /** 当前用户部门ID */
  deptId: number | string;
  /** 自定义部门ID列表 */
  customDeptIds: number[];
}

/**
 * 测试数据记录
 */
export interface TestDataScopeRecord {
  /** 主键ID */
  id: number;
  /** 标题 */
  title: string;
  /** 内容 */
  content?: string;
  /** 所属部门ID */
  deptId?: number;
  /** 所属部门名称 */
  deptName?: string;
  /** 创建人ID */
  createBy?: number;
  /** 创建人姓名 */
  createByName?: string;
  /** 创建时间 */
  createTime: string;
  /** 更新时间 */
  updateTime: string;
}

/**
 * 查询参数
 */
export interface TestDataScopeQueryParams {
  /** 页码 */
  page?: number;
  /** 每页数量 */
  size?: number;
  /** 标题关键词 */
  title?: string;
  /** 部门ID */
  deptId?: number;
  /** 创建人ID */
  createBy?: number;
}

/**
 * 分页响应
 */
export interface TestDataScopePageResult {
  /** 数据列表 */
  content: TestDataScopeRecord[];
  /** 总条数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页数量 */
  size: number;
  /** 数据权限信息 */
  dataScope: DataScopeInfo;
}

/**
 * 创建/更新参数
 */
export interface TestDataScopeDTO {
  /** 标题 */
  title: string;
  /** 内容 */
  content?: string;
}

/**
 * 获取测试数据列表 (自动应用数据权限过滤)
 */
export async function getTestDataScopeList(
  params: TestDataScopeQueryParams
): Promise<TestDataScopePageResult> {
  return await requestClient.get<TestDataScopePageResult>('/test/data-scope', {
    params,
  });
}

/**
 * 获取当前用户数据权限信息
 */
export async function getDataScopeInfo(): Promise<DataScopeInfo> {
  return await requestClient.get<DataScopeInfo>('/test/data-scope/scope-info');
}

/**
 * 获取测试数据详情
 */
export async function getTestDataScopeDetail(
  id: number
): Promise<TestDataScopeRecord> {
  return await requestClient.get<TestDataScopeRecord>(`/test/data-scope/${id}`);
}

/**
 * 创建测试数据
 */
export async function createTestDataScope(
  data: TestDataScopeDTO
): Promise<TestDataScopeRecord> {
  return await requestClient.post<TestDataScopeRecord>('/test/data-scope', data);
}

/**
 * 更新测试数据
 */
export async function updateTestDataScope(
  id: number,
  data: TestDataScopeDTO
): Promise<TestDataScopeRecord> {
  return await requestClient.put<TestDataScopeRecord>(
    `/test/data-scope/${id}`,
    data
  );
}

/**
 * 删除测试数据
 */
export async function deleteTestDataScope(id: number): Promise<void> {
  await requestClient.delete(`/test/data-scope/${id}`);
}
