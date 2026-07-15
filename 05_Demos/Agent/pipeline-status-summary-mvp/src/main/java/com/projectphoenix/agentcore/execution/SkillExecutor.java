package com.projectphoenix.agentcore.execution;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.skill.Skill;

/**
 * 以统一入口调用已经选定的 Skill。
 *
 * <p>该类不规划步骤，也不改变 Skill 内部 Workflow。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class SkillExecutor {
    /**
     * 执行指定 Skill。
     *
     * @param skill 已选定的 Skill
     * @param context 已完成参数校验的上下文
     * @return Skill 执行结果
     */
    public SkillResult execute(Skill skill, ExecutionContext context) {
        return skill.execute(context);
    }
}
