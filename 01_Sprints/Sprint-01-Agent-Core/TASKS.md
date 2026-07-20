# Sprint 1 Tasks：Agent Core / Skill & Tool Design

## Sprint Setup

- [ ] 确认 Sprint 1 只使用 Phase 0 已完成的业务基线
- [ ] 确认未知内部规则继续保留 TODO
- [x] 确认 MVP 只做最小实现，不写完整 MCP schema
- [x] 确认本阶段不展开完整 RAG、Evaluation 或生产工程化实现

## Agent Core Minimum Design Loop

- [ ] 建立与 2 个 MVP Skill 相关的顶层 Intent 路由关系，其余 Intent 暂不展开
- [ ] 明确 Skill 与 Tool 的职责边界
- [ ] 说明固定 Workflow 与动态 Agent 的取舍
- [ ] 统一缺失参数识别与追问方式
- [ ] 统一 Tool 调用失败的异常与兜底方式
- [ ] 统一 Agent 结构化输出骨架
- [ ] 统一风险分级、人机确认和转人工边界

## Skill 1：Pipeline Status Summary Skill

- [x] 第一版设计完成
- [x] 明确业务目标
- [x] 明确触发意图
- [x] 明确输入参数
- [x] 明确缺失参数处理
- [x] 明确执行步骤
- [x] 明确 Tool 候选
- [x] 明确 Knowledge 需求
- [x] 明确输出结构
- [x] 明确异常与兜底
- [x] 明确风险边界
- [x] 明确后续 Evaluation 方向

## 已固化的决策与治理机制

- [x] `ADR-001`：状态汇总类场景采用确定性 Workflow
- [x] `LEARNING-GOVERNANCE.md`：Project Phoenix 学习与迭代治理机制

## MVP-1.1 学习理解验收

- [x] 理解 Intent 决定参数提取契约
- [x] 理解 UNKNOWN 必须在 SkillRegistry 前拦截
- [ ] 准确理解 Skill 与 Workflow 的层级关系
- [x] 基本理解 ToolResult 与 EvidenceBundle 区别
- [x] 准确理解 PARTIAL 与 UNCERTAIN
- [ ] 理解 Workflow 决定事实、ResponseGenerator 负责表达
- [ ] 理解 SkillExecutor 当前价值与未来治理职责

本轮验收记录见：`learning-review-mvp-1.1.md`。

## MVP-1.1 关键证据实验与验收

- [x] 关键 Tool 失败实验
- [x] 补充 Tool 失败实验
- [x] Tool 失败后继续收集无依赖证据
- [x] 关键证据缺失返回 `UNCERTAIN`
- [x] 补充证据缺失返回 `PARTIAL`
- [x] Tool `SUCCESS` 但 `data = null` 视为证据缺失
- [x] MVP-1.1 关键证据理解验收
- [x] 验证全局阶段为 `BUILD` 时，构建明细升级为关键证据，缺失返回 `UNCERTAIN`

实验结论：

1. Tool 调用失败不直接决定 `SkillResult`。
2. Workflow 根据证据的重要性、完整性和冲突决定结果状态。
3. 继续执行属于执行策略，是否能输出确定结论属于证据判断。
4. `query_directbus_status` 在当前 Pipeline Status Summary Skill 中属于关键证据。
5. Build / Test 明细的证据等级可能随当前阶段变化；已验证 `BUILD` 阶段的 Build 明细属于关键证据，其他阶段和询问范围仍待验证。

## Design Hypotheses

以下判断尚未经过代码或实验验证，不作为已确认规则：

- [ ] `DH-001`：`query_build_detail` 与 `query_test_detail` 是否可并行调用
- [ ] `DH-002`：状态枚举、`isHistory` 过滤等内容应归为确定性规则，而非 RAG Knowledge
- [ ] `DH-003`：`query_build_failure_analysis` 是否需要支持多个失败对象批量查询
- [ ] `DH-004`：`errorSummary` 与 `aiAnalysisResult` 的证据等级和冲突处理规则
- [ ] `DH-005`：已有分析结果不存在时，MVP 的降级路径
- [ ] `DH-009`：模板式 ResponseGenerator 是否足以跑通首个学习闭环
- [ ] `DH-010`：对比 Retry Skill 的高阶 Tool 封装模式与“Skill 预检查 + 用户确认 + Tool 最终强校验”模式；最终安全规则必须保留在 Tool / 后端
- [ ] `DH-011`：验证不同 Skill 是否应具有专属证据模型、业务不变量、冲突规则和降级策略；完成两个 Skill 后再评估公共 Evidence Rule 接口
- [ ] `DH-012`：Pipeline Status Summary 中 Build / Test 明细的证据等级是否应随全局阶段、用户询问范围和当前业务节点动态变化

## Skill 2：Build Failure Analysis Skill

- [x] Build Failure Analysis Skill 最小可实现设计
- [x] 默认 Tool 范围确认
- [x] `query_build_failure_analysis` 语义确认
- [x] 证据不足输出“不确定”
- [x] MVP Out of Scope 确认

### MVP-1.2 最小实现（等待 Review）

- [ ] 增加 `BUILD_FAILURE_DIAGNOSE` Intent 路由
- [ ] 实现 Build Failure Analysis Skill 与确定性 Workflow
- [ ] 实现已有分析结果的 Mock Tool 与固定 Fixture
- [ ] 验证当前 / 历史失败隔离
- [ ] 验证关键证据缺失、分析缺失和证据冲突
- [ ] 保持只读诊断边界，不实现重构建、豁免或合入操作

实现计划见：`../../docs/superpowers/plans/2026-07-20-build-failure-analysis-mvp-1.2.md`。

## Skill 3：Merge Block Diagnose Skill

状态：暂缓，不属于当前 Agent Core MVP。

- [ ] 明确业务目标
- [ ] 明确触发意图
- [ ] 明确输入参数
- [ ] 明确缺失参数处理
- [ ] 明确执行步骤
- [ ] 明确 Tool 候选
- [ ] 明确 Knowledge 需求
- [ ] 明确输出结构
- [ ] 明确异常与兜底
- [ ] 明确风险边界
- [ ] 明确后续 Evaluation 方向

## Sprint Review

- [ ] 检查 3 个 Skill 是否完整覆盖统一设计骨架
- [ ] 检查所有结论是否可追溯到 Phase 0 业务基线
- [ ] 检查是否误写 Skill 实现代码或 MCP schema
- [ ] 检查是否提前展开 RAG、Evaluation 或生产工程细节
- [ ] 检查风险边界和人机确认是否保持一致
- [ ] 创建 Sprint 1 `REVIEW.md`
- [ ] 创建 Sprint 1 `NEXT.md`

## 下一步唯一优先事项

继续对现有 Pipeline Status Summary MVP 进行单知识点学习验收；MVP-1.2 Build Failure Analysis Skill 完整实现保持暂停。

### 暂时不要

- 不引入公共规则接口
- 不引入规则引擎
- 不接 LLM
- 不接 MCP
- 不接 RAG
- 不实现 Retry Skill
- 未完成 ChatGPT 学习验收前不开始 MVP-1.2 代码实现
