---
description: CX 任务规划 - 读取 PRD，检测技术栈，确保 Skills 就绪，创建 GitHub Epic + 子任务 Issues
allowed-tools: Bash, Read, Write, Glob, Grep, Edit, Task, AskUserQuestion, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

# CX Plan - 任务规划

读取 PRD，检测技术栈，确保项目级 Skills 就绪，创建 GitHub Epic + 子任务 Issues。

## 使用方法

```bash
/cx:plan [功能名称]     # 指定功能名称
/cx:plan               # 自动查找最新 PRD
```

**参数**: $ARGUMENTS

## 执行流程

### Step 1: 读取 PRD

```bash
# 查找 PRD 文件
if [ -z "$ARGUMENTS" ]; then
  prd_file=$(ls -t .claude/prds/*.md 2>/dev/null | head -1)
else
  prd_file=".claude/prds/$ARGUMENTS.md"
fi

cat "$prd_file"
```

### Step 2: 技术栈检测（双重检测）

#### 2.1 现有项目技术栈

扫描项目文件，识别已使用的技术：

```bash
# 后端检测
if [ -f "backend/pom.xml" ] || [ -f "pom.xml" ]; then
  # 检测 Spring Boot 版本
  spring_version=$(grep -A1 "spring-boot-starter-parent" pom.xml | grep version | sed 's/.*>\(.*\)<.*/\1/')
  echo "检测到 Spring Boot: $spring_version"
fi

# 前端检测
if [ -f "frontend/package.json" ] || [ -f "package.json" ]; then
  # 检测前端框架
  cat package.json | jq '.dependencies'
fi
```

#### 2.2 PRD 中的新技术依赖

分析 PRD 内容，识别需要引入的新技术：

| 功能关键词 | 可能的技术 | Context7 Library |
|-----------|-----------|------------------|
| PDF/盖章 | PDFBox | `/apache/pdfbox` |
| 登录/认证 | Sa-Token | `/sa-token` |
| 缓存 | Redis | `/redis` |
| 文件存储 | MinIO | `/minio/minio` |
| 二维码 | ZXing | `/zxing/zxing` |

### Step 3: 检查项目级 Skills

```bash
# 检查 .claude/skills/ 目录
ls -la .claude/skills/

# 对比需要的 Skills
# 例如：spring-boot-4, vben-admin, sa-token
```

#### 缺失 Skill 处理

**只有在缺失 Skill 时才询问用户：**

```json
{
  "questions": [{
    "question": "检测到需要以下技术但缺少对应 Skill，是否创建？",
    "header": "创建 Skill",
    "multiSelect": true,
    "options": [
      {"label": "sa-token (登录认证)", "description": "使用 Context7 查询并生成"},
      {"label": "pdfbox (PDF处理)", "description": "使用 Context7 查询并生成"},
      {"label": "跳过全部", "description": "手动处理"}
    ]
  }]
}
```

**自动创建 Skill：**

```python
# 1. 解析 Library ID
result = mcp__context7__resolve-library-id("sa-token", "Java login authentication")

# 2. 查询文档
docs = mcp__context7__query-docs("/dromara/sa-token", "login authentication permission")

# 3. 生成项目级 SKILL.md
# .claude/skills/sa-token/SKILL.md
```

### Step 4: 确保 GitHub Labels 存在

```bash
# 检查并创建系统标签
labels=(
  "epic:Epic任务:8b0000"
  "frontend:前端任务:61dafb"
  "backend:后端任务:6db33f"
  "database:数据库任务:336791"
  "p0:最高优先级:b60205"
  "p1:高优先级:d93f0b"
  "p2:中优先级:fbca04"
  "p3:低优先级:0e8a16"
)

for item in "${labels[@]}"; do
  IFS=':' read -r name desc color <<< "$item"
  gh label create "$name" --description "$desc" --color "$color" 2>/dev/null || true
done

# 创建功能标签
gh label create "feat:$功能名" --color "0366d6" 2>/dev/null || true
```

### Step 5: 任务拆分

根据 PRD 和技术栈，拆分任务：

```yaml
tasks:
  - title: "{功能}数据表设计"
    type: database
    priority: p0
    depends_on: []
    skills: []

  - title: "{功能} CRUD API"
    type: backend
    priority: p1
    depends_on: [1]
    skills: [spring-boot-4]

  - title: "{功能}管理页面"
    type: frontend
    priority: p2
    depends_on: [2]
    skills: [vben-admin]
```

### Step 6: 创建 Epic Issue

```bash
epic_body="## 功能概述

{PRD 摘要}

## 技术栈

| 技术 | 版本 | 用途 | Skill |
|------|------|------|-------|
| Spring Boot | 4.0.1 | 后端框架 | ✅ |
| Vben Admin | 5.x | 前端框架 | ✅ |
| MySQL | 8.0 | 数据库 | - |

## 任务列表

<!-- GitHub 自动显示进度条 -->
- [ ] #xx [database] 数据表设计
- [ ] #xx [backend] CRUD API
- [ ] #xx [frontend] 管理页面

## PRD

详见: .claude/prds/{功能名}.md

---
> CX System Epic
> 创建时间: {datetime}"

epic_number=$(gh issue create \
  --title "🎯 Epic: {功能名}" \
  --body "$epic_body" \
  --label "epic,feat:$功能名,p0" \
  | grep -oE '[0-9]+$')
```

### Step 7: 批量创建子任务 Issues

```bash
for task in tasks; do
  body="## 任务描述

{task_description}

## 技术要求

| 技术 | 版本 | Skill |
|------|------|-------|
| {tech} | {version} | {skill_status} |

## 相关 Skills

执行时自动加载:
- \`.claude/skills/{skill}/SKILL.md\`

## 验收标准

- [ ] {标准1}
- [ ] {标准2}

---
> Part of #$epic_number
> CX System Task"

  issue_number=$(gh issue create \
    --title "[{type}] {title}" \
    --body "$body" \
    --label "{type},feat:$功能名,{priority}" \
    | grep -oE '[0-9]+$')
done
```

### Step 8: 更新 Epic Task List

创建完子任务后，更新 Epic 的 Task List 添加实际 Issue 链接。

### Step 9: 输出结果

```
✅ CX Plan 完成

📋 功能: {功能名}
📄 PRD: .claude/prds/{功能名}.md

## 技术栈检测

| 技术 | 版本 | Skill | 状态 |
|------|------|-------|------|
| Spring Boot | 4.0.1 | spring-boot-4 | ✅ 已就绪 |
| Vben Admin | 5.x | vben-admin | ✅ 已就绪 |
| Sa-Token | 1.38 | sa-token | ✅ 新建 |

## 创建的 Issues

🎯 Epic #1: {功能名}
├── #2 [database] 数据表设计 (p0)
├── #3 [backend] CRUD API (p1)
│   └─ Skills: spring-boot-4
├── #4 [backend] 登录认证 (p1)
│   └─ Skills: spring-boot-4, sa-token
└── #5 [frontend] 管理页面 (p2)
    └─ Skills: vben-admin

## 下一步

执行任务: /cx:do #2
查看状态: /cx:issue
```

## 注意事项

1. **自动执行**: 除非检测到缺失 Skill，否则全程自动
2. **项目级 Skills**: 所有 Skills 创建在 `.claude/skills/` 目录
3. **GitHub 为主**: 任务状态由 GitHub Issues 管理
4. **依赖顺序**: database → backend → frontend
