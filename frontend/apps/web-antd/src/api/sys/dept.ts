/**
 * 部门管理 API
 * 对接后端 /api/sys/dept 接口
 *
 * @author CX
 * @since 2026-01-13
 */
import { requestClient } from '#/api/request';

/**
 * 部门信息
 */
export interface DeptRecord {
  /** 部门ID */
  id: number;
  /** 父部门ID */
  parentId: number;
  /** 部门名称 */
  deptName: string;
  /** 排序 */
  orderNum: number;
  /** 状态 (0禁用 1启用) */
  status: number;
  /** 子部门 */
  children?: DeptRecord[];
  /** 创建时间 */
  createdAt?: string;
  /** 更新时间 */
  updatedAt?: string;
}

/**
 * 获取部门树
 */
export async function getDeptTree() {
  return requestClient.get<DeptRecord[]>('/sys/dept/tree');
}

/**
 * 获取部门列表
 */
export async function getDeptList() {
  return requestClient.get<DeptRecord[]>('/sys/dept/list');
}

/**
 * 获取部门详情
 * @param id 部门ID
 */
export async function getDeptDetail(id: number) {
  return requestClient.get<DeptRecord>(`/sys/dept/${id}`);
}

/**
 * 创建部门
 * @param data 部门数据
 */
export async function createDept(data: Partial<DeptRecord>) {
  return requestClient.post<DeptRecord>('/sys/dept', data);
}

/**
 * 更新部门
 * @param id 部门ID
 * @param data 部门数据
 */
export async function updateDept(id: number, data: Partial<DeptRecord>) {
  return requestClient.put<DeptRecord>(`/sys/dept/${id}`, data);
}

/**
 * 删除部门
 * @param id 部门ID
 */
export async function deleteDept(id: number) {
  return requestClient.delete(`/sys/dept/${id}`);
}
