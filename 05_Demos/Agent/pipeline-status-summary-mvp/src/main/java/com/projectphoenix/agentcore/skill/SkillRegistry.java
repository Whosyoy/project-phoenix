package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.Intent;
import java.util.Map;

/**
 * 维护 Intent 到 Skill 的显式固定映射。
 *
 * <p>调用方必须在查询 Registry 前拦截 {@link Intent#UNKNOWN}。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class SkillRegistry {
    private final Map<Intent, Skill> skills;

    /**
     * 创建只包含一个 MVP Skill 的注册表。
     *
     * @param skill 需要注册的 Skill
     */
    public SkillRegistry(Skill skill) {
        this.skills = Map.of(skill.supportedIntent(), skill);
    }

    /**
     * 获取指定意图对应的 Skill。
     *
     * @param intent 已确认意图
     * @return 已注册的 Skill
     * @throws IllegalArgumentException 未找到对应 Skill 时抛出
     */
    public Skill get(Intent intent) {
        Skill skill = skills.get(intent);
        if (skill == null) {
            throw new IllegalArgumentException("No skill registered for " + intent);
        }
        return skill;
    }
}
