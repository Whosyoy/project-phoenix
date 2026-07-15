package com.projectphoenix.agentcore.response;

import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.model.UserRequest;

/**
 * 将 Skill 结果转换为面向用户的自然语言回答。
 *
 * <p>实现不得补充 {@code EvidenceBundle} 中不存在的事实。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface ResponseGenerator {
    /**
     * 根据结构化 Skill 结果生成回答。
     *
     * @param request 原始用户请求
     * @param result Skill 执行结果
     * @return 最终回答
     */
    String generate(UserRequest request, SkillResult result);
}
