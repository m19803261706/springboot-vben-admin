---
description: CX 任务执行 - 执行指定 Issue，自动加载相关 Skills，更新 GitHub 状态
allowed-tools: Bash, Read, Write, Glob, Grep, Edit, Task, TodoWrite, mcp__context7__resolve-library-id, mcp__context7__query-docs
---

# CX Do - 任务执行

执行指定的 GitHub Issue 任务，自动加载相关项目级 Skills，完成后更新 GitHub 状态。

## 使用方法

```bash
/cx:do #1        # 执行指定 Issue
/cx:do           # 自动选择下一个可执行的 Issue
/cx:do --all     # 依次执行所有 Open 的 Issues
```

**参数**: $ARGUMENTS

## ⚠️ 关键规则

**必须使用 TodoWrite 创建检查清单：**

```
1. [pending] 读取 Issue 详情和相关 Skills
2. [pending] 加载项目级 Skills
3. [pending] 执行任务代码实现
4. [pending] Git 提交代码
5. [pending] 更新 GitHub Issue（评论 + 关闭）
6. [pending] 更新 Epic Task List（勾选完成项）
7. [pending] 输出完成摘要
```

## 执行流程

### Step 1: 解析参数和读取 Issue

```bash
# 解析 Issue 编号
if [ -z "$ARGUMENTS" ]; then
  # 自动选择：找第一个 Open 且无依赖阻塞的 Issue
  issue_number=$(gh issue list --state open --label "database,backend,frontend" --json number -q '.[0].number')
else
  issue_number=$(echo "$ARGUMENTS" | grep -oE '[0-9]+')
fi

# 读取 Issue 详情
gh issue view $issue_number --json title,body,labels
```

### Step 2: 解析技术要求和加载 Skills

从 Issue body 中提取技术要求：

```markdown
## 技术要求

| 技术 | 版本 | Skill |
|------|------|-------|
| Spring Boot | 4.0.1 | spring-boot-4 |
| Sa-Token | 1.38 | sa-token |
```

**自动加载项目级 Skills：**

```bash
# 读取相关 Skills
for skill in skills_needed; do
  cat ".claude/skills/$skill/SKILL.md"
done
```

### Step 3: 检查依赖

```bash
# 从 Issue body 中提取 "Part of #xxx"
epic_number=$(echo "$body" | grep -oP 'Part of #\K[0-9]+')

# 检查是否有依赖的 Issue 未完成
# 依赖顺序: database → backend → frontend
```

### Step 4: 开始执行任务

根据任务类型执行：

#### 4.1 Database 任务

```bash
# 创建 Flyway 迁移脚本
# backend/src/main/resources/db/migration/V{n}__{description}.sql

# 创建 JPA Entity
# backend/src/main/java/.../entity/{Entity}.java
```

#### 4.2 Backend 任务

按加载的 Spring Boot 4 Skill 规范：

```bash
# 创建 Entity (如果需要)
# 创建 Repository
# 创建 Service
# 创建 Controller

# 所有代码必须：
# - 中文注释
# - 符合 Skill 中定义的代码规范
```

#### 4.3 Frontend 任务

按加载的 Vben Admin Skill 规范：

```bash
# 创建页面组件
# 创建 API 接口
# 更新路由配置
# 更新菜单配置
```

### Step 5: Git 提交

```bash
git add .
git commit -m "feat({模块}): {任务描述} (#$issue_number)

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>"

git push origin main
```

### Step 6: 更新 GitHub Issue

```bash
# 获取变更信息
commit_hash=$(git rev-parse --short HEAD)
files_changed=$(git diff --name-only HEAD~1)

# 添加完成评论
gh issue comment $issue_number --body "✅ **任务完成**

⏰ 完成时间: $(date '+%Y-%m-%d %H:%M:%S')

## 变更文件

\`\`\`
$files_changed
\`\`\`

## 提交

\`$commit_hash\`

---
_CX System Auto Complete_"

# 关闭 Issue
gh issue close $issue_number
```

### Step 7: 更新 Epic Task List

```bash
# 获取 Epic Issue body
epic_body=$(gh issue view $epic_number --json body -q '.body')

# 勾选对应任务
# - [ ] #xx → - [x] #xx
updated_body=$(echo "$epic_body" | sed "s/- \[ \] #$issue_number/- [x] #$issue_number/")

# 更新 Epic
gh issue edit $epic_number --body "$updated_body"

# 添加进度评论
gh issue comment $epic_number --body "📊 #$issue_number 已完成"
```

### Step 8: 输出结果

```
✅ CX Do 完成

📋 Issue: #$issue_number - {title}
🎯 Epic: #$epic_number
⏱️ 提交: $commit_hash

## 变更摘要

新建文件:
- backend/src/main/.../UserController.java
- backend/src/main/.../UserService.java

修改文件:
- backend/pom.xml

## 加载的 Skills

- spring-boot-4 ✅
- sa-token ✅

## Epic 进度

🎯 Epic #$epic_number
├── #2 [database] 数据表设计 ✅
├── #3 [backend] CRUD API ⬜ ← 下一个
└── #4 [frontend] 管理页面 ⬜

进度: ◉○○ 1/3 (33%)

## 下一步

/cx:do #3
```

## 错误处理

### 依赖未满足

```
⚠️ Issue #4 依赖未满足

依赖状态:
  #3 [backend] CRUD API: ❌ OPEN

建议:
  1. 先执行依赖: /cx:do #3
  2. 强制执行: /cx:do #4 --force
```

### 执行失败

```bash
# 添加失败评论
gh issue comment $issue_number --body "❌ **任务执行失败**

## 错误信息

{error_message}

## 已完成部分

{partial_progress}

---
_需要人工干预_"
```

## 注意事项

1. **TodoWrite 必须使用**: 确保所有步骤被执行
2. **Skills 自动加载**: 根据 Issue 中的技术要求加载
3. **中文注释**: 所有生成的代码必须包含中文注释
4. **Git 提交**: 每个任务独立提交
5. **Epic 更新**: 任务完成后必须更新 Epic 进度
