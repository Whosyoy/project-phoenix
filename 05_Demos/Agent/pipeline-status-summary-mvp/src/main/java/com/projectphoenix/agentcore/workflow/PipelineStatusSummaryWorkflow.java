package com.projectphoenix.agentcore.workflow;

import com.projectphoenix.agentcore.model.EvidenceBundle;
import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.tool.BuildDetailTool;
import com.projectphoenix.agentcore.tool.DirectBusStatusTool;
import com.projectphoenix.agentcore.tool.TestDetailTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.BuildDetailData;
import com.projectphoenix.agentcore.tool.payload.DirectBusStatusData;
import com.projectphoenix.agentcore.tool.payload.TestDetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * 流水线状态汇总的确定性 Workflow。
 *
 * <p>该 Workflow 固定调用全局状态、构建明细和测试明细 Tool，过滤历史记录，
 * 聚合结构化证据并识别全局状态与当前明细之间的冲突。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class PipelineStatusSummaryWorkflow {
    private static final String SKILL_NAME = "PipelineStatusSummarySkill";
    private final DirectBusStatusTool statusTool;
    private final BuildDetailTool buildTool;
    private final TestDetailTool testTool;

    /**
     * 创建确定性状态汇总 Workflow。
     *
     * @param statusTool 全局状态查询 Tool
     * @param buildTool 构建明细查询 Tool
     * @param testTool 测试明细查询 Tool
     */
    public PipelineStatusSummaryWorkflow(
            DirectBusStatusTool statusTool,
            BuildDetailTool buildTool,
            TestDetailTool testTool) {
        this.statusTool = statusTool;
        this.buildTool = buildTool;
        this.testTool = testTool;
    }

    /**
     * 执行固定 Tool 调用、当前记录过滤、冲突检查和证据聚合。
     *
     * @param context 已完成参数校验的执行上下文
     * @return 带完整证据链的 Skill 结果
     */
    public SkillResult execute(ExecutionContext context) {
        String id = context.applyBusId();
        ToolResult<DirectBusStatusData> status = statusTool.query(id);
        ToolResult<List<BuildDetailData>> builds = buildTool.query(id);
        ToolResult<List<TestDetailData>> tests = testTool.query(id);

        List<EvidenceBundle.EvidenceItem> evidence = new ArrayList<>();
        List<EvidenceBundle.ToolExecution> executions = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();

        recordExecution(status, executions, missing);
        recordExecution(builds, executions, missing);
        recordExecution(tests, executions, missing);

        if (status.data() != null) {
            add(evidence, "isRunning", String.valueOf(status.data().running()), status.toolName(), "isRunning");
            add(evidence, "stageStatus", stageName(status.data().stageStatus()), status.toolName(), "buildStageStatus");
            add(evidence, "nodeStatus", status.data().nodeStatus(), status.toolName(), "buildNodeStatus");
        }
        currentBuilds(builds).forEach(item -> add(
                evidence,
                "buildStatus",
                item.projectName() + "/" + item.baselineName() + "=" + item.status(),
                builds.toolName(),
                "buildStatus"));
        currentTests(tests).forEach(item -> add(
                evidence,
                "testStatus",
                item.projectName() + "/" + item.baselineName() + "=" + item.status(),
                tests.toolName(),
                "testStatus"));

        if (status.data() != null && !status.data().running() && hasActiveDetail(builds, tests)) {
            conflicts.add("全局流程已结束，但当前明细仍存在 RUNNING 状态");
        }

        boolean uncertain = !conflicts.isEmpty();
        EvidenceBundle bundle = new EvidenceBundle(
                context.request().requestId(),
                context.intent(),
                SKILL_NAME,
                id,
                evidence,
                executions,
                missing,
                conflicts,
                uncertain);
        SkillResult.Status resultStatus = uncertain ? SkillResult.Status.UNCERTAIN
                : missing.isEmpty() ? SkillResult.Status.SUCCESS : SkillResult.Status.PARTIAL;
        return new SkillResult(resultStatus, bundle, List.of());
    }

    private static void add(
            List<EvidenceBundle.EvidenceItem> target,
            String name,
            String value,
            String tool,
            String field) {
        target.add(new EvidenceBundle.EvidenceItem(name, value, tool, field));
    }

    private static void recordExecution(
            ToolResult<?> result,
            List<EvidenceBundle.ToolExecution> executions,
            List<String> missing) {
        executions.add(new EvidenceBundle.ToolExecution(
                result.toolName(),
                result.status().name(),
                result.errorMessage()));
        if (result.status() != ToolResult.Status.SUCCESS) {
            missing.add(result.toolName() + ": " + result.errorMessage());
        }
    }

    private static List<BuildDetailData> currentBuilds(ToolResult<List<BuildDetailData>> result) {
        return result.data() == null ? List.of() : result.data().stream().filter(item -> !item.history()).toList();
    }

    private static List<TestDetailData> currentTests(ToolResult<List<TestDetailData>> result) {
        return result.data() == null ? List.of() : result.data().stream().filter(item -> !item.history()).toList();
    }

    private static boolean hasActiveDetail(
            ToolResult<List<BuildDetailData>> builds,
            ToolResult<List<TestDetailData>> tests) {
        return currentBuilds(builds).stream().anyMatch(item -> "RUNNING".equals(item.status()))
                || currentTests(tests).stream().anyMatch(item -> "RUNNING".equals(item.status()));
    }

    private static String stageName(int stage) {
        return switch (stage) {
            case 1 -> "CONFLICT_CHECK";
            case 2 -> "BUILD";
            case 3 -> "TEST";
            case 4 -> "MERGE";
            default -> "UNKNOWN(" + stage + ")";
        };
    }
}
