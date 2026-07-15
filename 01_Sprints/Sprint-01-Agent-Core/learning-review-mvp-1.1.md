# MVP-1.1 Agent Core 代码理解验收

## 验收背景

- 当前阶段：MVP-1.1 代码理解验收
- 验收对象：Rory 对 Agent Core 最小实现的代码理解
- 综合评分：6.5 / 10
- 当前完成度：约 75%

本文记录 Rory 本轮学习回答、Review 纠正、验收结论和下一步学习重点。未通过或尚未准确掌握的内容不能作为已验证学习结论。

## 1. 为什么 AgentCore 先使用路由，再提取参数

### Rory 原始回答

每个路由对应的 Intent 所需参数不同。

例如：

- 查询 DirectBus 状态，需要 `applyBusId`。
- 发起提测，需要另一组业务参数。
- 重试操作也有不同的必填参数。

因此需要先识别 Intent，再根据对应业务能力的参数契约进行提取和校验。

### Review 结论

回答通过。Intent 决定后续参数契约，因此调用顺序应为：

```text
UserRequest
→ IntentRouter
→ ParameterExtractor
```

参数抽取不能脱离已确认的 Intent 猜测业务参数。

## 2. 为什么 UNKNOWN 要在 SkillRegistry 前拦截

### Rory 原始回答

`UNKNOWN` 不代表有效业务 Intent，应在进入 Skill 层前终止，避免无效意图继续传播、错误匹配 Skill 或调用无关 Tool。

正确处理方式：

```text
UNKNOWN
→ 返回澄清响应
→ 不进入 SkillRegistry
→ 不调用 Tool
```

### Review 结论

基本通过。`UNKNOWN` 是安全控制值，不是第三个业务 Intent。Agent Core 必须在查询 `SkillRegistry` 之前拦截并返回澄清响应。

## 3. Skill 与 Workflow 的理解

### Rory 原始回答

Skill 实现一个具体业务功能，例如查询 DirectBus 状态、发起提测、分析失败问题。

Workflow 用于编排复杂业务流程，可能涉及多个 Skill 和 Tool。

### Review 纠正

在当前 Project Phoenix MVP 中，关系应为：

```text
Agent
→ 选择 Skill
→ Skill 调用对应 Workflow
→ Workflow 编排多个 Tool
```

一个 Skill 通常表示一项完整业务能力。Workflow 是该 Skill 内部的确定性执行过程。

当前不设计 Workflow 调用多个 Skill。未来多个 Skill 的组合应由更高层 Planner 或 Orchestrator 负责。

## 4. ToolResult 与 EvidenceBundle 的区别

### Rory 本轮理解

`ToolResult` 表示一次 Tool 调用的原始技术结果，包括：

- Tool 名称
- 是否成功
- 返回数据
- 错误信息
- 调用元信息

`EvidenceBundle` 表示 Workflow 对多个 `ToolResult` 经过业务规则处理后形成的可追溯证据，包括：

- 当前与历史数据过滤
- 证据来源
- 缺失证据
- 冲突证据
- Tool 失败
- 不确定性原因

`EvidenceBundle` 可用于结果解释、错误复现、回归测试、评测和审计。

### Review 结论

基本通过。需要继续结合代码理解从原始 Tool 返回到业务证据的转换过程。

## 5. PARTIAL 与 UNCERTAIN 的区别

### Rory 原始回答

原回答未准确覆盖核心差异。

### Review 纠正

`PARTIAL`：证据不完整，但已有证据仍然支持部分可靠结论。

示例：

```text
DirectBus 全局状态成功
构建明细成功
测试明细失败
```

此时仍可以确认当前阶段和构建状态，但无法确认测试详情。

`UNCERTAIN`：关键证据不足或多个证据相互冲突，无法可靠判断业务事实。

示例：

```text
全局状态显示流程已经结束
但当前构建明细仍为 RUNNING
```

核心区别：

```text
PARTIAL
= 证据不完整，但部分结论可信

UNCERTAIN
= 关键证据冲突或不足，无法可靠下结论
```

## 6. 为什么 ResponseGenerator 不能重新判断业务状态

### Review 结论

Workflow 是业务事实和状态判断的唯一决策层。

正确链路：

```text
Workflow
→ 根据规则判断状态
→ 生成 EvidenceBundle 和 SkillResult
→ ResponseGenerator 组织表达
```

ResponseGenerator 只能改变表达方式，不能改变业务事实，原因包括：

- 避免出现两个事实决策中心。
- 防止 Workflow 与 ResponseGenerator 结论不一致。
- 避免未来 LLM ResponseGenerator 产生幻觉。
- 保证模板版与模型版基于同一 `SkillResult`。
- 方便测试和问题定位。

核心原则：

```text
Workflow 决定事实。
ResponseGenerator 只负责表达事实。
```

## 7. SkillExecutor 能否删除

### Rory 原始回答

不能，为了方便扩展。

### Review 纠正

当前 MVP 中 `SkillExecutor` 技术上可以删除，因为目前只是：

```java
return skill.execute(context);
```

但架构上暂时保留，作为统一执行治理边界。未来可能承载：

- 异常捕获
- Timeout
- Retry
- Trace
- Metrics
- 审计
- 权限校验
- 前后置处理
- Token 和成本统计

准确表述：当前可以删，但为了保留统一执行治理边界而暂时保留。如果后续长期没有公共职责，应考虑删除，避免空抽象。

## 当前验收结果

| 验收项 | 结果 | 说明 |
| --- | --- | --- |
| 路由后提取参数 | 通过 | 已理解 Intent 决定参数提取契约 |
| UNKNOWN 在 SkillRegistry 前拦截 | 基本通过 | 继续强化 UNKNOWN 是控制值而非业务 Intent |
| Skill 与 Workflow 边界 | 需要纠正 | 当前应理解为 Skill 调用 Workflow，Workflow 编排 Tool |
| ToolResult 与 EvidenceBundle | 基本通过 | 需要继续结合代码理解证据转换 |
| PARTIAL 与 UNCERTAIN | 暂未通过 | 需要重点掌握证据完整性与证据冲突的区别 |
| ResponseGenerator 职责边界 | 尚未掌握 | 需要明确事实判断与语言表达分离 |
| SkillExecutor | 部分通过 | 需要理解“当前可删除，但作为执行治理边界暂时保留” |

## 主要短板

- Skill 与 Workflow 的层级关系。
- `PARTIAL` 与 `UNCERTAIN` 的业务语义。
- Workflow 与 ResponseGenerator 的职责边界。
- SkillExecutor 作为预留抽象的真实价值。

## 四句核心结论

1. Skill 表示一项业务能力，Workflow 是该能力内部的确定性执行过程。
2. ToolResult 是一次 Tool 调用的原始技术结果，EvidenceBundle 是多个结果经过业务规则处理后的可追溯证据。
3. PARTIAL 表示证据不完整但部分结论可信，UNCERTAIN 表示关键证据不足或互相冲突。
4. Workflow 决定事实，ResponseGenerator 只负责表达事实。

## 下一步唯一优先事项

Rory 结合代码重新走一遍以下链路：

```text
ToolResult
→ Workflow 业务处理
→ EvidenceBundle
→ SkillResult
→ ResponseGenerator
```

## 暂时不要做

- 不开始 Build Failure Analysis Skill。
- 不接 LLM。
- 不接 Spring AI。
- 不接 MCP。
- 不新增复杂抽象。
- 不继续扩大代码范围。
