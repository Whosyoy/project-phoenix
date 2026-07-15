package com.projectphoenix.agentcore;

import com.projectphoenix.agentcore.execution.SkillExecutor;
import com.projectphoenix.agentcore.extractor.ParameterExtractor;
import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.model.UserRequest;
import com.projectphoenix.agentcore.response.ResponseGenerator;
import com.projectphoenix.agentcore.router.IntentRouter;
import com.projectphoenix.agentcore.skill.SkillRegistry;

/**
 * Agent Core 的薄编排入口，按固定顺序连接路由、参数抽取、Skill 和回答生成。
 *
 * <p>该类不包含 DirectBus 业务规则，也不承担动态规划职责。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class AgentCore {
    private final IntentRouter router;
    private final ParameterExtractor extractor;
    private final SkillRegistry registry;
    private final SkillExecutor executor;
    private final ResponseGenerator responseGenerator;

    /**
     * 创建 Agent Core 编排入口。
     *
     * @param router 意图路由器
     * @param extractor 参数抽取器
     * @param registry Skill 注册表
     * @param executor Skill 执行器
     * @param responseGenerator 回答生成器
     */
    public AgentCore(IntentRouter router, ParameterExtractor extractor, SkillRegistry registry,
                     SkillExecutor executor, ResponseGenerator responseGenerator) {
        this.router = router;
        this.extractor = extractor;
        this.registry = registry;
        this.executor = executor;
        this.responseGenerator = responseGenerator;
    }

    /**
     * 处理一次用户请求。
     *
     * <p>UNKNOWN 意图和缺少必需参数都会在访问 Skill Registry 前返回。</p>
     *
     * @param request 用户请求
     * @return 包含意图、结构化结果和自然语言回答的响应
     */
    public AgentResponse handle(UserRequest request) {
        Intent intent = router.route(request);
        if (intent == Intent.UNKNOWN) {
            SkillResult result = SkillResult.unsupported("无法识别为流水线状态查询，请明确询问状态或进度");
            return new AgentResponse(intent, result, responseGenerator.generate(request, result));
        }

        ExecutionContext context = extractor.extract(request, intent);
        if (!context.isComplete()) {
            SkillResult result = SkillResult.needsInput("请提供唯一的 apply_bus_id");
            return new AgentResponse(intent, result, responseGenerator.generate(request, result));
        }

        SkillResult result = executor.execute(registry.get(intent), context);
        return new AgentResponse(intent, result, responseGenerator.generate(request, result));
    }

    /**
     * Agent Core 对一次调用的完整响应。
     *
     * @param intent 路由结果
     * @param result Skill 或控制分支产生的结构化结果
     * @param answer 面向用户的回答
     */
    public record AgentResponse(Intent intent, SkillResult result, String answer) {
    }
}
