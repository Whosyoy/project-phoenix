package com.projectphoenix.agentcore.buildfailure.workflow;

import com.projectphoenix.agentcore.buildfailure.model.BuildFailureRequest;
import com.projectphoenix.agentcore.buildfailure.model.SkillResult;
import com.projectphoenix.agentcore.buildfailure.policy.BuildFailureEvidencePolicy;
import com.projectphoenix.agentcore.buildfailure.tool.BuildDetailTool;
import com.projectphoenix.agentcore.buildfailure.tool.BuildFailureAnalysisTool;
import com.projectphoenix.agentcore.buildfailure.tool.DirectBusStatusTool;
import com.projectphoenix.agentcore.buildfailure.tool.ToolResult;
import com.projectphoenix.agentcore.buildfailure.tool.payload.BuildDetailData;
import com.projectphoenix.agentcore.buildfailure.tool.payload.BuildFailureAnalysisData;
import com.projectphoenix.agentcore.buildfailure.tool.payload.DirectBusStatusData;

/**
 * 覆盖 Build Failure Workflow 的三个最小证据场景。
 *
 * <p>测试直接注入固定 Tool Fixture，不依赖第三方测试或 Mock 框架。</p>
 *
 * @author Rory
 * @since 2026-07-21
 */
public final class BuildFailureWorkflowTest {
    private int passed;

    /**
     * 运行全部 Build Failure Workflow 测试。
     *
     * @param args 命令行参数，本测试不使用
     */
    public static void main(String[] args) {
        new BuildFailureWorkflowTest().run();
    }

    private void run() {
        testCompleteAndConsistentEvidenceIsSuccess();
        testMissingFailureAnalysisIsPartial();
        testMismatchedBuildIdIsUncertain();
        System.out.println("PASS: " + passed + "/3 tests");
    }

    /**
     * 验证构建与失败分析关联一致时返回完整结论。
     */
    private void testCompleteAndConsistentEvidenceIsSuccess() {
        String applyBusId = "BUS-BUILD-SUCCESS";
        int[] analysisCalls = {0};

        DirectBusStatusTool statusTool = ignored -> ToolResult.success(
                "query_directbus_status",
                new DirectBusStatusData(applyBusId, "BUILD", "FAILED"));
        BuildDetailTool buildTool = ignored -> ToolResult.success(
                "query_build_detail",
                new BuildDetailData(applyBusId, "100", "FAILED"));
        BuildFailureAnalysisTool analysisTool = buildId -> {
            check("100".equals(buildId), "analysis uses current buildId");
            analysisCalls[0]++;
            return ToolResult.success(
                    "query_build_failure_analysis",
                    new BuildFailureAnalysisData("100", "compile error"));
        };

        SkillResult result = workflow(statusTool, buildTool, analysisTool)
                .handle(new BuildFailureRequest(applyBusId));

        check(result.status() == SkillResult.Status.SUCCESS, "complete evidence status");
        check(result.evidenceBundle().missingEvidences().isEmpty(), "no missing evidence");
        check(result.evidenceBundle().conflicts().isEmpty(), "no evidence conflict");
        check(result.evidenceBundle().evidences().size() == 3, "all three evidences retained");
        check(hasValidEvidence(result, "query_build_detail"), "valid build evidence");
        check(hasValidEvidence(result, "query_build_failure_analysis"), "valid analysis evidence");
        check(analysisCalls[0] == 1, "analysis tool called once");
        check("100".equals(buildData(result).buildId()), "build evidence keeps buildId");
        check("100".equals(analysisData(result).buildId()), "analysis evidence keeps buildId");
        check("构建 100 失败，原因：compile error".equals(result.message()),
                "reason is expressed only from valid evidence");
        passed++;
    }

    /**
     * 验证已有分析无法关联当前构建时返回不确定结论。
     */
    private void testMismatchedBuildIdIsUncertain() {
        String applyBusId = "BUS-BUILD-UNCERTAIN";
        int[] analysisCalls = {0};

        DirectBusStatusTool statusTool = ignored -> ToolResult.success(
                "query_directbus_status",
                new DirectBusStatusData(applyBusId, "BUILD", "FAILED"));
        BuildDetailTool buildTool = ignored -> ToolResult.success(
                "query_build_detail",
                new BuildDetailData(applyBusId, "100", "FAILED"));
        BuildFailureAnalysisTool analysisTool = buildId -> {
            check("100".equals(buildId), "conflict analysis uses current buildId");
            analysisCalls[0]++;
            return ToolResult.success(
                    "query_build_failure_analysis",
                    new BuildFailureAnalysisData("99", "compile error"));
        };

        SkillResult result = workflow(statusTool, buildTool, analysisTool)
                .handle(new BuildFailureRequest(applyBusId));

        check(result.status() == SkillResult.Status.UNCERTAIN, "buildId conflict status");
        check(result.evidenceBundle().conflicts().stream()
                        .anyMatch(item -> item.contains("buildId")),
                "buildId conflict recorded");
        check(result.evidenceBundle().evidences().size() == 3, "conflict evidences retained");
        check(hasValidEvidence(result, "query_build_detail"), "current build evidence retained");
        check(hasInvalidEvidence(result, "query_build_failure_analysis"),
                "conflicting analysis evidence retained");
        check(analysisCalls[0] == 1, "conflicting analysis called once");
        check("100".equals(buildData(result).buildId()), "current buildId retained");
        check("99".equals(analysisData(result).buildId()), "conflicting buildId retained");
        check("buildId mismatch".equals(analysisValidationMessage(result)),
                "conflict validation is explicit");
        check("关键证据不足或相互冲突，无法可靠判断当前构建失败原因".equals(result.message()),
                "invalid reason is not expressed as fact");
        check(!result.message().contains("compile error"), "invalid analysis reason is excluded");
        passed++;
    }

    /**
     * 验证已定位失败构建但缺少原因证据时返回部分结论。
     */
    private void testMissingFailureAnalysisIsPartial() {
        String applyBusId = "BUS-BUILD-PARTIAL";
        int[] analysisCalls = {0};

        DirectBusStatusTool statusTool = ignored -> ToolResult.success(
                "query_directbus_status",
                new DirectBusStatusData(applyBusId, "BUILD", "FAILED"));
        BuildDetailTool buildTool = ignored -> ToolResult.success(
                "query_build_detail",
                new BuildDetailData(applyBusId, "100", "FAILED"));
        BuildFailureAnalysisTool analysisTool = buildId -> {
            check("100".equals(buildId), "partial analysis uses current buildId");
            analysisCalls[0]++;
            return ToolResult.failure(
                    "query_build_failure_analysis",
                    "fixture: analysis unavailable");
        };

        SkillResult result = workflow(statusTool, buildTool, analysisTool)
                .handle(new BuildFailureRequest(applyBusId));

        check(result.status() == SkillResult.Status.PARTIAL, "missing analysis status");
        check(result.evidenceBundle().missingEvidences().contains(
                        "query_build_failure_analysis: fixture: analysis unavailable"),
                "analysis error retained as missing evidence");
        check(result.evidenceBundle().evidences().size() == 2, "available evidences retained");
        check(hasValidEvidence(result, "query_build_detail"), "build evidence retained");
        check(analysisCalls[0] == 1, "failed analysis called once");
        check(result.evidenceBundle().conflicts().isEmpty(), "missing evidence is not conflict");
        check("100".equals(buildData(result).buildId()), "partial buildId retained");
        check("已确认构建 100 失败，但失败原因或流水线上下文证据不完整".equals(result.message()),
                "partial conclusion contains no invented reason");
        passed++;
    }

    private BuildFailureWorkflow workflow(
            DirectBusStatusTool statusTool,
            BuildDetailTool buildTool,
            BuildFailureAnalysisTool analysisTool) {
        return new BuildFailureWorkflow(
                statusTool,
                buildTool,
                analysisTool,
                new BuildFailureEvidencePolicy());
    }

    private boolean hasValidEvidence(SkillResult result, String sourceTool) {
        return result.evidenceBundle().evidences().stream()
                .anyMatch(item -> sourceTool.equals(item.sourceTool()) && item.valid());
    }

    private boolean hasInvalidEvidence(SkillResult result, String sourceTool) {
        return result.evidenceBundle().evidences().stream()
                .anyMatch(item -> sourceTool.equals(item.sourceTool()) && !item.valid());
    }

    private BuildDetailData buildData(SkillResult result) {
        return result.evidenceBundle().evidences().stream()
                .filter(item -> "query_build_detail".equals(item.sourceTool()))
                .map(item -> (BuildDetailData) item.rawData())
                .findFirst()
                .orElseThrow();
    }

    private BuildFailureAnalysisData analysisData(SkillResult result) {
        return result.evidenceBundle().evidences().stream()
                .filter(item -> "query_build_failure_analysis".equals(item.sourceTool()))
                .map(item -> (BuildFailureAnalysisData) item.rawData())
                .findFirst()
                .orElseThrow();
    }

    private String analysisValidationMessage(SkillResult result) {
        return result.evidenceBundle().evidences().stream()
                .filter(item -> "query_build_failure_analysis".equals(item.sourceTool()))
                .map(item -> item.validationMessage())
                .findFirst()
                .orElseThrow();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
