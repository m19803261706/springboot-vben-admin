import { defineOverridesPreferences } from '@vben/preferences';

/**
 * @description 项目配置文件
 * 只需要覆盖项目中的一部分配置，不需要的配置不用覆盖，会自动使用默认配置
 * !!! 更改配置后请清空缓存，否则可能不生效
 */
export const overridesPreferences = defineOverridesPreferences({
  // overrides
  app: {
    name: import.meta.env.VITE_APP_TITLE,
    // 启用后端动态路由模式
    accessMode: 'backend',
    // 设置默认首页为系统管理-用户管理
    defaultHomePath: '/sys/user',
  },
  // 版权信息配置
  copyright: {
    companyName: '太初星集商贸有限公司',
    companySiteLink: '',
    date: '2026',
    enable: true,
    icp: '',
    icpLink: '',
  },
});
