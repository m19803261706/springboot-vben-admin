# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

Spring Boot 4 + Vben Admin 5 企业级权限管理系统，包含完整的 RBAC 权限体系和数据权限控制。

## 开发命令

### 后端
```bash
cd backend
mvn spring-boot:run              # 启动开发服务器 (端口 10001)
mvn clean package                # 构建
mvn test                         # 运行测试
mvn test -Dtest=UserServiceTest  # 运行单个测试类
```

### 前端
```bash
cd frontend
pnpm install                     # 安装依赖
pnpm dev:antd                    # 启动开发服务器 (端口 10004)
pnpm build:antd                  # 生产构建
pnpm typecheck                   # TypeScript 类型检查
```

### 访问地址
- 前端: http://localhost:10004
- 后端: http://localhost:10001
- API文档: http://localhost:10001/swagger-ui.html
- 默认账号: admin / admin123

## 项目架构

```
├── backend/                           # Spring Boot 4.0.1 后端
│   └── src/main/java/com/taichu/yingjiguanli/
│       ├── common/                    # 通用模块
│       │   ├── ApiResponse.java       # 统一响应格式
│       │   ├── BusinessException.java # 业务异常
│       │   └── datascope/             # 数据权限核心
│       │       ├── DataScopeHelper.java    # 权限过滤逻辑
│       │       └── DataScopeAspect.java    # AOP 拦截器
│       ├── config/                    # 配置类
│       │   ├── SaTokenConfig.java     # Sa-Token 拦截器
│       │   └── FlywayConfig.java      # 数据库迁移
│       ├── security/                  # 安全认证
│       │   └── StpInterfaceImpl.java  # Sa-Token 权限实现
│       └── modules/                   # 业务模块
│           ├── auth/                  # 认证模块 (登录/登出)
│           └── sys/                   # 系统管理 (用户/角色/菜单/部门)
│
└── frontend/apps/web-antd/            # Vben Admin 5.x 前端
    └── src/
        ├── api/                       # API 接口定义
        ├── views/                     # 页面组件
        │   └── sys/                   # 系统管理页面
        ├── stores/                    # Pinia 状态管理
        └── router/                    # 路由配置
```

## 核心技术要点

### 权限认证 (Sa-Token)
- Token 认证: Bearer 前缀，从 Header 读取
- 接口权限: `@SaCheckPermission("sys:user:add")`
- 登录实现: `StpUtil.login(userId)`

### 数据权限 (5种范围)
- DATA_SCOPE_ALL: 全部数据
- DATA_SCOPE_DEPT: 本部门
- DATA_SCOPE_DEPT_AND_CHILD: 本部门及下级
- DATA_SCOPE_SELF: 仅本人
- DATA_SCOPE_CUSTOM: 自定义部门

在 Service 中使用 `DataScopeHelper.buildDataScopePredicate()` 添加数据过滤。

### 数据库迁移 (Flyway)
迁移脚本: `backend/src/main/resources/db/migration/V{n}__{description}.sql`
启动时自动执行，baseline-version: 4

### 统一响应格式
```java
ApiResponse<T> { code, message, data, timestamp }
```

## 开发规范

### 后端模块结构
新增模块放在 `modules/{module}/` 下，包含:
- entity/ → 实体类
- repository/ → JPA 接口
- service/ → 服务接口和实现
- controller/ → REST 控制器
- dto/ → 输入数据对象
- vo/ → 输出视图对象

### 前端模块结构
新增模块在 `views/{module}/` 下创建页面，`api/{module}/` 下创建接口。
菜单通过后端 `/api/auth/menus` 动态加载。

### 代码风格
- 所有代码必须添加中文注释
- 后端: PascalCase 类名，camelCase 方法名，snake_case 数据库字段
- 前端: TypeScript + Vue 3 Composition API (`<script setup lang="ts">`)

## 开发 Skills

项目级 Skills 位于 `.claude/skills/`，包含详细代码模板：
- `spring-boot-4/SKILL.md` - 后端开发规范
- `vben-admin/SKILL.md` - 前端开发规范
- `sa-token/SKILL.md` - 权限认证框架
- `permission-system/SKILL.md` - 数据权限系统

## Git 提交规范

```
{type}({scope}): {description}

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
```

Type: feat, fix, docs, style, refactor, test, chore
