package com.projectphoenix.agentcore.buildfailure.policy;

import com.projectphoenix.agentcore.buildfailure.evidence.EvidenceBundle;
import com.projectphoenix.agentcore.buildfailure.model.SkillResult;

/**
 * 将 Build Failure 专属证据状态映射为 SkillResult 状态。
 *
 * <p>该类不是公共规则接口，只服务当前 Workflow。</p>
 *
 * @author Rory
 * @since 2026-07-21
 */
public final class BuildFailureEvidencePolicy {
    private static final String STATUS_TOOL = "query_directbus_status";
    private static final String BUILD_TOOL = "query_build_detail";
    private static final String ANALYSIS_TOOL = "query_build_failure_analysis";

    /**
     * 根据当前证据集合判断业务结果状态。
     *
     * @param bundle 已整理的证据集合
     * @return 当前业务结果状态
     */
    public SkillResult.Status evaluate(EvidenceBundle bundle) {
        if (!bundle.conflicts().isEmpty()) {
            return SkillResult.Status.UNCERTAIN;
        }
        if (!hasValidEvidence(bundle, BUILD_TOOL)) {
            return SkillResult.Status.UNCERTAIN;
        }
        if (!hasValidEvidence(bundle, STATUS_TOOL)
                || !hasValidEvidence(bundle, ANALYSIS_TOOL)) {
            return SkillResult.Status.PARTIAL;
        }
        return SkillResult.Status.SUCCESS;
    }

    private static boolean hasValidEvidence(EvidenceBundle bundle, String sourceTool) {
        return bundle.evidences().stream()
                .anyMatch(item -> sourceTool.equals(item.sourceTool()) && item.valid());
    }
}
