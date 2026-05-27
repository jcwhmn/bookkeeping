# Bookkeeping 用户指南

> 本文档是 Java 版 Bookkeeping 系统的用户操作指南。
> 
> **最后更新**: 2026-05-27
> 
> **更新规则**: 新增功能后，必须同步更新本文档。

---

## 目录

1. [快速开始](#1-快速开始)
2. [账户管理](#2-账户管理)
3. [交易管理](#3-交易管理)
4. [分类管理](#4-分类管理)
5. [标签管理](#5-标签管理)
6. [预算管理](#6-预算管理)
7. [统计与分析](#7-统计与分析)
8. [预算管理](#8-预算管理)
9. [汇率管理](#9-汇率管理)
10. [数据导入导出](#10-数据导入导出)
11. [用户设置](#11-用户设置)
12. [API 接口](#12-api-接口)

---

## 1. 快速开始

### 1.1 登录系统

1. 访问 `http://localhost:3000`（前端地址）
2. 输入用户名和密码
3. 点击登录按钮

**默认测试账号**:
- 用户名: `demo`
- 密码: `demo123`

### 1.2 首次使用引导

首次登录后，系统会显示新用户引导流程：

1. **创建账户** - 选择账户类型（现金、银行、信用卡等）
2. **添加分类** - 创建收入/支出分类（可使用预设模板快速创建）
3. **设置偏好** - 选择默认货币、语言等

### 1.3 仪表盘

登录后显示仪表盘，包含：

| 模块 | 说明 |
|------|------|
| 本月支出卡片 | 本月累计支出和收入 |
| 资产概览 | 总资产、总负债、净资产 |
| 今日/本周/本月收支 | 快速查看收支情况 |
| 近12个月趋势图 | 可视化展示收支趋势 |

---

## 2. 账户管理

### 2.1 创建账户

**接口**: `POST /api/v1/accounts`

**请求示例**:
```json
{
  "name": "我的银行账户",
  "accountType": 2,
  "currency": "CNY",
  "includeInTotal": true,
  "hidden": false,
  "notes": "工资卡"
}
```

### 2.2 账户类型

| 类型值 | 名称 | 说明 |
|--------|------|------|
| 1 | 现金 | 物理现金 |
| 2 | 银行 | 支票、储蓄账户 |
| 3 | 信用卡 | 信用卡负债 |
| 4 | 电子钱包 | 微信、支付宝等 |
| 5 | 债务 | 欠款账户 |
| 6 | 应收款 | 别人欠你的钱 |
| 7 | 投资 | 股票、基金等 |
| 8 | 储蓄 | 定期存款 |

### 2.3 账户模板

系统提供预设账户模板（无需登录即可查看）:

**接口**: `GET /api/v1/accounts/templates.json`

**响应示例**:
```json
{
  "success": true,
  "result": [
    {"name": "Cash", "accountType": 1, "icon": "account_balance_wallet", "color": "#4CAF50"},
    {"name": "Bank Account", "accountType": 2, "icon": "account_balance", "color": "#2196F3"},
    {"name": "Credit Card", "accountType": 3, "icon": "credit_card", "color": "#F44336"},
    {"name": "Savings", "accountType": 8, "icon": "savings", "color": "#00BCD4"},
    {"name": "Investment", "accountType": 7, "icon": "show_chart", "color": "#9C27B0"},
    {"name": "Wallet", "accountType": 4, "icon": "payment", "color": "#607D8B"}
  ]
}
```

### 2.4 其他账户操作

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取账户列表 | `/api/v1/accounts` | GET |
| 获取单个账户 | `/api/v1/accounts/{id}` | GET |
| 更新账户 | `/api/v1/accounts/{id}` | PUT |
| 删除账户 | `/api/v1/accounts/{id}` | DELETE |
| 重新排序 | `/api/v1/accounts/reorder` | PUT |

---

## 3. 交易管理

### 3.1 交易类型

| 类型值 | 名称 | 说明 |
|--------|------|------|
| 1 | 修改余额 | 直接调整账户余额 |
| 2 | 收入 | 正向金额 |
| 3 | 支出 | 负向金额 |
| 4 | 转账(转出) | 从源账户转出 |
| 5 | 转账(转入) | 转入目标账户 |

### 3.2 创建交易

**接口**: `POST /api/v1/transactions`

**请求示例**:
```json
{
  "type": 3,
  "amount": 5000,
  "accountId": 1,
  "categoryId": 5,
  "transactionTime": 1716806400,
  "notes": "午餐费用",
  "tagIds": [1, 2]
}
```

**字段说明**:
- `amount`: 金额，单位为分（100 = 1.00）
- `transactionTime`: Unix 时间戳（秒）
- `tagIds`: 标签 ID 列表

### 3.3 批量操作

支持对多条交易进行批量操作：

| 操作 | 接口 |
|------|------|
| 批量更新 | `PUT /api/v1/transactions/batch_update` |
| 批量删除 | `DELETE /api/v1/transactions/batch_delete` |

**批量更新请求示例**:
```json
{
  "transactionIds": [1, 2, 3],
  "categoryId": 10,
  "tagIds": [3]
}
```

### 3.4 交易查询

**接口**: `GET /api/v1/transactions`

**查询参数**:
| 参数 | 说明 |
|------|------|
| accountId | 按账户筛选 |
| categoryId | 按分类筛选 |
| type | 按交易类型筛选 |
| minTime / maxTime | 按时间范围筛选 |
| minAmount / maxAmount | 按金额范围筛选 |
| tagIds | 按标签筛选 |
| keyword | 搜索备注 |
| cursor | 分页游标 |
| limit | 每页条数（默认50） |

### 3.5 其他交易操作

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取交易详情 | `/api/v1/transactions/{id}` | GET |
| 更新交易 | `/api/v1/transactions/{id}` | PUT |
| 删除交易 | `/api/v1/transactions/{id}` | DELETE |
| 统计收支 | `/api/v1/transactions/amounts` | GET |
| 导入交易 | `/api/v1/transactions/import` | POST |
| 导出交易 | `/api/v1/transactions/export` | GET |

---

## 4. 分类管理

### 4.1 分类类型

| 类型值 | 名称 |
|--------|------|
| 1 | 收入 |
| 2 | 支出 |

### 4.2 创建分类

**接口**: `POST /api/v1/categories`

**请求示例**:
```json
{
  "name": "餐饮",
  "categoryType": 2,
  "icon": "restaurant",
  "color": "#FF9800",
  "sortOrder": 0,
  "hidden": false
}
```

### 4.3 预设分类模板

系统提供预设分类模板，可一键创建：

**接口**: `POST /api/v1/onboarding/create_defaults.json`

**请求示例**:
```json
{
  "type": "all"
}
```

**参数说明**:
- `type`: `income`（仅收入）、`expense`（仅支出）、`all`（全部）

**预设支出分类**:
1. Food & Dining
2. Transportation
3. Shopping
4. Bills & Utilities
5. Entertainment
6. Healthcare
7. Education
8. Travel
9. Personal Care
10. Other Expenses

**预设收入分类**:
1. Salary
2. Freelance
3. Investment Returns
4. Business Income
5. Gifts & Donations
6. Other Income

### 4.4 其他分类操作

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取分类列表 | `/api/v1/categories` | GET |
| 更新分类 | `/api/v1/categories/{id}` | PUT |
| 删除分类 | `/api/v1/categories/{id}` | DELETE |
| 批量更新 | `/api/v1/categories/batch_update` | PUT |

---

## 5. 标签管理

### 5.1 创建标签

**接口**: `POST /api/v1/transaction/tags`

**请求示例**:
```json
{
  "name": "工作相关",
  "color": "#2196F3"
}
```

### 5.2 标签组

支持将标签分组管理：

| 操作 | 接口 | 方法 |
|------|------|------|
| 创建标签组 | `/api/v1/transaction/tag-groups` | POST |
| 获取标签组 | `/api/v1/transaction/tag-groups` | GET |
| 更新标签组 | `/api/v1/transaction/tag-groups/{id}` | PUT |
| 删除标签组 | `/api/v1/transaction/tag-groups/{id}` | DELETE |

### 5.3 其他标签操作

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取标签列表 | `/api/v1/transaction/tags` | GET |
| 更新标签 | `/api/v1/transaction/tags` | PUT |
| 删除标签 | `/api/v1/transaction/tags` | DELETE |

---

## 6. 预算管理

### 6.1 创建预算

**接口**: `POST /api/v1/budgets`

**请求示例**:
```json
{
  "categoryId": 5,
  "budgetAmount": 200000,
  "period": 1,
  "alertThreshold": 80
}
```

**period 参数**:
- `1`: 每月
- `2`: 每周
- `3`: 每年
- `4`: 自定义

### 6.2 其他预算操作

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取预算列表 | `/api/v1/budgets` | GET |
| 更新预算 | `/api/v1/budgets/{id}` | PUT |
| 删除预算 | `/api/v1/budgets/{id}` | DELETE |

---

## 7. 统计与分析

### 7.1 交易统计

**接口**: `GET /api/v1/transactions/statistics`

**响应示例**:
```json
{
  "success": true,
  "result": {
    "totalIncome": 500000,
    "totalExpense": 300000,
    "netSavings": 200000,
    "transactionCount": 45,
    "topCategories": [...]
  }
}
```

### 7.2 分类统计

**接口**: `GET /api/v1/transactions/statistics/category`

**查询参数**:
- `startTime` / `endTime`: 时间范围
- `accountId`: 账户筛选
- `type`: 交易类型

### 7.3 趋势分析

**接口**: `GET /api/v1/transactions/statistics/trend`

**响应示例**:
```json
{
  "success": true,
  "result": {
    "monthly": [
      {"month": "2026-01", "income": 500000, "expense": 300000},
      {"month": "2026-02", "income": 600000, "expense": 350000}
    ]
  }
}
```

---

## 8. 汇率管理

### 8.1 获取汇率

**接口**: `GET /api/v1/exchange-rates`

**响应示例**:
```json
{
  "success": true,
  "result": {
    "baseCurrency": "CNY",
    "rates": {
      "USD": 0.1374,
      "EUR": 0.1267,
      "JPY": 21.56
    },
    "updatedAt": 1716806400
  }
}
```

### 8.2 设置自定义汇率

**接口**: `PUT /api/v1/exchange-rates/custom`

**请求示例**:
```json
{
  "currency": "USD",
  "rate": 0.14
}
```

---

## 9. 数据导入导出

### 9.1 导出交易

**接口**: `GET /api/v1/transactions/export`

**查询参数**:
- `format`: `csv` 或 `json`
- `accountId`: 账户筛选
- `startTime` / `endTime`: 时间范围

### 9.2 导入交易

**接口**: `POST /api/v1/transactions/import`

**支持格式**: CSV, JSON

---

## 10. 用户设置

### 10.1 获取用户信息

**接口**: `GET /api/v1/users/me`

### 10.2 更新用户信息

**接口**: `PUT /api/v1/users/me`

**可更新字段**:
- `nickname`: 昵称
- `avatar`: 头像 URL
- `defaultCurrency`: 默认货币
- `language`: 语言
- `defaultAccountId`: 默认账户
- `transactionEditScope`: 交易编辑范围
- `firstDayOfWeek`: 每周第一天
- `fiscalYearStart`: 会计年度起始
- `dateFormat`: 日期格式

### 10.3 密码管理

| 操作 | 接口 | 方法 |
|------|------|------|
| 修改密码 | `/api/v1/users/me/password` | PUT |
| 忘记密码 | `/api/v1/auth/forgot-password` | POST |
| 重置密码 | `/api/v1/auth/reset-password` | POST |

### 10.4 数据管理

| 操作 | 接口 | 方法 |
|------|------|------|
| 获取数据统计 | `/api/v1/data-management/statistics` | GET |
| 导出数据 | `/api/v1/data-management/export` | GET |
| 清空交易 | `/api/v1/data-management/clear-transactions` | DELETE |
| 清空所有数据 | `/api/v1/data-management/clear-all` | DELETE |

---

## 11. API 接口

### 11.1 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/auth/login` | POST | 登录 |
| `/api/v1/auth/register` | POST | 注册 |
| `/api/v1/auth/logout` | POST | 登出 |
| `/api/v1/auth/refresh` | POST | 刷新 Token |
| `/api/v1/auth/verify-email` | POST | 验证邮箱 |

### 11.2 公共接口（无需认证）

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/accounts/templates.json` | GET | 账户模板 |
| `/v3/api-docs` | GET | OpenAPI 文档 |
| `/doc.html` | GET | Swagger UI |
| `/health` | GET | 健康检查 |

### 11.3 引导接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/v1/onboarding/status.json` | GET | 获取引导状态 |
| `/api/v1/onboarding/complete.json` | POST | 完成引导 |
| `/api/v1/onboarding/create_defaults.json` | POST | 创建默认分类 |

### 11.4 统一响应格式

```json
{
  "success": true,
  "result": { ... },
  "errorCode": null,
  "errorMessage": null
}
```

**错误响应**:
```json
{
  "success": false,
  "result": null,
  "errorCode": 100001,
  "errorMessage": "User not found"
}
```

### 11.5 错误码

| 错误码 | 说明 |
|--------|------|
| 100001 | 用户不存在 |
| 100002 | 用户名已存在 |
| 100003 | 邮箱已存在 |
| 100004 | 密码错误 |
| 200001 | 账户不存在 |
| 200002 | 账户名称重复 |
| 300001 | 分类不存在 |
| 300002 | 分类名称重复 |
| 400001 | 交易不存在 |
| 400002 | 标签不存在 |
| 400003 | 预算不存在 |
| 900001 | 认证失败 |
| 900002 | Token 过期 |
| 900003 | 权限不足 |

### 11.6 金额单位

所有金额在 API 中以**分**为单位存储和传输：

| 显示金额 | API 传输值 |
|----------|-----------|
| ¥100.00 | 10000 |
| $50.50 | 5050 |
| €25.00 | 2500 |

### 11.7 时间格式

所有时间以 **Unix 时间戳（秒）** 传输：

```json
{
  "transactionTime": 1716806400
}
```

对应 `2024-05-27 16:00:00 UTC`。

---

## 附录 A: 快速参考

### A.1 账户类型速查表

```
1 = 现金
2 = 银行账户
3 = 信用卡
4 = 电子钱包
5 = 债务
6 = 应收款
7 = 投资
8 = 储蓄
```

### A.2 交易类型速查表

```
1 = 修改余额
2 = 收入
3 = 支出
4 = 转账(转出)
5 = 转账(转入)
```

### A.3 分类类型速查表

```
1 = 收入
2 = 支出
```

---

## 附录 B: Swagger UI

访问 API 文档界面：

1. 打开浏览器访问: `http://localhost:8080/doc.html`
2. 页面会自动重定向到 Swagger UI
3. 点击右上角 **Authorize** 按钮，输入 JWT Token
4. 输入格式: `Bearer <your_token>`
5. 点击 **Close** 即可测试各接口

---

**文档版本**: v1.0  
**创建日期**: 2026-05-27  
**最后更新**: 2026-05-27