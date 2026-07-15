package com.projectphoenix.agentcore.tool.payload;

/**
 * 项目和基线级测试明细的最小返回数据。
 *
 * @param projectName 项目名称
 * @param baselineName 基线名称
 * @param status 测试状态
 * @param history 是否为历史记录
 * @author Rory
 * @since 2026-07-15
 */
public record TestDetailData(String projectName, String baselineName, String status, boolean history) {
}
