package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.TestDetailData;
import java.util.List;

public interface TestDetailTool {
    ToolResult<List<TestDetailData>> query(String applyBusId);
}
