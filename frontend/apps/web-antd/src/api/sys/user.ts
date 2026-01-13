/**
 * 用户管理 API
 * 对接后端 /api/sys/user 接口
 *
 * @author CX
 * @since 2026-01-13
 */
import { requestClient } from '#/api/request';

/**
 * 用户角色信息
 */
export interface UserRole {
  /** 角色ID */
  id: number;
  /** 角色名称 */
  roleName: string;
  /** 角色编码 */
  roleCode: string;
}

/**
 * 用户信息
 */
export interface UserRecord {
  /** 用户ID */
  id: number;
  /** 用户名 */
  username: string;
  /** 真实姓名 */
  realName: string;
  /** 手机号 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 头像URL */
  avatar?: string;
  /** 部门ID */
  deptId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 状态 (0禁用 1启用) */
  status: number;
  /** 角色列表 */
  roles: UserRole[];
  /** 创建时间 */
  createdAt: string;
  /** 更新时间 */
  updatedAt: string;
}

/**
 * 用户列表查询参数
 */
export interface UserQueryParams {
  /** 页码 (从0开始) */
  page?: number;
  /** 每页数量 */
  size?: number;
  /** 用户名 */
  username?: string;
  /** 真实姓名 */
  realName?: string;
  /** 手机号 */
  phone?: string;
  /** 部门ID */
  deptId?: number;
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
 * 用户创建参数
 */
export interface UserCreateParams {
  /** 用户名 */
  username: string;
  /** 密码 */
  password: string;
  /** 真实姓名 */
  realName: string;
  /** 手机号 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 头像URL */
  avatar?: string;
  /** 部门ID */
  deptId?: number;
  /** 状态 (0禁用 1启用) */
  status?: number;
  /** 角色ID列表 */
  roleIds?: number[];
}

/**
 * 用户更新参数
 */
export interface UserUpdateParams {
  /** 真实姓名 */
  realName?: string;
  /** 手机号 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 头像URL */
  avatar?: string;
  /** 部门ID */
  deptId?: number;
  /** 状态 (0禁用 1启用) */
  status?: number;
  /** 角色ID列表 */
  roleIds?: number[];
}

/**
 * 获取用户分页列表
 * @param params 查询参数
 */
export async function getUserList(params: UserQueryParams) {
  return requestClient.get<PageResult<UserRecord>>('/sys/user', { params });
}

/**
 * 获取用户详情
 * @param id 用户ID
 */
export async function getUserDetail(id: number) {
  return requestClient.get<UserRecord>(`/sys/user/${id}`);
}

/**
 * 创建用户
 * @param data 创建参数
 */
export async function createUser(data: UserCreateParams) {
  return requestClient.post<UserRecord>('/sys/user', data);
}

/**
 * 更新用户
 * @param id 用户ID
 * @param data 更新参数
 */
export async function updateUser(id: number, data: UserUpdateParams) {
  return requestClient.put<UserRecord>(`/sys/user/${id}`, data);
}

/**
 * 删除用户
 * @param id 用户ID
 */
export async function deleteUser(id: number) {
  return requestClient.delete(`/sys/user/${id}`);
}

/**
 * 重置用户密码
 * @param id 用户ID
 * @param newPassword 新密码
 */
export async function resetUserPassword(id: number, newPassword: string) {
  return requestClient.put(`/sys/user/${id}/reset-password`, { newPassword });
}

/**
 * 更新用户状态
 * @param id 用户ID
 * @param status 状态 (0禁用 1启用)
 */
export async function updateUserStatus(id: number, status: number) {
  return requestClient.put(`/sys/user/${id}/status`, { status });
}

/**
 * 分配用户角色
 * @param id 用户ID
 * @param roleIds 角色ID列表
 */
export async function assignUserRoles(id: number, roleIds: number[]) {
  return requestClient.put(`/sys/user/${id}/role`, { roleIds });
}
