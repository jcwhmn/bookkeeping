# ezBookkeeping 产品功能规格文档

> 本文档基于 ezBookkeeping v1.5.0 (Go + Vue 3) 完整分析生成，作为 Java 版 Bookkeeping 系统重建的基础参考。

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术架构概览](#2-技术架构概览)
3. [数据模型](#3-数据模型)
4. [功能模块详述](#4-功能模块详述)
   - [4.1 用户认证与授权](#41-用户认证与授权)
   - [4.2 账户管理](#42-账户管理)
   - [4.3 交易管理](#43-交易管理)
   - [4.4 交易分类管理](#44-交易分类管理)
   - [4.5 交易标签管理](#45-交易标签管理)
   - [4.6 交易模板与定时交易](#46-交易模板与定时交易)
   - [4.7 统计与分析](#47-统计与分析)
   - [4.8 洞察分析器](#48-洞察分析器)
   - [4.9 数据导入导出](#49-数据导入导出)
   - [4.10 汇率管理](#410-汇率管理)
   - [4.11 AI/LLM 功能](#411-aillm-功能)
   - [4.12 MCP 协议服务](#412-mcp-协议服务)
   - [4.13 文件存储](#413-文件存储)
   - [4.14 数据管理](#414-数据管理)
   - [4.15 用户偏好设置](#415-用户偏好设置)
   - [4.16 应用设置](#416-应用设置)
   - [4.17 地图服务](#417-地图服务)
   - [4.18 首页仪表盘](#418-首页仪表盘)
   - [4.19 账户对账](#419-账户对账)
   - [4.20 系统管理](#420-系统管理)
5. [API 接口清单](#5-api-接口清单)
6. [非功能性需求](#6-非功能性需求)
7. [安全需求](#7-安全需求)

---

## 1. 项目概述

### 1.1 产品定义

ezBookkeeping 是一款轻量级、自托管的个人/小企业记账应用，支持桌面和移动端，具备 PWA 能力。

### 1.2 目标用户

- 个人用户日常记账
- 自由职业者财务管理
- 小企业主收支跟踪

### 1.3 核心价值

- **隐私可控**：开源自托管，数据完全由用户掌控
- **轻量高效**：低资源消耗，可运行在树莓派、NAS 等设备
- **功能全面**：多账户、多币种、多时区、AI 识别、定时交易
- **跨平台**：支持 Windows/macOS/Linux，Docker 一键部署

---

## 2. 技术架构概览

| 层级 | 原技术栈 (Go) | Java 版建议替代 |
|------|--------------|----------------|
| 后端框架 | Gin (Go) | Spring Boot 3 + Spring MVC |
| ORM | XORM (Go) | MyBatis-Plus / JPA + Hibernate |
| 数据库 | SQLite / MySQL / PostgreSQL | MySQL / PostgreSQL (主推) |
| 认证 | 自研 JWT + OAuth2 | Spring Security + OAuth2 Client |
| 前端 | Vue 3 + Vuetify 3 + Framework7 | 保持不变或替换 |
| 对象存储 | 本地 / MinIO / WebDAV | MinIO SDK / Spring Resource |
| 定时任务 | gocron | Spring Scheduler / Quartz |
| 邮件 | 自研 SMTP | Spring Mail (JavaMailSender) |
| LLM | 多 Provider SDK | Spring AI / 直接 HTTP 调用 |
| 构建工具 | Go mod + Vite | Maven / Gradle |

### 2.1 数据库支持

原系统支持三种数据库：SQLite、MySQL、PostgreSQL。Java 版建议以 MySQL 为主，PostgreSQL 为可选。

### 2.2 认证机制

- 内部认证：用户名/邮箱 + 密码 + 盐值哈希
- 外部认证：OAuth 2.0 / OIDC（支持 GitHub、GitEA、Nextcloud、通用 OIDC）
- 双因素认证：TOTP（Time-based One-Time Password）
- Token 类型：JWT（会话）、API Token（长期）、MCP Token（AI 集成）

### 2.3 统一响应格式

所有 REST API 响应遵循标准信封格式：

```json
{
  "success": true,
  "result": { ... }
}
```

```json
{
  "success": false,
  "errorCode": 200001,
  "errorMessage": "错误描述",
  "path": "/api/..."
}
```

错误码格式：`category * 100000 + subCategory * 1000 + index`

| 分类 | 范围 | 说明 |
|------|------|------|
| 系统错误 | 1xxxxx | 系统级异常 |
| 通用错误 | 2xxxxx | 业务级异常 |
| 用户相关 | 2x1xxx | subCategory=1 |
| Token 相关 | 2x2xxx | subCategory=2 |
| 2FA 相关 | 2x3xxx | subCategory=3 |
| 账户相关 | 2x4xxx | subCategory=4 |
| 交易相关 | 2x5xxx | subCategory=5 |
| 分类相关 | 2x6xxx | subCategory=6 |
| 标签相关 | 2x7xxx | subCategory=7 |
| 数据相关 | 2x8xxx | subCategory=8 |
| MCP 相关 | 2x14xxx | subCategory=14 |
| LLM 相关 | 2x15xxx | subCategory=15 |

---

## 3. 数据模型

### 3.1 核心实体关系图

```
User (1) ─────────< Transaction (N)
  │                    │
  │                    ├── Category (N:1)
  │                    ├── Account (N:1) [Source]
  │                    ├── Account (N:1) [Destination]
  │                    ├── Tag (N:M) via TransactionTagIndex
  │                    └── Picture (1:N) via TransactionPictureInfo
  │
  ├──< Account (N)
  │     └──< SubAccount (N) [self-referencing parent]
  │
  ├──< TransactionCategory (N)
  │     └──< SubCategory (N) [self-referencing parent]
  │
  ├──< TransactionTag (N) ──< TransactionTagGroup (N:1)
  │
  ├──< TransactionTemplate (N) [normal + scheduled]
  │
  ├──< TokenRecord (N)
  ├──< TwoFactor (1)
  ├──< UserExternalAuth (N)
  ├──< UserCustomExchangeRate (N)
  ├──< UserApplicationCloudSetting (N)
  └──< InsightsExplorer (N)
```

### 3.2 User（用户）

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT PK | 用户唯一 ID |
| Username | VARCHAR(32) UNIQUE | 用户名 |
| Email | VARCHAR(100) UNIQUE | 邮箱 |
| Nickname | VARCHAR(64) | 昵称 |
| Password | VARCHAR(64) | 密码哈希 |
| Salt | VARCHAR(10) | 密码盐值 |
| CustomAvatarType | VARCHAR(10) | 头像类型 |
| DefaultAccountId | BIGINT | 默认账户 ID |
| UseLastReconciledTime | BOOLEAN | 是否使用最后对账时间限制编辑 |
| TransactionEditScope | TINYINT | 交易可编辑范围 (0-7) |
| Language | VARCHAR(10) | 界面语言 |
| DefaultCurrency | VARCHAR(3) | 默认货币 (ISO 4217) |
| FirstDayOfWeek | TINYINT | 每周第一天 (0=周日) |
| FiscalYearStart | SMALLINT | 会计年度起始月日 (MMDD 编码) |
| CalendarDisplayType | TINYINT | 日历显示类型 |
| DateDisplayType | TINYINT | 日期显示类型 |
| LongDateFormat | TINYINT | 长日期格式 |
| ShortDateFormat | TINYINT | 短日期格式 |
| LongTimeFormat | TINYINT | 长时间格式 |
| ShortTimeFormat | TINYINT | 短时间格式 |
| FiscalYearFormat | TINYINT | 会计年度格式 |
| CurrencyDisplayType | TINYINT | 货币显示方式 (0-11) |
| NumeralSystem | TINYINT | 数字系统 (0-5) |
| DecimalSeparator | TINYINT | 小数分隔符 |
| DigitGroupingSymbol | TINYINT | 数字分组符号 |
| DigitGrouping | TINYINT | 数字分组方式 |
| CoordinateDisplayType | TINYINT | 坐标显示格式 |
| ExpenseAmountColor | TINYINT | 支出金额颜色 |
| IncomeAmountColor | TINYINT | 收入金额颜色 |
| FeatureRestriction | BIGINT | 功能限制位掩码 |
| Disabled | BOOLEAN | 是否禁用 |
| Deleted | BOOLEAN | 是否删除 (软删除) |
| EmailVerified | BOOLEAN | 邮箱是否已验证 |
| CreatedUnixTime | BIGINT | 创建时间 (Unix 秒) |
| UpdatedUnixTime | BIGINT | 更新时间 |
| DeletedUnixTime | BIGINT | 删除时间 |
| LastLoginUnixTime | BIGINT | 最后登录时间 |

#### 交易编辑范围枚举 (TransactionEditScope)

| 值 | 含义 |
|----|------|
| 0 | 禁止编辑 |
| 1 | 允许编辑所有交易 |
| 2 | 仅可编辑今天及以后的交易 |
| 3 | 仅可编辑最近 24 小时及以后的交易 |
| 4 | 仅可编辑本周及以后的交易 |
| 5 | 仅可编辑本月及以后的交易 |
| 6 | 仅可编辑本年及以后的交易 |
| 7 | 仅可编辑最后对账时间之后的交易 |

#### 金额颜色枚举 (AmountColorType)

| 值 | 含义 |
|----|------|
| 0 | 默认 |
| 1 | 绿色 |
| 2 | 红色 |
| 3 | 黄色 |
| 4 | 黑白 |

#### 功能限制枚举 (FeatureRestriction) - 位掩码

| 位 | 功能 |
|----|------|
| 1 | 修改密码 |
| 2 | 修改邮箱 |
| 3 | 修改个人资料基本信息 |
| 4 | 修改头像 |
| 5 | 登出其他会话 |
| 6 | 启用双因素认证 |
| 7 | 禁用双因素认证 |
| 8 | 忘记密码 |
| 9 | 导入交易 |
| 10 | 导出交易 |
| 11 | 清空所有数据 |
| 12 | 同步应用设置 |
| 13 | MCP 访问 |
| 14 | AI 图像识别创建交易 |
| 15 | OAuth 2.0 登录 |
| 16 | 解绑第三方登录 |
| 17 | 生成 API Token |

### 3.3 TokenRecord（令牌记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| TokenId | BIGINT PK | Token 唯一 ID |
| Uid | BIGINT | 所属用户 ID |
| UserToken | VARCHAR(512) | Token 哈希值 |
| TokenType | TINYINT | Token 类型 (1=普通/5=MCP/8=API) |
| UserAgent | VARCHAR(256) | 客户端 User-Agent |
| LastSeen | BIGINT | 最后活跃时间 |
| ExpiredUnixTime | BIGINT | 过期时间 |
| CreatedUnixTime | BIGINT | 创建时间 |

### 3.4 Account（账户）

| 字段 | 类型 | 说明 |
|------|------|------|
| AccountId | BIGINT PK | 账户 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| Category | TINYINT | 账户分类 (1-9) |
| Type | TINYINT | 账户类型 (1=单账户/2=多子账户) |
| ParentAccountId | BIGINT | 父账户 ID (顶级为 0) |
| Name | VARCHAR(64) | 账户名称 |
| DisplayOrder | INT | 显示排序 |
| Icon | BIGINT | 图标 ID |
| Color | VARCHAR(6) | 颜色 (Hex RGB) |
| Currency | VARCHAR(3) | 货币代码 (ISO 4217) |
| Balance | BIGINT | 余额 |
| Comment | VARCHAR(255) | 备注 |
| Extend | BLOB (JSON) | 扩展数据 |
| Hidden | BOOLEAN | 是否隐藏 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

#### 账户分类 (AccountCategory)

| 值 | 名称 | 资产/负债 |
|----|------|----------|
| 1 | 现金 (Cash) | 资产 |
| 2 | 支票账户 (Checking) | 资产 |
| 3 | 信用卡 (Credit Card) | 负债 |
| 4 | 虚拟账户 (Virtual) | 资产 |
| 5 | 债务 (Debt) | 负债 |
| 6 | 应收款 (Receivables) | 资产 |
| 7 | 投资 (Investment) | 资产 |
| 8 | 储蓄账户 (Savings) | 资产 |
| 9 | 存单 (CD) | 资产 |

#### 账户扩展数据 (AccountExtend - JSON)

```json
{
  "lastReconciledTime": 1700000000,
  "creditCardStatementDate": 15
}
```

- `lastReconciledTime`: 最后对账时间戳 (仅当启用时)
- `creditCardStatementDate`: 信用卡账单日 (1-28，仅信用卡类型)

### 3.5 Transaction（交易）

| 字段 | 类型 | 说明 |
|------|------|------|
| TransactionId | BIGINT PK | 交易 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| Type | TINYINT | 数据库交易类型 (1-5) |
| CategoryId | BIGINT | 分类 ID |
| AccountId | BIGINT | 账户 ID |
| TransactionTime | BIGINT | 交易时间序列 ID (用于排序+唯一约束) |
| TimezoneUtcOffset | SMALLINT | 时区偏移 (分钟, -720~840) |
| Amount | BIGINT | 金额 |
| RelatedId | BIGINT | 关联交易 ID (转账时使用) |
| RelatedAccountId | BIGINT | 关联账户 ID (转账目标) |
| RelatedAccountAmount | BIGINT | 关联账户金额 |
| HideAmount | BOOLEAN | 是否隐藏金额 |
| Comment | VARCHAR(255) | 备注 |
| GeoLongitude | DOUBLE | 经度 |
| GeoLatitude | DOUBLE | 纬度 |
| CreatedIp | VARCHAR(39) | 创建 IP |
| ScheduledCreated | BOOLEAN | 是否由定时任务创建 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |
| DeletedUnixTime | BIGINT | 删除时间 |

#### 交易类型枚举

**前端交易类型 (TransactionType):**

| 值 | 含义 |
|----|------|
| 1 | 修改余额 (Modify Balance) |
| 2 | 收入 (Income) |
| 3 | 支出 (Expense) |
| 4 | 转账 (Transfer) |

**数据库存储类型 (TransactionDbType):**

| 值 | 含义 |
|----|------|
| 1 | 修改余额 (MODIFY_BALANCE) |
| 2 | 收入 (INCOME) |
| 3 | 支出 (EXPENSE) |
| 4 | 转出 (TRANSFER_OUT) |
| 5 | 转入 (TRANSFER_IN) |

> **重要**：一笔转账交易在数据库中对应两条记录（TRANSFER_OUT + TRANSFER_IN），通过 RelatedId 互相关联。

#### 业务约束

- 每笔交易最多关联 **10 个标签**
- 每笔交易最多附加 **10 张图片**
- 金额范围：-99,999,999,999 ~ 99,999,999,999
- 备注最多 255 字符
- 时区偏移范围：-720 ~ +840 分钟（UTC-12 到 UTC+14）

### 3.6 TransactionCategory（交易分类）

| 字段 | 类型 | 说明 |
|------|------|------|
| CategoryId | BIGINT PK | 分类 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| Type | TINYINT | 分类类型 (1=收入/2=支出/3=转账) |
| ParentCategoryId | BIGINT | 父分类 ID (0=顶级) |
| Name | VARCHAR(64) | 分类名称 |
| DisplayOrder | INT | 显示排序 |
| Icon | BIGINT | 图标 ID |
| Color | VARCHAR(6) | 颜色 (Hex RGB) |
| Hidden | BOOLEAN | 是否隐藏 |
| Comment | VARCHAR(255) | 备注 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

> 支持两级分类体系：一级分类(主分类) → 二级分类(子分类)

### 3.7 TransactionTag（交易标签）

| 字段 | 类型 | 说明 |
|------|------|------|
| TagId | BIGINT PK | 标签 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| TagGroupId | BIGINT | 所属标签组 ID (0=无分组) |
| Name | VARCHAR(64) | 标签名称 |
| DisplayOrder | INT | 显示排序 |
| Hidden | BOOLEAN | 是否隐藏 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.8 TransactionTagIndex（交易-标签关联）

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT | 用户 ID |
| TagId | BIGINT | 标签 ID |
| TransactionId | BIGINT | 交易 ID |

> 多对多关联表

### 3.9 TransactionTemplate（交易模板）

包含两种类型：
- **普通模板 (type=1)**：可复用的交易蓝图
- **定时模板 (type=2)**：带重复规则的定时交易

| 字段 | 类型 | 说明 |
|------|------|------|
| TemplateId | BIGINT PK | 模板 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| TemplateType | TINYINT | 模板类型 (1=普通/2=定时) |
| Name | VARCHAR(64) | 模板名称 |
| Type | TINYINT | 交易类型 |
| CategoryId | BIGINT | 分类 ID |
| AccountId | BIGINT | 来源账户 ID |
| RelatedAccountId | BIGINT | 目标账户 ID |
| Amount | BIGINT | 金额 |
| RelatedAccountAmount | BIGINT | 目标账户金额 |
| HideAmount | BOOLEAN | 是否隐藏金额 |
| Comment | VARCHAR(255) | 备注 |
| GeoLongitude | DOUBLE | 经度 |
| GeoLatitude | DOUBLE | 纬度 |
| DisplayOrder | INT | 显示排序 |
| Hidden | BOOLEAN | 是否隐藏 |
| ScheduleFrequency | TINYINT | 重复频率 (0=禁用/1=每天/2=每周/3=每月/4=每年) |
| ScheduleDay | INT | 频率参数 (周几/几号) |
| ScheduleMonth | INT | 频率参数 (月) |
| ScheduleHour | INT | 执行小时 |
| ScheduleMinute | INT | 执行分钟 |
| ScheduleTimezoneOffset | SMALLINT | 定时任务时区 |
| ScheduleStartTime | BIGINT | 生效开始时间 |
| ScheduleEndTime | BIGINT | 生效结束时间 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.10 TransactionPictureInfo（交易图片）

| 字段 | 类型 | 说明 |
|------|------|------|
| PictureId | BIGINT PK | 图片 ID (UUID 字符串) |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| TransactionId | BIGINT | 关联交易 ID |
| PictureExtension | VARCHAR(10) | 图片扩展名 |
| CreatedUnixTime | BIGINT | 创建时间 |

### 3.11 InsightsExplorer（洞察分析器）

| 字段 | 类型 | 说明 |
|------|------|------|
| ExplorerId | BIGINT PK | 分析器 ID |
| Uid | BIGINT | 所属用户 ID |
| Deleted | BOOLEAN | 软删除标记 |
| Name | VARCHAR(64) | 分析器名称 |
| Config | TEXT (JSON) | 分析器配置 JSON |
| DisplayOrder | INT | 显示排序 |
| Hidden | BOOLEAN | 是否隐藏 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.12 Exchange Rates（汇率）

**LatestExchangeRate（系统缓存，非持久化）:**

| 字段 | 说明 |
|------|------|
| DataSource | 数据来源标识 |
| BaseCurrency | 基准货币 (ISO 4217) |
| UpdateTime | 最后更新时间 |
| ExchangeRates | 汇率列表 (货币 → 汇率) |

**UserCustomExchangeRate（用户自定义）:**

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT PK | 用户 ID |
| Currency | VARCHAR(3) PK | 货币代码 |
| Rate | BIGINT | 汇率 (×10^8 存储) |
| DeletedUnixTime | BIGINT PK | 删除时间 |
| CreatedUnixTime | BIGINT | 创建时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.13 TwoFactor（双因素认证）

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT PK | 用户 ID |
| Secret | VARCHAR(32) | TOTP 密钥 |
| RecoveryCodes | VARCHAR(255) | 恢复码 (逗号分隔) |
| CreatedUnixTime | BIGINT | 启用时间 |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.14 UserExternalAuth（外部认证绑定）

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT | 用户 ID |
| ExternalAuthType | VARCHAR(32) | 外部认证类型 |
| ExternalUserId | VARCHAR(128) | 外部用户 ID |
| ExternalUsername | VARCHAR(128) | 外部用户名 |
| CreatedUnixTime | BIGINT | 绑定时间 |

### 3.15 UserApplicationCloudSetting（应用云设置）

| 字段 | 类型 | 说明 |
|------|------|------|
| Uid | BIGINT | 用户 ID |
| SettingKey | VARCHAR(64) | 设置键 |
| SettingValue | TEXT | 设置值 (JSON) |
| UpdatedUnixTime | BIGINT | 更新时间 |

### 3.16 标签筛选表达式格式

标签筛选使用分号 `;` 分隔多个条件，每个条件格式为 `type:tagId1,tagId2,...`

| Type | 含义 |
|------|------|
| 0 | 包含任意 (HAS_ANY) |
| 1 | 包含全部 (HAS_ALL) |
| 2 | 不包含任意 (NOT_HAS_ANY) |
| 3 | 不包含全部 (NOT_HAS_ALL) |

示例：`0:1,2;1:3` = (包含标签1或2) 且 (必须包含标签3)
特殊值：`none` = 无标签

---

## 4. 功能模块详述

### 4.1 用户认证与授权

#### 4.1.1 内部认证（用户名/邮箱 + 密码）

**注册流程：**
1. 用户填写：用户名、邮箱、昵称、密码
2. 选择：默认货币、每周第一天、界面语言
3. 可选：预设默认分类模板（收入/支出/转账分类）
4. 注册成功后自动登录，返回 JWT Token
5. 如启用邮箱验证，可能需验证后才可登录

**登录流程：**
1. 输入用户名或邮箱 + 密码
2. 如启用 2FA，返回 `need2FA=true`，需进行第二步验证
3. 验证通过后返回 JWT Token + 用户信息 + 云端设置

**登出：** 吊销当前 JWT Token

#### 4.1.2 OAuth 2.0 / OIDC 外部认证

支持的外部认证平台：
- **OIDC**（通用 OpenID Connect 协议）
- **GitHub**（OAuth App）
- **GitEA**（自托管 Git 服务）
- **Nextcloud**

**流程：**
1. 前端重定向到服务端 `/oauth2/login?platform=xxx`
2. 服务端重定向到第三方授权页面
3. 用户授权后回调 `/oauth2/callback`
4. 如用户未注册且启用自动注册，自动创建账户
5. 可能需要补充密码或 2FA 验证

**外部认证管理（已登录用户）：**
- 查看已绑定的第三方账号
- 解绑第三方登录（需密码验证）

#### 4.1.3 双因素认证 (2FA / TOTP)

**启用流程：**
1. 请求启用 → 服务端生成 TOTP 密钥
2. 返回密钥 + QR Code（供 Authenticator App 扫描）
3. 用户输入 6 位验证码确认
4. 确认后返回恢复码（用于设备丢失时恢复）

**登录时的 2FA 流程：**
1. 密码验证通过 → 返回 2FA 中间 Token
2. 用户输入 6 位 TOTP 验证码
3. 验证通过 → 返回正式 JWT Token

**恢复码登录：**
- 使用 11 位恢复码替代 TOTP 验证码

**恢复码管理：**
- 查看/重新生成恢复码（需密码验证）

**禁用 2FA：**
- 需输入密码确认

#### 4.1.4 Token 管理

| Token 类型 | 用途 | 有效期 |
|-----------|------|--------|
| JWT Token | 浏览器会话 | 默认 30 天 (可配置) |
| API Token | 程序化 API 访问 | 自定义 (最长 2^32-1 秒) |
| MCP Token | AI/MCP 协议访问 | 自定义 |
| 临时 Token | 2FA 中间状态 | 默认 5 分钟 |
| 邮箱验证 Token | 邮箱验证 | 默认 60 分钟 |
| 密码重置 Token | 密码重置 | 默认 60 分钟 |

**Token 管理 API：**
- 列出所有活跃 Token 及其信息（类型、UA、最后活跃时间）
- 单独吊销指定 Token
- 批量吊销所有 Token（保留当前）
- 刷新当前 JWT Token（获取新的 Token + 最新用户信息）

#### 4.1.5 密码重置

1. 用户在登录页点击"忘记密码"
2. 输入注册邮箱
3. 系统发送密码重置邮件（含 Token 链接）
4. 用户通过链接访问重置页，输入新密码

#### 4.1.6 邮箱验证

1. 注册后或手动触发发送验证邮件
2. 邮件中包含验证链接
3. 用户点击链接完成验证
4. 可配置：强制邮箱验证后登录 / 忘记密码时要求已验证

#### 4.1.7 登录速率限制

- 每 IP 每分钟最多失败次数（默认 5 次）
- 每用户每分钟最多失败次数（默认 5 次）
- 超限后临时锁定

---

### 4.2 账户管理

#### 4.2.1 账户 CRUD

**创建账户：**
- 必填：名称、分类、类型（单/多子账户）、图标、颜色、货币
- 可选：余额、余额日期、备注、信用卡账单日
- 支持创建时同时创建多个子账户（当类型为"多子账户"时）

**修改账户：**
- 可修改：名称、图标、颜色、货币、余额、最后对账时间、备注、信用卡账单日
- 可设置隐藏/显示
- 支持同时修改子账户列表

**删除账户：**
- 删除账户及其所有子账户
- 单独删除某个子账户（关联交易移到父账户）
- 删除前检查关联数据

#### 4.2.2 账户列表

- 按分类分组显示（现金、支票、信用卡等 9 类）
- 显示净资产、总负债、总资产统计
- 支持仅显示可见账户
- 支持拖拽排序（需保存）
- 支持隐藏/显示账户
- 可配置不参与总金额计算的账户

#### 4.2.3 账户分类

| 分类 | 图标 | 资产/负债 | 说明 |
|------|------|----------|------|
| 现金 | 💵 | 资产 | 现金、钱包 |
| 支票账户 | 🏦 | 资产 | 银行活期账户 |
| 信用卡 | 💳 | 负债 | 信用卡额度账户 |
| 虚拟账户 | 🌐 | 资产 | 支付宝、微信等 |
| 债务 | 📋 | 负债 | 贷款、借款 |
| 应收款 | 📥 | 资产 | 别人欠你的钱 |
| 投资 | 📈 | 资产 | 股票、基金等 |
| 储蓄账户 | 🐷 | 资产 | 定期储蓄 |
| 存单 | 📄 | 资产 | 大额存单 |

---

### 4.3 交易管理

#### 4.3.1 交易类型

| 类型 | 说明 | 涉及账户 |
|------|------|---------|
| 修改余额 | 直接调整账户余额，无分类 | 1 个来源账户 |
| 收入 | 收入记录 | 1 个来源账户 + 分类 |
| 支出 | 支出记录 | 1 个来源账户 + 分类 |
| 转账 | 资金在账户间转移 | 来源账户 + 目标账户 |

#### 4.3.2 创建交易

**必填字段：**
- 交易类型
- 金额
- 来源账户
- 交易时间（含时区偏移）

**可选字段：**
- 分类（收入/支出类型必选）
- 目标账户 + 目标金额（转账类型必选）
- 标签（最多 10 个）
- 图片附件（需先上传获取 ID）
- 备注（最多 255 字符）
- 地理位置（经纬度）
- 隐藏金额标记

**业务规则：**
- 系统自动生成 UUID 防止重复提交
- 修改余额可正可负
- 转账生成两条数据库记录（转出+转入），互相通过 RelatedId 关联

#### 4.3.3 修改交易

- 需检查用户编辑权限（基于 TransactionEditScope）
- 需检查账户可见性
- 修改转账时同步更新关联记录
- 标签变更时同步更新 TransactionTagIndex 表

#### 4.3.4 删除交易

- 单个删除
- 批量删除（需密码验证）
- 删除转账时同时删除关联记录
- 删除时同步清理 TagIndex 和 Picture 关联

#### 4.3.5 交易查询

**查询模式：**

| 模式 | 说明 |
|------|------|
| 分页列表 | 基于时间序列 ID 的游标分页（max_time 向后翻页） |
| 按月列表 | 按指定年月查询全部交易 |
| 全部列表 | 按时间范围查询全部（不分页） |
| 单条详情 | 按 ID 查询单条交易 |

**筛选条件：**

| 筛选维度 | 说明 |
|---------|------|
| 交易类型 | 全部/修改余额/收入/支出/转账 |
| 分类 | 按一级或二级分类 ID |
| 账户 | 按来源或目标账户 ID |
| 标签 | 复杂标签表达式（包含任意/全部/不包含等） |
| 金额范围 | 自定义金额区间 |
| 关键词 | 搜索备注内容 |
| 日期范围 | 今天/昨天/本周/上周/本月/上月/本年/去年/最近30/60/90天/自定义 |
| 是否有图片 | 筛选有/无图片附件的交易 |

**分页参数：**
- 每页条数：1~50（必传）
- 游标：max_time（上一页最后一条的时间序列 ID）
- 可选返回总数（with_count=true）

#### 4.3.6 批量操作

| 操作 | 说明 |
|------|------|
| 批量修改分类 | 选中交易统一修改为指定分类 |
| 批量修改账户 | 选中交易统一修改为指定账户（来源或目标） |
| 批量添加标签 | 选中交易统一添加指定标签 |
| 批量移除标签 | 选中交易统一移除指定标签 |
| 批量清除标签 | 选中交易清除所有标签 |
| 批量删除 | 删除选中的所有交易 |

#### 4.3.7 转移交易

将一个账户下的所有交易记录转移到另一个账户。

---

### 4.4 交易分类管理

#### 4.4.1 两级分类体系

- **一级分类（主分类）：** 如"餐饮"、"交通"、"工资"
- **二级分类（子分类）：** 如"餐饮"下的"早餐"、"午餐"、"晚餐"

#### 4.4.2 分类类型

| 类型 | 值 | 说明 |
|------|----|------|
| 收入 | 1 | 工资、奖金、投资收益等 |
| 支出 | 2 | 餐饮、交通、购物等 |
| 转账 | 3 | 账户间转账分类 |

#### 4.4.3 CRUD 操作

- 单个创建/修改/删除
- 批量创建（含子分类层级）
- 预设分类（系统提供默认分类模板）
- 隐藏/显示分类
- 拖拽排序

#### 4.4.4 分类属性

- 名称（64 字符）
- 图标 ID
- 颜色（Hex RGB 6 位）
- 备注（255 字符）
- 父分类 ID

---

### 4.5 交易标签管理

#### 4.5.1 标签

- 名称（64 字符）
- 所属标签组
- 支持隐藏/显示
- 支持拖拽排序

#### 4.5.2 标签组

- 名称
- 支持拖拽排序
- 删除标签组时检查关联标签

#### 4.5.3 操作

- 单个创建标签
- 批量创建标签（多行输入）
- 修改/删除标签
- 隐藏/显示标签
- 标签组管理

---

### 4.6 交易模板与定时交易

#### 4.6.1 模板类型

| 类型 | 说明 |
|------|------|
| 普通模板 | 预设交易蓝图，在交易列表页快速创建 |
| 定时模板 | 带重复频率的模板，由后台定时任务自动创建 |

#### 4.6.2 模板内容

模板包含完整的交易信息（类型、金额、分类、账户、备注等），外加模板名称。

#### 4.6.3 定时交易重复频率

| 频率 | 参数 | 说明 |
|------|------|------|
| 禁用 (0) | - | 普通模板 |
| 每天 (1) | 执行时分 | 每天执行 |
| 每周 (2) | 周几 + 时分 | 指定星期几 |
| 每月 (3) | 几号 + 时分 | 指定日期 |
| 每年 (4) | 月-日 + 时分 | 指定月日 |

#### 4.6.4 定时交易生命周期

- **生效开始时间**: 定时交易从何时开始生效
- **生效结束时间**: 定时交易到何时停止生效
- **时区**: 定时交易的时区设置

#### 4.6.5 Cron 定时任务

系统后台定时任务按计划扫描用户定时模板，自动创建对应交易。

#### 4.6.6 模板管理

- 表格列出所有模板
- 显示：名称、类型、金额、分类、来源/目标账户
- 支持隐藏/显示
- 支持拖拽排序
- 从模板快速创建交易（交易列表页"添加"按钮下拉菜单）

---

### 4.7 统计与分析

#### 4.7.1 分类分析 (Categorical Analysis)

| 图表类型 | 说明 |
|---------|------|
| 饼图 (Pie) | 按分类/账户展示支出/收入占比 |
| 堆叠柱状图 (Bar) | 按分类/账户对比 |
| 桑基图 (Sankey) | 展示收入→支出流向 |

**分析维度：**
- 支出分析：按分类、按账户、按二级分类
- 收入分析：按分类、按账户、按二级分类
- 概览分析：收入 + 支出（桑基图）

#### 4.7.2 趋势分析 (Trend Analysis)

| 图表类型 | 说明 |
|---------|------|
| 柱状图 | 每月/每周/每天/每年收支趋势 |
| 堆叠柱状图 | 按分类堆叠的趋势 |
| 折线图 | 平滑趋势线 |
| 堆叠面积图 | 按分类堆叠的面积趋势 |

**时间聚合维度：** 按月 / 按周 / 按天 / 按年

#### 4.7.3 资产趋势 (Asset Trends)

- 按时间展示每个账户的开盘余额和收盘余额
- 按月/按周/按天/按年聚合

#### 4.7.4 通用功能

- 时间范围选择：与交易列表页相同（今天~自定义）
- 时间范围偏移：左右箭头切换时间段
- 筛选条件：账户、分类、标签、关键词
- 排序方式：按金额升序/降序、按名称
- 可配置默认筛选条件和图表类型
- 导出图表为图片或 CSV 数据

#### 4.7.5 数据筛选

| 筛选项 | 说明 |
|--------|------|
| 排除账户 | 统计中排除的账户 ID 列表 |
| 排除分类 | 统计中排除的分类 ID 列表 |
| 标签筛选 | 标签表达式 |
| 关键词 | 备注关键词搜索 |
| 时区模式 | 使用本地时区 / 交易时区 |

---

### 4.8 洞察分析器 (Insights Explorer)

自定义多维度数据查询和可视化分析工具。

#### 4.8.1 查询配置 (Query)

- **X 轴维度**：分类、账户、时间等
- **Y 轴维度**：金额
- 支持多级分组
- 支持筛选条件

#### 4.8.2 视图模式

| 选项卡 | 功能 |
|--------|------|
| 查询配置 | 配置 X/Y 轴维度和筛选 |
| 图表 | 根据配置生成可视化（柱状图、饼图、折线图、面积图） |
| 数据表 | 表格展示查询结果，可排序 |
| 可编辑表 | 在数据表中直接编辑交易（批量修改分类、账户、标签） |

#### 4.8.3 管理功能

- 保存当前分析器配置
- 加载已保存的分析器
- 修改/删除分析器
- 拖拽调整显示顺序
- 隐藏/显示分析器

---

### 4.9 数据导入导出

#### 4.9.1 支持导入的格式

| 格式 | 说明 |
|------|------|
| CSV | 通用 CSV |
| OFX (Open Financial Exchange) | 银行标准格式 |
| QFX (Quicken) | Quicken 专有格式 |
| QIF (Quicken Interchange) | Quicken 交换格式 |
| IIF (Intuit Interchange) | QuickBooks 格式 |
| Camt.052 / Camt.053 | ISO 20022 银行格式 |
| MT940 | SWIFT 银行格式 |
| GnuCash | 开源记账软件 |
| Firefly III | 开源记账软件 |
| Beancount | 纯文本记账格式 |
| 自定义格式 | 用户自定义列映射 |

#### 4.9.2 导入流程

1. 上传文件（最大 10MB）
2. 系统自动识别格式并解析
3. 自定义格式需配置：
   - 列映射
   - 自定义脚本解析（可选）
4. 预览解析后的交易数据
5. 可选：批量替换分类/账户
6. 确认导入
7. 异步处理（可查看导入进度）

#### 4.9.3 数据导出

- **CSV 格式**：逗号分隔
- **TSV 格式**：制表符分隔
- 支持按筛选条件导出（类型、分类、账户、标签、日期范围、关键词）

#### 4.9.4 数据清理

- 清空所有用户数据（需密码验证）
- 清空所有交易（需密码验证）
- 清空指定账户的所有交易（需密码验证）

---

### 4.10 汇率管理

#### 4.10.1 汇率数据来源

支持 18 个中央银行/机构的汇率数据：
- 欧洲中央银行 (ECB)
- 加拿大银行
- 捷克国家银行
- 丹麦国家银行
- 格鲁吉亚国家银行
- 匈牙利中央银行
- 以色列银行
- 哈萨克斯坦国家银行
- 缅甸中央银行
- 挪威银行
- 波兰国家银行
- 罗马尼亚国家银行
- 俄罗斯银行
- 瑞士国家银行
- 乌克兰国家银行
- 乌兹别克斯坦中央银行
- **用户自定义**（手动设置）

#### 4.10.2 汇率功能

- 自动获取/更新最新汇率
- 基准货币切换
- 汇率列表表格展示
- 金额实时换算
- 自定义汇率设置
- 显示数据来源和更新时间

---

### 4.11 AI/LLM 功能

#### 4.11.1 收据图片识别

上传收据/发票图片，AI 自动识别并生成交易信息。

**支持的 LLM 提供商：**

| Provider | API | 说明 |
|----------|-----|------|
| OpenAI | chat.openai.com | GPT-4 Vision |
| OpenAI Compatible | 自定义 | 兼容 OpenAI 协议的 API |
| Anthropic | api.anthropic.com | Claude |
| Anthropic Compatible | 自定义 | 兼容 Anthropic 协议的 API |
| OpenRouter | openrouter.ai | 多模型路由 |
| Ollama | 本地 | 本地运行的大模型 |
| LM Studio | 本地 | 本地运行的大模型 |
| Google AI | ai.google.dev | Gemini |

#### 4.11.2 识别流程

1. 用户上传收据图片
2. 系统将图片发送给 LLM（附带当前用户的分类、账户、标签列表作为上下文）
3. LLM 返回结构化 JSON（交易类型、金额、分类、账户、标签、备注等）
4. 前端展示识别结果，用户确认或修改后创建交易

#### 4.11.3 配置要点

- 最大识别图片大小（默认 10MB）
- 请求超时（默认 60 秒）
- 支持代理请求
- Anthropic 模型最大 Token 数可配置
- OpenAI 兼容 / Anthropic 兼容模式支持自定义 base URL

---

### 4.12 MCP 协议服务 (Model Context Protocol)

MCP 是一个为 AI 大模型设计的协议，允许 AI 通过标准化接口访问用户的记账数据。

#### 4.12.1 MCP 端点

- **入口**: `POST /mcp` (JSON-RPC 2.0)
- **认证**: MCP Token（需提前在用户设置中生成）
- **底层传输**: HTTP + SSE (Server-Sent Events)

#### 4.12.2 支持的 MCP 方法

| 方法 | 说明 |
|------|------|
| `initialize` | 初始化 MCP 会话 |
| `resources/list` | 列出可用资源 |
| `resources/read` | 读取指定资源 |
| `tools/list` | 列出可用工具 |
| `tools/call` | 调用工具 |
| `ping` | 心跳检测 |

#### 4.12.3 MCP 工具列表

通过 MCP，AI 可以执行的记账操作包括：
- 查询交易列表（含筛选）
- 创建交易
- 查询账户列表
- 查询分类列表
- 查询标签列表
- 获取统计数据
- 获取汇率

#### 4.12.4 安全控制

- 需单独生成 MCP Token
- 可通过功能限制禁用用户的 MCP 访问
- 可配置 MCP 服务端的 IP 白名单
- 提供 Agent Skill 脚本工具 (ebktools.sh/ps1)

---

### 4.13 文件存储

#### 4.13.1 存储后端

| 类型 | 说明 |
|------|------|
| 本地文件系统 | 默认存储方式 |
| MinIO | 兼容 S3 的对象存储 |
| WebDAV | 网络存储协议 |

#### 4.13.2 存储内容

1. **用户头像**
   - 提供者：内部存储 / Gravatar
   - 最大大小：1MB（可配置）

2. **交易图片**
   - 上传后获得 Picture ID
   - 与交易关联（多对多）
   - 支持清理未使用的图片

#### 4.13.3 图片上传流程

1. 在交易编辑页上传图片
2. 图片上传到存储服务，返回 Picture ID
3. 保存交易时关联 Picture ID
4. 删除交易时自动清理关联图片

---

### 4.14 数据管理

#### 4.14.1 数据统计

返回用户数据的整体概览：
- 账户总数
- 分类总数
- 标签总数
- 交易总数
- 图片总数
- 分析器数量
- 模板数量
- 定时交易数量

#### 4.14.2 数据清理

| 操作 | 需验证 |
|------|--------|
| 清空所有数据 | 需密码 |
| 清空所有交易 | 需密码 |
| 清空指定账户的交易 | 需密码 |

---

### 4.15 用户偏好设置

#### 4.15.1 基本信息

| 设置项 | 说明 | 选项/范围 |
|--------|------|----------|
| 邮箱 | 修改邮箱地址 | - |
| 昵称 | 修改显示昵称 | 64 字符 |
| 密码 | 修改登录密码 | 需旧密码 |
| 头像 | 上传/更换/删除头像 | - |
| 默认账户 | 快速记账的默认账户 | - |
| 默认货币 | ISO 4217 货币代码 | USD/CNY/EUR 等 |
| 语言 | 界面语言 | 19 种语言 |
| 每周第一天 | 周日/周一 | 0-6 |
| 交易编辑范围 | 可编辑交易的时限 | 0-7 (见枚举) |
| 会计年度起始 | 自定义会计年度起始月日 | MM-DD |

#### 4.15.2 日期时间格式

| 设置项 | 说明 |
|--------|------|
| 日历显示类型 | 日历年/会计年度 |
| 日期显示类型 | - |
| 长日期格式 | YYYY-MM-DD / MM/DD/YYYY / DD/MM/YYYY |
| 短日期格式 | MM-DD / DD-MM |
| 长时间格式 | 12h / 24h 及显示样式 |
| 短时间格式 | 12h / 24h 及显示样式 |
| 会计年度格式 | 年度显示格式 |

#### 4.15.3 数字和货币格式

| 设置项 | 说明 | 选项 |
|--------|------|------|
| 货币显示方式 | 符号/代码/名称的位置 | 12 种组合 |
| 数字系统 | 阿拉伯数字/本地数字 | 0-5 |
| 小数分隔符 | . 或 , | - |
| 数字分组符号 | , / . / 空格 / ' | - |
| 数字分组方式 | 无/千分位/万分位/印度式 | 0-3 |

#### 4.15.4 显示偏好

| 设置项 | 说明 |
|--------|------|
| 坐标显示格式 | DMS / DMM / DD |
| 支出金额颜色 | 默认/绿色/红色/黄色/黑白 |
| 收入金额颜色 | 默认/绿色/红色/黄色/黑白 |

---

### 4.16 应用设置

#### 4.16.1 基本设置

| 设置项 | 说明 |
|--------|------|
| 显示账户余额 | 控制账户列表是否显示余额 |
| 自动更新汇率 | 开启/关闭自动拉取最新汇率 |
| 快捷添加按钮 | 在导航栏显示"+"按钮 |

#### 4.16.2 应用锁 (Application Lock)

- **PIN 码锁**：6 位数字 PIN 码锁定应用
- **WebAuthn 解锁**：指纹/面部/安全密钥解锁
- 设置/修改/禁用
- 锁定后跳转到解锁页，需输入 PIN 或生物识别解锁

#### 4.16.3 统计默认设置

| 设置项 | 说明 |
|--------|------|
| 默认图表数据类型 | 支出/收入/概览 |
| 默认时区类型 | 本地时区/交易时区 |
| 默认账户筛选 | 预设排除账户 |
| 默认分类筛选 | 预设排除分类 |
| 默认排序方式 | 金额降序/升序/名称 |
| 默认分类图表类型 | 饼图/柱状图 |
| 默认趋势图类型 | 柱状图/堆叠柱状图/折线图/面积图 |
| 默认资产趋势图类型 | 柱状图/折线图 |
| 各类图表默认数据范围 | 本月/本年/最近12月等 |

#### 4.16.4 设置云端同步

- 开启/关闭应用设置云端同步
- 设置以 JSON 形式存储在服务器
- 跨设备同步用户偏好

#### 4.16.5 浏览器缓存管理

- 地图瓦片缓存过期时间
- 汇率数据缓存过期时间

#### 4.16.6 主题

- 浅色模式
- 深色模式
- 自动（跟随系统）

---

### 4.17 地图服务

#### 4.17.1 地图提供商

| Provider | 说明 |
|----------|------|
| OpenStreetMap | 默认，免费开源 |
| OpenStreetMap Humanitarian | 人道主义用途 |
| OpenTopoMap | 地形图 |
| ÖPNVKarte | 公共交通地图 |
| CyclOSM | 骑行地图 |
| CartoDB | Carto 底图 |
| TomTom | 商业地图 |
| Tianditu (天地图) | 中国 |
| Google Maps | 商业地图 |
| Baidu Map (百度地图) | 中国 |
| Amap (高德地图) | 中国 |
| 自定义 | 自定义瓦片服务器 |

#### 4.17.2 地图功能

- 交易地理位置标记（经纬度）
- 地图展示交易地点
- 支持代理转发（隐私保护）
- 自定义瓦片服务器 URL

---

### 4.18 首页仪表盘

#### 4.18.1 数据卡片

| 卡片 | 内容 |
|------|------|
| 本月支出 | 本月累计支出金额（支持隐藏/刷新） |
| 资产概览 | 总资产、总负债、净资产、账户数量 |
| 今日收支 | 今日收入和支出金额（点击查看明细） |
| 本周收支 | 本周收入和支出金额 |
| 本月收支 | 本月收入和支出金额 |
| 本年收支 | 本年收入和支出金额 |

#### 4.18.2 趋势图

- 近 12 个月收支趋势（柱状图/折线图）
- 点击任意月份可跳转到该月交易列表

---

### 4.19 账户对账 (Reconciliation Statement)

#### 4.19.1 功能

- 选择一个账户和起止日期
- 展示该时间段内所有交易流水
- 每笔交易显示：时间、类型、金额、备注、对方账户、开盘/收盘余额
- 显示统计：期初余额、期末余额、总流入、总流出

---

### 4.20 系统管理

#### 4.20.1 服务端配置

支持通过 INI 配置文件管理所有系统设置，包括：
- 服务器（协议、地址、端口、域名、GZip、日志）
- 数据库（类型、连接、连接池、自动更新）
- 邮件（SMTP 配置）
- 日志（级别、文件、轮转）
- 存储（类型、端点、凭证）
- LLM（多 Provider 配置）
- UUID 生成器
- 重复提交检查
- Cron 定时任务
- 安全（密钥、Token 有效期、速率限制）
- 认证（内部/OAuth2/OIDC/2FA）
- 用户功能开关
- 数据（导入导出限制）
- 通知（注册/登录/打开提示）
- 地图（Provider、API Key）
- 汇率（数据源、代理）

#### 4.20.2 CLI 命令行工具

- `server run` — 启动 Web 服务器
- `database update` — 更新数据库表结构
- `user-data` — 用户数据管理（增删改查用户、导入导出交易等）
- `cron-jobs` — 定时任务管理（列出/运行）
- `security gen-secret-key` — 生成密钥
- `utility parse-default-request-id` — 解析请求 ID
- `utility send-test-mail` — 发送测试邮件

#### 4.20.3 健康检查

- `GET /healthz.json` — 服务器健康检查（无需认证）
- `GET /api/systems/version.json` — 版本信息

#### 4.20.4 服务端设置暴露

- `GET /server_settings.js` — 以 JavaScript 形式返回前端需要的服务端设置（地图、功能开关等）

---

## 5. API 接口清单

### 5.1 系统/公开接口（无需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/healthz.json` | 健康检查 |
| GET | `/server_settings.js` | 服务端设置（JS） |
| GET | `/api/systems/version.json` | 版本信息 |

### 5.2 认证接口（无需 Bearer Token）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/authorize.json` | 登录 |
| POST | `/api/register.json` | 注册 |
| GET | `/api/logout.json` | 登出 |
| POST | `/api/2fa/authorize.json` | 2FA 验证登录 |
| POST | `/api/2fa/recovery.json` | 2FA 恢复码登录 |
| GET | `/oauth2/login` | OAuth2 登录跳转 |
| GET | `/oauth2/callback` | OAuth2 回调 |
| POST | `/api/oauth2/authorize.json` | OAuth2 最终登录 |
| POST | `/api/verify_email/resend.json` | 重发验证邮件（无需认证） |
| POST | `/api/verify_email/by_token.json` | 通过 Token 验证邮箱 |
| POST | `/api/forget_password/request.json` | 请求密码重置 |
| POST | `/api/forget_password/reset/by_token.json` | 通过 Token 重置密码 |

### 5.3 Token 管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/tokens/list.json` | Token 列表 |
| POST | `/api/v1/tokens/generate/api.json` | 生成 API Token |
| POST | `/api/v1/tokens/generate/mcp.json` | 生成 MCP Token |
| POST | `/api/v1/tokens/revoke.json` | 吊销 Token |
| POST | `/api/v1/tokens/revoke_all.json` | 吊销所有 Token |
| POST | `/api/v1/tokens/refresh.json` | 刷新 Token |

### 5.4 用户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/users/profile/get.json` | 获取用户信息 |
| POST | `/api/v1/users/profile/update.json` | 更新用户信息 |
| POST | `/api/v1/users/avatar/update.json` | 上传头像 |
| POST | `/api/v1/users/avatar/remove.json` | 删除头像 |
| POST | `/api/v1/users/verify_email/resend.json` | 重发验证邮件 |
| GET | `/api/v1/users/external_auth/list.json` | 外部认证列表 |
| POST | `/api/v1/users/external_auth/unlink.json` | 解绑外部认证 |

### 5.5 2FA 管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/users/2fa/status.json` | 2FA 状态 |
| POST | `/api/v1/users/2fa/enable/request.json` | 请求启用 2FA |
| POST | `/api/v1/users/2fa/enable/confirm.json` | 确认启用 2FA |
| POST | `/api/v1/users/2fa/disable.json` | 禁用 2FA |
| POST | `/api/v1/users/2fa/recovery/regenerate.json` | 重新生成恢复码 |

### 5.6 应用云设置

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/users/settings/cloud/get.json` | 获取云设置 |
| POST | `/api/v1/users/settings/cloud/update.json` | 更新云设置 |
| POST | `/api/v1/users/settings/cloud/disable.json` | 禁用云同步 |

### 5.7 账户管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/accounts/list.json` | 账户列表 |
| GET | `/api/v1/accounts/get.json` | 获取单个账户 |
| POST | `/api/v1/accounts/add.json` | 创建账户 |
| POST | `/api/v1/accounts/modify.json` | 修改账户 |
| POST | `/api/v1/accounts/hide.json` | 隐藏/显示账户 |
| POST | `/api/v1/accounts/move.json` | 排序账户 |
| POST | `/api/v1/accounts/delete.json` | 删除账户 |
| POST | `/api/v1/accounts/sub_account/delete.json` | 删除子账户 |

### 5.8 交易管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transactions/count.json` | 交易计数 |
| GET | `/api/v1/transactions/list.json` | 分页列表 |
| GET | `/api/v1/transactions/list/by_month.json` | 按月列表 |
| GET | `/api/v1/transactions/list/all.json` | 全部列表 |
| GET | `/api/v1/transactions/get.json` | 单条详情 |
| POST | `/api/v1/transactions/add.json` | 创建交易 |
| POST | `/api/v1/transactions/modify.json` | 修改交易 |
| POST | `/api/v1/transactions/delete.json` | 删除交易 |
| POST | `/api/v1/transactions/batch_delete.json` | 批量删除 |
| POST | `/api/v1/transactions/batch_update/category.json` | 批量修改分类 |
| POST | `/api/v1/transactions/batch_update/account.json` | 批量修改账户 |
| POST | `/api/v1/transactions/batch_update/tag/add.json` | 批量添加标签 |
| POST | `/api/v1/transactions/batch_update/tag/remove.json` | 批量移除标签 |
| POST | `/api/v1/transactions/batch_update/tag/clear.json` | 批量清除标签 |
| POST | `/api/v1/transactions/move/all.json` | 转移所有交易 |

### 5.9 交易统计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transactions/statistics.json` | 分类统计 |
| GET | `/api/v1/transactions/statistics/trends.json` | 趋势统计 |
| GET | `/api/v1/transactions/statistics/asset_trends.json` | 资产趋势 |
| GET | `/api/v1/transactions/amounts.json` | 时间段金额 |
| GET | `/api/v1/transactions/reconciliation_statements.json` | 对账单 |

### 5.10 交易导入

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/transactions/parse_custom_file.json` | 解析自定义格式 |
| POST | `/api/v1/transactions/parse_import.json` | 解析标准格式 |
| POST | `/api/v1/transactions/import.json` | 确认导入 |
| GET | `/api/v1/transactions/import/process.json` | 导入进度 |

### 5.11 交易图片

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/transaction/pictures/upload.json` | 上传图片 |
| POST | `/api/v1/transaction/pictures/remove_unused.json` | 清理未使用图片 |

### 5.12 交易分类

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transaction/categories/list.json` | 分类列表 |
| GET | `/api/v1/transaction/categories/get.json` | 单条分类 |
| POST | `/api/v1/transaction/categories/add.json` | 创建分类 |
| POST | `/api/v1/transaction/categories/add_batch.json` | 批量创建 |
| POST | `/api/v1/transaction/categories/modify.json` | 修改分类 |
| POST | `/api/v1/transaction/categories/hide.json` | 隐藏/显示 |
| POST | `/api/v1/transaction/categories/move.json` | 排序 |
| POST | `/api/v1/transaction/categories/delete.json` | 删除 |

### 5.13 交易标签

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transaction/tags/list.json` | 标签列表 |
| GET | `/api/v1/transaction/tags/get.json` | 单条标签 |
| POST | `/api/v1/transaction/tags/add.json` | 创建标签 |
| POST | `/api/v1/transaction/tags/add_batch.json` | 批量创建 |
| POST | `/api/v1/transaction/tags/modify.json` | 修改标签 |
| POST | `/api/v1/transaction/tags/hide.json` | 隐藏/显示 |
| POST | `/api/v1/transaction/tags/move.json` | 排序 |
| POST | `/api/v1/transaction/tags/delete.json` | 删除 |

### 5.14 标签组

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transaction/tags/groups/list.json` | 标签组列表 |
| GET | `/api/v1/transaction/tags/groups/get.json` | 单个标签组 |
| POST | `/api/v1/transaction/tags/groups/add.json` | 创建标签组 |
| POST | `/api/v1/transaction/tags/groups/modify.json` | 修改标签组 |
| POST | `/api/v1/transaction/tags/groups/move.json` | 排序标签组 |
| POST | `/api/v1/transaction/tags/groups/delete.json` | 删除标签组 |

### 5.15 交易模板

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/transaction/templates/list.json` | 模板列表 |
| GET | `/api/v1/transaction/templates/get.json` | 单个模板 |
| POST | `/api/v1/transaction/templates/add.json` | 创建模板 |
| POST | `/api/v1/transaction/templates/modify.json` | 修改模板 |
| POST | `/api/v1/transaction/templates/hide.json` | 隐藏/显示 |
| POST | `/api/v1/transaction/templates/move.json` | 排序 |
| POST | `/api/v1/transaction/templates/delete.json` | 删除 |

### 5.16 洞察分析器

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/insights/explorers/list.json` | 分析器列表 |
| GET | `/api/v1/insights/explorers/get.json` | 单个分析器 |
| POST | `/api/v1/insights/explorers/add.json` | 创建 |
| POST | `/api/v1/insights/explorers/modify.json` | 修改 |
| POST | `/api/v1/insights/explorers/hide.json` | 隐藏/显示 |
| POST | `/api/v1/insights/explorers/move.json` | 排序 |
| POST | `/api/v1/insights/explorers/delete.json` | 删除 |

### 5.17 汇率

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/exchange_rates/latest.json` | 最新汇率 |
| POST | `/api/v1/exchange_rates/user_custom/update.json` | 更新自定义汇率 |
| POST | `/api/v1/exchange_rates/user_custom/delete.json` | 删除自定义汇率 |

### 5.18 数据管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/data/statistics.json` | 数据统计 |
| POST | `/api/v1/data/clear/all.json` | 清除所有数据 |
| POST | `/api/v1/data/clear/transactions.json` | 清除所有交易 |
| POST | `/api/v1/data/clear/transactions/by_account.json` | 清除指定账户交易 |
| GET | `/api/v1/data/export.csv` | 导出 CSV |
| GET | `/api/v1/data/export.tsv` | 导出 TSV |

### 5.19 LLM

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/llm/transactions/recognize_receipt_image.json` | AI 识别收据 |

### 5.20 MCP

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/mcp` | MCP JSON-RPC 端点 |

---

## 6. 非功能性需求

### 6.1 性能要求

| 指标 | 要求 |
|------|------|
| API 响应时间 | 普通请求 < 2 秒 |
| 交易列表加载 | 1000+ 条记录 < 1 秒 |
| 文件上传 | 支持最大 10MB 文件 |
| 图片处理 | AI 识别超时默认 60 秒 |
| 并发连接 | 支持多用户同时操作 |

### 6.2 可用性

- **跨平台**：Windows、macOS、Linux
- **Docker 部署**：一键启动
- **PWA 支持**：移动端可添加到主屏幕
- **低资源设备**：可运行在树莓派、NAS 等
- **响应式 UI**：桌面版（Vuetify）+ 移动版（Framework7）独立优化

### 6.3 国际化

- **语言支持**：19 种语言（含翻译进度跟踪）
- **多币种**：支持所有 ISO 4217 货币
- **多时区**：每笔交易独立时区
- **本地化格式**：日期、时间、数字、货币格式可自定义

### 6.4 可扩展性

- 插件化的 LLM Provider 架构
- 插件化的汇率数据源
- 插件化的文件存储后端
- 插件化的导入格式解析器
- 插件化的地图 Provider
- 插件化的 OAuth 2.0 Provider

### 6.5 数据持久化

- 支持 SQLite（单文件）、MySQL、PostgreSQL
- 软删除设计（Deleted 字段 + DeletedUnixTime）
- 数据库自动更新表结构
- 分库设计（UserDB、TokenDB、UserDataDB 逻辑分离）

### 6.6 日志

- 支持 Console 和 File 两种输出
- 日志级别：Debug / Info / Warn / Error
- 区分：通用日志、请求日志、SQL 查询日志
- 支持日志文件轮转（按大小和天数）

---

## 7. 安全需求

### 7.1 认证安全

| 措施 | 说明 |
|------|------|
| 密码哈希 | 密码 + 随机盐值 |
| JWT 签名 | 使用可配置密钥签名 |
| Token 过期 | 可配置过期时间（会话/临时/API） |
| 速率限制 | IP 维度 / 用户维度 |
| 重复提交检测 | 基于内存缓存，可配置间隔 |
| 2FA | TOTP + 恢复码 |

### 7.2 数据安全

| 措施 | 说明 |
|------|------|
| 软删除 | 用户数据标记删除，非物理删除 |
| 数据隔离 | 所有查询均带 Uid 条件 |
| 编辑权限 | 基于 TransactionEditScope 控制 |
| 功能限制 | 基于 FeatureRestriction 位掩码 |
| 密码验证 | 敏感操作需密码二次确认 |
| 请求 ID | 每个请求带唯一 ID 用于追踪 |

### 7.3 网络安全

| 措施 | 说明 |
|------|------|
| HTTPS | 支持 TLS 证书配置 |
| Unix Socket | 支持 Unix Socket 模式 |
| IP 白名单 | API Token / MCP 可配置 IP 限制 |
| 代理支持 | HTTP/HTTPS/SOCKS5 代理 |
| TLS 跳过验证 | 可配置是否跳过 TLS 验证 |

### 7.4 存储安全

| 措施 | 说明 |
|------|------|
| MinIO | 支持 Access Key + Secret Key |
| WebDAV | 支持用户名密码认证 |
| 文件上传限制 | 可配置最大文件大小 |

---

## 附录 A. 前端页面清单

### 桌面版路由 (Vue Router Hash 模式)

| 路由 | 页面 | 认证要求 |
|------|------|---------|
| `/` | 首页仪表盘 | 已登录+已解锁 |
| `/login` | 登录 | 未登录 |
| `/signup` | 注册 | 未登录 |
| `/unlock` | 应用解锁 | 已登录+已锁定 |
| `/forgetpassword` | 忘记密码 | 未登录 |
| `/resetpassword` | 重置密码 | 无需认证 |
| `/verify_email` | 验证邮箱 | 无需认证 |
| `/oauth2_callback` | OAuth2 回调 | 无需认证 |
| `/transaction/list` | 交易明细 | 已登录+已解锁 |
| `/statistics/transaction` | 统计分析 | 已登录+已解锁 |
| `/insights/explorer` | 洞察分析器 | 已登录+已解锁 |
| `/account/list` | 账户管理 | 已登录+已解锁 |
| `/category/list` | 分类管理 | 已登录+已解锁 |
| `/tag/list` | 标签管理 | 已登录+已解锁 |
| `/template/list` | 模板管理 | 已登录+已解锁 |
| `/schedule/list` | 定时交易 | 已登录+已解锁 |
| `/exchange_rates` | 汇率数据 | 已登录+已解锁 |
| `/user/settings` | 用户设置 | 已登录+已解锁 |
| `/app/settings` | 应用设置 | 已登录+已解锁 |
| `/about` | 关于 | 已登录+已解锁 |

### 移动版路由 (Framework7 异步路由)

桌面版的 20 个页面 + 额外的 30 个移动端独立页面（共约 50 个页面），主要包括：
- 独立的交易新增/编辑/详情页
- 独立的账户新增/编辑/对账/转移页
- 独立的分类新增/编辑/预设/总览页
- 独立的标签组管理页
- 独立的模板新增/编辑页
- 独立设置子页面（文字大小、页面设置、筛选器、账户分类排序等）
- 独立的数据管理、2FA、会话管理、用户资料页

---

## 附录 B. 支持的导入格式详表

| 格式 | 扩展名 | 说明 |
|------|--------|------|
| CSV | .csv | 通用 CSV，含自定义列映射 |
| OFX | .ofx | Open Financial Exchange 1.x/2.x |
| QFX | .qfx | Quicken Financial Exchange |
| QIF | .qif | Quicken Interchange Format |
| IIF | .iif | Intuit Interchange Format |
| Camt.052 | .xml | ISO 20022 银行账户报告 |
| Camt.053 | .xml | ISO 20022 银行对账单 |
| MT940 | .sta/.txt | SWIFT MT940 格式 |
| GnuCash | .gnucash | GnuCash XML 格式 |
| Firefly III | .csv | Firefly III CSV 导出 |
| Beancount | .beancount | 纯文本记账格式 |
| Alipay | .csv | 支付宝账单 |
| WeChat | .csv | 微信账单 |
| JD.com | .csv | 京东账单 |
| Feidee | .xls/.xlsx | 随手记导出 |
| MoneyWiz | .csv | MoneyWiz 导出 |

---

## 附录 C. 前端技术栈参考

| 层级 | 原技术 | Java 版可复用 |
|------|--------|-------------|
| 框架 | Vue 3 (Composition API) | 完全复用 |
| 桌面 UI | Vuetify 3 (Material Design 3) | 完全复用 |
| 移动 UI | Framework7 v9 | 完全复用 |
| 状态管理 | Pinia | 完全复用 |
| 路由 | vue-router 5 / Framework7 Router | 完全复用 |
| 图表 | ECharts 6 | 完全复用 |
| 地图 | Leaflet / Framework7 Map | 完全复用 |
| 国际化 | vue-i18n 11 | 完全复用 |
| 构建 | Vite 7 | 完全复用 |
| PWA | vite-plugin-pwa | 完全复用 |
| 拖拽 | vuedraggable | 完全复用 |
| HTTP | axios | 完全复用 |

> 前端代码可整体迁移，仅需将 API 请求的 base URL 指向 Java 后端即可。
