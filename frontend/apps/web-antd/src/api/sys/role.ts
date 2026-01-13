/**
 * 角色管理 API
 * 对接后端 /api/sys/role 接口
 *
 * @author CX
 * @since 2026-01-13
 */
import { requestClient } from '#/api/request';

/**
 * 角色信息
 */
export interface RoleRecord {
  /** 角色ID */
  id: number;
  /** 角色名称 */
  roleName: string;
  /** 角色编码 */
  roleCode: string;
  /** 排序 */
  orderNum: number;
  /** 数据权限范围 */
  dataScope: number;
  /** 状态 (0禁用 1启用) */
  status: number;
  /** 备注 */
  remark?: string;
  /** 菜单ID列表 */
  menuIds?: number[];
  /** 部门ID列表 (自定义数据权限时使用) */
  deptIds?: number[];
  /** 创建时间 */
  createdAt?: string;
  /** 更新时间 */
  updatedAt?: string;
}

/**
 * 角色查询参数
 */
export interface RoleQueryParams {
  /** 页码 */
  page?: number;
  /** 每页数量 */
  size?: number;
  /** 角色名称 */
  roleName?: string;
  /** 角色编码 */
  roleCode?: string;
  /** 状态 */
  status?: number;
}

/**
 * 分页响应
 */
export interface PageResult<T> {
  /** 数据列表 */
  content: T[];
  /** 总条数 */
  totalElements: number;
  /** 总页数 */
  totalPages: number;
  /** 当前页码 */
  number: number;
  /** 每页数量 */
  size: number;
}

/**
 * 获取角色分页列表
 * @param params 查询参数
 */
export async function getRoleList(params?: RoleQueryParams) {
  return requestClient.get<PageResult<RoleRecord>>('/sys/role', { params });
}

/**
 * 获取所有角色（不分页）
 */
export async function getAllRoles() {
  // 获取全部启用状态的角色
  return requestClient.get<PageResult<RoleRecord>>('/sys/role', {
    params: { page: 0, size: 1000, status: 1 },
  });
}

/**
 * 获取角色详情
 * @param id 角色ID
 */
export async function getRoleDetail(id: number) {
  return requestClient.get<RoleRecord>(`/sys/role/${id}`);
}

/**
 * 创建角色
 * @param data 角色数据
 */
export async function createRole(data: Partial<RoleRecord>) {
  return requestClient.post<RoleRecord>('/sys/role', data);
}

/**
 * 更新角色
 * @param id 角色ID
 * @param data 角色数据
 */
export async function updateRole(id: number, data: Partial<RoleRecord>) {
  return requestClient.put<RoleRecord>(`/sys/role/${id}`, data);
}

/**
 * 删除角色
 * @param id 角色ID
 */
export async function deleteRole(id: number) {
  return requestClient.delete(`/sys/role/${id}`);
}

/**
 * 更新角色状态
 * @param id 角色ID
 * @param status 状态
 */
export async function updateRoleStatus(id: number, status: number) {
  return requestClient.put(`/sys/role/${id}/status`, { status });
}

/**
 * 分配角色菜单
 * @param id 角色ID
 * @param menuIds 菜单ID列表
 */
export async function assignRoleMenus(id: number, menuIds: number[]) {
  return requestClient.put(`/sys/role/${id}/menu`, { menuIds });
}

/**
 * 设置数据权限范围
 * @param id 角色ID
 * @param dataScope 数据权限类型 (1-全部 2-本部门 3-本部门及下级 4-仅本人 5-自定义)
 * @param deptIds 部门ID列表 (dataScope=5时使用)
 */
export async function setRoleDataScope(
  id: number,
  dataScope: number,
  deptIds?: number[],
) {
  return requestClient.put(`/sys/role/${id}/data-scope`, { dataScope, deptIds });
}
