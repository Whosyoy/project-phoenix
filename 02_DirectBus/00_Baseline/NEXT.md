# 下一阶段：Sprint 1 Agent Core / Skill & Tool Design

## 阶段目标

Phase 0 已完成 DirectBus 业务理解、核心场景、用户问题样本和第一版映射表。正式 Sprint 1 进入 Agent Core / Skill & Tool Design，重点是把已确认的业务意图进一步收敛为可设计、可组合、可验证的核心 Skill。

Sprint 1 继续以现有 Phase 0 文档为业务基线，不新增或猜测 DirectBus 内部规则。

## 下一阶段范围

下一阶段要做：

- 明确核心 Skill 的职责和适用场景。
- 明确每个 Skill 的输入与输出。
- 明确每个 Skill 需要调用的 Tool 候选。
- 明确每个 Skill 依赖的 Knowledge 类型。
- 明确每个 Skill 的风险边界和人工确认要求。
- 明确每个 Skill 后续需要覆盖的评测方向。

下一阶段暂不做：

- 不做完整 RAG 方案或知识库实现。
- 不做完整 Evaluation 框架、黄金数据集或评测代码。
- 不做完整 MCP schema 或真实系统接入。
- 不做生产级 Agent Core、权限、审计、灰度、监控或部署实现。
- 不扩写 Phase 0 尚未确认的业务规则。

## 优先设计的 3 个核心 Skill

### 1. 流水线状态汇总技能（Pipeline Status Summary Skill）

面向“当前直通车到哪一步了”一类问题，优先完成状态查询与汇总能力的设计。

后续需要明确：

- 输入
- 输出
- 调用的 Tool 候选
- 需要的 Knowledge 类型
- 风险边界
- 评测方向

### 2. 构建失败分析技能（Build Failure Analysis Skill）

面向“为什么一直卡在构建失败”一类问题，优先完成当前失败定位与证据化分析能力的设计。

后续需要明确：

- 输入
- 输出
- 调用的 Tool 候选
- 需要的 Knowledge 类型
- 风险边界
- 评测方向

### 3. 合入阻塞诊断技能（Merge Block Diagnose Skill）

面向“为什么没有进入合入”一类问题，优先完成合入条件核对与阻塞项诊断能力的设计。

后续需要明确：

- 输入
- 输出
- 调用的 Tool 候选
- 需要的 Knowledge 类型
- 风险边界
- 评测方向

## 统一设计模板

后续设计每个核心 Skill 时，统一使用以下结构：

| 设计项 | 需要回答的问题 |
| --- | --- |
| Skill 目标 | 该 Skill 解决哪类已确认的用户问题？ |
| 输入 | Skill 开始处理前必须获得哪些业务标识和上下文？ |
| 输出 | Skill 应返回哪些状态、证据、解释和下一步建议？ |
| Tool 候选 | 为获得实时数据，可能调用哪些已有候选 Tool？ |
| Knowledge 类型 | 为解释状态或规则，需要哪些已识别的知识类型？ |
| 风险边界 | 哪些结论不能直接给出，哪些动作必须停止或转人工？ |
| 评测方向 | 后续需要从哪些方向判断 Skill 是否正确、完整和安全？ |
| TODO | 哪些内部规则仍未确认，不能在设计中自行补全？ |

该模板只用于 Skill & Tool Design，不代表完整接口定义、RAG 方案或 Evaluation 实现。

## 推荐推进顺序

```text
Pipeline Status Summary Skill
  -> Build Failure Analysis Skill
  -> Merge Block Diagnose Skill
```

先设计低风险的状态汇总 Skill，建立统一的输入、输出和证据组织方式；再进入构建失败分析；最后处理需要同时核对正常路径与豁免路径的合入阻塞诊断。

完成这 3 个核心 Skill 的第一版设计后，再基于 Phase 0 的 8 个顶层意图决定其他 Skill 的后续优先级，不在当前阶段一次性展开全部能力。
