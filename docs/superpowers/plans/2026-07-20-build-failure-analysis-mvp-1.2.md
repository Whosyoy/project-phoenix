# Build Failure Analysis MVP-1.2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

> **Status:** 暂停。未完成 ChatGPT 学习验收并通过 Review 前，不执行本计划。

**Goal:** 在现有 Agent Core Demo 中增加 Build Failure Analysis Skill 的纯 Java、框架无关、只读诊断最小闭环，同时保持 Pipeline Status Summary 回归测试通过。

**Architecture:** 在现有 `pipeline-status-summary-mvp` 源树中增量加入第二个 Intent、Skill、确定性 Workflow 和 Mock Tool，复用 `AgentCore`、参数抽取、`ToolResult`、`EvidenceBundle`、`SkillResult` 与模板回答生成器。Build Failure 专属的关键证据、冲突和降级规则只放在 `BuildFailureAnalysisWorkflow`，不抽取公共规则接口。

**Tech Stack:** Java 17、Java Record、JDK 自带编译器与断言；无第三方依赖。

## Global Constraints

- 纯 Java 17，不引入 Spring Boot、Spring AI、Ragent、LangChain4j 或其他 Agent 框架。
- 不接真实 LLM、MCP、网络或公司接口，只使用固定 Fixture。
- 只实现 Build Failure Analysis 的只读诊断，不执行重构建、豁免、提测、合入或其他副作用操作。
- 不引入公共 Evidence Rule 接口、规则引擎、规则 DSL、RAG、Retry Skill 或第三个 Skill。
- `Intent.UNKNOWN` 和缺少 `apply_bus_id` 继续在 `SkillRegistry` 前拦截。
- Workflow 决定事实、证据冲突和 `SkillResult`；ResponseGenerator 只表达已有结果。
- 当前失败只使用 `history = false`；历史失败不能替代当前失败。
- `query_build_failure_analysis` 表示消费构建侧已有分析，不表示本地 Agent 独立完成根因分析。
- 保留现有目录名称，MVP-1.2 Review 后再决定是否重命名为通用 Agent Core Demo。

---

## Approach Decision

采用“在现有 Demo 中增量加入第二个 Skill”的方案：

- 能真实验证双 Intent 路由和显式 Skill Registry。
- 能直接复用已经验证的 Evidence 与 Response 边界。
- 不复制 Agent Core 基础类型。
- 不产生与学习目标无关的目录迁移。

不采用独立 Demo，因为会复制 Router、Registry、Evidence 和 Response 代码；不在本阶段重命名目录，因为会制造大量无业务价值的路径 diff。

## Planned File List

### Modify

- `05_Demos/Agent/pipeline-status-summary-mvp/README.md`：补充双 Skill 调用链、Build Failure Fixture 和测试说明。
- `05_Demos/Agent/pipeline-status-summary-mvp/run-tests.sh`：依次运行 Pipeline Status 与 Build Failure 两个测试入口。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/AgentCore.java`：将 `UNKNOWN` 澄清提示改为同时覆盖状态查询与构建失败诊断；不改变编排职责。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/model/Intent.java`：新增 `BUILD_FAILURE_DIAGNOSE`，保留 `UNKNOWN` 控制值。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/router/RuleBasedIntentRouter.java`：增加受限失败诊断规则和副作用操作拦截。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/SkillRegistry.java`：支持显式注册两个 Skill，不增加扫描或动态发现。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/payload/BuildDetailData.java`：补充失败阶段、模块、错误摘要和重构建次数，并保留状态汇总所需的简化构造器。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/FixtureData.java`：新增 Build Failure 固定场景。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockBuildDetailTool.java`：按 Fixture 返回当前失败、历史失败、字段缺失或 Tool 失败。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockDirectBusStatusTool.java`：为 Build Failure Fixture 返回确定的构建阶段状态。

### Create

- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/BuildFailureAnalysisTool.java`：已有分析结果查询契约。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/payload/BuildFailureAnalysisData.java`：已有分析及关联对象的类型化 Payload。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockBuildFailureAnalysisTool.java`：正常、缺失和关联冲突 Fixture。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/BuildFailureAnalysisSkill.java`：绑定 `BUILD_FAILURE_DIAGNOSE` 并委托 Workflow。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/workflow/BuildFailureAnalysisWorkflow.java`：固定 Tool 调用、当前失败过滤、证据组合与状态判断。
- `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java`：Build Failure 独立验收入口。

### Explicitly Unchanged

- `RuleBasedParameterExtractor.java`
- `SkillExecutor.java`
- `EvidenceBundle.java`
- `SkillResult.java`
- `ResponseGenerator.java`
- `TemplateResponseGenerator.java`
- `PipelineStatusSummaryWorkflow.java`

Build Failure 测试入口最终固定为 8 个测试：2 个路由 / 边界测试，加 6 个端到端证据场景。Mock Tool 与 Fixture 的断言并入对应端到端场景，不额外增加测试计数。

## Build Failure Evidence Rules

仅在 `BuildFailureAnalysisWorkflow` 中实现以下优先级：

1. `query_build_detail` 失败或无数据：`FAILED`，因为无法完成定位当前失败这一核心目标。
2. 当前失败的 `failureStage`、`failureModule` 或 `errorSummary` 缺失：`UNCERTAIN`。
3. 已有分析无法关联当前项目 / 基线，或与结构化证据冲突：`UNCERTAIN`。
4. `query_directbus_status` 或 `query_build_failure_analysis` 失败，但当前结构化失败证据完整：`PARTIAL`。
5. 完整查询确认没有当前失败：`SUCCESS`，不得使用历史失败替代。
6. 证据完整且一致：`SUCCESS`。

Workflow 仍应在无依赖时继续收集后续证据，并把所有 Tool 执行记录、缺失项和可用事实写入同一个 `EvidenceBundle`。

---

### Task 1: Add the second Intent and explicit registry support

**Files:**
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/AgentCore.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/model/Intent.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/router/RuleBasedIntentRouter.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/SkillRegistry.java`
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java`

**Interfaces:**
- Consumes: `IntentRouter.route(UserRequest)`、`Skill.supportedIntent()`。
- Produces: `Intent.BUILD_FAILURE_DIAGNOSE`；支持一个或两个显式 Skill 的 `SkillRegistry(Skill... skills)`。

- [ ] **Step 1: Write failing routing and registry tests**

在新测试入口中先加入以下断言：

```java
private void testRoutesBuildFailureIntent() {
    Intent intent = new RuleBasedIntentRouter().route(
            request("分析构建失败原因 apply_bus_id=BUS-BUILD-FAILURE"));
    check(intent == Intent.BUILD_FAILURE_DIAGNOSE, "build failure intent");
    passed++;
}

private void testRejectsRebuildAction() {
    Intent intent = new RuleBasedIntentRouter().route(
            request("帮我执行重构建 apply_bus_id=BUS-BUILD-FAILURE"));
    check(intent == Intent.UNKNOWN, "rebuild action remains unsupported");
    passed++;
}
```

- [ ] **Step 2: Run the tests and verify the compile failure**

Run:

```bash
PATH=/opt/homebrew/Cellar/openjdk@17/17.0.19/libexec/openjdk.jdk/Contents/Home/bin:$PATH \
  ./05_Demos/Agent/pipeline-status-summary-mvp/run-tests.sh
```

Expected: compilation fails because `BUILD_FAILURE_DIAGNOSE` and多 Skill Registry 支持尚不存在。

- [ ] **Step 3: Add the Intent and deterministic router rules**

Add the enum value without changing existing values:

```java
public enum Intent {
    PIPELINE_STATUS_QUERY,
    BUILD_FAILURE_DIAGNOSE,
    UNKNOWN
}
```

Implement three rule groups in `RuleBasedIntentRouter`:

```java
private static final List<String> BUILD_FAILURE_TERMS = List.of(
        "失败原因", "哪里失败", "哪个项目", "ai分析", "ai 分析", "重构建过", "上一次失败");
private static final List<String> SIDE_EFFECT_TERMS = List.of(
        "执行重构建", "发起重构建", "立即重构建", "帮我重构建");

@Override
public Intent route(UserRequest request) {
    String text = request.userText() == null ? "" : request.userText().toLowerCase();
    if (SIDE_EFFECT_TERMS.stream().anyMatch(text::contains)) {
        return Intent.UNKNOWN;
    }
    boolean status = STATUS_TERMS.stream().anyMatch(text::contains);
    boolean failure = BUILD_FAILURE_TERMS.stream().anyMatch(text::contains);
    if (status == failure) {
        return Intent.UNKNOWN;
    }
    return failure ? Intent.BUILD_FAILURE_DIAGNOSE : Intent.PIPELINE_STATUS_QUERY;
}
```

- [ ] **Step 4: Support explicit multi-Skill registration**

Use an `EnumMap` and reject duplicate Intent registrations:

```java
public SkillRegistry(Skill... registeredSkills) {
    EnumMap<Intent, Skill> mutableSkills = new EnumMap<>(Intent.class);
    for (Skill skill : registeredSkills) {
        Skill previous = mutableSkills.put(skill.supportedIntent(), skill);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate skill for " + skill.supportedIntent());
        }
    }
    this.skills = Map.copyOf(mutableSkills);
}
```

Do not add reflection, annotations, classpath scanning or plugin discovery.

- [ ] **Step 5: Generalize the UNKNOWN clarification copy**

只修改 `AgentCore` 的控制分支提示，使其明确支持“流水线状态查询”与“当前构建失败诊断”两类请求。不得在 `AgentCore` 中增加业务事实判断，也不得改变 `UNKNOWN` 在 Registry 前拦截的顺序。

- [ ] **Step 6: Run existing and new routing tests**

Expected: Pipeline Status 的 7 个既有测试继续通过；新路由测试通过。

- [ ] **Step 7: Commit the routing slice**

```bash
git add 05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/AgentCore.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/model/Intent.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/router/RuleBasedIntentRouter.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/SkillRegistry.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java
git commit -m "feat(agent-core): 增加构建失败诊断路由"
```

---

### Task 2: Add typed build-failure Tool contracts and fixtures

**Files:**
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/payload/BuildDetailData.java`
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/payload/BuildFailureAnalysisData.java`
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/BuildFailureAnalysisTool.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/FixtureData.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockBuildDetailTool.java`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockDirectBusStatusTool.java`
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool/mock/MockBuildFailureAnalysisTool.java`
- Test: `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java`

**Interfaces:**
- Consumes: `ToolResult<T>` and `BuildDetailTool.query(String)`.
- Produces: enriched `BuildDetailData` and `BuildFailureAnalysisTool.query(String, String, String)`.

- [ ] **Step 1: Write failing Mock Tool tests**

Cover these fixed IDs:

```text
BUS-BUILD-FAILURE
BUS-BUILD-HISTORY-ONLY
BUS-BUILD-DETAIL-FAILURE
BUS-BUILD-STRUCTURED-MISSING
BUS-BUILD-ANALYSIS-MISSING
BUS-BUILD-ANALYSIS-CONFLICT
```

Assert that normal data includes one current failure and one history failure, history-only contains no current record, detail failure returns `FAILURE`, analysis missing returns `FAILURE`, and analysis conflict returns a mismatched baseline.

- [ ] **Step 2: Run tests and verify they fail before the contracts exist**

Expected: compilation fails for missing `BuildFailureAnalysisTool`, `BuildFailureAnalysisData` and enriched failure fields.

- [ ] **Step 3: Enrich BuildDetailData while preserving Pipeline Status construction**

Use the following record shape and convenience constructor:

```java
public record BuildDetailData(
        String projectName,
        String baselineName,
        String status,
        boolean history,
        String failureStage,
        String failureModule,
        String errorSummary,
        int rebuildCount) {

    public BuildDetailData(String projectName, String baselineName, String status, boolean history) {
        this(projectName, baselineName, status, history, null, null, null, 0);
    }
}
```

- [ ] **Step 4: Add the existing-analysis payload and Tool contract**

```java
public record BuildFailureAnalysisData(
        String projectName,
        String baselineName,
        String analysisResult) {
}
```

```java
public interface BuildFailureAnalysisTool {
    ToolResult<BuildFailureAnalysisData> query(
            String applyBusId,
            String projectName,
            String baselineName);
}
```

- [ ] **Step 5: Implement fixed Mock Tool behavior**

For every Build Failure Fixture, return deterministic data only. A current failed detail uses:

```java
new BuildDetailData(
        "phoenix-api",
        "main",
        "FAILED",
        false,
        "COMPILE",
        "agent-core",
        "cannot find symbol",
        1)
```

The history record uses the same project / baseline with `history = true`. The structured-missing scenario sets `errorSummary = null`. The analysis-conflict scenario returns `baselineName = "release"` while the current failure uses `baselineName = "main"`.

- [ ] **Step 6: Run Tool tests and the existing Pipeline Status suite**

Expected: Mock Tool tests pass and existing Pipeline Status tests remain `PASS: 7/7 tests`.

- [ ] **Step 7: Commit the Tool slice**

```bash
git add 05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/tool \
  05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java
git commit -m "feat(agent-core): 增加构建失败证据 Fixture"
```

---

### Task 3: Implement BuildFailureAnalysisWorkflow and Skill

**Files:**
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/workflow/BuildFailureAnalysisWorkflow.java`
- Create: `05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/BuildFailureAnalysisSkill.java`
- Test: `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java`

**Interfaces:**
- Consumes: `DirectBusStatusTool`、`BuildDetailTool`、`BuildFailureAnalysisTool`、`ExecutionContext`。
- Produces: `SkillResult` with a Build Failure-specific `EvidenceBundle`.

- [ ] **Step 1: Add failing Workflow acceptance tests**

Add these scenarios to the Build Failure test entry:

```text
normal current failure            -> SUCCESS
history-only failure              -> SUCCESS, no current failure, analysis Tool not called
build detail Tool failure         -> FAILED, global status evidence retained
structured failure field missing  -> UNCERTAIN
analysis Tool failure             -> PARTIAL, structured failure evidence retained
analysis association mismatch     -> UNCERTAIN
```

For each scenario assert `SkillResult.status`, `missingEvidence`, `conflicts`, Tool execution records, current/history isolation, and retained evidence.

- [ ] **Step 2: Run tests and verify they fail because Workflow and Skill are absent**

Expected: compilation fails for `BuildFailureAnalysisWorkflow` and `BuildFailureAnalysisSkill`.

- [ ] **Step 3: Implement the minimal Skill**

```java
public final class BuildFailureAnalysisSkill implements Skill {
    private final BuildFailureAnalysisWorkflow workflow;

    public BuildFailureAnalysisSkill(BuildFailureAnalysisWorkflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public Intent supportedIntent() {
        return Intent.BUILD_FAILURE_DIAGNOSE;
    }

    @Override
    public SkillResult execute(ExecutionContext context) {
        return workflow.execute(context);
    }
}
```

- [ ] **Step 4: Implement the fixed Workflow sequence**

Implement this exact order inside `execute`:

```text
query_directbus_status
→ query_build_detail
→ split current/history
→ locate current FAILED records
→ query_build_failure_analysis once per current failure in stable list order
→ compare analysis project/baseline with current failure
→ build EvidenceBundle
→ resolve SkillResult status using Build Failure-specific rules
```

Do not stop after `query_directbus_status` failure because build detail is independent. Stop analysis queries when build detail is unavailable or when no current failure exists.

Use explicit local predicates rather than a shared policy interface:

```java
private static boolean isMissing(ToolResult<?> result) {
    return result.status() != ToolResult.Status.SUCCESS || result.data() == null;
}

private static boolean missingStructuredFailureEvidence(BuildDetailData detail) {
    return detail.failureStage() == null
            || detail.failureModule() == null
            || detail.errorSummary() == null;
}
```

Resolve status in this order:

```java
if (isMissing(builds)) {
    resultStatus = SkillResult.Status.FAILED;
} else if (!conflicts.isEmpty() || structuredEvidenceMissing) {
    resultStatus = SkillResult.Status.UNCERTAIN;
} else if (statusMissing || analysisMissing) {
    resultStatus = SkillResult.Status.PARTIAL;
} else {
    resultStatus = SkillResult.Status.SUCCESS;
}
```

Add Evidence Items with explicit fact names such as `currentBuildFailure`, `failureStage`, `failureModule`, `errorSummary`, `rebuildCount`, `aiAnalysisResult` and `historicalBuildFailure`. Never copy an analysis result into evidence unless its project and baseline match the current failure.

- [ ] **Step 5: Run Build Failure tests**

Expected: every scenario in the new test main passes, including history isolation and conflict handling.

- [ ] **Step 6: Run the complete test script**

Expected output contains both suites:

```text
PASS: 7/7 tests
PASS: 8/8 tests
```

- [ ] **Step 7: Commit the Skill and Workflow slice**

```bash
git add 05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/skill/BuildFailureAnalysisSkill.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/main/java/com/projectphoenix/agentcore/workflow/BuildFailureAnalysisWorkflow.java \
  05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java
git commit -m "feat(agent-core): 实现构建失败分析最小闭环"
```

---

### Task 4: Wire both Skills, run regression, and document the MVP

**Files:**
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/run-tests.sh`
- Modify: `05_Demos/Agent/pipeline-status-summary-mvp/README.md`
- Test: `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/AgentCoreMvpTest.java`
- Test: `05_Demos/Agent/pipeline-status-summary-mvp/src/test/java/com/projectphoenix/agentcore/BuildFailureAnalysisMvpTest.java`

**Interfaces:**
- Consumes: both Skill implementations and the explicit `SkillRegistry`.
- Produces: one test command that compiles once and executes both independent suites.

- [ ] **Step 1: Assemble both Skills in Build Failure acceptance tests**

Construct `AgentCore` with both registered Skills. Keep object construction explicit; do not add dependency injection or a factory framework.

- [ ] **Step 2: Update run-tests.sh**

After compilation, execute both test mains:

```sh
java -ea -cp "$OUT/classes" com.projectphoenix.agentcore.AgentCoreMvpTest
java -ea -cp "$OUT/classes" com.projectphoenix.agentcore.BuildFailureAnalysisMvpTest
```

- [ ] **Step 3: Update README**

Document:

- the two supported Intents and Skills;
- Build Failure fixed Tool sequence;
- current/history isolation;
- existing-analysis provenance boundary;
- Build Failure-specific `SUCCESS` / `PARTIAL` / `UNCERTAIN` / `FAILED` meanings;
- all Fixture IDs and expected states;
- explicit out-of-scope list.

- [ ] **Step 4: Run all verification commands**

```bash
git diff --check
PATH=/opt/homebrew/Cellar/openjdk@17/17.0.19/libexec/openjdk.jdk/Contents/Home/bin:$PATH \
  ./05_Demos/Agent/pipeline-status-summary-mvp/run-tests.sh
```

Expected:

```text
PASS: 7/7 tests
PASS: 8/8 tests
```

- [ ] **Step 5: Verify scope mechanically**

```bash
rg -n "Spring|LangChain|Ragent|MCP client|Drools|EvidenceRule|RetrySkill" \
  05_Demos/Agent/pipeline-status-summary-mvp/src
```

Expected: no framework, rule-engine, public evidence-rule abstraction or Retry Skill implementation appears in source.

- [ ] **Step 6: Commit final wiring and documentation**

```bash
git add 05_Demos/Agent/pipeline-status-summary-mvp/run-tests.sh \
  05_Demos/Agent/pipeline-status-summary-mvp/README.md \
  05_Demos/Agent/pipeline-status-summary-mvp/src
git commit -m "test(agent-core): 完成构建失败分析回归验证"
```

## Review Gate

Review 通过前不执行本计划。Review 需要确认：

- 是否接受在现有 Demo 目录中增量加入第二个 Skill，而不立即重命名目录。
- Build Failure 的关键证据和状态优先级是否合理。
- `query_build_failure_analysis` 使用项目 + 基线进行本地 Fixture 关联是否足够，且未被误写为已确认公司接口字段。
- 已有分析缺失返回 `PARTIAL`、结构化关键字段缺失返回 `UNCERTAIN` 是否符合学习目标。
- 测试场景是否足以验证当前 / 历史隔离、证据保留和 Tool 调用策略。
- 文件清单是否保持最小职责且没有引入公共规则抽象。
