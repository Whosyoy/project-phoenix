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

public final class AgentCore {
    private final IntentRouter router;
    private final ParameterExtractor extractor;
    private final SkillRegistry registry;
    private final SkillExecutor executor;
    private final ResponseGenerator responseGenerator;

    public AgentCore(IntentRouter router, ParameterExtractor extractor, SkillRegistry registry,
                     SkillExecutor executor, ResponseGenerator responseGenerator) {
        this.router = router;
        this.extractor = extractor;
        this.registry = registry;
        this.executor = executor;
        this.responseGenerator = responseGenerator;
    }

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

    public record AgentResponse(Intent intent, SkillResult result, String answer) {}
}
