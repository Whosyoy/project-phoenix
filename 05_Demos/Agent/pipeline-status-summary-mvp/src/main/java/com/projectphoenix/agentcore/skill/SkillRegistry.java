package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.Intent;
import java.util.Map;

public final class SkillRegistry {
    private final Map<Intent, Skill> skills;

    public SkillRegistry(Skill skill) {
        this.skills = Map.of(skill.supportedIntent(), skill);
    }

    public Skill get(Intent intent) {
        Skill skill = skills.get(intent);
        if (skill == null) throw new IllegalArgumentException("No skill registered for " + intent);
        return skill;
    }
}
