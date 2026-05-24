本页面定义了复式记账系统 Season 2 的**标准化开发流程**。每个功能特性都遵循相同的四阶段管道：从问题发现、架构设计、编码实现到质量验证。通过规范化的流程确保后端与前端同步交付，减少返工并提升代码质量。

---

## 整体流程概览

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Discovery  │ →  │   Design   │ →  │Implement    │ →  │ Verification│
│  (Day 1-2)  │    │  (Day 3-5)  │    │ (Day 6-14)  │    │  (Day 15+)  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
     1-2天              3-5天           6-14天             持续
```

四阶段流程采用**时间盒（Timeboxing）**管理，每个阶段有明确的交付物和入口标准。当前阶段未通过评审不得进入下一阶段。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L7-L17)

---

## Phase 1: Discovery（探索发现）

发现阶段的目标是**识别问题、理解技术栈、细化需求**。这是编码前的准备工作，决定了后续设计的准确性。

### 1.1 缺陷排查（Bug Hunt）

在开始新功能前，必须先发现并记录现有系统的问题：

| 检查项 | 操作方法 | 输出位置 |
|--------|----------|----------|
| 运行测试套件 | `./gradlew test` | 控制台输出 + 测试报告 |
| 手动冒烟测试 | 测试登录、仪表盘、账户、交易、分类页面 | `docs/bugs/SEASON2-BUGS.md` |
| UI 问题截图 | 捕获浏览器中的异常界面 | Bugs 文档附件 |
| 问题分类 | 按 P0/P1/P2 严重性分级 | 同上 |

发现的问题记录格式包含：Bug ID、严重性、模块、发现日期、根因分析、修复方案、验证方法。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L19-L28), [SEASON2-BUGS.md](docs/bugs/SEASON2-BUGS.md#L1-L50)

### 1.2 技术栈审计（Tech Stack Audit）

验证现有技术栈的兼容性和一致性：

| 审计项 | 检查内容 | 文档位置 |
|--------|----------|----------|
| Spring Boot + Nuxt 兼容性 | 确认版本组合无冲突 | `AGENTS.md` |
| 依赖版本 | 检查 `build.gradle.kts` 和 `package.json` | 对应文件 |
| 数据库架构一致性 | PostgreSQL schema 与 JPA 实体匹配 | `openapi.yaml` |
| OpenAPI 规范覆盖 | 每个功能的 API 是否已定义 | `openapi.yaml` |
| 技术债务 | 发现潜在风险点 | `docs/tech-debt/TECHNICAL-DEBT.md` |

技术债务文档需记录债务项、影响范围、建议修复时间、当前缓解措施。

### 1.3 功能待办细化（Feature Backlog Refinement）

将高优先级功能分解为可执行的用户故事：

```
FEATURE-TXN-001: Transaction Edit
├── 用户故事: 作为用户，我希望编辑现有交易以便修正错误
├── 验收标准: 点击交易行 → 打开编辑对话框 → 表单预填充当前值 → 保存更新 → 取消放弃更改
├── API 契约: PUT /api/v1/transactions/{id} with request body
└── 数据库迁移: 无（已有字段修改）
```

每个用户故事必须包含：**标题**、**验收标准**、**API 契约**、**数据库迁移需求**、**优先级（P0/P1/P2）**、**依赖关系**。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L29-L37), [SEASON2-BACKLOG.md](docs/backlog/SEASON2-BACKLOG.md#L1-L50)

---

## Phase 2: Design（架构设计）

设计阶段是**后端优先原则**的核心——先定义 API 契约，再实现代码。设计评审通过后才能进入实现阶段。

### 2.1 页面设计规范

每个新增或修改的页面需要创建设计规范文档：

```
docs/design/pages/
├── 07-transactions-edit.md      # 交易编辑对话框
├── 08-transactions-transfer.md # 转账功能
├── 09-tags.md                   # 标签系统
├── 10-budget.md                 # 预算管理
├── 11-reports.md                # 报表页面
└── 12-settings.md               # 设置页面
```

设计规范文档必须包含以下章节：

| 章节 | 内容说明 |
|------|----------|
| **Page Purpose** | 页面的业务目标 |
| **Wireframe** | ASCII 或可视化布局草图 |
| **Components** | 所有 UI 组件及其状态定义 |
| **Data Model** | API 请求/响应数据结构 |
| **API Endpoints** | 完整端点路径、HTTP 方法、载荷 |
| **i18n Keys** | 所有需要翻译的文本 |
| **Edge Cases** | 空状态、加载状态、错误状态、边界条件 |
| **Mobile** | 响应式行为说明 |

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L39-L60), [07-transactions-edit.md](docs/design/pages/07-transactions-edit.md#L1-L20)

### 2.2 API 契约评审

编码前必须确认以下 API 契约细节：

| 确认项 | 规范内容 |
|--------|----------|
| 端点路径 | 路径与 OpenAPI 规范一致 |
| 请求/响应 DTO | 结构体字段与类型匹配 |
| 错误码 | 符合 `category * 100000 + subCategory * 1000 + index` 格式 |
| 交易类型 | 1=Modify Balance, 2=Income, 3=Expense, 4=Transfer_OUT, 5=Transfer_IN |

API 契约确认后写入设计规范文档，作为前后端开发的共同约定。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L62-L72)

### 2.3 数据模型与迁移

| 场景 | 操作 |
|------|------|
| 新增实体 | 创建 Flyway 迁移文件 `V4__new_entity.sql` |
| 新增字段 | 创建 ALTER TABLE 迁移文件 |
| 删除操作 | 采用软删除（soft delete）约定 |

**软删除规范**：所有删除操作使用 `deleted` 标志位，绝不物理删除数据。保留 `deleted_unix_time` 时间戳记录删除时间。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L74-L79)

---

## Phase 3: Implementation（编码实现）

实现阶段遵循**后端优先**原则：后端 API 契约驱动前端 UI 开发。

### 3.1 后端优先原则

```
┌──────────────────┐
│   API 契约定义   │  ← OpenAPI 规范 + 设计规范
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   后端实现        │  ← Entity → Repository → DTO → Service → Controller
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   单元测试       │  ← JUnit 5, 无 Spring 上下文
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   集成测试       │  ← @SpringBootTest
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│   前端开发       │  ← API Composable → 页面 → 组件 → 状态 → i18n
└──────────────────┘
```

后端未完成测试验证，前端不得开始联调。这确保了 API 契约的稳定性。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L81-L90)

### 3.2 后端实现顺序

每个功能的后端实现严格按以下顺序：

```
1. Entity（JPA 类 + Flyway 迁移）
2. Repository（Spring Data JPA）
3. DTO（请求 + 响应 Record）
4. Service（业务逻辑）
5. Controller（REST 端点 + Swagger 注解）
6. 异常处理（错误码定义）
7. 单元测试（纯 JUnit，无 Spring 上下文）
8. 集成测试（@SpringBootTest）
```

### 3.3 前端实现顺序

```
1. API Composable（useApi.ts 更新）
2. 页面布局（Wireframe 实现）
3. 组件（对话框、表单、表格）
4. 状态管理（Pinia Store 如有需要）
5. 国际化键值（en-US.json, zh-CN.json）
```

### 3.4 代码标准规范

| 规范项 | 具体要求 |
|--------|----------|
| **金额存储** | BIGINT（分/厘），前端显示时除以 100 |
| **时间戳** | Unix epoch 秒数（BIGINT） |
| **软删除** | `deleted` 标志位，禁止物理删除 |
| **响应格式** | `{success, result, errorCode, errorMessage}` |
| **命名约定** | PascalCase 实体、camelCase DTO、kebab-case 文件 |
| **测试覆盖** | 每个 Service/Controller 配 1 个测试类，80%+ 覆盖率目标 |

**金额计算示例**：

```java
// ❌ 错误：浮点数精度问题
amount: 85.00  // 可能出现 84.99999999999999

// ✅ 正确：整数存储
amount: 8500   // $85.00 = 8500 cents
```

**响应信封示例**：

```json
{
  "success": true,
  "result": {
    "transactionId": 1,
    "amount": 8500,
    "transactionTime": 1717104000
  },
  "errorCode": null,
  "errorMessage": null
}
```

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L91-L108), [AGENTS.md](AGENTS.md#L1-L100)

---

## Phase 4: Verification（质量验证）

验证阶段确保功能达到**完成定义（Definition of Done）**标准。

### 4.1 自动化测试

| 测试类型 | 位置 | 执行命令 |
|----------|------|----------|
| 单元测试 | `src/test/java/` | `./gradlew test` |
| 集成测试 | `src/integrationTest/java/` | `./gradlew integrationTest` |
| 综合报告 | - | `./gradlew allTestsReport` |

**测试命名规范**：每个测试类对应一个 Service 或 Controller，测试方法描述测试场景。

```java
@Test
void updateTransaction_withValidData_updatesSuccessfully() { }

@Test
void updateTransaction_withNonExistentId_throwsNotFoundException() { }
```

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L111-L120), [AGENTS.md](AGENTS.md#L150-L180)

### 4.2 手动测试

| 测试项 | 说明 |
|--------|------|
| 测试用例执行 | 执行 `docs/qa/TEST-CASES.md` 中的测试用例 |
| 浏览器兼容性 | Chrome + 移动端视口 |
| 签收确认 | 测试人员签名确认 |

测试用例覆盖认证、账户管理、交易管理、分类管理四大模块，每个 P0 用例必须通过。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L122-L126), [TEST-CASES.md](docs/qa/TEST-CASES.md#L1-L80)

### 4.3 冒烟测试清单

完成手动测试后，执行以下冒烟测试：

```
□ 登录/注册流程正常
□ 仪表盘加载真实数据
□ 创建账户 → 出现在列表中
□ 创建交易 → 更新账户余额
□ 编辑交易 → 正确保存
□ 删除交易 → 余额回滚
□ 转账 → 创建 2 条关联交易
□ 日期筛选 → 显示正确交易
□ 标签 → 可添加/移除
□ CSV 导出 → 下载有效文件
□ 1920px 和 375px 视口下各页面渲染正常
□ 浏览器控制台无错误
```

冒烟测试通过后，功能可进入发布候选。

---

## 文档结构规范

```
docs/
├── design/
│   └── pages/          # 页面设计规范
│       ├── 07-transactions-edit.md
│       ├── 08-transactions-transfer.md
│       └── ...
├── backlog/            # 功能待办清单
│   └── SEASON2-BACKLOG.md
├── bugs/               # 缺陷报告
│   └── SEASON2-BUGS.md
├── tech-debt/          # 技术债务追踪
│   └── TECHNICAL-DEBT.md
├── qa/                 # 测试用例
│   └── TEST-CASES.md
└── roadmap/            # 路线图
    └── SEASON2-ROADMAP.md
```

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L130-L150)

---

## 功能实施顺序

基于 OpenAPI 分析和现有代码结构，Season 2 分 5 个 Sprint 实施：

| Sprint | 周期 | 核心功能 |
|--------|------|----------|
| Sprint 1 | Week 1-2 | 缺陷修复 → 交易编辑/删除/日期选择 |
| Sprint 2 | Week 3-4 | 转账支持 → 月份导航 → 搜索筛选 |
| Sprint 3 | Week 5-6 | 标签后端/前端 → 仪表盘图表 |
| Sprint 4 | Week 7-8 | 交易统计 → 预算管理 → 月度报表 |
| Sprint 5 | Week 9-10 | 拖拽排序 → CSV 导出 → 用户设置 → UX 优化 |

每个 Sprint 以缺陷修复开场，确保基线质量后再开始新功能开发。

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L185-L215)

---

## 完成定义（Definition of Done）

功能判定为**已完成**的条件：

- [ ] 后端实现并通过测试
- [ ] 前端实现并通过测试
- [ ] 所有单元测试通过（`./gradlew test`）
- [ ] 创建设计规范并评审通过
- [ ] 手动测试用例执行并通过
- [ ] 该功能无已知缺陷
- [ ] 两种语言（en/zh）的 i18n 键值已添加

Sources: [DEVELOPMENT-WORKFLOW.md](docs/workflow/DEVELOPMENT-WORKFLOW.md#L245-L267)

---

## 重要规则

| 规则 | 说明 |
|------|------|
| **设计先行** | 先规范后编码，禁止边做边改 |
| **后端优先** | API 契约驱动 UI 开发 |
| **持续测试** | 不要把测试留到最后 |
| **迁移文件神圣** | 绝不修改旧的迁移文件 |
| **快速测试** | 纯 JUnit 优先，`@SpringBootTest` 仅在必要时使用 |
| **决策文档化** | 架构选择使用 ADR（Architecture Decision Record） |
| **小 PR 原则** | 每个 PR 一个功能，最多 400 行变更 |

违反上述规则将导致返工增加和质量下降。任何架构决策需记录在 `docs/superpowers/adr/` 目录下。

---

## 后续步骤

完成本页面后，建议按以下顺序阅读：

1. **[测试策略 - 单元测试与集成测试](15-ce-shi-ce-lue-dan-yuan-ce-shi-yu-ji-cheng-ce-shi)** — 深入理解测试框架和覆盖率目标
2. **[编码规范 - Lombok 使用与 DTO 映射](16-bian-ma-gui-fan-lombok-shi-yong-yu-dto-ying-she)** — 掌握代码标准细节
3. **[系统架构 - Spring Boot + Nuxt 4 全栈设计](3-xi-tong-jia-gou-spring-boot-nuxt-4-quan-zhan-she-ji)** — 理解技术栈整体架构

---

*最后更新：2026-05-22*
*本文档为 Season 2 核心流程规范，请严格遵循*