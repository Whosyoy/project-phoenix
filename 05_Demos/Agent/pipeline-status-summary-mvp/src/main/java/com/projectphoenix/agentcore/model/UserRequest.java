package com.projectphoenix.agentcore.model;

import java.util.Map;

/**
 * 表示一次用户请求及已经确认的会话上下文。
 *
 * @param requestId 请求唯一标识
 * @param userText 用户原始文本
 * @param confirmedContext 已由用户确认的上下文参数
 * @author Rory
 * @since 2026-07-15
 */
public record UserRequest(String requestId, String userText, Map<String, String> confirmedContext) {
    /**
     * 创建不可变用户请求，并将空上下文规范化为空 Map。
     */
    public UserRequest {
        confirmedContext = confirmedContext == null ? Map.of() : Map.copyOf(confirmedContext);
    }
}
