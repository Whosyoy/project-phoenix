package com.projectphoenix.agentcore.buildfailure.tool.payload;

/**
 * DirectBus 状态 Tool 的最小数据。
 *
 * @param applyBusId 返回数据所属的直通车标识
 * @param stage 当前全局阶段
 * @param status 当前全局状态
 * @author Rory
 * @since 2026-07-21
 */
public record DirectBusStatusData(String applyBusId, String stage, String status) {
}
