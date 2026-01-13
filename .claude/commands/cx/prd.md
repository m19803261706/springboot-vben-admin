---
description: CX 需求收集 - 对话式收集需求，生成 PRD 文档
allowed-tools: Bash, Read, Write, Glob, Grep, AskUserQuestion
---

# CX PRD - 需求收集

通过对话式问答收集需求，生成结构化的 PRD 文档。

## 使用方法

```bash
/cx:prd [功能名称]     # 指定功能名称
/cx:prd               # 交互式输入功能名称
```

**参数**: $ARGUMENTS

## 执行流程

### Step 1: 确定功能名称

如果未提供功能名称，询问用户：

```json
{
  "questions": [{
    "question": "请输入功能名称（如：用户管理、订单系统）",
    "header": "功能名",
    "options": [
      {"label": "用户管理", "description": "用户注册、登录、权限等"},
      {"label": "自定义输入", "description": "输入其他功能名称"}
    ]
  }]
}
```

### Step 2: 对话式需求收集

依次询问以下问题：

**Q1: 功能概述**
```
这个功能要解决什么问题？请简要描述。
```

**Q2: 核心功能点**
```json
{
  "question": "这个功能包含哪些核心子功能？（可多选）",
  "header": "子功能",
  "multiSelect": true,
  "options": [
    {"label": "列表展示", "description": "分页、搜索、筛选"},
    {"label": "新增/编辑", "description": "表单录入"},
    {"label": "详情查看", "description": "数据展示"},
    {"label": "删除操作", "description": "单个/批量删除"},
    {"label": "导入导出", "description": "Excel/CSV"},
    {"label": "其他", "description": "自定义输入"}
  ]
}
```

**Q3: 技术偏好**
```json
{
  "question": "有什么特殊的技术要求吗？",
  "header": "技术要求",
  "multiSelect": true,
  "options": [
    {"label": "无特殊要求", "description": "使用项目默认技术栈"},
    {"label": "需要缓存", "description": "Redis 缓存"},
    {"label": "需要权限控制", "description": "细粒度权限"},
    {"label": "需要文件上传", "description": "图片/文档上传"},
    {"label": "其他", "description": "自定义输入"}
  ]
}
```

**Q4: 优先级**
```json
{
  "question": "这个功能的优先级是？",
  "header": "优先级",
  "options": [
    {"label": "P0 - 紧急", "description": "阻塞其他工作，必须立即完成"},
    {"label": "P1 - 高", "description": "本周内完成"},
    {"label": "P2 - 中", "description": "本迭代完成"},
    {"label": "P3 - 低", "description": "可延后"}
  ]
}
```

### Step 3: 生成 PRD 文档

创建 `.claude/prds/{功能名}.md`：

```markdown
# {功能名} PRD

> 创建时间: {datetime}
> 优先级: {priority}

## 1. 功能概述

{用户描述的功能概述}

## 2. 核心功能

{根据用户选择列出}

### 2.1 {子功能1}
- 描述: ...
- 验收标准: ...

### 2.2 {子功能2}
- 描述: ...
- 验收标准: ...

## 3. 技术要求

{用户选择的技术要求}

## 4. 非功能需求

- 性能: 列表加载 < 1s
- 安全: 权限校验
- 兼容: 主流浏览器

## 5. 验收标准

- [ ] {标准1}
- [ ] {标准2}
- [ ] {标准3}

---
> CX System PRD
> 功能: {功能名}
```

### Step 4: 输出结果

```
✅ PRD 创建完成

📄 文件: .claude/prds/{功能名}.md
📋 功能: {功能名}
🏷️ 优先级: {priority}

核心功能:
• {子功能1}
• {子功能2}
• {子功能3}

📌 下一步: /cx:plan {功能名}
```

## 注意事项

1. PRD 存放在项目 `.claude/prds/` 目录
2. 如果目录不存在，自动创建
3. 如果同名 PRD 已存在，询问是否覆盖
4. PRD 用于 `/cx:plan` 的输入
