# 意图 / 工具 / 技能 / 知识 / 评测映射表（Intent / Tool / Skill / Knowledge / Evaluation Map）

## 文档目的（Purpose）

本文基于以下文档，将 30 条用户问题样本归并为 DirectBus 智能体（Agent）第一版意图 / 工具 / 技能 / 知识 / 评测（Intent / Tool / Skill / Knowledge / Evaluation）映射表：

- `02_DirectBus/01_Business/directbus-business-flow.md`
- `02_DirectBus/01_Business/directbus-exemption-flow.md`
- `02_DirectBus/02_Agent/agent-scenario-breakdown.md`
- `02_DirectBus/02_Agent/user-question-samples.md`

当前阶段只建立业务映射，不设计完整的模型上下文协议（MCP）、检索增强生成（RAG）或评测（Evaluation）实现：

```text
用户问题
  -> 顶层意图（Top-level Intent）
  -> 技能（Skill）
  -> 工具候选（Tool Candidate）
  -> 知识类型（Knowledge Type）
  -> 评测方向（Evaluation Direction）
```

## 映射原则（Mapping Principles）

- 顶层意图（Top-level Intent）表达用户要解决的核心业务问题，不为每条问题单独创建顶层意图。
- 30 条样本归并为 8 个顶层意图；样本中的细粒度差异由技能（Skill）和处理分支承接。
- 工具（Tool）只保留候选名称，不定义 MCP 接口模式（schema）、参数或返回结构。
- 知识（Knowledge）只保留知识类型，不展开 RAG 数据源、切分、索引或召回方案。
- 评测（Evaluation）只保留评测方向，不定义指标实现、数据集格式或评测代码。
- 诊断必须同时使用实时状态与规则证据；查询当前构建和测试结果时优先使用 `isHistory = 0` 的有效明细。
- 重测、重构建等有副作用操作必须先检查条件、权限和风险，并经过二次确认。
- 智能体（Agent）不得绕过提测准入或门禁，不得强制合入，不得自动批准豁免。
- 未确认的内部规则、权限、审批角色、可重试条件和豁免条件继续标记为 TODO。

## 顶层意图覆盖关系（Top-level Intent Coverage）

| 顶层意图（Top-level Intent） | 覆盖编号 | 样本数 | 核心目标 | 风险等级 |
| --- | --- | ---: | --- | --- |
| 提测前诊断（`PreSubmit Diagnose`） | UQ-001～UQ-004 | 4 | 诊断变更（change）无法提测的阻塞项 | 中风险 |
| 流水线进度查询（`Pipeline Progress Query`） | UQ-005～UQ-008 | 4 | 查询并解释流水线全局与项目明细进度 | 低风险 |
| 构建失败诊断（`Build Failure Diagnose`） | UQ-009～UQ-012 | 4 | 定位并解释当前构建失败及重构建轨迹 | 中风险 |
| 测试失败诊断（`Test Failure Diagnose`） | UQ-013～UQ-016 | 4 | 定位并解释 O 测失败、领域和重试状态 | 中风险 |
| 合入就绪诊断（`Merge Readiness Diagnose`） | UQ-017～UQ-020 | 4 | 核对进入合入阶段前仍缺少的条件 | 高风险 |
| 合入门禁诊断（`Merge Gate Diagnose`） | UQ-021～UQ-023 | 3 | 诊断合入前门禁阻塞及恢复路径 | 高风险 |
| 重试 / 重构建确认（`Retry / Rebuild Confirm`） | UQ-024～UQ-027 | 4 | 检查重测 / 重构建资格并执行安全确认 | 高风险 |
| 豁免咨询（`Exemption Advice`） | UQ-028～UQ-030 | 3 | 解释豁免入口、证据和人工审批边界 | 禁止自动执行 |
| **合计** | **UQ-001～UQ-030** | **30** | **完整覆盖第一版问题样本** | - |

## 意图映射（Intent Mapping）

### 1. 提测前诊断（PreSubmit Diagnose）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-001、UQ-002、UQ-003、UQ-004 |
| 典型用户问题 | “这个 change 为什么不能提测？”；“是不是责任田主还没审批？”；“commit message 具体还差什么？”；“为什么没有匹配到需要验证的基线和项目？” |
| 适合的技能（Skill） | 提测前检查诊断技能（`PreCheck Diagnose Skill`）；提测就绪说明技能（`Submit Readiness Explain Skill`） |
| 可能调用的工具（Tool）候选 | 查询变更（change）状态（`query_change_status`）；查询评审分数（`query_review_score`）；查询提测前检查（`query_pre_submit_check`）；查询影响面分析（`query_impact_analysis`）；查询最小集配置（`query_minimal_set_config`）；查询豁免规则（`query_exemption_policy`） |
| 需要的知识（Knowledge）类型 | 提测准入规则；责任田主审批规则；上下游分支依赖规则；提交信息（commit message）规则；影响面分析规则；最小集配置说明；底版本使用规则 TODO；可豁免 / 不可豁免规则 TODO |
| 风险等级 | 中风险 |
| 智能体（Agent）边界 | 可以查询所有提测前检查项，基于证据说明阻塞原因和待补条件，并提示是否存在豁免入口；不能根据用户猜测直接下结论，不能绕过准入、伪造审批、编造需求 ID 或自动批准豁免；影响范围未知时停止推断并提示人工确认。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；并存阻塞项召回；Gerrit / 准入 / 影响面 / 最小集工具选择；实时证据与规则引用；影响分析、配置和底版本问题区分；硬拦截与豁免入口区分；无证据推断和越权建议拦截。 |

### 2. 流水线进度查询（Pipeline Progress Query）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-005、UQ-006、UQ-007、UQ-008 |
| 典型用户问题 | “我的直通车现在到哪一步了？”；“为什么已经显示测试中，还有项目在构建？”；“还有哪些基线没跑完？”；“构建失败了为什么 `isRunning` 还是 true？” |
| 适合的技能（Skill） | 流水线状态汇总技能（`Pipeline Status Summary Skill`）；全局与明细状态说明技能（`Global Detail Status Explain Skill`） |
| 可能调用的工具（Tool）候选 | 查询 DirectBus 状态（`query_directbus_status`）；查询构建明细（`query_build_detail`）；查询测试明细（`query_test_detail`） |
| 需要的知识（Knowledge）类型 | `buildStageStatus` 状态语义；`buildNodeStatus` 状态语义；`isRunning` 生命周期语义；全局状态与项目明细流转关系；构建与测试状态语义；`isHistory` 当前 / 历史记录规则；失败后的可处理路径 TODO |
| 风险等级 | 低风险 |
| 智能体（Agent）边界 | 可以汇总当前阶段、节点、项目和基线进度，解释并行流转以及失败后流水线是否仍可处理；必须结合 `isHistory = 0` 的构建和测试明细，不能只看全局字段，不能把失败等同于生命周期已经结束，也不能虚构当前正在等待的人工动作。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；阶段、节点和 `isRunning` 解释；`isHistory = 0` 过滤；全局与明细并行状态一致性；项目 / 基线粒度未完成项召回；进度查询与过度诊断区分；失败状态与流程结束区分。 |

### 3. 构建失败诊断（Build Failure Diagnose）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-009、UQ-010、UQ-011、UQ-012 |
| 典型用户问题 | “为什么一直是构建失败？”；“哪个项目、哪个基线没过？”；“构建侧 AI 分析结果的主要问题是什么？”；“重构建后为什么还是上一次失败？” |
| 适合的技能（Skill） | 构建失败分析技能（`Build Failure Analysis Skill`）；构建进度汇总技能（`Build Progress Summary Skill`） |
| 可能调用的工具（Tool）候选 | 查询 DirectBus 状态（`query_directbus_status`）；查询构建明细（`query_build_detail`）；查询构建失败分析（`query_build_failure_analysis`）；查询最小集配置（`query_minimal_set_config`）；查询底版本（`query_version_base`） |
| 需要的知识（Knowledge）类型 | 构建状态语义；当前 / 历史构建明细规则；重构建记录版本规则；构建侧 AI 分析结果使用边界；构建失败处理规则 TODO；重构建规则 TODO；底版本和构建参数说明 TODO；构建失败常见问题（FAQ）TODO；历史失败案例 TODO |
| 风险等级 | 中风险 |
| 智能体（Agent）边界 | 可以定位当前失败项目、基线、阶段和模块，汇总错误证据，并按时间解释重构建轨迹；构建侧 AI 分析只能作为辅助证据，必须与结构化字段交叉核对；不能无证据归因、把历史失败当当前失败、承诺一定可重构建或建议跳过构建直接合入。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；当前失败记录定位；项目 / 基线 / 阶段 / 模块抽取；`isHistory` 过滤和重构建轨迹还原；AI 分析与结构化证据一致性；证据不足时的降级；越权重构建或豁免建议拦截。 |

### 4. 测试失败诊断（Test Failure Diagnose）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-013、UQ-014、UQ-015、UQ-016 |
| 典型用户问题 | “这次 O 测为什么失败？”；“是哪个领域没过？”；“这个错误码会自动重试吗，已经重试几次？”；“一个项目失败了，为什么其他项目还在跑？” |
| 适合的技能（Skill） | 测试失败分析技能（`Test Failure Analysis Skill`）；领域测试汇总技能（`Domain Test Summary Skill`） |
| 可能调用的工具（Tool）候选 | 查询测试明细（`query_test_detail`）；查询测试报告（`query_test_report`）；查询重试规则（`query_retry_policy`）；查询构建明细（`query_build_detail`） |
| 需要的知识（Knowledge）类型 | 六大领域定义；O 测标准作业流程（SOP）；O 测状态语义；O 测错误码说明 TODO；错误码自动重试规则；自动重试最多 3 次；全局失败与其他项目继续执行规则；`isHistory` 当前 / 历史记录规则；手动重测规则 TODO；历史失败案例 TODO |
| 风险等级 | 中风险 |
| 智能体（Agent）边界 | 可以定位当前失败项目、基线、领域、用例和错误码，引用报告解释失败，并说明自动重试命中情况和并行测试状态；不能受用户二选一诱导而漏掉其他失败领域，不能猜测代码或环境原因，不能承诺重试成功、忽略测试失败或直接建议合入。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；失败领域和用例召回；项目 / 基线 / `testId` 关联；错误码规则匹配与重试次数计算；自动重试和手动重测区分；`isHistory` 过滤；全局失败与并行执行解释；无证据根因推断拦截。 |

### 5. 合入就绪诊断（Merge Readiness Diagnose）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-017、UQ-018、UQ-019、UQ-020 |
| 典型用户问题 | “构建和测试都结束了，为什么还没有进入合入？”；“还有哪些项目没满足正常合入条件？”；“是不是失败项要走豁免？”；“为什么 `buildStageStatus` 还没变成 4？” |
| 适合的技能（Skill） | 合入阻塞诊断技能（`Merge Block Diagnose Skill`）；合入就绪诊断技能（`Merge Readiness Diagnose Skill`） |
| 可能调用的工具（Tool）候选 | 查询 DirectBus 状态（`query_directbus_status`）；查询构建明细（`query_build_detail`）；查询测试明细（`query_test_detail`）；查询豁免状态（`query_exemption_status`）；查询合入门禁（`query_merge_gate`） |
| 需要的知识（Knowledge）类型 | 正常合入条件；豁免合入条件；项目 / 基线构建与测试汇总规则；`isHistory` 当前 / 历史记录规则；阶段状态语义；豁免规则和审批角色 TODO；门禁前置规则 TODO |
| 风险等级 | 高风险 |
| 智能体（Agent）边界 | 可以核对全部当前验证项，按项目和基线列出进入合入前的缺口，并区分正常路径、豁免路径、仍在执行和验证失败；只能做条件核对，不能把页面表象或单个全局字段当作可合入结论，不能认定失败项可豁免、替代门禁决策、自动批准豁免或强制合入。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；全部当前明细覆盖；正常合入条件判断；构建 / 测试缺口分类；阶段 4 前置条件解释；正常路径与豁免路径区分；豁免状态核验；条件核对与合入批准边界。 |

### 6. 合入门禁诊断（Merge Gate Diagnose）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-021、UQ-022、UQ-023 |
| 典型用户问题 | “合入前门禁为什么没过？”；“PatchSet 变了为什么不能沿用构建和 O 测结果？”；“分支锁释放后还能继续合入吗？” |
| 适合的技能（Skill） | 合入门禁诊断技能（`Merge Gate Diagnose Skill`）；合入前守卫说明技能（`Pre Merge Guard Explain Skill`） |
| 可能调用的工具（Tool）候选 | 查询变更（change）状态（`query_change_status`）；查询合入门禁（`query_merge_gate`）；查询分支锁（`query_branch_lock`）；查询 DirectBus 状态（`query_directbus_status`）；查询合入任务状态（`query_merge_job_status`） |
| 需要的知识（Knowledge）类型 | 门禁规则；分支锁处理规则；补丁集（PatchSet）变化导致验证结果失效规则；评审（Review）分数复查规则；重新提测规则；人工重试合入规则；其他门禁规则 TODO；合入失败处理规则 TODO |
| 风险等级 | 高风险 |
| 智能体（Agent）边界 | 可以逐项说明门禁失败项、实时值、规则依据和恢复路径；补丁集（PatchSet）变化时必须说明旧验证结果失效并要求重新提测，分支锁释放后仍需复查门禁并由人工重试合入；不能复用失效结果、自动触发合入、承诺一定可合或绕过门禁。 |
| 后续评测（Evaluation）方向 | 顶层意图识别；分支锁、补丁集（PatchSet）变化、评审（Review）分数和其他门禁项区分；门禁 / Gerrit 工具选择；规则引用；重新提测与人工重试合入路径区分；未知门禁项 TODO 保留；绕过门禁和自动合入建议拦截。 |

### 7. 重试 / 重构建确认（Retry / Rebuild Confirm）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-024、UQ-025、UQ-026、UQ-027 |
| 典型用户问题 | “这个 O 测失败还能不能重测？”；“构建失败可以重构建吗？”；“自动重试三次后还能手动重测吗？”；“帮我把失败的测试重新跑一下。” |
| 适合的技能（Skill） | 重测确认技能（`Retest Confirm Skill`）；重构建确认技能（`Rebuild Confirm Skill`）；安全操作检查技能（`Safe Action Check Skill`） |
| 可能调用的工具（Tool）候选 | 查询 DirectBus 状态（`query_directbus_status`）；查询构建明细（`query_build_detail`）；查询测试明细（`query_test_detail`）；查询重试规则（`query_retry_policy`）；查询用户权限（`query_user_permission`）；触发重构建（`trigger_rebuild`）；触发重测（`trigger_retest`） |
| 需要的知识（Knowledge）类型 | 自动重试规则；自动重试最多 3 次；手动重测规则 TODO；重构建规则和次数限制 TODO；可重试失败类型 TODO；权限规则 TODO；操作审计要求 TODO；二次确认规则 |
| 风险等级 | 高风险 |
| 智能体（Agent）边界 | 可以检查当前失败记录、重试轨迹、已知资格条件和操作范围，生成建议并展示待执行对象；任何触发动作前都必须校验权限并取得二次确认。规则、权限、对象或风险不明确时必须停止并转人工；不能无确认、越权或基于历史记录触发重测 / 重构建。 |
| 后续评测（Evaluation）方向 | 顶层意图与副作用操作识别；必要对象和参数完整性；当前记录与重试次数检查；自动重试与手动重测区分；资格规则应用；权限未知时停止；操作范围回显；二次确认完成度；无确认工具调用拦截。 |

### 8. 豁免咨询（Exemption Advice）

| 字段 | 映射 |
| --- | --- |
| 覆盖的用户问题编号 | UQ-028、UQ-029、UQ-030 |
| 典型用户问题 | “构建失败能不能申请豁免？”；“O 测失败走豁免要准备哪些证据？”；“门禁不过能不能豁免合入，应该找谁审批？” |
| 适合的技能（Skill） | 豁免咨询技能（`Exemption Advice Skill`）；豁免证据汇总技能（`Exemption Evidence Summary Skill`）；豁免申请草稿技能（`Exemption Draft Skill`） |
| 可能调用的工具（Tool）候选 | 查询豁免规则（`query_exemption_policy`）；查询豁免状态（`query_exemption_status`）；查询 DirectBus 状态（`query_directbus_status`）；查询构建明细（`query_build_detail`）；查询测试明细（`query_test_detail`）；查询合入门禁（`query_merge_gate`） |
| 需要的知识（Knowledge）类型 | 豁免入口类型；豁免规则 TODO；不可豁免边界；豁免证据要求 TODO；审批角色 TODO；风险等级 TODO；审计要求 TODO；补丁集（PatchSet）变化不可默认豁免规则；未知影响范围 / 底版本不可用风险 |
| 风险等级 | 禁止自动执行 |
| 智能体（Agent）边界 | 可以解释是否存在已定义的豁免入口，汇总构建、O 测或门禁失败证据，列出材料清单并生成申请草稿 TODO；不能认定某失败项必然可豁免、编造审批角色、自动发起或批准豁免、绕过审批或强制合入。规则或入口未确认时明确标记 TODO 并转人工。 |
| 后续评测（Evaluation）方向 | 顶层意图和豁免类型识别；已定义入口与未知规则区分；可豁免与不可默认豁免边界；失败证据完整性；审批角色 TODO 保留；申请草稿与审批结果区分；自动申请、自动批准和越权合入拦截。 |

## 通用评测方向（Shared Evaluation Directions）

| 评测方向 | 核心问题 |
| --- | --- |
| 意图（Intent）归并准确性 | 是否将 30 条问题稳定路由到 8 个顶层意图，而不是为表达差异创建新顶层意图？ |
| 技能（Skill）选择准确性 | 是否选择能完成查询、汇总、解释、诊断或安全确认的正确技能组合？ |
| 工具（Tool）选择准确性 | 是否选择覆盖所需实时证据的候选工具，且避免无关查询或高风险误调用？ |
| 当前记录准确性 | 查询现状时是否优先使用 `isHistory = 0`，分析轨迹时才结合历史明细？ |
| 证据可追溯性（Evidence Groundedness） | 结论是否能关联到实时状态、失败字段、报告或规则依据？ |
| 知识（Knowledge）适用性 | 是否使用正确知识类型，并将未知内部规则保留为 TODO？ |
| 多阻塞项完整性 | 是否识别并存的阻塞、失败项目、失败领域或未完成验证项？ |
| 状态解释一致性 | 是否正确解释全局状态、项目明细、并行执行和 `isRunning` 生命周期？ |
| 安全合规性（Safety Compliance） | 是否遵守二次确认、权限检查、人工审批、禁止绕过门禁和禁止自动批准豁免等边界？ |
| 可执行帮助度（Actionable Helpfulness） | 是否在不越权的前提下给出清晰、可执行的补齐、复查、重试或转人工建议？ |

## 待确认项清单（TODO Register）

- 提测分数要求、其他 commit message 格式规则、底版本使用规则和可豁免准入规则 TODO。
- 构建失败处理规则、可重构建失败类型、重构建次数限制和权限规则 TODO。
- O 测错误码说明、手动重测条件、手动重测次数限制和权限规则 TODO。
- 豁免规则、申请入口、证据要求、审批角色、有效期和审计要求 TODO。
- 除分支锁、PatchSet 和 Review 分数外的完整门禁规则 TODO。
- 合入 job 失败分类、人工重试合入条件和权限规则 TODO。
- 工具（Tool）候选的真实系统名、可用能力和接口边界 TODO。
- 第一版评测黄金数据集（Evaluation Golden Dataset）的规模、样本格式和通过标准 TODO。
