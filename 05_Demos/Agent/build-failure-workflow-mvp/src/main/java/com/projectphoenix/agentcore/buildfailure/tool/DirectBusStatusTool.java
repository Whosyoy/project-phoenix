package com.projectphoenix.agentcore.buildfailure.tool;

import com.projectphoenix.agentcore.buildfailure.tool.payload.DirectBusStatusData;

/**
 * 查询 DirectBus 当前上下文的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-21
 */
public interface DirectBusStatusTool {
    /**
     * 查询指定直通车的当前状态。
     *
     * @param applyBusId 直通车标识
     * @return 原始 Tool 结果
     */
    ToolResult<DirectBusStatusData> query(String applyBusId);
}
