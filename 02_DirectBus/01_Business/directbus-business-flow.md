# DirectBus Business Flow

## Purpose

本文用于梳理 DirectBus / CI-CD 的真实业务流程，为后续 Agent 场景拆解、Tool 设计、Knowledge 设计和 Evaluation 设计提供业务输入。

当前阶段只做业务理解，不做代码实现。

## Actors

| Actor | Description | TODO |
| --- | --- | --- |
| 开发人员 | 发起提测、查看状态、处理失败和阻塞 | TODO |
| Reviewer / Committer | 参与代码评审和合入决策 | TODO |
| CI/CD 系统 | 执行构建、测试、门禁和流水线任务 | TODO |
| 研发平台 / DirectBus | 编排提测、门禁、合入等流程 | TODO |
| Agent | 解释状态、诊断阻塞、辅助操作确认 | TODO |

## External Systems

| System | Role in Flow | Data Needed by Agent | TODO |
| --- | --- | --- | --- |
| Gerrit | 代码评审、变更状态、合入入口 | changeId、owner、branch、review 状态、merge 状态 | TODO |
| Jenkins | 构建任务、构建日志、构建结果 | jobId、buildId、status、log、artifact | TODO |
| O 测平台 | 测试任务和测试结果 | testId、case、失败原因、报告链接 | TODO |
| 门禁系统 | 规则校验和准入结果 | ruleId、ruleName、checkResult、blockReason | TODO |
| 冲突检测系统 | 检测代码冲突和合入风险 | conflictFiles、targetBranch、conflictReason | TODO |
| 合入流水线 | 统一编排构建、测试、门禁、合入 | pipelineId、stage、status、error | TODO |

## Main Flow

```text
开发人员发起提测
  -> Gerrit 创建或更新变更
  -> DirectBus 触发合入流水线
  -> Jenkins 执行构建
  -> O 测平台执行测试
  -> 门禁系统执行规则校验
  -> 冲突检测系统检查合入风险
  -> DirectBus 汇总状态
  -> 满足条件后进入合入流程
  -> 合入成功或失败
```

## Stage Breakdown

| Stage | Business Question | Key Status | Failure / Block Reason | TODO |
| --- | --- | --- | --- | --- |
| 提测发起 | 这次提测是否被系统接收？ | TODO | TODO | TODO |
| Gerrit 评审 | 当前变更是否满足评审要求？ | TODO | TODO | TODO |
| Jenkins 构建 | 构建是否成功？失败在哪里？ | TODO | TODO | TODO |
| O 测 | 哪些测试失败？是否可重测？ | TODO | TODO | TODO |
| 门禁校验 | 哪条规则阻塞了合入？ | TODO | TODO | TODO |
| 冲突检测 | 是否存在代码冲突？ | TODO | TODO | TODO |
| 重测 / 重构建 | 是否允许重新触发？风险是什么？ | TODO | TODO | TODO |
| 合入 | 是否满足合入条件？ | TODO | TODO | TODO |

## State Model Draft

| State | Meaning | Enter Condition | Exit Condition | TODO |
| --- | --- | --- | --- | --- |
| TODO | TODO | TODO | TODO | TODO |

## Business Risks

- 误判状态，导致用户执行错误操作。TODO
- 自动触发高风险操作，绕过人工确认。TODO
- 缺少证据链，导致诊断结论不可追溯。TODO
- 业务规则更新后，Agent 仍使用旧知识。TODO

## Open Questions

- DirectBus 中真实状态枚举有哪些？TODO
- O 测失败是否分为环境问题、用例问题、代码问题？TODO
- 哪些门禁规则是硬阻塞，哪些只是提醒？TODO
- 重测 / 重构建是否有次数限制、权限限制、冷却时间？TODO
- 合入失败后是否存在自动回滚或人工处理流程？TODO
