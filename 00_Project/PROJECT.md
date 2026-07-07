# Project Phoenix

> 从 Java 平台工程师，到企业级 AI Agent 工程师
> 像工程师一样构建 AI，而不只是使用 Prompt。

---

## 1. 项目愿景

Project Phoenix 是一个围绕真实 DevOps / CI-CD 场景展开的企业级 AI Agent 学习、实践与沉淀项目。

这个项目不是为了简单学习某个 AI 框架，也不是为了做一个浅层 Demo，而是希望基于真实研发平台业务，把 Java 后端、研发效能、CI/CD、复杂状态机、异步编排等既有工程经验，迁移到 AI 应用开发和企业级 Agent 工程中。

最终目标是形成一套可以支撑 AI Native / AI 应用开发 / AI 平台研发岗位面试的完整项目资产。

---

## 2. 项目背景

我目前具备以下基础：

* 8 年 Java 后端研发经验
* Spring Boot、MyBatis、Redis、RocketMQ、MySQL 等后端工程能力
* 研发平台 / CI-CD / DevOps 相关业务经验
* Gerrit 提测、Jenkins 构建、O 测、门禁校验、合入流水线等复杂业务场景经验
* 多阶段状态机、异步编排、回调收敛、并发控制等复杂系统经验
* Cursor、Claude Code、Superpowers、OneSpec、AI README、MCP 等 AI Native 实践经验

当前转型方向不是算法工程师，而是：

* AI 应用开发工程师
* 大模型应用开发工程师
* AI Native 工程师
* Agent 工程化开发工程师
* AI 平台后端工程师

---

## 3. 核心目标

在 4 个月左右，完成一套企业级 DirectBus Agent 的系统化学习、设计、实践和面试资产沉淀。

核心目标包括：

1. 能完整讲清企业级 Agent 的生命周期，包括 Intent、Planner、Skill、Tool、Knowledge、Evaluation、Production Engineering。
2. 能基于真实 CI/CD 场景设计 DirectBus Agent，而不是停留在通用聊天机器人或简单 RAG Demo。
3. 能将 MCP / Tool Calling、RAG、知识库治理、评测体系、权限审计、幂等限流、灰度上线等能力映射到真实业务中。
4. 能沉淀架构图、设计文档、Demo、评测集、面试问答、简历项目描述等可复用资产。
5. 能开始投递 AI Native / AI 应用开发 / AI 平台研发相关岗位，并通过面试反馈持续迭代。

---

## 4. 项目定位

Project Phoenix 不是单纯的学习笔记仓库，而是一个 AI 工程成长仓。

它包含四类资产：

| 资产类型 | 内容                                         | 目标        |
| ---- | ------------------------------------------ | --------- |
| 架构资产 | 架构图、时序图、ADR、设计文档                           | 形成工程化设计能力 |
| 代码资产 | Demo、实验项目、Ragent 改造                        | 形成可展示实操能力 |
| 业务资产 | DirectBus 场景拆解、Tool / Skill / Knowledge 映射 | 形成真实业务壁垒  |
| 职业资产 | 简历、STAR、面试问答、项目话术                          | 支撑跳槽和面试   |

---

## 5. 核心项目：DirectBus Agent

DirectBus Agent 是本项目的核心业务载体。

它的目标是基于 Gerrit 合入流水线场景，设计一个面向研发的智能助手，支持通过自然语言完成：

* 合入状态查询
* 合入阻塞诊断
* 构建失败分析
* O 测失败分析
* 门禁规则解释
* 冲突检测解释
* 重测 / 重构建确认
* 历史失败案例检索
* 规则、SOP、FAQ、问题单知识检索

核心链路：

```text
用户问题
  -> 意图识别
  -> 参数抽取 / 澄清追问
  -> 任务规划
  -> 调用 MCP / Tool 获取实时状态
  -> 检索规则、SOP、历史案例等知识
  -> 生成带证据的诊断结论
  -> 对中高风险操作进行人机确认
  -> 执行动作或给出建议
  -> 记录日志并进入评测闭环
```

---

## 6. Core Principles

### Principle 1：Business First

所有学习都必须映射到真实业务场景。

学习 Agent，不只是理解 Planner，而是要回答：DirectBus 哪些场景真的需要 Planner？
学习 RAG，不只是理解向量检索，而是要回答：DirectBus 的规则、SOP、日志、历史案例应该如何组织和更新？

---

### Principle 2：Engineering First

目标不是做一个 Demo，而是按企业级工程方式设计 AI 应用。

每个能力都要考虑：

* 权限
* 审计
* 幂等
* 限流
* 降级
* 灰度
* 回滚
* 可观测性
* 安全边界

---

### Principle 3：Understand Before Coding

代码可以由 AI 辅助生成，但架构、边界、取舍必须自己理解。

重点不是“代码是不是自己敲的”，而是：

* 这个设计是不是自己的？
* 为什么这样拆 Skill？
* 为什么这样定义 Tool？
* 为什么这里需要 RAG？
* 为什么这里不能自动执行？
* 如果线上出错怎么兜底？

---

### Principle 4：Architecture Before Framework

先明确业务问题、数据流、边界和架构，再选择框架。

不要被 Spring AI、LangGraph、Ragent、OpenAI Agents SDK 等框架牵着走。框架只是实现方式，真正重要的是：

* 业务流程
* Agent 边界
* Tool 设计
* Knowledge 设计
* Evaluation 设计
* 工程化治理

---

### Principle 5：Output Every Week

每周必须沉淀至少一个可展示成果。

成果可以是：

* 一份设计文档
* 一张架构图
* 一个最小 Demo
* 一组评测样本
* 一份面试问答
* 一段项目介绍
* 一次复盘总结

没有产出，就不算真正完成学习。

---

## 7. Roadmap

### M1：Agent Core

目标：完成 DirectBus Agent 的核心链路设计。

重点能力：

* Intent
* Planner
* Skill
* Tool / MCP
* Prompt
* 风险分级
* 人机确认

核心产出：

* DirectBus 业务流程图
* Agent 总体架构图
* Intent 分类表
* Skill 设计文档
* MCP Tool 清单
* 30 秒 / 2 分钟项目介绍初稿

---

### M2：Knowledge / RAG

目标：完成 DirectBus 知识增强方案设计。

重点能力：

* Chunk
* Embedding
* Metadata
* Hybrid Search
* Rerank
* Query Rewrite
* Citation
* Knowledge Pipeline
* Version / Gray / Rollback

核心产出：

* DirectBus 知识库设计文档
* 文档切分策略
* 检索流程图
* 知识库热更新方案
* RAG 面试问答

---

### M3：Evaluation

目标：建立 Agent 和 RAG 的评测体系。

重点能力：

* Golden Dataset
* Intent Accuracy
* Tool Accuracy
* Retrieval Recall
* Answer Groundedness
* Hallucination Rate
* Safety Interception
* Regression Test

核心产出：

* 50 到 100 条评测样本
* 评测指标表
* 评测报告模板
* 自动评测 Demo
* Evaluation 面试问答

---

### M4：Production Engineering

目标：补齐企业级 AI 应用上线能力。

重点能力：

* 权限控制
* 审计日志
* 幂等设计
* 限流降级
* 超时重试
* Prompt 注入防护
* 数据脱敏
* 灰度上线
* 可观测性
* 成本控制

核心产出：

* 生产级工程化设计文档
* 高风险操作确认流程
* 权限与审计设计
* 灰度发布方案
* Production 面试问答

---

### M5：Interview Assets

目标：把项目能力转化为简历和面试竞争力。

核心产出：

* AI 应用版简历
* 30 秒自我介绍
* 2 分钟项目介绍
* 5 分钟项目深挖版本
* STAR 项目复盘
* 高频面试问答
* 技术取舍问答
* 面试复盘记录

---

## 8. Success Criteria

完成 Project Phoenix 后，应该达到以下标准：

* 能完整讲清 DirectBus Agent 的业务背景、技术架构、工程边界和落地价值。
* 能解释 Agent、MCP、RAG、Evaluation、Production Engineering 的完整生命周期。
* 能基于真实业务回答面试官的深挖问题，而不是背概念。
* 有可展示的 Demo、架构图、设计文档、评测样本和项目复盘。
* 简历第一屏能够体现 AI 应用开发定位，而不是传统 Java 后端。
* 能开始稳定投递 AI Native / AI 应用开发 / AI 平台研发相关岗位。
* 能通过面试反馈持续补齐短板并迭代项目材料。

---

## 9. 当前阶段

当前阶段：Sprint 1 - Business Understanding / 业务理解

当前重点：

* 梳理 DirectBus 业务流程
* 拆解适合 Agent 的业务场景
* 建立 Tool / Skill / Knowledge / Evaluation 映射
* 形成 DirectBus Agent 第一版业务地图

---

## 10. 下一里程碑

下一里程碑：完成 DirectBus Agent MVP 的业务与架构设计。

需要完成：

* DirectBus 业务流程图
* 10 到 20 个高频用户问题
* 意图分类表
* Tool / Skill / Knowledge 初步映射
* 风险分级表
* 第一版 Agent 总体架构图
* 第一版项目介绍话术
