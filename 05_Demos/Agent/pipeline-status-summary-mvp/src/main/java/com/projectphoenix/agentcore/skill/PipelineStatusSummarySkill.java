package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.workflow.PipelineStatusSummaryWorkflow;

public final class PipelineStatusSummarySkill implements Skill {
    private final PipelineStatusSummaryWorkflow workflow;

    public PipelineStatusSummarySkill(PipelineStatusSummaryWorkflow workflow) {
        this.workflow = workflow;
    }

    @Override public Intent supportedIntent() { return Intent.PIPELINE_STATUS_QUERY; }

    @Override public SkillResult execute(ExecutionContext context) { return workflow.execute(context); }
}
