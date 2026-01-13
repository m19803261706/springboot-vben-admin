import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, '/api'),
            // 后端服务地址
            target: 'http://localhost:10001',
            ws: true,
          },
        },
      },
    },
  };
});
