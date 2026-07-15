package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.BuildDetailTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.BuildDetailData;
import java.util.List;

public final class MockBuildDetailTool implements BuildDetailTool {
    @Override
    public ToolResult<List<BuildDetailData>> query(String id) {
        return ToolResult.success("query_build_detail", List.of(
                new BuildDetailData("phoenix-api", "main", "SUCCESS", false),
                new BuildDetailData("phoenix-api", "main", "FAILED", true)));
    }
}
