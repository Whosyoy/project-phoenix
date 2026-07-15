package com.projectphoenix.agentcore.model;

import java.util.List;

/**
 * 汇总 Workflow 产生的结构化事实、Tool 执行记录和不确定性。
 *
 * <p>Response Generator 只能依据本对象中的内容生成事实性回答。</p>
 *
 * @param requestId 请求唯一标识
 * @param intent 已确认意图
 * @param skillName 实际执行的 Skill 名称
 * @param applyBusId 查询范围中的流水线标识
 * @param evidenceItems 带来源的事实证据
 * @param toolExecutions Tool 执行摘要
 * @param missingEvidence 缺失的证据
 * @param conflicts 相互冲突的证据
 * @param uncertain 是否无法形成确定结论
 * @author Rory
 * @since 2026-07-15
 */
public record EvidenceBundle(
        String requestId,
        Intent intent,
        String skillName,
        String applyBusId,
        List<EvidenceItem> evidenceItems,
        List<ToolExecution> toolExecutions,
        List<String> missingEvidence,
        List<String> conflicts,
        boolean uncertain) {

    /**
     * 创建不可变证据集合。
     */
    public EvidenceBundle {
        evidenceItems = List.copyOf(evidenceItems);
        toolExecutions = List.copyOf(toolExecutions);
        missingEvidence = List.copyOf(missingEvidence);
        conflicts = List.copyOf(conflicts);
    }

    /**
     * 描述一个可追溯到 Tool 字段的事实。
     *
     * @param factName 事实名称
     * @param factValue 事实值
     * @param sourceTool 来源 Tool
     * @param sourceField 来源字段
     */
    public record EvidenceItem(String factName, String factValue, String sourceTool, String sourceField) {
    }

    /**
     * 描述一次 Tool 执行的结果摘要。
     *
     * @param toolName Tool 名称
     * @param status 执行状态
     * @param errorMessage 错误信息，成功时为空
     */
    public record ToolExecution(String toolName, String status, String errorMessage) {
    }
}
