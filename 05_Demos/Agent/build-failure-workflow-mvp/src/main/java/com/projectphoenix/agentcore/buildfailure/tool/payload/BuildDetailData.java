package com.projectphoenix.agentcore.buildfailure.tool.payload;

/**
 * Build Detail Tool 的最小当前构建数据。
 *
 * @param applyBusId 返回数据所属的直通车标识
 * @param buildId 当前构建对象标识
 * @param status 当前构建状态
 * @author Rory
 * @since 2026-07-21
 */
public record BuildDetailData(String applyBusId, String buildId, String status) {
}
