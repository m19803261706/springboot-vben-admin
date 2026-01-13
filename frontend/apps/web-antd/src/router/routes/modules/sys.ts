/**
 * 系统管理路由配置
 *
 * @author CX
 * @since 2026-01-13
 */
import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/sys',
    name: 'System',
    meta: {
      icon: 'lucide:settings',
      order: 100,
      title: '系统管理',
    },
    children: [
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('#/views/sys/user/index.vue'),
        meta: {
          icon: 'lucide:users',
          title: '用户管理',
          authority: ['sys:user:list'],
        },
      },
      {
        path: 'role',
        name: 'RoleManagement',
        component: () => import('#/views/sys/role/index.vue'),
        meta: {
          icon: 'lucide:shield',
          title: '角色管理',
          authority: ['sys:role:list'],
        },
      },
      {
        path: 'menu',
        name: 'MenuManagement',
        component: () => import('#/views/sys/menu/index.vue'),
        meta: {
          icon: 'lucide:menu',
          title: '菜单管理',
          authority: ['sys:menu:list'],
        },
      },
      {
        path: 'dept',
        name: 'DeptManagement',
        component: () => import('#/views/sys/dept/index.vue'),
        meta: {
          icon: 'lucide:building-2',
          title: '部门管理',
          authority: ['sys:dept:list'],
        },
      },
    ],
  },
];

export default routes;
