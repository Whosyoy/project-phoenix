package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.BuildDetailData;
import java.util.List;

public interface BuildDetailTool {
    ToolResult<List<BuildDetailData>> query(String applyBusId);
}
