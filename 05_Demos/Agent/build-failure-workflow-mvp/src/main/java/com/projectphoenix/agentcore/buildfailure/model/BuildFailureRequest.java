package com.projectphoenix.agentcore.buildfailure.model;

/**
 * 表示 Build Failure Workflow 的最小输入。
 *
 * @param applyBusId 需要分析的直通车标识
 * @author Rory
 * @since 2026-07-21
 */
public record BuildFailureRequest(String applyBusId) {
}
