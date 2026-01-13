/**
 * 菜单管理 API
 * 对接后端 /api/sys/menu 接口
 *
 * @author CX
 * @since 2026-01-13
 */
import { requestClient } from '#/api/request';

/**
 * 菜单类型
 * 0-目录 1-菜单 2-按钮
 */
export enum MenuType {
  /** 目录 */
  DIRECTORY = 0,
  /** 菜单 */
  MENU = 1,
  /** 按钮 */
  BUTTON = 2,
}

/**
 * 菜单信息
 */
export interface MenuRecord {
  /** 菜单ID */
  id: number;
  /** 父菜单ID */
  parentId: number;
  /** 菜单名称 */
  menuName: string;
  /** 菜单类型 (0目录 1菜单 2按钮) */
  menuType: MenuType;
  /** 路由路径 */
  path?: string;
  /** 组件路径 */
  component?: string;
  /** 权限标识 */
  permission?: string;
  /** 图标 */
  icon?: string;
  /** 排序 */
  orderNum: number;
  /** 状态 (0禁用 1启用) */
  status: number;
  /** 是否可见 (0隐藏 1显示) */
  visible: number;
  /** 是否缓存 (0否 1是) */
  keepAlive: number;
  /** 是否外链 (0否 1是) */
  isFrame: number;
  /** 子菜单 */
  children?: MenuRecord[];
  /** 创建时间 */
  createdAt?: string;
  /** 更新时间 */
  updatedAt?: string;
}

/**
 * 菜单创建/更新参数
 */
export interface MenuFormData {
  /** 父菜单ID */
  parentId?: number;
  /** 菜单名称 */
  menuName: string;
  /** 菜单类型 */
  menuType: MenuType;
  /** 路由路径 */
  path?: string;
  /** 组件路径 */
  component?: string;
  /** 权限标识 */
  permission?: string;
  /** 图标 */
  icon?: string;
  /** 排序 */
  orderNum?: number;
  /** 状态 */
  status?: number;
  /** 是否可见 */
  visible?: number;
  /** 是否缓存 */
  keepAlive?: number;
  /** 是否外链 */
  isFrame?: number;
}

/**
 * 获取菜单树
 */
export async function getMenuTree() {
  return requestClient.get<MenuRecord[]>('/sys/menu/tree');
}

/**
 * 获取菜单列表 (平铺)
 */
export async function getMenuList() {
  return requestClient.get<MenuRecord[]>('/sys/menu/list');
}

/**
 * 获取菜单详情
 * @param id 菜单ID
 */
export async function getMenuDetail(id: number) {
  return requestClient.get<MenuRecord>(`/sys/menu/${id}`);
}

/**
 * 创建菜单
 * @param data 菜单数据
 */
export async function createMenu(data: MenuFormData) {
  return requestClient.post<MenuRecord>('/sys/menu', data);
}

/**
 * 更新菜单
 * @param id 菜单ID
 * @param data 菜单数据
 */
export async function updateMenu(id: number, data: MenuFormData) {
  return requestClient.put<MenuRecord>(`/sys/menu/${id}`, data);
}

/**
 * 删除菜单
 * @param id 菜单ID
 */
export async function deleteMenu(id: number) {
  return requestClient.delete(`/sys/menu/${id}`);
}
