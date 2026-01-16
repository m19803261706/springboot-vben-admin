# Spring Boot + Vben Admin 权限管理模板

开箱即用的企业级后台管理系统模板，基于 Spring Boot 4 + Vben Admin 5 + Sa-Token 构建的 RBAC 权限管理系统。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 基础框架 |
| Spring Data JPA | 3.4.x | ORM 框架 |
| Hibernate | 7.2.0 | JPA 实现 |
| Sa-Token | 1.38.0 | 权限认证 |
| Flyway | 11.x | 数据库迁移 |
| MySQL | 8.0+ | 数据库 |
| Java | 21+ | 开发语言 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vben Admin | 5.x | 后台模板 |
| Ant Design Vue | 4.x | UI 组件库 |
| TypeScript | 5.x | 开发语言 |
| Vite | 6.x | 构建工具 |

## 功能特性

### 权限管理
- **用户管理**: 用户增删改查、状态管理、部门分配
- **角色管理**: 角色配置、菜单权限分配、数据权限设置
- **菜单管理**: 动态菜单、按钮权限、图标配置
- **部门管理**: 部门树结构、层级管理

### 数据权限
支持 5 种数据权限范围：
- `DATA_SCOPE_ALL` - 全部数据权限
- `DATA_SCOPE_DEPT` - 本部门数据
- `DATA_SCOPE_DEPT_AND_CHILD` - 本部门及下级部门
- `DATA_SCOPE_SELF` - 仅本人数据
- `DATA_SCOPE_CUSTOM` - 自定义部门数据

### 认证授权
- JWT Token 认证 (Sa-Token)
- 多端登录控制
- 接口权限校验 (`@SaCheckPermission`)
- 动态路由加载

## 快速开始

### 1. 环境要求
- JDK 21+
- Node.js 20+
- MySQL 8.0+
- pnpm 9+

### 2. 数据库配置
创建数据库并修改 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```

### 3. 启动后端
```bash
cd backend
mvn spring-boot:run
```
后端启动后，Flyway 会自动执行数据库迁移。

### 4. 启动前端
```bash
cd frontend
pnpm install
pnpm dev:antd
```

### 5. 访问系统
- 前端地址: http://localhost:10004
- 后端地址: http://localhost:10001
- API 文档: http://localhost:10001/swagger-ui.html

### 默认账号
| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | admin123 | 超级管理员 |

## 项目结构

```
├── backend/                          # 后端项目
│   ├── src/main/java/
│   │   └── com/taichu/yingjiguanli/
│   │       ├── common/               # 公共模块
│   │       │   ├── datascope/        # 数据权限
│   │       │   └── entity/           # 基础实体
│   │       ├── config/               # 配置类
│   │       ├── modules/              # 业务模块
│   │       │   └── sys/              # 系统管理
│   │       │       ├── controller/
│   │       │       ├── service/
│   │       │       ├── repository/
│   │       │       ├── entity/
│   │       │       ├── dto/
│   │       │       └── vo/
│   │       └── security/             # 安全模块
│   └── src/main/resources/
│       ├── db/migration/             # Flyway 迁移脚本
│       └── application.yml           # 配置文件
│
└── frontend/                         # 前端项目
    └── apps/web-antd/
        └── src/
            ├── api/                  # API 接口
            ├── views/                # 页面
            ├── router/               # 路由
            └── store/                # 状态管理
```

## 数据库迁移

使用 Flyway 管理数据库版本，迁移脚本位于 `backend/src/main/resources/db/migration/`。

添加新迁移：
```bash
# 创建新的迁移文件
touch backend/src/main/resources/db/migration/V5__your_migration.sql
```

启动应用时 Flyway 会自动执行未执行的迁移。

## 开发指南

### 添加新模块

1. **后端**
   - 在 `modules/` 下创建新模块目录
   - 创建 entity, repository, service, controller, dto, vo
   - 添加菜单和权限的 Flyway 迁移

2. **前端**
   - 在 `views/` 下创建页面
   - 在 `api/` 下创建接口
   - 菜单会通过后端动态加载

### 数据权限使用

在 Service 中使用 `DataScopeHelper`:
```java
@Autowired
private DataScopeHelper dataScopeHelper;

public Page<Entity> findPage(QueryDTO query) {
    Specification<Entity> spec = (root, cq, cb) -> {
        List<Predicate> predicates = new ArrayList<>();
        // 添加数据权限过滤
        Predicate dataScopePredicate = dataScopeHelper.buildDataScopePredicate(
            root, cb, "deptId", "createBy");
        if (dataScopePredicate != null) {
            predicates.add(dataScopePredicate);
        }
        return cb.and(predicates.toArray(new Predicate[0]));
    };
    return repository.findAll(spec, pageable);
}
```

## License

MIT License
