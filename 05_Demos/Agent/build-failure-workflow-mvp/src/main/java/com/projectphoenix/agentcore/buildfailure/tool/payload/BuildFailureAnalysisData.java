package com.projectphoenix.agentcore.buildfailure.tool.payload;

/**
 * Build Failure Analysis Tool 的最小已有分析数据。
 *
 * @param buildId 已有分析对应的构建对象标识
 * @param reason 已有系统返回的失败原因
 * @author Rory
 * @since 2026-07-21
 */
public record BuildFailureAnalysisData(String buildId, String reason) {
}
