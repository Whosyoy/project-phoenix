package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.DirectBusStatusTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.DirectBusStatusData;

/**
 * 使用固定 Fixture 模拟 DirectBus 全局状态查询。
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class MockDirectBusStatusTool implements DirectBusStatusTool {
    @Override
    public ToolResult<DirectBusStatusData> query(String id) {
        if (FixtureData.CRITICAL_MISSING.equals(id)) {
            return ToolResult.failure("query_directbus_status", "fixture: directbus status unavailable");
        }
        if (FixtureData.CONFLICT.equals(id)) {
            return ToolResult.success("query_directbus_status", new DirectBusStatusData(id, false, 4, "COMPLETED"));
        }
        return ToolResult.success("query_directbus_status", new DirectBusStatusData(id, true, 3, "TESTING"));
    }
}
