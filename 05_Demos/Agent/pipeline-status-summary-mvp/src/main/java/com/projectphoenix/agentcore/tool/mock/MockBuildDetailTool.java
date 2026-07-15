package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.BuildDetailTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.BuildDetailData;

import java.util.List;

/**
 * 使用固定 Fixture 模拟构建明细查询。
 *
 * <p>返回一条当前成功记录和一条历史失败记录，用于验证历史记录过滤。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class MockBuildDetailTool implements BuildDetailTool {
    @Override
    public ToolResult<List<BuildDetailData>> query(String id) {
        return ToolResult.success("query_build_detail", List.of(
                new BuildDetailData("phoenix-api", "main", "SUCCESS", false),
                new BuildDetailData("phoenix-api", "main", "FAILED", true)));
    }
}
