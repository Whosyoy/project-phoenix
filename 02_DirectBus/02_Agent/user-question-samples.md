# User Question Samples

## Purpose

本文用于沉淀 DirectBus Agent 的用户问题样本。后续这些样本会继续演进为 Intent 设计、Prompt 设计、Tool 测试用例和 Evaluation Golden Dataset。

当前阶段样本只表达业务意图，不绑定具体实现。

## Sample Design Rules

- 每个样本必须映射到 DirectBus / CI-CD 真实场景。
- 不清楚的状态名、规则名、字段名用 TODO 标记。
- 样本需要覆盖正常表达、模糊表达、缺少参数、多轮追问和高风险操作。
- 高风险操作样本必须标记是否需要人机确认。

## Query Samples

| ID | User Question | Expected Intent | Missing Parameters | Need Tool | Need Knowledge | Need Confirmation | TODO |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Q-001 | 这个 change 现在合入到哪一步了？ | QueryMergeStatus | changeId | Yes | No | No | TODO |
| Q-002 | 帮我看下我的提测是不是卡住了 | QueryPipelineStatus | user / changeId / pipelineId | Yes | No | No | TODO |
| Q-003 | 这个构建现在是什么状态？ | QueryBuildStatus | buildId / jobId / changeId | Yes | No | No | TODO |

## Diagnosis Samples

| ID | User Question | Expected Intent | Missing Parameters | Need Tool | Need Knowledge | Need Confirmation | TODO |
| --- | --- | --- | --- | --- | --- | --- | --- |
| D-001 | 为什么这个单不能合入？ | DiagnoseMergeBlocker | changeId | Yes | Yes | No | TODO |
| D-002 | 这个门禁为什么没过？ | DiagnoseGateFailure | changeId / ruleId | Yes | Yes | No | TODO |
| D-003 | O 测失败是代码问题还是环境问题？ | DiagnoseOTestFailure | testId / reportUrl / changeId | Yes | Yes | No | TODO |
| D-004 | Jenkins 构建失败帮我看下原因 | DiagnoseBuildFailure | buildId / logUrl / changeId | Yes | Maybe | No | TODO |

## Explanation Samples

| ID | User Question | Expected Intent | Missing Parameters | Need Tool | Need Knowledge | Need Confirmation | TODO |
| --- | --- | --- | --- | --- | --- | --- | --- |
| E-001 | 这个门禁规则是什么意思？ | ExplainGateRule | ruleId / ruleName | Maybe | Yes | No | TODO |
| E-002 | 为什么冲突检测会拦住我的单？ | ExplainConflictCheck | changeId / conflictId | Yes | Yes | No | TODO |
| E-003 | DirectBus 合入流程到底有哪些步骤？ | ExplainBusinessFlow | None | No | Yes | No | TODO |

## Action Samples

| ID | User Question | Expected Intent | Missing Parameters | Need Tool | Need Knowledge | Need Confirmation | TODO |
| --- | --- | --- | --- | --- | --- | --- | --- |
| A-001 | 帮我重跑一下 O 测 | RequestRetest | changeId / testId | Yes | Maybe | Yes | TODO |
| A-002 | 重新触发一下 Jenkins 构建 | RequestRebuild | buildId / jobId / changeId | Yes | Maybe | Yes | TODO |
| A-003 | 这个单可以合了吗？ | AskMergeReadiness | changeId | Yes | Yes | Yes | TODO |

## Ambiguous / Multi-turn Samples

| ID | User Question | Ambiguity | Expected Clarification | TODO |
| --- | --- | --- | --- |
| M-001 | 帮我看下这个失败了 | 不知道是构建、O 测、门禁还是合入失败 | 请提供 changeId、流水线 ID 或失败页面链接 | TODO |
| M-002 | 为什么又挂了？ | 缺少对象和失败类型 | 你想看哪个 change 或哪条流水线？ | TODO |
| M-003 | 这个能不能再跑一次？ | 不知道重跑构建还是测试，也不知道权限 | 你想重跑 Jenkins 构建还是 O 测？ | TODO |

## Future Evaluation Labels

| Label | Meaning | TODO |
| --- | --- | --- |
| intent_expected | 期望识别出的意图 | TODO |
| required_slots | 必须抽取或追问的参数 | TODO |
| tools_expected | 期望调用的工具 | TODO |
| knowledge_expected | 期望检索的知识类型 | TODO |
| safety_expected | 是否必须确认、拒绝或降级 | TODO |
| answer_criteria | 合格回答标准 | TODO |
