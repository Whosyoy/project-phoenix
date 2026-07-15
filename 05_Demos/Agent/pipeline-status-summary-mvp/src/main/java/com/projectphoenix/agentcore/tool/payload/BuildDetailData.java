package com.projectphoenix.agentcore.tool.payload;

/**
 * 项目和基线级构建明细的最小返回数据。
 *
 * @param projectName 项目名称
 * @param baselineName 基线名称
 * @param status 构建状态
 * @param history 是否为历史记录
 * @author Rory
 * @since 2026-07-15
 */
public record BuildDetailData(String projectName, String baselineName, String status, boolean history) {
}
