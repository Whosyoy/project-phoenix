package com.projectphoenix.agentcore.tool;

import com.projectphoenix.agentcore.tool.payload.BuildDetailData;

import java.util.List;

/**
 * 查询构建明细的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface BuildDetailTool {
    /**
     * 查询指定流水线的构建明细。
     *
     * @param applyBusId 流水线唯一标识
     * @return 包含当前和历史构建明细的 Tool 结果
     */
    ToolResult<List<BuildDetailData>> query(String applyBusId);
}
