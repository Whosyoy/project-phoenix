# Intent / Tool / Skill / Knowledge / Evaluation Map

## Purpose

本文用于建立 DirectBus Agent 的核心映射关系：

```text
用户问题
  -> Intent
  -> Skill
  -> Tool
  -> Knowledge
  -> Evaluation
```

当前阶段只定义业务映射骨架，不实现 Tool、Skill 或 Evaluation 代码。

## Mapping Principles

- Intent 描述用户要解决的业务问题。
- Skill 描述 Agent 内部的业务能力组合。
- Tool 描述可调用的外部系统能力。
- Knowledge 描述需要检索或引用的规则、SOP、FAQ、日志说明和历史案例。
- Evaluation 描述如何判断 Agent 是否答对、调对工具、遵守安全边界。

## Intent Taxonomy Draft

| Intent | Description | Example Question | Risk Level | TODO |
| --- | --- | --- | --- | --- |
| QueryMergeStatus | 查询合入状态 | 这个 change 现在合入到哪一步了？ | Low | TODO |
| QueryBuildStatus | 查询构建状态 | 这个构建现在是什么状态？ | Low | TODO |
| QueryPipelineStatus | 查询流水线状态 | 我的提测是不是卡住了？ | Low | TODO |
| DiagnoseMergeBlocker | 诊断合入阻塞 | 为什么这个单不能合入？ | Medium | TODO |
| DiagnoseBuildFailure | 诊断构建失败 | Jenkins 构建失败原因是什么？ | Medium | TODO |
| DiagnoseOTestFailure | 诊断 O 测失败 | O 测失败是代码问题还是环境问题？ | Medium | TODO |
| DiagnoseGateFailure | 诊断门禁失败 | 这个门禁为什么没过？ | Medium | TODO |
| ExplainGateRule | 解释门禁规则 | 这个门禁规则是什么意思？ | Low | TODO |
| ExplainConflictCheck | 解释冲突检测 | 为什么冲突检测会拦住我的单？ | Low / Medium | TODO |
| RequestRetest | 请求重测 | 帮我重跑一下 O 测 | Medium / High | TODO |
| RequestRebuild | 请求重构建 | 重新触发一下 Jenkins 构建 | Medium / High | TODO |
| AskMergeReadiness | 询问是否可合入 | 这个单可以合了吗？ | High | TODO |

## Tool Candidates

| Tool | External System | Capability | Input | Output | TODO |
| --- | --- | --- | --- | --- | --- |
| GerritChangeQueryTool | Gerrit | 查询 change 基本信息和评审状态 | changeId | changeStatus、owner、branch、reviewStatus | TODO |
| JenkinsBuildQueryTool | Jenkins | 查询构建状态和日志 | jobId / buildId / changeId | buildStatus、logUrl、failureSummary | TODO |
| OTestQueryTool | O 测平台 | 查询测试状态和报告 | testId / changeId | testStatus、failedCases、reportUrl | TODO |
| GateCheckQueryTool | 门禁系统 | 查询门禁结果 | changeId / ruleId | gateStatus、failedRules、blockReason | TODO |
| ConflictCheckQueryTool | 冲突检测系统 | 查询冲突检测结果 | changeId / branch | conflictStatus、files、reason | TODO |
| PipelineQueryTool | 合入流水线 | 查询流水线整体状态 | pipelineId / changeId | stages、currentStage、status | TODO |
| RetestActionTool | O 测平台 / 流水线 | 发起重测 | testId / changeId / confirmationToken | actionResult | TODO |
| RebuildActionTool | Jenkins / 流水线 | 发起重构建 | jobId / buildId / confirmationToken | actionResult | TODO |

## Skill Candidates

| Skill | Responsibility | Uses Tools | Uses Knowledge | TODO |
| --- | --- | --- | --- | --- |
| MergeStatusSkill | 汇总合入状态和当前阻塞点 | GerritChangeQueryTool, PipelineQueryTool | Maybe | TODO |
| BuildFailureDiagnosisSkill | 分析 Jenkins 构建失败 | JenkinsBuildQueryTool | 构建失败 SOP、历史失败案例 | TODO |
| OTestFailureDiagnosisSkill | 分析 O 测失败 | OTestQueryTool | O 测 SOP、环境问题 FAQ、历史案例 | TODO |
| GateFailureDiagnosisSkill | 分析门禁失败 | GateCheckQueryTool | 门禁规则、SOP | TODO |
| ConflictExplanationSkill | 解释冲突检测结果 | ConflictCheckQueryTool | 冲突处理 SOP | TODO |
| SafeActionSkill | 执行前风险检查和人机确认 | RetestActionTool, RebuildActionTool | 权限规则、操作 SOP | TODO |

## Knowledge Candidates

| Knowledge Type | Example Content | Used By | Freshness Requirement | TODO |
| --- | --- | --- | --- | --- |
| 门禁规则 | 规则说明、阻塞条件、处理方式 | ExplainGateRule, DiagnoseGateFailure | High | TODO |
| SOP | 构建失败处理、O 测失败处理、冲突处理 | Diagnosis Skills | Medium / High | TODO |
| FAQ | 常见失败原因和处理建议 | Diagnosis Skills | Medium | TODO |
| 历史案例 | 相似失败、处理结论、问题单链接 | Diagnosis Skills | Medium | TODO |
| 日志说明 | Jenkins 日志关键字、错误码解释 | BuildFailureDiagnosisSkill | Medium | TODO |
| 权限与操作规则 | 谁可以重测、重构建、合入 | SafeActionSkill | High | TODO |

## End-to-End Mapping

| Intent | Required Slots | Skill | Tools | Knowledge | Evaluation Focus | Safety Policy | TODO |
| --- | --- | --- | --- | --- | --- | --- | --- |
| QueryMergeStatus | changeId | MergeStatusSkill | GerritChangeQueryTool, PipelineQueryTool | Maybe | 状态是否正确、是否说明当前阶段 | Low risk, direct answer | TODO |
| DiagnoseMergeBlocker | changeId | MergeStatusSkill | GerritChangeQueryTool, PipelineQueryTool, GateCheckQueryTool, ConflictCheckQueryTool | 门禁规则、冲突 SOP | 阻塞原因是否正确、证据是否完整 | Medium risk, evidence required | TODO |
| DiagnoseBuildFailure | buildId / changeId | BuildFailureDiagnosisSkill | JenkinsBuildQueryTool | 构建 SOP、历史案例、日志说明 | 是否定位失败阶段和关键日志 | Medium risk, evidence required | TODO |
| DiagnoseOTestFailure | testId / changeId | OTestFailureDiagnosisSkill | OTestQueryTool | O 测 SOP、FAQ、历史案例 | 是否区分失败类型 | Medium risk, evidence required | TODO |
| ExplainGateRule | ruleId / ruleName | GateFailureDiagnosisSkill | GateCheckQueryTool | 门禁规则 | 解释是否基于规则来源 | Low risk, cite source | TODO |
| RequestRetest | testId / changeId | SafeActionSkill | OTestQueryTool, RetestActionTool | 权限与操作规则、O 测 SOP | 是否完成风险检查和确认 | Confirmation required | TODO |
| RequestRebuild | jobId / buildId / changeId | SafeActionSkill | JenkinsBuildQueryTool, RebuildActionTool | 权限与操作规则、构建 SOP | 是否完成风险检查和确认 | Confirmation required | TODO |
| AskMergeReadiness | changeId | MergeStatusSkill | GerritChangeQueryTool, PipelineQueryTool, GateCheckQueryTool, ConflictCheckQueryTool | 门禁规则、合入 SOP | 是否给出建议而非越权决策 | High risk, human decision | TODO |

## Evaluation Dimensions

| Dimension | Question | Example Metric | TODO |
| --- | --- | --- | --- |
| Intent Accuracy | 是否识别对用户意图？ | intent_accuracy | TODO |
| Slot Accuracy | 是否抽取或追问必要参数？ | slot_f1 / missing_slot_recall | TODO |
| Tool Accuracy | 是否调用正确工具？ | tool_selection_accuracy | TODO |
| Retrieval Quality | 是否检索到正确知识？ | recall@k / citation_accuracy | TODO |
| Answer Groundedness | 回答是否有证据支撑？ | groundedness_score | TODO |
| Safety Compliance | 是否遵守确认、拒绝、降级策略？ | safety_pass_rate | TODO |
| User Helpfulness | 是否给出可执行下一步？ | human_rating / rubric_score | TODO |

## Open Questions

- Intent 是否需要更细粒度，例如区分 QueryGateStatus 和 DiagnoseGateFailure？TODO
- Tool 是按系统拆，还是按业务能力拆？TODO
- Skill 是否需要引入 Planner，还是先用固定流程编排？TODO
- Knowledge 的更新责任人和版本策略是什么？TODO
- Evaluation 第一批 Golden Dataset 需要多少条？TODO
