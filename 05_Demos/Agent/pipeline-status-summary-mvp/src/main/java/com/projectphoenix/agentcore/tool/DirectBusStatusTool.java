package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.DirectBusStatusData;

public interface DirectBusStatusTool {
    ToolResult<DirectBusStatusData> query(String applyBusId);
}
