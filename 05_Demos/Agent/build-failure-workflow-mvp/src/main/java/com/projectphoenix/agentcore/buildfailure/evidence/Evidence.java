package com.projectphoenix.agentcore.buildfailure.evidence;

/**
 * 表示经过 Workflow 确定性校验的证据。
 *
 * @param sourceTool 证据来源 Tool
 * @param rawData 原始类型化数据
 * @param valid 是否可用于当前业务结论
 * @param validationMessage 关联校验说明
 * @author Rory
 * @since 2026-07-21
 */
public record Evidence(
        String sourceTool,
        Object rawData,
        boolean valid,
        String validationMessage) {
}
