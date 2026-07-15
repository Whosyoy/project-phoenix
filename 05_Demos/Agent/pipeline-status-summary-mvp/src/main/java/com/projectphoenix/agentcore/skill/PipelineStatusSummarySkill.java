package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.workflow.PipelineStatusSummaryWorkflow;

/**
 * 流水线状态汇总 Skill，负责绑定意图并委托固定 Workflow 执行。
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class PipelineStatusSummarySkill implements Skill {
    private final PipelineStatusSummaryWorkflow workflow;

    /**
     * 创建流水线状态汇总 Skill。
     *
     * @param workflow 确定性状态汇总 Workflow
     */
    public PipelineStatusSummarySkill(PipelineStatusSummaryWorkflow workflow) {
        this.workflow = workflow;
    }

    @Override
    public Intent supportedIntent() {
        return Intent.PIPELINE_STATUS_QUERY;
    }

    @Override
    public SkillResult execute(ExecutionContext context) {
        return workflow.execute(context);
    }
}
