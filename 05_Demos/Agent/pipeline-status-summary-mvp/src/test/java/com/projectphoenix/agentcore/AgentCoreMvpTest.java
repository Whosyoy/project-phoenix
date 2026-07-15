package com.projectphoenix.agentcore;

import com.projectphoenix.agentcore.execution.SkillExecutor;
import com.projectphoenix.agentcore.extractor.RuleBasedParameterExtractor;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.model.UserRequest;
import com.projectphoenix.agentcore.response.TemplateResponseGenerator;
import com.projectphoenix.agentcore.router.RuleBasedIntentRouter;
import com.projectphoenix.agentcore.skill.PipelineStatusSummarySkill;
import com.projectphoenix.agentcore.skill.SkillRegistry;
import com.projectphoenix.agentcore.tool.mock.MockBuildDetailTool;
import com.projectphoenix.agentcore.tool.mock.MockDirectBusStatusTool;
import com.projectphoenix.agentcore.tool.mock.MockTestDetailTool;
import com.projectphoenix.agentcore.workflow.PipelineStatusSummaryWorkflow;

import java.util.Map;

public final class AgentCoreMvpTest {
    private int passed;

    public static void main(String[] args) {
        new AgentCoreMvpTest().run();
    }

    private void run() {
        testNormalStatusQuery();
        testUnknownIntentStopsBeforeRegistry();
        testMissingApplyBusId();
        testPartialToolFailure();
        testGlobalDetailConflict();
        System.out.println("PASS: " + passed + "/5 tests");
    }

    private void testNormalStatusQuery() {
        AgentCore.AgentResponse response = core().handle(request("当前进度 apply_bus_id=BUS-NORMAL"));
        check(response.result().status() == SkillResult.Status.SUCCESS, "normal status");
        check(response.answer().contains("stageStatus=TEST"), "normal evidence response");
        passed++;
    }

    private void testUnknownIntentStopsBeforeRegistry() {
        AgentCore.AgentResponse response = core().handle(request("帮我执行重构建 apply_bus_id=BUS-NORMAL"));
        check(response.intent() == Intent.UNKNOWN, "unknown intent");
        check(response.result().status() == SkillResult.Status.UNSUPPORTED, "unknown blocked");
        passed++;
    }

    private void testMissingApplyBusId() {
        AgentCore.AgentResponse response = core().handle(new UserRequest("req", "当前流水线状态", Map.of()));
        check(response.result().status() == SkillResult.Status.NEEDS_INPUT, "missing id");
        check(response.result().evidence() == null, "missing id has no tool evidence");
        passed++;
    }

    private void testPartialToolFailure() {
        AgentCore.AgentResponse response = core().handle(request("查询状态 apply_bus_id=BUS-PARTIAL"));
        check(response.result().status() == SkillResult.Status.PARTIAL, "partial status");
        check(response.answer().contains("query_test_detail"), "partial missing evidence");
        passed++;
    }

    private void testGlobalDetailConflict() {
        AgentCore.AgentResponse response = core().handle(request("查询状态 apply_bus_id=BUS-CONFLICT"));
        check(response.result().status() == SkillResult.Status.UNCERTAIN, "conflict status");
        check(response.answer().contains("不确定"), "conflict response");
        passed++;
    }

    private AgentCore core() {
        PipelineStatusSummaryWorkflow workflow = new PipelineStatusSummaryWorkflow(
                new MockDirectBusStatusTool(), new MockBuildDetailTool(), new MockTestDetailTool());
        PipelineStatusSummarySkill skill = new PipelineStatusSummarySkill(workflow);
        return new AgentCore(new RuleBasedIntentRouter(), new RuleBasedParameterExtractor(),
                new SkillRegistry(skill), new SkillExecutor(), new TemplateResponseGenerator());
    }

    private UserRequest request(String text) {
        return new UserRequest("req", text, Map.of());
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
