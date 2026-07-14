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

## Design Hypotheses

以下判断尚未经过代码或实验验证，不作为已确认规则：

- [ ] `DH-001`：`query_build_detail` 与 `query_test_detail` 是否可并行调用
- [ ] `DH-002`：状态枚举、`isHistory` 过滤等内容应归为确定性规则，而非 RAG Knowledge
- [ ] `DH-003`：`query_build_failure_analysis` 是否需要支持多个失败对象批量查询
- [ ] `DH-004`：`errorSummary` 与 `aiAnalysisResult` 的证据等级和冲突处理规则
- [ ] `DH-005`：已有分析结果不存在时，MVP 的降级路径

## Skill 2：Build Failure Analysis Skill

- [x] Build Failure Analysis Skill 最小可实现设计
- [x] 默认 Tool 范围确认
- [x] `query_build_failure_analysis` 语义确认
- [x] 证据不足输出“不确定”
- [x] MVP Out of Scope 确认

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

完成 Agent Core MVP 的技术设计与最小实现。

### MVP 仅包含

- `PIPELINE_STATUS_QUERY`
- `BUILD_FAILURE_DIAGNOSE`
- Pipeline Status Summary Skill
- Build Failure Analysis Skill
- 模拟 Tool
- 结构化证据
- 自然语言解释

### 暂不实现

- 第三个 Skill
- 完整 MCP
- RAG
- Evaluation
- 生产工程化
