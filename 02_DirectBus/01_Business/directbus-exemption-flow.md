# DirectBus Exemption Flow

## Purpose

本文用于沉淀 DirectBus 豁免流程的业务骨架。

豁免是 DirectBus 主流程上的例外通道，用于处理提测准入、构建 / 测试失败、合入前门禁不满足等场景。当前阶段只记录已知入口和待澄清问题，不展开具体实现。

## Relationship with Main Flow

主流程文档：

- `02_DirectBus/01_Business/directbus-business-flow.md`

本文只补充主流程中的豁免入口、审批角色、风险边界和待确认规则。

## Definition

豁免是指在某个规则不满足、节点失败或流程阻塞时，由关键角色审批后，允许 DirectBus 继续提测或继续合入的业务例外机制。

豁免不是普通成功路径，也不应该绕过审计。

## Known Entry Points

| Entry Point | Scenario | Possible Result After Approval | TODO |
| --- | --- | --- | --- |
| 提测前准入检查失败 | Gerrit 提交打分不满足、项目信息异常或其他准入规则不满足 | 允许继续提测 | TODO: 补充可豁免规则 |
| 构建失败 | 构建验证失败但业务允许继续推进 | 允许继续后续测试或合入流程 | TODO: 补充失败类型和审批角色 |
| 测试失败 | O 测失败但业务允许继续推进 | 允许继续门禁合入 | TODO: 补充领域、错误码、审批角色 |
| 合入前门禁不满足 | 分支锁、Gerrit 分数、其他门禁规则不满足 | 允许继续合入或人工重试 | TODO: 区分哪些规则绝不可豁免 |

## Non-Exemption Boundaries

以下场景当前不应默认视为可豁免：

| Scenario | Reason | TODO |
| --- | --- | --- |
| patchset 有更改 | 当前验证结果已经失效，必须重新提测 | TODO: 确认是否存在特殊例外 |
| 未知影响范围 | 验证项目和基线不明确，继续提测风险高 | TODO |
| 底版本不可用 | 构建输入不可靠，验证结果不可解释 | TODO |

## Approval Model Draft

| Field | Meaning | TODO |
| --- | --- | --- |
| requester | 发起豁免的人 | TODO |
| approver | 关键审批角色 | TODO |
| reason | 豁免原因 | TODO |
| scope | 豁免适用范围，例如某个 change、patchset、项目、基线或阶段 | TODO |
| evidence | 支撑豁免的证据，例如失败摘要、报告链接、规则说明 | TODO |
| expirePolicy | 豁免是否过期、何时失效 | TODO |
| auditLog | 审批和执行记录 | TODO |

## Risk Levels

| Risk Level | Example | Expected Control | TODO |
| --- | --- | --- | --- |
| Low | 低风险规则提醒类豁免 | 审批 + 记录 | TODO |
| Medium | 构建失败或测试失败后继续推进 | 关键角色审批 + 证据链 | TODO |
| High | 合入前门禁不满足后继续合入 | 强审批 + 审计 + 明确责任人 | TODO |

## Agent Boundary

DirectBus Agent 可以在豁免流程中辅助：

- 解释为什么当前流程进入豁免入口。
- 汇总失败原因、规则依据和证据链接。
- 提醒用户需要哪个角色审批。
- 生成豁免申请草稿 TODO。

DirectBus Agent 不应：

- 自动批准豁免。
- 绕过关键角色审批。
- 在证据不足时建议继续合入。
- 将可豁免规则和不可豁免规则混淆。

## Open Questions

- 哪些提测前准入规则可以豁免，哪些绝对不能豁免？TODO
- 构建失败豁免需要哪些角色审批？TODO
- 测试失败豁免是否按六大领域区分审批人？TODO
- 合入前门禁豁免是否允许覆盖分支锁？TODO
- Gerrit 分数不满足是否可以豁免？TODO
- 豁免是否绑定 patchset？TODO
- 豁免是否有有效期？TODO
- 豁免审批是否需要写入审计日志？TODO
- Agent 生成豁免申请时必须引用哪些证据？TODO
