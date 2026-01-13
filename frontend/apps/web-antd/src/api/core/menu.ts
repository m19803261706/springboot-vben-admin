import type { RouteRecordStringComponent } from '@vben/types';

import { requestClient } from '#/api/request';

/**
 * 后端路由返回格式
 */
interface BackendRoute {
  name: string;
  path: string;
  component: string;
  redirect?: string;
  meta: {
    title: string;
    icon?: string;
    order?: number;
    hideInMenu?: boolean;
    keepAlive?: boolean;
    authority?: string[];
    affixTab?: boolean;
    link?: string;
    ignoreAccess?: boolean;
  };
  children?: BackendRoute[];
}

/**
 * 转换后端路由格式为前端路由格式
 * 后端返回的路由格式需要转换为 Vben Admin 的 RouteRecordStringComponent 格式
 */
function transformRoutes(routes: BackendRoute[]): RouteRecordStringComponent[] {
  return routes.map((route) => {
    const result: RouteRecordStringComponent = {
      name: route.name,
      path: route.path,
      component: route.component,
      redirect: route.redirect,
      meta: {
        title: route.meta.title,
        icon: route.meta.icon,
        order: route.meta.order,
        hideInMenu: route.meta.hideInMenu,
        keepAlive: route.meta.keepAlive,
        authority: route.meta.authority,
        affixTab: route.meta.affixTab,
        link: route.meta.link,
        ignoreAccess: route.meta.ignoreAccess,
      },
    };

    // 递归处理子路由
    if (route.children && route.children.length > 0) {
      result.children = transformRoutes(route.children);
    }

    return result;
  });
}

/**
 * 获取用户所有菜单 (路由)
 * 调用后端 /api/auth/routes 接口
 */
export async function getAllMenusApi(): Promise<RouteRecordStringComponent[]> {
  const routes = await requestClient.get<BackendRoute[]>('/auth/routes');
  return transformRoutes(routes);
}
