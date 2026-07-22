package com.projectphoenix.agentcore.buildfailure.tool;

/**
 * 表示一次 Tool 调用返回的原始技术事实。
 *
 * <p>该类型不判断证据是否有效，也不决定最终业务状态。</p>
 *
 * @param toolName Tool 稳定名称
 * @param success Tool 调用是否成功
 * @param data Tool 原始类型化数据
 * @param errorMessage Tool 失败说明，成功时为空
 * @param <T> Tool 返回数据类型
 * @author Rory
 * @since 2026-07-21
 */
public record ToolResult<T>(String toolName, boolean success, T data, String errorMessage) {
    /**
     * 创建成功结果。
     *
     * @param toolName Tool 名称
     * @param data Tool 数据
     * @param <T> Tool 数据类型
     * @return 成功的原始 Tool 结果
     */
    public static <T> ToolResult<T> success(String toolName, T data) {
        return new ToolResult<>(toolName, true, data, null);
    }

    /**
     * 创建失败结果。
     *
     * @param toolName Tool 名称
     * @param errorMessage 失败说明
     * @param <T> Tool 预期数据类型
     * @return 失败的原始 Tool 结果
     */
    public static <T> ToolResult<T> failure(String toolName, String errorMessage) {
        return new ToolResult<>(toolName, false, null, errorMessage);
    }
}
