package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.TestDetailData;

import java.util.List;

/**
 * 查询测试明细的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface TestDetailTool {
    /**
     * 查询指定流水线的测试明细。
     *
     * @param applyBusId 流水线唯一标识
     * @return 包含当前和历史测试明细的 Tool 结果
     */
    ToolResult<List<TestDetailData>> query(String applyBusId);
}
