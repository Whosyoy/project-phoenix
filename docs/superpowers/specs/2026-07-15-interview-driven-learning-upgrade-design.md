# Project Phoenix 面试驱动学习增量升级设计

## 1. 背景与目标

Project Phoenix 当前路线已经覆盖 Agent Core、MCP、RAG、Evaluation 和生产工程等关键能力，主方向正确。本次不推翻路线、不打断当前 Sprint 1 节奏，而是把项目目标从“学习并完成企业级 Agent 项目”进一步明确为：

> 达到 AI 应用开发、Agent 工程化和 AI 平台后端岗位的可面试、可入职水平。

学习闭环统一升级为：

```text
Build -> Explain -> Defend
```

- **Build**：能够独立实现或验证，理解真实调用链和运行结果。
- **Explain**：能够解释架构、边界、设计原因和主要取舍。
- **Defend**：能够回答面试追问，比较替代方案，并说明生产工程约束。

## 2. 设计原则

### 2.1 保持主计划与当前节奏

- 不改变当前 Sprint 1 的唯一优先事项。
- 不提前开始 MCP、RAG、部署或框架扩展。
- 不重写已经完成的业务基线和 Agent Core 资产。
- 使用现有项目文档、Sprint 文档和模板承载新增要求。

### 2.2 面试准备前置

面试准备不再集中到最后一个 Sprint。每个 Sprint 都同步沉淀：

- Interview Acceptance Criteria
- Interview Checkpoint
- 项目证据
- 设计理由与 Trade-off
- 高频问题、回答要点和后续追问

最终 Interview Loop 负责整合、模拟和迭代，不是第一次准备面试。

### 2.3 证据约束不变

- 未实现或未验证的内容继续标记为 `Design Hypothesis`。
- 不能把设计方案描述成生产成果。
- 面试表达必须能追溯到代码、实验、业务文档或明确的设计决策。
- DirectBus 业务事实仍以 Rory 的确认结果为准。

## 3. 文档变更范围

### 3.1 项目入口与定位

更新以下文件：

- `README.md`
- `00_Project/PROJECT.md`

变更内容：

- 明确“面试与入职导向”的最终目标。
- 增加 `Build -> Explain -> Defend` 项目原则。
- 说明代码、架构、业务和职业资产如何形成统一证据链。
- 修正与仓库实际进度不一致的 Sprint 1 状态描述。

### 3.2 路线图

更新 `00_Project/ROADMAP.md`：

- 保留 Agent Core、MCP、RAG、Knowledge Governance、Evaluation 和 Production Engineering 主线。
- 在 Production Engineering 前新增 `AI Application Engineering` 阶段。
- 该阶段覆盖 Python 工程、FastAPI、Pydantic、异步、配置、日志、Docker、Redis/Postgres、Tracing、部署和 CI/CD。
- 为每个阶段增加面试验收重点。
- 明确 Interview Loop 是集中整理与模拟，不取代各阶段 Checkpoint。

为避免破坏已有引用，新增阶段采用语义化名称，不强制在本次改动中批量重命名所有既有 Sprint 目录。未来创建新 Sprint 时以更新后的路线图为准。

### 3.3 学习治理

更新 `00_Project/LEARNING-GOVERNANCE.md`：

- 保留现有四层验收的证据要求。
- 用 `Build -> Explain -> Defend` 作为更容易执行和表达的上层验收模型。
- 建立每周或每阶段 Interview Checkpoint。
- 要求 Review 明确列出可回答问题、项目证据、薄弱追问和补齐动作。

四层验收与三层模型的对应关系如下：

| 三层模型 | 现有四层验收 |
| --- | --- |
| Build | 代码或实验实践、DirectBus 业务映射 |
| Explain | 理论理解、架构与 Trade-off 表达 |
| Defend | 简历与面试资产、追问和替代方案答辩 |

现有四层验收不会删除，以免损失业务映射和证据约束。

### 3.4 模板

更新：

- `09_Templates/Sprint.md`
- `09_Templates/Interview.md`

Sprint 模板增加：

- Build / Explain / Defend 验收项
- Interview Checkpoint
- Project Evidence
- 当前薄弱点与下一步唯一优先事项

Interview 模板增加：

- 一句话回答
- 展开回答
- Project Evidence
- Design Rationale
- Alternatives / Trade-offs
- Follow-up Questions
- Evidence Status

不创建重复的第二套 Interview Notes 目录。具体 Sprint 可以按需在自己的目录中使用增强后的模板。

### 3.5 当前 Sprint

更新 `01_Sprints/Sprint-01-Agent-Core/README.md`，只增加与当前范围直接相关的面试验收：

- 能解释 `ToolResult -> Workflow -> EvidenceBundle -> SkillResult -> ResponseGenerator`。
- 能解释 Skill、Workflow 和 Tool 的边界。
- 能答辩确定性 Workflow 与自主规划的取舍。
- 能说明结构化输出、失败降级和证据约束。

`TASKS.md` 的当前唯一优先事项和“暂时不要”列表保持不变；只在必要时增加与上述验收对应的检查项，不扩大实现范围。

## 4. 新阶段边界：AI Application Engineering

该阶段用于补齐“会设计 Agent”到“能交付 AI 服务”之间的工程能力，不等同于 Production Engineering。

### AI Application Engineering

关注应用构建与交付基础：

- Python 工程结构与依赖管理
- typing、Pydantic、asyncio
- FastAPI 服务接口与依赖注入
- 配置、日志、异常处理
- Redis / Postgres 的基础集成
- Docker 容器化
- Tracing 基础接入
- CI/CD 与最小部署闭环

### Production Engineering

关注生产治理：

- 权限与审计
- 幂等、限流、超时、重试和降级
- 安全与数据脱敏
- 灰度、回滚和高可用
- 可观测性、成本和运行治理

两个阶段分离，避免在学习 FastAPI 或容器化时提前承担完整生产治理范围。

## 5. 验收标准

本次增量升级完成后应满足：

1. 项目入口、项目定位、路线图和学习治理对最终目标表述一致。
2. 原有主线和当前 Sprint 1 唯一优先事项没有被改变。
3. `Build -> Explain -> Defend` 有明确含义，并能映射到现有四层验收。
4. 每个后续 Sprint 都有统一方式沉淀面试验收和项目证据。
5. AI Application Engineering 与 Production Engineering 边界清楚。
6. 不新增重复文档体系，不批量重命名既有目录。
7. 所有状态描述与当前仓库实际进度一致。
8. 文档链接、阶段顺序和术语经过一致性检查。

## 6. 非目标

本次不做：

- Agent Core 功能开发或重构
- MCP、RAG、Evaluation 或部署实现
- 新框架选型
- 批量创建未来 Sprint 空目录
- 简历内容或完整面试题库编写
- 对未验证能力作生产级成果表述

## 7. 后续实施顺序

1. 更新项目入口和定位。
2. 更新路线图和阶段边界。
3. 更新学习治理与验收模型。
4. 更新 Sprint 和 Interview 模板。
5. 为当前 Sprint 1 增加面试验收但不扩大范围。
6. 全局检查状态、阶段名称、链接和术语一致性。

