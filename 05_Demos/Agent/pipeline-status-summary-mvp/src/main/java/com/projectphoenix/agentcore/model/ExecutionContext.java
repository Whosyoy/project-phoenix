package com.projectphoenix.agentcore.model;

import java.util.List;

/**
 * 保存路由和参数抽取完成后的 Skill 执行上下文。
 *
 * @param request 原始用户请求
 * @param intent 已确认的顶层意图
 * @param applyBusId 流水线唯一标识
 * @param missingParameters 缺失的必需参数
 * @author Rory
 * @since 2026-07-15
 */
public record ExecutionContext(UserRequest request, Intent intent, String applyBusId, List<String> missingParameters) {
    /**
     * 创建不可变执行上下文。
     */
    public ExecutionContext {
        missingParameters = List.copyOf(missingParameters);
    }

    /**
     * 判断执行所需参数是否完整。
     *
     * @return 没有缺失参数时返回 {@code true}
     */
    public boolean isComplete() {
        return missingParameters.isEmpty();
    }
}
