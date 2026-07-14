# Sprint 1：Agent Core / Skill & Tool Design

## Status

In Progress

## Goal

完成 DirectBus Agent Core 的最小设计闭环，把 Phase 0 已确认的顶层 Intent、业务证据和 Agent 边界进一步收敛为可执行的 Skill 设计骨架。

当前 Sprint 只做 Markdown 设计，不写代码，不定义 MCP schema，不展开完整 RAG、Evaluation 或生产工程化实现。

## Business Baseline

Sprint 1 只基于 Phase 0 已完成的业务基线继续设计：

- DirectBus 主业务流程
- DirectBus 豁免流程
- Agent 场景拆解
- 30 条用户问题样本
- Intent / Tool / Skill / Knowledge / Evaluation 映射

未知内部规则继续保留 TODO，不在 Sprint 1 中推测补全。

## Scope

### In Scope

- Intent 到 Skill 的路由关系
- Skill 与 Tool 的职责边界
- 固定 Workflow 与动态 Agent 的取舍
- 缺失参数识别与追问
- Tool 调用失败时的异常与兜底设计
- Agent 结构化输出
- 风险边界与人机确认
- 3 个核心 Skill 的第一版设计骨架

### Out of Scope

- Skill 实现代码
- MCP schema 和真实系统接入
- 完整 RAG 方案与知识库实现
- 完整 Evaluation 框架、数据集和评测代码
- 生产级权限、审计、幂等、限流、灰度、监控和部署实现
- Phase 0 尚未确认的新业务规则

## Priority Skills

Sprint 1 只优先设计以下 3 个核心 Skill：

1. 流水线状态汇总技能（Pipeline Status Summary Skill）
2. 构建失败分析技能（Build Failure Analysis Skill）
3. 合入阻塞诊断技能（Merge Block Diagnose Skill）

## Skill Design Skeleton

每个核心 Skill 后续统一明确：

1. 业务目标
2. 触发意图
3. 输入参数
4. 缺失参数处理
5. 执行步骤
6. Tool 候选
7. Knowledge 需求
8. 输出结构
9. 异常与兜底
10. 风险边界
11. 后续 Evaluation 方向

## Sprint Deliverables

- Agent Core 最小设计说明
- Intent 到 Skill 路由关系
- Skill 与 Tool 边界说明
- 固定 Workflow 与动态 Agent 取舍说明
- 缺参追问、Tool 失败兜底和结构化输出约定
- 3 个核心 Skill 的第一版设计文档
- Sprint 1 复盘与下一阶段衔接文档

## Acceptance Criteria

- 3 个核心 Skill 都使用统一设计骨架。
- 每个 Skill 都能追溯到 Phase 0 已确认的顶层 Intent 和用户问题。
- 每个 Skill 都明确输入、输出、Tool、Knowledge、异常兜底和风险边界。
- 明确哪些步骤使用固定 Workflow，哪些位置允许动态 Agent 判断。
- 缺失参数时能够停止执行并进入追问设计。
- Tool 调用失败时具有明确的降级或转人工边界。
- 有副作用或高风险场景保留人机确认，不产生越权设计。
- 所有未知内部规则继续使用 TODO 标记。

## Task Entry

详细任务清单见：

- `01_Sprints/Sprint-01-Agent-Core/TASKS.md`
