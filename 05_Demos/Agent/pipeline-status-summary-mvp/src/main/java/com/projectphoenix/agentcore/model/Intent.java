package com.projectphoenix.agentcore.model;

/**
 * Agent Core 支持的顶层意图。
 *
 * <p>{@link #UNKNOWN} 仅用于安全拦截，不能进入 Skill Registry。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public enum Intent {
    PIPELINE_STATUS_QUERY,
    UNKNOWN
}
