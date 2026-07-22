package com.projectphoenix.agentcore.buildfailure.evidence;

import java.util.List;

/**
 * 表示 Workflow 整理后的事实、缺失证据和冲突证据。
 *
 * <p>该对象只保存 Evidence，不直接保存 ToolResult。</p>
 *
 * @param evidences 已完成关联校验的证据
 * @param missingEvidences 缺失的证据说明
 * @param conflicts 相互冲突的证据说明
 * @author Rory
 * @since 2026-07-21
 */
public record EvidenceBundle(
        List<Evidence> evidences,
        List<String> missingEvidences,
        List<String> conflicts) {
    /**
     * 固化不可变证据集合。
     */
    public EvidenceBundle {
        evidences = List.copyOf(evidences);
        missingEvidences = List.copyOf(missingEvidences);
        conflicts = List.copyOf(conflicts);
    }
}
