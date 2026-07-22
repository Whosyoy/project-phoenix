package com.projectphoenix.agentcore.buildfailure.tool;

import com.projectphoenix.agentcore.buildfailure.tool.payload.BuildDetailData;

/**
 * 查询当前构建对象的只读 Tool 契约。
 *
 * @author Rory
 * @since 2026-07-21
 */
public interface BuildDetailTool {
    /**
     * 查询指定直通车的当前构建明细。
     *
     * @param applyBusId 直通车标识
     * @return 原始 Tool 结果
     */
    ToolResult<BuildDetailData> query(String applyBusId);
}
