# Sprint 1 Tasks：Agent Core / Skill & Tool Design

## Sprint Setup

- [ ] 确认 Sprint 1 只使用 Phase 0 已完成的业务基线
- [ ] 确认未知内部规则继续保留 TODO
- [ ] 确认本阶段不写代码、不写 MCP schema
- [ ] 确认本阶段不展开完整 RAG、Evaluation 或生产工程化实现

## Agent Core Minimum Design Loop

- [ ] 建立与 3 个优先 Skill 相关的顶层 Intent 路由关系，其余 Intent 暂不展开
- [ ] 明确 Skill 与 Tool 的职责边界
- [ ] 说明固定 Workflow 与动态 Agent 的取舍
- [ ] 统一缺失参数识别与追问方式
- [ ] 统一 Tool 调用失败的异常与兜底方式
- [ ] 统一 Agent 结构化输出骨架
- [ ] 统一风险分级、人机确认和转人工边界

## Skill 1：Pipeline Status Summary Skill

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

## Skill 2：Build Failure Analysis Skill

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

## Skill 3：Merge Block Diagnose Skill

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
