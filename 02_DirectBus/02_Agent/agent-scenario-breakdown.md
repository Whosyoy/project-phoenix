# Agent Scenario Breakdown

## Purpose

本文用于完成 Sprint 1 的核心目标：从 DirectBus 业务流程中识别哪些场景适合 Agent，哪些更适合 Tool 查询，哪些必须人机确认，哪些只能辅助说明或转人工。

当前阶段只做业务场景拆解，不写代码，不设计 RAG / MCP / Evaluation 的完整实现。

## Source Documents

- `02_DirectBus/01_Business/directbus-business-flow.md`
- `02_DirectBus/01_Business/directbus-exemption-flow.md`

## 判断原则

- Business First: 场景必须来自 DirectBus 提测、构建、O 测、门禁、合入、豁免等真实业务流程。
- Engineering First: Agent 不能替代 DirectBus、Gerrit、门禁系统或关键审批角色做最终决策。
- Tool First When Simple: 单纯状态读取优先视为 Tool 查询；只有需要解释、归因、汇总、判断边界时才体现 Agent 价值。
- Human Confirmation: 重测、重构建、豁免、合入等有副作用或高风险场景必须保留人工确认。
- Evidence Required: 诊断类回答必须基于实时数据、规则或证据，未知内部细节保留 TODO。

## S-001: change 为什么不能提测

### 用户问题示例

- “这个 change 为什么不能提测？”
- “为什么点直通车提测失败？”
- “这个提交还差什么才能提测？”

### 是否适合 Agent

非常适合 Agent

### 判断理由

这个场景不是单纯查一个状态。提测前需要结合 Gerrit change 状态、PatchSet、Review 分数、责任田主审批、上下游分支合入状态、commit message 需求 ID、影响面分析、最小集配置和验证项目确认。

它适合 Agent 的原因是：用户真正想知道的是“为什么不能提测”和“下一步补什么”，需要跨系统查询、规则解释和阻塞原因归因。没有副作用操作，但如果涉及豁免入口，就会进入审批和安全边界。

### 需要的实时数据

- Gerrit change 状态
- PatchSet
- Review 分数
- 责任田主审批状态
- 上下游分支合入状态
- commit message 检查结果
- 用户提测参数
- 影响面分析结果
- 最小集配置查询结果
- 可豁免状态 TODO

### 需要的知识

- 提测准入规则
- 责任田主审批规则
- commit message 需求 ID 规则
- 上下游分支依赖规则
- 最小集配置说明
- 可豁免 / 不可豁免规则 TODO

### 可能调用的 Tool

- `query_change_status`
- `query_review_score`
- `query_pre_submit_check`
- `query_impact_analysis`
- `query_minimal_set_config`
- `query_exemption_policy`

### 可能封装的 Skill

- PreCheck Diagnose Skill
- Submit Readiness Explain Skill

### 风险等级

中风险

### Agent 边界

Agent 可以查询和解释不能提测的原因，可以生成补齐建议，可以提示是否存在豁免入口。Agent 不能绕过提测准入，不能伪造审批，不能自动批准豁免。

### 后续 Evaluation 方向

- 是否正确识别提测阻塞原因
- 是否正确选择 Gerrit / 准入 / 影响面 / 最小集配置相关工具
- 是否引用正确准入规则
- 是否区分硬拦截和可申请豁免
- 是否避免建议绕过提测规则

## S-002: 当前直通车到哪一步了

### 用户问题示例

- “我的直通车现在到哪一步了？”
- “这个 apply_bus_id 现在是在构建还是测试？”
- “为什么显示测试中，但还有项目在构建？”

### 是否适合 Agent

更适合 Tool 查询

### 判断理由

如果用户只问当前阶段，本质上是状态查询，更适合 Tool 直接读取 `isRunning`、`buildStageStatus`、`buildNodeStatus`。但 DirectBus 的状态有工程语义：全局 `buildStageStatus = 3` 不代表所有项目构建完成，`buildNodeStatus = 构建失败 / 测试失败` 也不代表所有明细都结束。

因此这个场景的基础能力是 Tool 查询，Agent 的价值在于解释全局状态和构建 / 测试明细之间的关系。

### 需要的实时数据

- apply_bus_id
- isRunning
- buildStageStatus
- buildNodeStatus
- 构建明细
- O 测明细
- isHistory
- projectName
- baselineName
- buildStatus
- testStatus
- startTime / endTime

### 需要的知识

- buildStageStatus 含义
- buildNodeStatus 含义
- 全局状态与明细状态关系
- isHistory 规则

### 可能调用的 Tool

- `query_directbus_status`
- `query_build_detail`
- `query_test_detail`

### 可能封装的 Skill

- Pipeline Status Summary Skill
- Global Detail Status Explain Skill

### 风险等级

低风险

### Agent 边界

Agent 可以查询和解释当前进度，可以说明哪些项目成功、失败、仍在执行。Agent 不能只看全局字段就下结论，必须结合 `isHistory = 0` 的构建明细和测试明细。

### 后续 Evaluation 方向

- 是否正确解释 buildStageStatus / buildNodeStatus
- 是否正确过滤 isHistory
- 是否识别“全局测试中但部分项目仍在构建”
- 是否正确给出项目 / 基线粒度进度

## S-003: 为什么一直卡在构建失败

### 用户问题示例

- “为什么一直构建失败？”
- “哪个项目构建挂了？”
- “构建失败但为什么还显示运行中？”

### 是否适合 Agent

非常适合 Agent

### 判断理由

构建失败不是单字段状态。Agent 需要结合全局状态、构建明细、失败阶段、失败模块、错误摘要、构建侧 AI 分析结果、重构建次数和 isRunning 语义做诊断。

这个场景需要跨实时状态和失败证据做多步骤判断，而且“构建失败但 isRunning=true”容易被误解，适合 Agent 做工程语义解释。

### 需要的实时数据

- apply_bus_id
- isRunning
- buildStageStatus
- buildNodeStatus
- 构建明细
- isHistory
- projectName
- baselineName
- buildStatus
- artifactUrl
- failureStage
- failureModule
- errorSummary
- aiAnalysisResult
- retryCount
- rebuildCount

### 需要的知识

- 构建状态语义
- 构建失败处理规则 TODO
- 重构建规则 TODO
- 构建侧 AI 分析结果使用边界
- 底版本和构建参数说明 TODO
- 历史失败案例 TODO

### 可能调用的 Tool

- `query_directbus_status`
- `query_build_detail`
- `query_build_failure_analysis`
- `query_minimal_set_config`
- `query_version_base`

### 可能封装的 Skill

- Build Failure Analysis Skill
- Build Progress Summary Skill

### 风险等级

中风险

### Agent 边界

Agent 可以汇总构建失败原因、失败项目、失败模块和构建侧 AI 分析结果。Agent 不能把构建侧 AI 分析结果直接当最终结论，不能承诺一定可以重构建，不能建议跳过构建失败直接合入。

### 后续 Evaluation 方向

- 是否正确定位失败项目 / 基线
- 是否正确引用失败阶段、失败模块、错误摘要
- 是否区分当前失败和历史失败
- 是否解释构建失败但 isRunning 仍为 true
- 是否避免越权建议重构建或豁免

## S-004: 为什么 O 测失败

### 用户问题示例

- “O 测为什么失败？”
- “是哪个领域测试没过？”
- “为什么测试失败后还在重试？”

### 是否适合 Agent

非常适合 Agent

### 判断理由

O 测失败需要结合测试明细、领域、失败用例、错误码、错误摘要、报告链接、自动重试次数和历史记录。它不是简单返回失败状态，而是要解释失败发生在哪个领域、是否命中自动重试规则、是否还有其他项目仍在测试。

这个场景需要实时状态、规则知识和多明细汇总，适合 Agent。

### 需要的实时数据

- apply_bus_id
- O 测明细
- isHistory
- projectName
- baselineName
- testId
- domain
- testStatus
- failedCase
- errorCode
- errorSummary
- reportUrl
- retryCount
- retestCount
- startTime / endTime

### 需要的知识

- 六大领域定义
- O 测 SOP
- O 测错误码自动重试规则
- 自动重试最多 3 次
- 手动重测规则 TODO
- 历史失败案例 TODO

### 可能调用的 Tool

- `query_test_detail`
- `query_test_report`
- `query_retry_policy`
- `query_build_detail`

### 可能封装的 Skill

- Test Failure Analysis Skill
- Domain Test Summary Skill

### 风险等级

中风险

### Agent 边界

Agent 可以解释失败领域、失败用例、错误码和是否命中自动重试规则。Agent 不能自行判断测试失败可以忽略，不能绕过 O 测失败直接建议合入，不能把历史失败误判为当前失败。

### 后续 Evaluation 方向

- 是否正确识别失败领域和失败用例
- 是否正确应用错误码自动重试规则
- 是否区分自动重试和手动重测
- 是否正确过滤 isHistory
- 是否避免建议绕过测试失败

## S-005: 为什么没有进入合入

### 用户问题示例

- “为什么还没进入合入？”
- “构建和测试看起来结束了，为什么没合？”
- “为什么一直卡在测试阶段？”

### 是否适合 Agent

非常适合 Agent

### 判断理由

是否进入合入不是单点状态，取决于正常路径和豁免路径。正常路径要求所有需要验证的基线项目构建成功且测试成功；豁免路径需要关键角色审批。

Agent 需要结合全局状态、构建明细、O 测明细、isHistory、失败情况和豁免状态判断缺口，因此非常适合 Agent。

### 需要的实时数据

- apply_bus_id
- isRunning
- buildStageStatus
- buildNodeStatus
- 构建明细
- O 测明细
- isHistory
- buildStatus
- testStatus
- domain
- errorCode
- 豁免状态 TODO

### 需要的知识

- 正常合入条件
- 豁免合入条件
- 构建 / 测试明细汇总规则
- isHistory 规则
- 豁免规则 TODO
- 门禁前置规则 TODO

### 可能调用的 Tool

- `query_directbus_status`
- `query_build_detail`
- `query_test_detail`
- `query_exemption_status`
- `query_merge_gate`

### 可能封装的 Skill

- Merge Block Diagnose Skill
- Merge Readiness Diagnose Skill

### 风险等级

高风险

### Agent 边界

Agent 可以说明没有进入合入的直接原因和缺失条件，可以区分正常合入和豁免合入。Agent 不能替代门禁系统做合入决策，不能强制合入，不能自动批准豁免。

### 后续 Evaluation 方向

- 是否正确判断正常合入条件是否满足
- 是否识别仍在构建 / 测试的项目
- 是否识别失败项目和失败领域
- 是否区分正常路径和豁免路径
- 是否避免只看全局状态误判

## S-006: 合入前为什么门禁不过

### 用户问题示例

- “合入前门禁为什么不过？”
- “为什么分支锁导致不能合？”
- “patchset 变了为什么要重新提测？”

### 是否适合 Agent

适合 Agent

### 判断理由

合入前门禁涉及分支锁、PatchSet 是否变化、Review 分数是否仍满足合入、其他门禁规则 TODO。用户需要知道当前是临时阻塞、验证结果失效，还是需要人工重试或重新提测。

这个场景需要结合实时状态和规则知识，适合 Agent。但由于靠近合入动作，风险等级高，Agent 只能解释和建议，不能自动合入。

### 需要的实时数据

- apply_bus_id
- Gerrit change 状态
- PatchSet
- Review 分数
- 分支锁
- 门禁状态
- 合入前校验结果
- buildStageStatus
- buildNodeStatus
- 合入 job 状态 TODO

### 需要的知识

- 门禁规则
- 分支锁处理规则
- PatchSet 变化导致验证结果失效规则
- Review 分数复查规则
- 合入失败处理规则 TODO

### 可能调用的 Tool

- `query_change_status`
- `query_merge_gate`
- `query_branch_lock`
- `query_directbus_status`
- `query_merge_job_status`

### 可能封装的 Skill

- Merge Gate Diagnose Skill
- Pre Merge Guard Explain Skill

### 风险等级

高风险

### Agent 边界

Agent 可以查询和解释门禁不过的原因。PatchSet 变化时必须提示旧验证结果失效并要求重新提测；分支锁存在时可以说明锁释放后人工重试合入。Agent 不能绕过门禁，不能强制合入。

### 后续 Evaluation 方向

- 是否正确区分分支锁、PatchSet 变化、Review 分数不满足
- 是否正确选择门禁和 Gerrit 查询工具
- 是否引用正确门禁规则
- 是否识别重新提测和人工重试合入的区别
- 是否避免越权合入建议

## S-007: 能不能重测 / 重构建

### 用户问题示例

- “这个能不能重测？”
- “构建失败了能不能重构建？”
- “测试失败还能再跑一次吗？”

### 是否适合 Agent

适合 Agent 辅助，但必须人机确认

### 判断理由

重测 / 重构建是有副作用的操作。Agent 适合做条件检查、风险说明、证据汇总和二次确认前的建议，但不适合直接无确认执行。

该场景需要结合当前状态、失败类型、错误码、自动重试次数、重构建次数、手动重测规则、用户权限和操作审计要求。部分规则仍是 TODO，不能编造。

### 需要的实时数据

- apply_bus_id
- isRunning
- buildStageStatus
- buildNodeStatus
- 构建明细
- O 测明细
- buildStatus
- testStatus
- errorCode
- retryCount
- rebuildCount
- retestCount
- isHistory
- 用户权限 TODO

### 需要的知识

- 自动重试规则
- 手动重测规则 TODO
- 重构建规则 TODO
- 权限规则 TODO
- 操作审计要求 TODO
- 哪些失败类型可以重试 TODO

### 可能调用的 Tool

- `query_directbus_status`
- `query_build_detail`
- `query_test_detail`
- `query_retry_policy`
- `query_user_permission`
- `trigger_rebuild`
- `trigger_retest`

### 可能封装的 Skill

- Retest Confirm Skill
- Rebuild Confirm Skill
- Safe Action Check Skill

### 风险等级

高风险

### Agent 边界

Agent 可以判断是否具备重测 / 重构建的前置条件，可以生成操作建议，可以要求用户二次确认。中风险动作必须二次确认；规则、权限或风险不明确时必须停止并提示人工确认。Agent 不能无确认触发重测 / 重构建。

### 后续 Evaluation 方向

- 是否正确识别操作意图
- 是否正确检查当前状态和明细
- 是否正确识别自动重试最多 3 次
- 是否正确要求人机确认
- 是否避免在权限未知时执行操作

## S-008: 能不能申请豁免

### 用户问题示例

- “这个失败能不能申请豁免？”
- “构建失败可以走豁免合入吗？”
- “谁能审批这个豁免？”

### 是否适合 Agent

不适合自动化，只能辅助说明或转人工

### 判断理由

豁免是 DirectBus 主流程上的例外通道，可能覆盖提测前准入失败、构建失败、O 测失败、合入前门禁不满足等场景。它涉及关键角色审批、风险等级、证据链和审计，不能由 Agent 自动决策。

Agent 的价值在于解释是否存在豁免入口、汇总失败证据、提示需要的审批角色 TODO、生成申请草稿 TODO。最终是否批准必须由人处理。

### 需要的实时数据

- apply_bus_id
- Gerrit change 状态
- PatchSet
- 当前阻塞阶段
- buildStageStatus
- buildNodeStatus
- 构建明细
- O 测明细
- 门禁状态
- 失败证据
- 豁免状态 TODO
- 审批记录 TODO
- 审计记录 TODO

### 需要的知识

- 豁免规则
- 不可豁免边界
- 审批角色 TODO
- 风险等级 TODO
- 审计要求 TODO
- PatchSet 变化不可默认豁免规则
- 未知影响范围 / 底版本不可用风险

### 可能调用的 Tool

- `query_exemption_policy`
- `query_exemption_status`
- `query_directbus_status`
- `query_build_detail`
- `query_test_detail`
- `query_merge_gate`

### 可能封装的 Skill

- Exemption Advice Skill
- Exemption Evidence Summary Skill
- Exemption Draft Skill

### 风险等级

禁止自动执行

### Agent 边界

Agent 可以查询和解释豁免入口，可以生成建议和申请草稿，可以汇总证据。Agent 不能自动批准豁免，不能绕过审批，不能在证据不足时建议继续合入，不能强制合入。

### 后续 Evaluation 方向

- 是否正确识别豁免场景
- 是否区分可豁免和不可默认豁免
- 是否正确要求关键角色审批
- 是否引用失败证据
- 是否避免自动批准豁免或越权合入

## Cross-Scenario Summary

| Category | Scenarios |
| --- | --- |
| 非常适合 Agent | change 为什么不能提测；为什么一直卡在构建失败；为什么 O 测失败；为什么没有进入合入 |
| 适合 Agent | 合入前为什么门禁不过 |
| 更适合 Tool 查询 | 当前直通车到哪一步了 |
| 适合 Agent 辅助，但必须人机确认 | 能不能重测 / 重构建 |
| 不适合自动化，只能辅助说明或转人工 | 能不能申请豁免 |

## Shared Agent Boundaries

- Agent 可以查询和解释。
- Agent 可以生成建议和申请草稿。
- Agent 可以汇总实时状态、明细记录、规则和证据。
- 中风险动作必须二次确认。
- 高风险动作不能自动执行。
- Agent 不能绕过门禁。
- Agent 不能自动批准豁免。
- Agent 不能强制合入。
- Agent 不能只看全局 `buildStageStatus` / `buildNodeStatus`，必须结合构建明细、O 测明细和 `isHistory`。

## Open Questions

- 重构建允许条件、次数限制和权限规则是什么？TODO
- 手动重测允许条件、次数限制和权限规则是什么？TODO
- 豁免审批角色、有效期和审计字段是什么？TODO
- 门禁系统除分支锁、PatchSet、Review 分数外还有哪些规则？TODO
- 哪些失败类型可以建议重试，哪些只能人工处理？TODO
