package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.TestDetailTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.TestDetailData;
import java.util.List;

public final class MockTestDetailTool implements TestDetailTool {
    @Override
    public ToolResult<List<TestDetailData>> query(String id) {
        if (FixtureData.PARTIAL.equals(id)) {
            return ToolResult.failure("query_test_detail", "fixture: test detail unavailable");
        }
        String status = FixtureData.CONFLICT.equals(id) ? "RUNNING" : "RUNNING";
        return ToolResult.success("query_test_detail", List.of(
                new TestDetailData("phoenix-api", "main", status, false)));
    }
}
