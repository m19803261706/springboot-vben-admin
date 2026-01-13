import type { UserInfo } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 后端返回的用户信息类型
 */
interface BackendUserInfo {
  userId: number;
  username: string;
  realName: string;
  avatar?: string;
  deptId?: number;
  deptName?: string;
  roles: string[];
  permissions: string[];
  homePath?: string;
}

/**
 * 获取用户信息
 * 调用后端 /api/auth/info 接口
 */
export async function getUserInfoApi(): Promise<UserInfo> {
  const res = await requestClient.get<BackendUserInfo>('/auth/info');
  // 转换为 Vben Admin 标准格式
  return {
    userId: String(res.userId),
    username: res.username,
    realName: res.realName,
    avatar: res.avatar || '',
    roles: res.roles,
    homePath: res.homePath,
  };
}
