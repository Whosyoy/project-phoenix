package com.projectphoenix.agentcore.buildfailure.model;

import com.projectphoenix.agentcore.buildfailure.evidence.EvidenceBundle;

/**
 * 表示 Build Failure Workflow 的最终业务结果。
 *
 * @param status 证据完整性与可靠性状态
 * @param message 基于有效证据形成的最小结论
 * @param evidenceBundle Workflow 整理后的证据集合
 * @author Rory
 * @since 2026-07-21
 */
public record SkillResult(Status status, String message, EvidenceBundle evidenceBundle) {
    /**
     * 当前 MVP 支持的业务结果状态。
     */
    public enum Status {
        SUCCESS,
        PARTIAL,
        UNCERTAIN
    }
}
