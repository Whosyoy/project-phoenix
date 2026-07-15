package com.projectphoenix.agentcore.execution;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.skill.Skill;

public final class SkillExecutor {
    public SkillResult execute(Skill skill, ExecutionContext context) {
        return skill.execute(context);
    }
}
