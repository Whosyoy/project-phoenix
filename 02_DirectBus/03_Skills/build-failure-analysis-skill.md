# 构建失败分析技能（Build Failure Analysis Skill）

本文定义 Sprint 1 的 Build Failure Analysis MVP。采用“确定性 Workflow + 有限模型判断”，只做只读诊断设计；未知业务规则继续标记 TODO。

## 1. 业务目标

基于指定 `apply_bus_id`，定位当前构建失败的项目、基线、阶段和模块，汇总已有失败证据，并解释当前失败与历史重构建记录的关系。

本 Skill 只回答“哪里失败、现有证据说明什么、当前能否确定原因”。无充分证据时必须输出“不确定”，不负责触发重构建、申请豁免或决定合入。

## 2. 触发 Intent 与典型问题

顶层 Intent：构建失败诊断（`Build Failure Diagnose`），覆盖 UQ-009～UQ-012。

| 细粒度 Intent | 典型问题 |
| --- | --- |
| `DiagnoseBuildFailure` | “这条直通车为什么一直是构建失败？” |
| `LocateBuildFailureItem` | “到底是哪个项目、哪个基线构建没过？” |
| `SummarizeBuildAIAnalysis` | “构建侧返回了 AI 分析结果，能帮我看下主要问题吗？” |
| `ExplainRebuildHistory` | “我已经重构建过了，为什么看到的还是上一次失败？” |

重构建资格或执行、豁免、测试失败、合入阻塞不属于本 Skill。

## 3. 输入参数与缺参处理

| 输入 | 必需性 | 处理规则 |
| --- | --- | --- |
| `apply_bus_id` | 必需 | 唯一定位 DirectBus；不得猜测或生成 |
| 用户原始问题 | 必需 | 识别细粒度 Intent 和回答重点 |
| `projectName`、`baselineName` | 可选 | 仅在用户明确指定或当前明细能唯一匹配时缩小范围 |
| 已确认会话上下文 | 可选 | 只复用唯一、明确且未产生歧义的参数 |

- 缺少唯一 `apply_bus_id`：停止 Tool 调用并追问。
- 存在多个候选 ID、项目或基线：要求用户明确选择，不做模糊匹配。
- 仅有“上次失败”等相对描述但上下文不唯一：追问失败对象或 `apply_bus_id`。

## 4. Tool 候选及最小返回字段

默认 Workflow 使用以下候选 Tool；这里只定义职责和最小字段，不定义 MCP schema。

| Tool | 职责 | MVP 最小返回字段 |
| --- | --- | --- |
| `query_directbus_status` | 查询 DirectBus 全局状态和生命周期信息 | `apply_bus_id`、`isRunning`、`buildStageStatus`、`buildNodeStatus` |
| `query_build_detail` | 查询当前有效构建明细、失败项目、失败阶段和重构建轨迹 | `projectName`、`baselineName`、`buildStatus`、`failureStage`、`failureModule`、`errorSummary`、`retryCount`、`rebuildCount`、`startTime`、`endTime`、`isHistory` |
| `query_build_failure_analysis` | 返回构建侧已有系统生成的分析结果，供本地 Agent 消费和解释 | `aiAnalysisResult`，以及可用于交叉核对的失败对象或关联字段 TODO |

`query_build_failure_analysis` 不表示本地 Agent 自行完成根因分析。若真实接口返回的是关键日志、错误码、失败阶段等原始证据，而不是已有分析结果，候选名称应调整为 `query_build_failure_evidence`，真实字段与关联方式 TODO。

按需候选：`query_minimal_set_config`、`query_version_base`。仅当用户明确追问，或当前失败证据明确指向最小集配置 / 底版本时调用；相关规则仍为 TODO，不加入默认 Workflow。

## 5. 确定性 Workflow

以下步骤由固定流程控制，模型不得跳过或重排默认查询：

| 步骤 | 确定性动作 |
| ---: | --- |
| 1 | 校验 Intent、`apply_bus_id` 和可选范围；缺参则停止并追问 |
| 2 | 调用 `query_directbus_status`，记录全局状态和生命周期信息 |
| 3 | 调用 `query_build_detail`，将 `isHistory = 0` 当前记录与 `isHistory = 1` 历史记录分组 |
| 4 | 只从当前记录定位 `buildStatus = 构建失败` 的项目、基线及结构化失败字段 |
| 5 | 针对当前失败对象调用 `query_build_failure_analysis`，获取已有系统分析结果 |
| 6 | 将已有分析与 `failureStage`、`failureModule`、`errorSummary` 交叉核对 |
| 7 | 用户询问重构建轨迹时，按项目、基线和时间关联当前 / 历史记录；时间缺失则不推断顺序 |
| 8 | 生成固定输出块，并执行证据充分性和风险边界检查 |

当前失败判断只使用 `isHistory = 0`；`isHistory = 1` 仅用于解释历史轨迹。没有当前失败记录时，不得用历史失败替代当前结论。

最小集配置和底版本查询不属于上述默认 Workflow；满足按需条件时进入独立查询分支，具体规则和结果解释继续标记 TODO。

## 6. 模型允许参与的位置

模型可以：识别细粒度 Intent、抽取已确认参数、摘要已有 AI 分析、按项目与基线组织证据、用自然语言解释当前 / 历史记录差异。

模型不可以：修改状态或 `isHistory` 规则、把已有 AI 分析当最终根因、从相关性强行推导因果、补写缺失日志或字段、判断一定可重构建 / 豁免 / 合入。

## 7. 证据不足时的处理

出现以下任一情况，结论必须标记为“不确定”：

- 当前失败记录缺少 `failureStage`、`failureModule`、`errorSummary` 等关键证据。
- 已有 AI 分析与结构化构建明细不一致，或无法关联到当前失败对象。
- Tool 调用失败、只返回历史记录、存在多个当前有效记录冲突。
- 重构建记录缺少时间或关联字段，无法可靠还原顺序。

输出已确认事实、缺失或冲突信息及受影响的判断；报告 / 日志入口和人工排查路径未确认时标记 TODO，不编造下一步操作。

## 8. 最小输出契约

| 输出块 | 内容 |
| --- | --- |
| 查询范围 | `apply_bus_id`、项目和基线范围 |
| 当前结论 | 已确认结论，或明确的“不确定” |
| 当前失败项 | 当前失败的项目、基线、阶段、模块和状态 |
| 证据摘要 | `errorSummary`、已有系统分析及二者一致性 |
| 重构建轨迹 | 当前 / 历史记录、次数与可确认的时间顺序 |
| 缺失与冲突 | Tool 失败、缺失字段、冲突记录和受影响结论 |
| 边界提示 | 只读诊断；资格判断或执行动作需路由其他 Skill |

先给结论，再给可追溯证据；必须标明分析来源是结构化字段、已有系统分析还是历史记录。

## 9. 风险边界

风险等级：中风险，只读诊断。

- 不把历史失败当作当前失败，不把构建失败直接解释为流水线结束。
- 不把构建侧 AI 分析包装成本地 Agent 独立完成的根因结论。
- 不在证据不足时强行归因；必须输出“不确定”。
- 不承诺重构建可以成功，不触发重构建，不建议跳过构建直接合入。
- 不自动扩大到最小集配置、底版本、豁免或合入规则判断。

## 10. MVP Out of Scope

- Skill 实现代码、MCP schema、真实 Tool 接口与权限接入。
- 完整日志解析、根因知识库、RAG 方案和历史案例检索。
- 完整 Evaluation 设计、评测数据集和评测代码。
- 重构建资格判断及执行、豁免申请、人工结束或合入操作。
- 最小集配置与底版本的默认查询及规则推理。
- Merge Block Diagnose Skill 或其他 Sprint 1 Skill 的设计。
