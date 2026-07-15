package com.projectphoenix.agentcore.skill;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;

/**
 * 定义单一业务能力的最小运行契约。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface Skill {
    /**
     * 返回当前 Skill 唯一支持的顶层意图。
     *
     * @return 支持的意图
     */
    Intent supportedIntent();

    /**
     * 使用已完成参数校验的上下文执行 Skill。
     *
     * @param context 执行上下文
     * @return 带结构化证据的执行结果
     */
    SkillResult execute(ExecutionContext context);
}
