package com.projectphoenix.agentcore.tool.payload;

/**
 * DirectBus 全局状态查询的最小返回数据。
 *
 * @param applyBusId 流水线唯一标识
 * @param running 流程是否仍处于可处理生命周期
 * @param stageStatus 全局阶段状态码
 * @param nodeStatus 当前节点状态
 * @author Rory
 * @since 2026-07-15
 */
public record DirectBusStatusData(String applyBusId, boolean running, int stageStatus, String nodeStatus) {
}
