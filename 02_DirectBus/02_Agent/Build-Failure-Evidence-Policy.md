# Build Failure Analysis Skill 证据策略

- 状态：设计草案
- 适用阶段：Sprint 1 Agent Core 学习与设计阶段
- 适用能力：Build Failure Analysis Skill
- 实现状态：仅定义证据策略，不包含 Workflow、Tool、MCP 或规则引擎实现

## 1. 文档目标

本文描述 Build Failure Analysis 场景中的证据分级、关联校验、结果状态和冲突规则，回答以下问题：

- 用户真正要解决的问题是什么。
- 为回答问题需要查询哪些 Tool。
- 哪些证据是关键证据、上下文证据和补充证据。
- 什么情况下返回 `SUCCESS`、`PARTIAL` 或 `UNCERTAIN`。
- 哪些数据虽然存在，但不能作为当前结论的有效证据。

本文只定义 Build Failure Analysis Skill 专属策略。当前不抽象公共 `EvidencePolicy`、`EvidenceRule` 或通用规则接口。

## 2. 用户问题与目标结论

用户核心问题是：

> 为什么构建失败？

该问题需要拆成三个可独立验证的目标结论：

| 目标结论 | 需要回答的内容 | 无法确认时的影响 |
| --- | --- | --- |
| 失败对象 | 哪个项目、基线和 `buildId` 构建失败 | 无法确定正在分析哪个构建，整体结论不可靠 |
| 失败原因 | 当前失败对象的结构化错误和已有分析结果说明了什么 | 可以定位失败对象，但不能确认失败原因 |
| 当前关联 | 当前失败是否属于用户指定的 `apply_bus_id` 和当前生命周期 | 无法确认证据是否属于当前流水线 |

`PARTIAL` 与 `UNCERTAIN` 的区别应基于上述目标结论判断：

- 能可靠回答其中一部分，并明确拒绝回答缺失部分，可以返回 `PARTIAL`。
- 无法确认当前失败对象，或已有证据互相冲突，必须返回 `UNCERTAIN`。

## 3. 证据分级原则

Evidence 的重要性不由 Tool 名称永久决定，而由用户问题、目标结论和当前业务上下文决定。

| 证据等级 | 定义 | 缺失后的处理 |
| --- | --- | --- |
| 关键证据 | 直接支撑用户当前核心问题或某个目标结论 | 对应结论不能成立；根据其他目标结论是否仍可靠，返回 `PARTIAL` 或 `UNCERTAIN` |
| 上下文证据 | 用于确认其他证据是否属于当前业务对象、生命周期和查询范围 | 无法完成关联验证时，不得把孤立证据直接当作当前事实 |
| 补充证据 | 用于增强解释、复现路径和历史背景 | 未被用户明确询问时，缺失不影响核心结论；被明确询问时，可能使结果降级为 `PARTIAL` |

同一类证据的等级可能随问题变化。例如：

- 历史构建记录对于“为什么当前构建失败”通常是补充证据。
- 历史构建记录对于“重构建后为什么还是上一次失败”会升级为关键证据。
- `query_directbus_status` 对失败原因本身是上下文证据；对“当前失败是否属于当前流水线”则是不可缺少的关联证据。

## 4. 候选 Tool 与证据定位

### 4.1 `query_directbus_status`

作用：获取当前 DirectBus 的整体状态、阶段和生命周期信息。

基础定位：上下文证据。

主要用途：

- 确认用户指定的 `apply_bus_id` 当前是否有效。
- 判断当前流水线所处生命周期和业务阶段。
- 验证构建明细是否可能属于当前流程。
- 辅助识别当前状态与构建记录之间的冲突。

建议包含的关联信息：`apply_bus_id`、`isRunning`、`buildStageStatus`、`buildNodeStatus` 及可用于判断数据时效性的字段。

Tool 成功但 `data = null` 时，按上下文证据缺失处理，不能视为已确认当前生命周期。

### 4.2 `query_build_detail`

作用：查询当前和历史构建信息，定位当前失败对象。

基础定位：关键证据。

主要用途：

- 过滤 `isHistory = false` 的当前有效记录。
- 确认失败项目和基线。
- 获取当前 `buildId` 和构建状态。
- 获取 `failureStage`、`failureModule`、`errorSummary` 等结构化失败字段。
- 区分当前失败与历史失败、重构建记录。

如果无法定位唯一的当前失败对象，就无法证明后续分析结果属于哪个构建，结果应为 `UNCERTAIN`。

### 4.3 `query_build_failure_analysis`

作用：查询构建侧已有的失败分析结果。

基础定位：针对“为什么失败”这一目标结论的关键证据。

主要用途：

- 获取已有失败分析结果。
- 获取分析结果对应的 `buildId` 或等价稳定关联键。
- 与当前 Build Detail 的失败对象和结构化字段进行关联校验。

本策略使用 `buildId` 表达关联要求，不限定真实接口字段名。如果真实 Tool 不返回 `buildId`，必须提供能够唯一关联当前构建尝试的等价稳定键，否则分析结果不能升级为当前失败事实。

该 Tool 返回的是构建侧已有分析，不表示本地 Agent 独立完成了根因分析。分析内容只有在关联一致且未与结构化证据冲突时才是有效证据。

### 4.4 其他信息

候选信息包括：

- commit 信息。
- 历史构建记录。
- 重构建次数和时间。
- 测试结果。
- 日志或产物链接。

基础定位：补充证据。

这些信息用于补充失败背景、解释重构建轨迹或提供人工复核入口。只有当用户问题明确关注 commit、历史失败或重构建轨迹时，相应信息才升级为关键证据。

## 5. 用户问题驱动的证据矩阵

| 用户问题 | 关键证据 | 上下文证据 | 补充证据 |
| --- | --- | --- | --- |
| 哪个构建失败？ | 当前 Build Detail、`buildId`、项目、基线、构建状态 | DirectBus 当前生命周期 | Failure Analysis、commit、历史记录 |
| 为什么构建失败？ | 当前 Build Detail、结构化失败字段、与当前 `buildId` 一致的 Failure Analysis | DirectBus 当前生命周期和查询范围 | commit、历史构建、测试结果、日志链接 |
| 当前失败是否属于当前流水线？ | 当前 Build Detail 与 DirectBus 的关联信息 | DirectBus 状态、阶段和生命周期 | 历史记录、测试结果 |
| 为什么重构建后还是上一次失败？ | 当前与历史 Build Detail、构建尝试关联键、时间和重构建轨迹 | DirectBus 当前生命周期 | Failure Analysis、commit、测试结果 |

证据分级必须在确定用户问题后进行，不能先按 Tool 建立一套固定等级再套用所有问题。

## 6. 证据有效性与关联校验

Tool 返回数据后，Workflow 仍需完成以下确定性校验，才能把数据写入可用于结论的 Evidence：

1. **技术可用性**：Tool 调用成功，并且 `data` 不为 `null`。
2. **查询范围**：返回数据属于用户指定的 `apply_bus_id`，没有扩大到其他流水线。
3. **当前记录**：回答当前失败时，只使用 `isHistory = false` 的当前有效构建记录。
4. **失败对象**：当前记录能够唯一确定项目、基线、`buildId` 和失败状态。
5. **对象关联**：Failure Analysis 的 `buildId` 或等价关联键与当前 Build Detail 一致。
6. **尝试版本**：分析结果属于当前构建尝试，不是重构建前的旧分析。
7. **内容一致性**：分析结果未与 `failureStage`、`failureModule`、`errorSummary` 等结构化字段发生明确冲突。
8. **生命周期一致性**：DirectBus 当前生命周期与被标记为当前的构建记录不存在已确认的业务不变量冲突。

因此：

```text
ToolResult.SUCCESS
不等于
Evidence 有效
```

Evidence 必须同时满足来源明确、查询范围正确、当前记录有效和关联关系一致。

## 7. SkillResult 状态规则

### 7.1 `SUCCESS`

满足以下条件时返回 `SUCCESS`：

- `query_build_detail` 查询成功，并定位到唯一的当前失败构建。
- 当前失败对象包含可关联的 `buildId` 或等价稳定关联键。
- `query_build_failure_analysis` 查询成功。
- Failure Analysis 与当前 Build Detail 的关联键一致。
- DirectBus 上下文能够确认该失败属于当前查询范围。
- 当前上下文、构建状态和分析内容不存在证据冲突。

此时已经可以可靠回答：

- 哪个构建失败。
- 为什么失败。
- 当前失败是否属于当前流水线。

未被用户询问的补充证据缺失，不影响 `SUCCESS`。

### 7.2 `PARTIAL`

当部分目标结论可靠、其他目标结论因证据缺失而无法回答，但不存在关键证据冲突时，返回 `PARTIAL`。

典型场景：

- Build Detail 成功，已经定位唯一的当前失败对象，但 Failure Analysis 查询失败或缺失。
- Build Detail 的项目、基线、`buildId` 和失败状态已确认，但只能提供结构化错误摘要，不能确认失败原因。
- Build Detail 与 Failure Analysis 关联一致，但 DirectBus 上下文暂时不可用；现有范围足以说明失败对象和原因，但不能确认当前生命周期关系。
- 用户明确询问重构建轨迹或 commit 背景，而相应补充证据缺失；核心失败对象和原因仍然可靠。

`PARTIAL` 必须明确区分：

```text
已确认：哪个构建失败
未确认：为什么失败，或是否属于当前生命周期
```

Failure Analysis 缺失时，不得把 `errorSummary` 或模型推断包装成已确认根因。

### 7.3 `UNCERTAIN`

当无法确认当前失败对象，或关键证据之间存在冲突，导致已有数据无法可靠归属于同一个当前构建时，返回 `UNCERTAIN`。

典型场景：

- `query_build_detail` 失败、无数据或无法定位唯一当前失败对象。
- Failure Analysis 与当前 Build Detail 的 `buildId` 不一致。
- Failure Analysis 缺少任何可验证的对象关联键，无法证明它属于当前失败。
- DirectBus 当前生命周期与当前 Build Detail 存在已确认的状态冲突。
- 只存在历史失败记录，无法确认当前流水线是否仍有该失败。
- 存在多个当前失败记录，但用户范围不足以确定分析对象。
- 多个分析结果指向同一当前构建，却给出互相排斥且无法消解的结论。

`UNCERTAIN` 不表示所有 Evidence 都不可信，而表示这些 Evidence 无法共同支撑当前核心结论。已确认事实仍应保留在 `EvidenceBundle` 中。

## 8. 状态判断优先级

Workflow 应按以下顺序判断业务结果：

1. 存在证据冲突：`UNCERTAIN`。
2. 无法唯一确定当前失败对象或关联对象：`UNCERTAIN`。
3. 当前失败对象已确认，但失败原因证据缺失：`PARTIAL`。
4. 核心结论可靠，但用户明确要求的补充解释缺失：`PARTIAL`。
5. 失败对象、失败原因和当前关联均完整且一致：`SUCCESS`。

Tool 调用失败不会直接决定 `SkillResult`。Workflow 必须先判断失败的 Tool 影响了哪个目标结论，以及其他证据是否仍能支持部分可靠回答。

## 9. 证据冲突

以下情况属于证据冲突：

| 冲突类型 | 示例 | 影响 |
| --- | --- | --- |
| 构建对象冲突 | 当前 Build Detail 为 `build-100`，Failure Analysis 指向 `build-99` | 无法把失败原因归属于当前构建，返回 `UNCERTAIN` |
| 查询范围冲突 | Build Detail 或分析结果属于其他 `apply_bus_id`、项目或基线 | 证据超出当前查询范围，返回 `UNCERTAIN` |
| 生命周期冲突 | DirectBus 显示已进入 `TEST` 且明确标记当前构建阶段已经结束，但当前 Build Detail 仍标记同一构建正在执行 | 无法判断生命周期或记录时效性，返回 `UNCERTAIN` |
| 当前 / 历史冲突 | 只有 `isHistory = true` 的失败记录，却被当作当前失败 | 无法确认当前失败对象，返回 `UNCERTAIN` |
| 构建尝试冲突 | 当前记录属于重构建后的尝试，分析结果来自重构建前的尝试 | 分析结果失效，返回 `UNCERTAIN` |
| 内容冲突 | 结构化字段显示依赖下载失败，分析结果却明确指向编译语法错误 | 原因证据互相排斥，返回 `UNCERTAIN` |
| 多当前记录冲突 | 存在多个当前失败构建，但用户未指定项目或基线，且无法唯一匹配 | 无法确定分析对象，返回 `UNCERTAIN` |

仅凭 DirectBus 进入测试阶段，不应自动判定与仍在构建的项目冲突。只有已确认的业务不变量明确表明该构建不可能继续执行时，才将其记录为生命周期冲突，避免忽略 DirectBus 中可能存在的并行流转。

## 10. 证据处理链路

```text
用户问题与查询范围
→ 确定需要回答的目标结论
→ 调用候选 Tool 并获得 ToolResult
→ 过滤当前 / 历史记录
→ 校验 apply_bus_id、buildId 和构建尝试关联
→ 识别证据缺失与证据冲突
→ 构建 EvidenceBundle
→ 生成 SkillResult
→ ResponseGenerator 表达最终结论
```

Workflow 是事实判断的唯一决策层。它负责：

- 选择当前任务需要的证据。
- 判断证据等级。
- 验证对象关联和生命周期一致性。
- 记录已确认事实、缺失证据和冲突证据。
- 决定 `SUCCESS`、`PARTIAL` 或 `UNCERTAIN`。

ResponseGenerator 只能基于 `SkillResult` 和 `EvidenceBundle` 组织表达，不能重新判断失败对象、补写失败原因或覆盖不确定状态。

## 11. EvidenceBundle 最小表达要求

无论最终状态为何，EvidenceBundle 都应显式表达：

- 查询范围：`apply_bus_id`，以及用户指定的项目或基线范围。
- 已确认事实：当前可可靠陈述的失败对象、结构化字段和分析结果。
- 证据来源：事实来自哪个 Tool、哪个字段和哪个构建对象。
- 缺失证据：缺失的 Tool 数据、关联键或用户要求的补充信息。
- 冲突证据：冲突双方的值、来源和受影响结论。
- 未确认结论：当前不能回答什么，以及不能回答的原因。
- Tool 执行结果：技术成功、失败或无数据状态。

例如，Failure Analysis 缺失时可以保留：

```text
已确认：build-100 / project-a / main 当前构建失败
未确认：失败原因
缺失证据：query_build_failure_analysis
结果：PARTIAL
```

buildId 冲突时应表达：

```text
当前 Build Detail：build-100
Failure Analysis：build-99
冲突：分析结果不能关联到当前失败对象
结果：UNCERTAIN
```

## 12. 当前范围与非目标

本文不设计或实现：

- Build Failure Analysis Workflow 代码。
- Tool 接口或 Mock Tool 代码。
- MCP schema、MCP Server 或真实公司接口。
- 公共 `EvidencePolicy`、`EvidenceRule` 或 Validator 抽象。
- Drools、规则 DSL 或其他规则引擎。
- LLM 根因分析、RAG、历史案例检索或 Evaluation 实现。
- 重构建、豁免、合入或其他有副作用操作。

规则在未来实现阶段应先直接放入 Build Failure Analysis Skill 专属 Workflow。至少完成多个 Skill 的证据实验后，再判断是否存在值得抽象的共同模式。

## 13. Design Notes

1. Tool 成功不代表业务结论一定可信。
2. Evidence 存在不代表 Evidence 有效，需要验证关联关系。
3. Workflow 负责证据组合和业务判断。
4. ResponseGenerator 只负责表达最终结论。
5. 当前不抽象公共 EvidencePolicy，等待多个 Skill 验证共同模式。
