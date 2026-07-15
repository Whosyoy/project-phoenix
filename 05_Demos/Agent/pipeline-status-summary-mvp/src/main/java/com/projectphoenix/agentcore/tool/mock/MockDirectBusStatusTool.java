package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.DirectBusStatusTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.DirectBusStatusData;

public final class MockDirectBusStatusTool implements DirectBusStatusTool {
    @Override
    public ToolResult<DirectBusStatusData> query(String id) {
        if (FixtureData.CONFLICT.equals(id)) {
            return ToolResult.success("query_directbus_status", new DirectBusStatusData(id, false, 4, "COMPLETED"));
        }
        return ToolResult.success("query_directbus_status", new DirectBusStatusData(id, true, 3, "TESTING"));
    }
}
