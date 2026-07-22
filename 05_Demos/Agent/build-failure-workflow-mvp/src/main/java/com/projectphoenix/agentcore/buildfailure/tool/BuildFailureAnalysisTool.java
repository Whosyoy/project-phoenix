package com.projectphoenix.agentcore.buildfailure.tool;

import com.projectphoenix.agentcore.buildfailure.tool.payload.BuildFailureAnalysisData;

/**
 * 查询已有构建失败分析的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-21
 */
public interface BuildFailureAnalysisTool {
    /**
     * 查询指定构建对象的已有失败分析。
     *
     * @param buildId 构建对象标识
     * @return 原始 Tool 结果
     */
    ToolResult<BuildFailureAnalysisData> query(String buildId);
}
