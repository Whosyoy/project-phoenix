# Agent Scenario Breakdown

## Purpose

本文用于拆解 DirectBus 中哪些业务问题适合 Agent，哪些适合普通系统查询，哪些必须保留人工确认。

当前阶段只做业务场景判断，不做 Agent Core 实现。

## Scenario Classification

| Scenario Type | Description | Agent Suitability | Reason | TODO |
| --- | --- | --- | --- | --- |
| 状态查询 | 查询提测、构建、测试、门禁、合入状态 | Medium | 适合 Tool 查询和自然语言汇总，但不需要复杂推理 | TODO |
| 阻塞诊断 | 判断为什么不能合入或推进 | High | 需要整合多系统状态、规则和历史案例 | TODO |
| 失败分析 | 分析构建失败、O 测失败、门禁失败 | High | 需要读取日志、报告、规则和上下文 | TODO |
| 规则解释 | 解释门禁规则、SOP、流程要求 | High | 适合 Knowledge / RAG 支撑 | TODO |
| 操作执行 | 重测、重构建、重新触发流水线 | Medium | 可以辅助，但中高风险操作必须确认 | TODO |
| 合入决策 | 判断是否应该合入 | Low / Medium | 可给建议，但不应替代责任人决策 | TODO |

## Agent-Fit Criteria

一个 DirectBus 场景适合 Agent，通常需要满足以下条件：

- 用户问题是自然语言表达，且参数可能不完整。
- 需要跨 Gerrit、Jenkins、O 测、门禁、流水线等多个系统整合信息。
- 需要解释原因，而不是只返回状态码。
- 需要引用规则、SOP、历史案例或日志证据。
- 需要根据风险等级决定是否追问、确认或拒绝执行。

## Not-Agent-First Criteria

以下场景不应优先做成 Agent：

- 单字段、单系统、无解释需求的查询。
- 已有稳定按钮或固定表单能高效完成的操作。
- 权限边界不清晰的自动执行。
- 缺少可验证数据来源的推测性回答。
- 需要业务负责人最终拍板的高风险决策。

## Candidate Scenarios

| ID | Scenario | User Goal | Agent Role | Risk Level | Human Confirmation | TODO |
| --- | --- | --- | --- | --- | --- | --- |
| S-001 | 查询合入状态 | 知道当前卡在哪一步 | 汇总多系统状态 | Low | No | TODO |
| S-002 | 诊断合入阻塞 | 知道为什么不能合入 | 归因并给证据 | Medium | No | TODO |
| S-003 | 分析构建失败 | 知道失败原因和下一步 | 读取日志并解释 | Medium | No | TODO |
| S-004 | 分析 O 测失败 | 判断失败类型和处理方式 | 结合测试报告和历史案例 | Medium | No | TODO |
| S-005 | 解释门禁规则 | 理解某条规则为什么阻塞 | 检索规则和 SOP | Low | No | TODO |
| S-006 | 发起重测 | 希望重新触发测试 | 检查条件并要求确认 | Medium / High | Yes | TODO |
| S-007 | 发起重构建 | 希望重新触发构建 | 检查条件并要求确认 | Medium / High | Yes | TODO |
| S-008 | 判断是否可以合入 | 判断是否具备合入条件 | 给出建议和证据 | High | Yes | TODO |

## Risk Policy Draft

| Risk Level | Examples | Agent Behavior | TODO |
| --- | --- | --- | --- |
| Low | 状态查询、规则解释 | 可直接回答，需给来源 | TODO |
| Medium | 失败分析、阻塞诊断 | 回答需带证据和置信度，必要时追问 | TODO |
| High | 重测、重构建、合入建议 | 必须确认，不自动执行关键动作 | TODO |

## Open Questions

- 哪些 DirectBus 操作属于高风险？TODO
- 哪些失败类型可以自动建议重试？TODO
- Agent 是否允许直接触发重测 / 重构建？TODO
- 诊断结论是否需要展示证据链接？TODO
- 是否需要区分普通开发、模块 Owner、管理员权限？TODO
