package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;

public interface Skill {
    Intent supportedIntent();
    SkillResult execute(ExecutionContext context);
}
