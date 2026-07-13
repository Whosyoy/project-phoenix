# Phase 0：DirectBus Business Baseline

## Goal

梳理 DirectBus / CI-CD 真实业务流程，明确哪些问题适合由 Agent 支撑，形成后续正式 Sprint 的业务输入。

## Principles

- Business First: 所有学习和实践必须映射到 DirectBus / CI-CD 真实业务场景。
- Engineering First: 先理解企业级业务边界，再考虑 Demo 或框架实现。
- Understand Before Coding: Phase 0 不写业务代码，优先沉淀业务资产。
- Architecture Before Framework: 暂不绑定 Spring AI、Ragent、LangGraph 或其他框架。
- Output Every Week: Phase 0 形成可展示的业务流程、场景拆解、问题样本和映射表。

## Scope

### In Scope

- DirectBus 业务流程梳理
- Agent 适用场景拆解
- 用户问题样本设计
- Intent / Tool / Skill / Knowledge / Evaluation 映射
- Phase 0 任务清单与验收标准

### Out of Scope

- Agent Core 代码实现
- MCP Tool 真实接入
- RAG 知识库实现
- 自动评测框架实现
- 生产环境权限、审计、灰度的完整实现

## Deliverables

| Deliverable | Path | Status |
| --- | --- | --- |
| DirectBus 业务流程 | `02_DirectBus/01_Business/directbus-business-flow.md` | 完成 |
| DirectBus 豁免流程 | `02_DirectBus/01_Business/directbus-exemption-flow.md` | 完成 |
| Agent 场景拆解 | `02_DirectBus/02_Agent/agent-scenario-breakdown.md` | 完成 |
| 用户问题样本 | `02_DirectBus/02_Agent/user-question-samples.md` | 完成 |
| Intent / Tool / Skill / Knowledge / Evaluation 映射 | `02_DirectBus/02_Agent/intent-tool-skill-knowledge-evaluation-map.md` | 完成 |
| Phase 0 复盘 | `02_DirectBus/00_Baseline/REVIEW.md` | 完成 |
| 下一阶段衔接 | `02_DirectBus/00_Baseline/NEXT.md` | 完成 |

## Task Checklist

- [x] 梳理 DirectBus 主流程
- [x] 标记关键系统边界：Gerrit、Jenkins、O 测、门禁、合入流水线
- [x] 标记已确认的关键状态，未知状态继续保留 TODO
- [x] 标记已确认的关键异常，未知规则继续保留 TODO
- [x] 区分查询类、诊断类、解释类、操作类、风险类问题
- [x] 设计第一批 30 条用户问题样本
- [x] 建立 Intent / Tool / Skill / Knowledge / Evaluation 初版映射
- [x] 完成 Phase 0 复盘与 Sprint 1 Agent Core 阶段衔接

## Acceptance Criteria

- 能用一张流程或一段结构化说明讲清 DirectBus 主链路。
- 能说明哪些 DirectBus 场景适合 Agent，哪些不适合。
- 每类核心场景至少有一批用户问题样本。
- 每个核心 Intent 都能映射到候选 Tool、Skill、Knowledge 和 Evaluation 方式。
- 所有未知业务细节都显式标记为 TODO，不用猜测填充。

## Review Questions

- Phase 0 的已确认结论、Agent 边界和剩余 TODO 见 `REVIEW.md`。
- Sprint 1 优先设计的 Agent Core 能力和范围边界见 `NEXT.md`。
