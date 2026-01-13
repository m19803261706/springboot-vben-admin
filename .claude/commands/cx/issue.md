---
description: CX Issue 管理 - 查看/管理 GitHub Issues 状态
allowed-tools: Bash, Read
---

# CX Issue - Issues 管理

查看和管理 GitHub Issues 状态。

## 使用方法

```bash
/cx:issue           # 显示所有 Open Issues 概览
/cx:issue #1        # 查看指定 Issue 详情
/cx:issue epic      # 只显示 Epic Issues
/cx:issue next      # 显示下一个可执行的 Issue
```

**参数**: $ARGUMENTS

## 执行流程

### 模式 1: 概览（无参数）

```bash
echo "📋 CX Issues 概览"
echo ""

# 列出所有 Epic
echo "🎯 Epic Issues:"
gh issue list --label "epic" --json number,title,state --jq '.[] | "  #\(.number) \(.title) [\(.state)]"'

echo ""

# 列出所有 Open Issues（按类型分组）
echo "📝 Open Issues:"

echo ""
echo "  [database]"
gh issue list --state open --label "database" --json number,title --jq '.[] | "    #\(.number) \(.title)"'

echo ""
echo "  [backend]"
gh issue list --state open --label "backend" --json number,title --jq '.[] | "    #\(.number) \(.title)"'

echo ""
echo "  [frontend]"
gh issue list --state open --label "frontend" --json number,title --jq '.[] | "    #\(.number) \(.title)"'

# 统计
echo ""
echo "📊 统计:"
total=$(gh issue list --state all --json number | jq length)
open=$(gh issue list --state open --json number | jq length)
closed=$(gh issue list --state closed --json number | jq length)
echo "  总计: $total | Open: $open | Closed: $closed"
```

### 模式 2: Issue 详情（#号）

```bash
issue_number=$(echo "$ARGUMENTS" | grep -oE '[0-9]+')

echo "📋 Issue #$issue_number 详情"
echo ""

# 获取 Issue 详情
gh issue view $issue_number

echo ""
echo "🏷️ Labels:"
gh issue view $issue_number --json labels --jq '.labels[].name'

echo ""
echo "💬 评论:"
gh issue view $issue_number --comments
```

### 模式 3: Epic 列表

```bash
echo "🎯 Epic Issues"
echo ""

gh issue list --label "epic" --state all --json number,title,state,body --jq '.[] | "
#\(.number) \(.title)
状态: \(.state)
"'
```

### 模式 4: 下一个可执行（next）

```bash
echo "🔍 查找下一个可执行 Issue..."
echo ""

# 按优先级和依赖查找
# 1. p0 > p1 > p2 > p3
# 2. database > backend > frontend

# 先找 database 类型的 Open Issues
next_db=$(gh issue list --state open --label "database" --json number,title --jq '.[0]')
if [ -n "$next_db" ]; then
  echo "📋 下一个任务 (database):"
  echo "$next_db"
  echo ""
  echo "执行: /cx:do #$(echo $next_db | jq -r '.number')"
  exit 0
fi

# 再找 backend
next_be=$(gh issue list --state open --label "backend" --json number,title --jq '.[0]')
if [ -n "$next_be" ]; then
  echo "📋 下一个任务 (backend):"
  echo "$next_be"
  echo ""
  echo "执行: /cx:do #$(echo $next_be | jq -r '.number')"
  exit 0
fi

# 最后找 frontend
next_fe=$(gh issue list --state open --label "frontend" --json number,title --jq '.[0]')
if [ -n "$next_fe" ]; then
  echo "📋 下一个任务 (frontend):"
  echo "$next_fe"
  echo ""
  echo "执行: /cx:do #$(echo $next_fe | jq -r '.number')"
  exit 0
fi

echo "✅ 所有任务已完成！"
```

## 输出示例

### 概览输出

```
📋 CX Issues 概览

🎯 Epic Issues:
  #1 Epic: 用户管理 [OPEN]
  #10 Epic: 订单系统 [CLOSED]

📝 Open Issues:

  [database]
    #2 用户表设计

  [backend]
    #3 用户 CRUD API
    #4 登录认证接口

  [frontend]
    #5 用户管理页面
    #6 登录页面

📊 统计:
  总计: 10 | Open: 5 | Closed: 5
```

### Issue 详情输出

```
📋 Issue #3 详情

title: [backend] 用户 CRUD API
state: OPEN
author: cx

## 任务描述

实现用户的增删改查 API。

## 技术要求

| 技术 | 版本 | Skill |
|------|------|-------|
| Spring Boot | 4.0.1 | spring-boot-4 |

...

🏷️ Labels:
backend
feat:用户管理
p1

💬 评论:
  (无评论)
```

## 注意事项

1. 需要 `gh` CLI 工具已登录
2. 需要在 Git 仓库目录中执行
3. 只显示当前仓库的 Issues
