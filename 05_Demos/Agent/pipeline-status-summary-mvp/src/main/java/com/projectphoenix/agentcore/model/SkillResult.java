package com.projectphoenix.agentcore.model;

import java.util.List;

/**
 * 封装 Skill 的执行状态、证据和非事实性提示信息。
 *
 * @param status Skill 执行状态
 * @param evidence Workflow 生成的证据；未执行 Skill 时可为空
 * @param messages 追问或边界提示
 * @author Rory
 * @since 2026-07-15
 */
public record SkillResult(Status status, EvidenceBundle evidence, List<String> messages) {
    /**
     * 创建不可变 Skill 结果。
     */
    public SkillResult {
        messages = List.copyOf(messages);
    }

    /**
     * Skill 的最小执行状态集合。
     */
    public enum Status {
        SUCCESS, NEEDS_INPUT, PARTIAL, UNCERTAIN, FAILED, UNSUPPORTED
    }

    /**
     * 创建意图不受支持的结果。
     *
     * @param message 边界说明
     * @return 不受支持的 Skill 结果
     */
    public static SkillResult unsupported(String message) {
        return new SkillResult(Status.UNSUPPORTED, null, List.of(message));
    }

    /**
     * 创建需要用户补充参数的结果。
     *
     * @param message 参数追问
     * @return 等待输入的 Skill 结果
     */
    public static SkillResult needsInput(String message) {
        return new SkillResult(Status.NEEDS_INPUT, null, List.of(message));
    }
}
