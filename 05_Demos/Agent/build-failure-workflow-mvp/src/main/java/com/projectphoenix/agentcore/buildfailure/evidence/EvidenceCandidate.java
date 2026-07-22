package com.projectphoenix.agentcore.buildfailure.evidence;

/**
 * 表示等待业务关联校验的原始证据候选。
 *
 * @param sourceTool 候选数据来源 Tool
 * @param rawData Tool 返回的原始类型化数据
 * @author Rory
 * @since 2026-07-21
 */
public record EvidenceCandidate(String sourceTool, Object rawData) {
}
