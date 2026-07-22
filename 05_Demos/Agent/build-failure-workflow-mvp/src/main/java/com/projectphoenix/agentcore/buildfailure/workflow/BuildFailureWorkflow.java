package com.projectphoenix.agentcore.buildfailure.workflow;

import com.projectphoenix.agentcore.buildfailure.evidence.Evidence;
import com.projectphoenix.agentcore.buildfailure.evidence.EvidenceBundle;
import com.projectphoenix.agentcore.buildfailure.evidence.EvidenceCandidate;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 构建失败分析的确定性最小 Workflow。
 *
 * <p>该 Workflow 负责 Tool 调用、Evidence Candidate 转换、关联校验、
 * EvidenceBundle 组装和 Build Failure 专属 Policy 调用。</p>
 *
 * @author Rory
 * @since 2026-07-21
 */
public final class BuildFailureWorkflow {
    private static final String STATUS_TOOL = "query_directbus_status";
    private static final String BUILD_TOOL = "query_build_detail";
    private static final String ANALYSIS_TOOL = "query_build_failure_analysis";

    private final DirectBusStatusTool statusTool;
    private final BuildDetailTool buildTool;
    private final BuildFailureAnalysisTool analysisTool;
    private final BuildFailureEvidencePolicy evidencePolicy;

    /**
     * 创建 Build Failure Workflow。
     *
     * @param statusTool DirectBus 上下文 Tool
     * @param buildTool 当前构建 Tool
     * @param analysisTool 已有失败分析 Tool
     * @param evidencePolicy Build Failure 专属证据策略
     */
    public BuildFailureWorkflow(
            DirectBusStatusTool statusTool,
            BuildDetailTool buildTool,
            BuildFailureAnalysisTool analysisTool,
            BuildFailureEvidencePolicy evidencePolicy) {
        this.statusTool = statusTool;
        this.buildTool = buildTool;
        this.analysisTool = analysisTool;
        this.evidencePolicy = evidencePolicy;
    }

    /**
     * 执行 ToolResult 到 SkillResult 的完整证据链。
     *
     * @param request Workflow 输入
     * @return 带 EvidenceBundle 的业务结果
     */
    public SkillResult handle(BuildFailureRequest request) {
        ToolResult<DirectBusStatusData> statusResult = statusTool.query(request.applyBusId());
        ToolResult<BuildDetailData> buildResult = buildTool.query(request.applyBusId());

        List<EvidenceCandidate> candidates = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();

        collectCandidate(STATUS_TOOL, statusResult, candidates, missing, conflicts);
        collectCandidate(BUILD_TOOL, buildResult, candidates, missing, conflicts);

        BuildDetailData buildDetail = usableFailedBuild(buildResult, request.applyBusId());
        if (buildDetail != null) {
            ToolResult<BuildFailureAnalysisData> analysisResult = analysisTool.query(buildDetail.buildId());
            collectCandidate(ANALYSIS_TOOL, analysisResult, candidates, missing, conflicts);
        } else {
            missing.add(ANALYSIS_TOOL + ": skipped because current failed build is unavailable");
        }

        List<Evidence> evidences = candidates.stream()
                .map(candidate -> validate(candidate, request, buildDetail, conflicts, missing))
                .toList();

        EvidenceBundle bundle = new EvidenceBundle(evidences, missing, conflicts);
        SkillResult.Status status = evidencePolicy.evaluate(bundle);
        return new SkillResult(status, message(status, bundle), bundle);
    }

    private static void collectCandidate(
            String sourceTool,
            ToolResult<?> result,
            List<EvidenceCandidate> candidates,
            List<String> missing,
            List<String> conflicts) {
        if (!sourceTool.equals(result.toolName())) {
            conflicts.add(sourceTool + " returned unexpected toolName: " + result.toolName());
        }
        if (!result.success()) {
            missing.add(sourceTool + ": " + result.errorMessage());
        } else if (result.data() == null) {
            missing.add(sourceTool + ": no data");
        } else {
            candidates.add(new EvidenceCandidate(sourceTool, result.data()));
        }
    }

    private static BuildDetailData usableFailedBuild(
            ToolResult<BuildDetailData> result,
            String applyBusId) {
        if (!result.success() || result.data() == null) {
            return null;
        }
        BuildDetailData data = result.data();
        return Objects.equals(applyBusId, data.applyBusId())
                && "FAILED".equals(data.status())
                && !isBlank(data.buildId()) ? data : null;
    }

    private static Evidence validate(
            EvidenceCandidate candidate,
            BuildFailureRequest request,
            BuildDetailData buildDetail,
            List<String> conflicts,
            List<String> missing) {
        if (candidate.rawData() instanceof DirectBusStatusData status) {
            return validateStatus(candidate, request, status, conflicts, missing);
        }
        if (candidate.rawData() instanceof BuildDetailData build) {
            return validateBuild(candidate, request, build, conflicts, missing);
        }
        if (candidate.rawData() instanceof BuildFailureAnalysisData analysis) {
            return validateAnalysis(candidate, buildDetail, analysis, conflicts, missing);
        }
        return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "unsupported evidence type");
    }

    private static Evidence validateStatus(
            EvidenceCandidate candidate,
            BuildFailureRequest request,
            DirectBusStatusData status,
            List<String> conflicts,
            List<String> missing) {
        if (!Objects.equals(request.applyBusId(), status.applyBusId())) {
            conflicts.add(STATUS_TOOL + " applyBusId does not match request");
            return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "applyBusId mismatch");
        }
        if (isBlank(status.stage()) || isBlank(status.status())) {
            if (isBlank(status.stage())) {
                missing.add(STATUS_TOOL + ".stage");
            }
            if (isBlank(status.status())) {
                missing.add(STATUS_TOOL + ".status");
            }
            return new Evidence(
                    candidate.sourceTool(),
                    candidate.rawData(),
                    false,
                    "stage or status is missing");
        }
        return new Evidence(candidate.sourceTool(), candidate.rawData(), true, "DirectBus context confirmed");
    }

    private static Evidence validateBuild(
            EvidenceCandidate candidate,
            BuildFailureRequest request,
            BuildDetailData build,
            List<String> conflicts,
            List<String> missing) {
        if (!Objects.equals(request.applyBusId(), build.applyBusId())) {
            conflicts.add(BUILD_TOOL + " applyBusId does not match request");
            return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "applyBusId mismatch");
        }
        if (isBlank(build.buildId())) {
            missing.add(BUILD_TOOL + ".buildId");
            return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "buildId is missing");
        }
        boolean valid = "FAILED".equals(build.status());
        return new Evidence(
                candidate.sourceTool(),
                candidate.rawData(),
                valid,
                valid ? "current failed build confirmed" : "build status is not FAILED");
    }

    private static Evidence validateAnalysis(
            EvidenceCandidate candidate,
            BuildDetailData buildDetail,
            BuildFailureAnalysisData analysis,
            List<String> conflicts,
            List<String> missing) {
        if (buildDetail == null || !Objects.equals(buildDetail.buildId(), analysis.buildId())) {
            conflicts.add(ANALYSIS_TOOL + " buildId does not match current build");
            return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "buildId mismatch");
        }
        if (isBlank(analysis.reason())) {
            missing.add(ANALYSIS_TOOL + ".reason");
            return new Evidence(candidate.sourceTool(), candidate.rawData(), false, "reason is missing");
        }
        return new Evidence(candidate.sourceTool(), candidate.rawData(), true, "buildId matched");
    }

    private static String message(SkillResult.Status status, EvidenceBundle bundle) {
        BuildDetailData build = validData(bundle.evidences(), BuildDetailData.class);
        BuildFailureAnalysisData analysis = validData(bundle.evidences(), BuildFailureAnalysisData.class);
        if (status == SkillResult.Status.SUCCESS && build != null && analysis != null) {
            return "构建 " + build.buildId() + " 失败，原因：" + analysis.reason();
        }
        if (status == SkillResult.Status.PARTIAL && build != null) {
            return "已确认构建 " + build.buildId() + " 失败，但失败原因或流水线上下文证据不完整";
        }
        return "关键证据不足或相互冲突，无法可靠判断当前构建失败原因";
    }

    private static <T> T validData(List<Evidence> evidences, Class<T> type) {
        return evidences.stream()
                .filter(Evidence::valid)
                .map(Evidence::rawData)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
