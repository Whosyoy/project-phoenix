package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.DirectBusStatusData;

/**
 * 查询 DirectBus 全局状态的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface DirectBusStatusTool {
    /**
     * 查询指定流水线的全局状态。
     *
     * @param applyBusId 流水线唯一标识
     * @return 类型化 Tool 结果
     */
    ToolResult<DirectBusStatusData> query(String applyBusId);
}
