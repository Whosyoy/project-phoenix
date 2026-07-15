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

/**
 * 覆盖 Pipeline Status Summary MVP-1.1 五个最小验收场景。
 *
 * <p>测试使用 JDK 断言机制，不依赖第三方测试框架。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class AgentCoreMvpTest {
    private int passed;

    /**
     * 运行无第三方依赖的测试入口。
     *
     * @param args 命令行参数，本测试不使用
     */
    public static void main(String[] args) {
        new AgentCoreMvpTest().run();
    }

    /**
     * 依次执行全部 MVP 验收场景。
     */
    private void run() {
        testNormalStatusQuery();
        testUnknownIntentStopsBeforeRegistry();
        testMissingApplyBusId();
        testPartialToolFailure();
        testGlobalDetailConflict();
        System.out.println("PASS: " + passed + "/5 tests");
    }

    /**
     * 验证正常状态查询返回完整且可追溯的证据。
     */
    private void testNormalStatusQuery() {
        AgentCore.AgentResponse response = core().handle(request("当前进度 apply_bus_id=BUS-NORMAL"));
        check(response.result().status() == SkillResult.Status.SUCCESS, "normal status");
        check(response.answer().contains("stageStatus=TEST"), "normal evidence response");
        passed++;
    }

    /**
     * 验证 UNKNOWN 意图在进入 Skill Registry 前被拦截。
     */
    private void testUnknownIntentStopsBeforeRegistry() {
        AgentCore.AgentResponse response = core().handle(request("帮我执行重构建 apply_bus_id=BUS-NORMAL"));
        check(response.intent() == Intent.UNKNOWN, "unknown intent");
        check(response.result().status() == SkillResult.Status.UNSUPPORTED, "unknown blocked");
        passed++;
    }

    /**
     * 验证缺少 apply_bus_id 时停止执行并追问。
     */
    private void testMissingApplyBusId() {
        AgentCore.AgentResponse response = core().handle(new UserRequest("req", "当前流水线状态", Map.of()));
        check(response.result().status() == SkillResult.Status.NEEDS_INPUT, "missing id");
        check(response.result().evidence() == null, "missing id has no tool evidence");
        passed++;
    }

    /**
     * 验证非关键 Tool 失败时只返回部分结果。
     */
    private void testPartialToolFailure() {
        AgentCore.AgentResponse response = core().handle(request("查询状态 apply_bus_id=BUS-PARTIAL"));
        check(response.result().status() == SkillResult.Status.PARTIAL, "partial status");
        check(response.answer().contains("query_test_detail"), "partial missing evidence");
        passed++;
    }

    /**
     * 验证全局状态与当前明细冲突时输出不确定结果。
     */
    private void testGlobalDetailConflict() {
        AgentCore.AgentResponse response = core().handle(request("查询状态 apply_bus_id=BUS-CONFLICT"));
        check(response.result().status() == SkillResult.Status.UNCERTAIN, "conflict status");
        check(response.answer().contains("不确定"), "conflict response");
        passed++;
    }

    /**
     * 创建使用固定 Fixture 的最小 Agent Core。
     */
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
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
