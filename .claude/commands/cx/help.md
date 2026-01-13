---
description: CX 帮助文档 - 显示所有 CX 命令使用指南
---

# CX 智能体系统帮助

CX (Claude eXtension) 是一套轻量级的项目开发智能体系统。

## 核心理念

```
极简设计：
• 无 Memory MCP - 所有状态由 GitHub Issues 维护
• 项目级 Skills - 每个项目自己的技术栈文档
• 智能依赖检测 - plan 时自动识别缺失的 skills
```

## 命令列表

| 命令 | 用途 | 示例 |
|------|------|------|
| `/cx:help` | 显示帮助文档 | `/cx:help` |
| `/cx:prd` | 需求收集，生成 PRD | `/cx:prd 用户管理` |
| `/cx:plan` | 任务规划，创建 Issues | `/cx:plan 用户管理` |
| `/cx:do` | 执行指定 Issue | `/cx:do #1` 或 `/cx:do` |
| `/cx:issue` | 查看/管理 Issues | `/cx:issue` 或 `/cx:issue #1` |
| `/cx:skill` | 创建/更新项目级 Skill | `/cx:skill spring-boot-4` |

## 工作流程

```
/cx:prd [功能名]     # Step 1: 收集需求，生成 PRD
        ↓
/cx:plan [功能名]    # Step 2: 技术栈检测 + 创建 Issues
        ↓
/cx:do [#issue]      # Step 3: 执行任务，更新 Issues
        ↓
      完成！
```

## 项目结构

```
{项目根目录}/
├── .claude/
│   ├── skills/                # 项目级 Skills
│   │   ├── spring-boot-4/
│   │   │   └── SKILL.md
│   │   └── vben-admin/
│   │       └── SKILL.md
│   └── prds/                  # PRD 文档
│       └── {功能名}.md
└── ...
```

## GitHub Labels

| 标签 | 颜色 | 用途 |
|------|------|------|
| `epic` | `#8b0000` | Epic 任务标识 |
| `frontend` | `#61dafb` | 前端任务 |
| `backend` | `#6db33f` | 后端任务 |
| `database` | `#336791` | 数据库任务 |
| `p0` ~ `p3` | - | 优先级 |

## 快速开始

```bash
# 1. 确保项目有 Git 仓库和 GitHub remote
git remote -v

# 2. 开始需求收集
/cx:prd 用户管理

# 3. 规划任务（自动创建 Issues）
/cx:plan 用户管理

# 4. 执行第一个任务
/cx:do
```

## 注意事项

1. **项目级 Skills**: 所有 Skills 存放在项目 `.claude/skills/` 目录
2. **GitHub 为主**: 任务状态完全由 GitHub Issues 管理，无本地状态文件
3. **自动检测**: plan 时自动检测技术栈，缺失 Skill 会询问是否创建
4. **中文注释**: 所有生成的代码必须包含中文注释
