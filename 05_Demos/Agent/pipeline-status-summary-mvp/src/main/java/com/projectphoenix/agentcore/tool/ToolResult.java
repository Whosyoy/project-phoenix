package com.projectphoenix.agentcore.tool;

/**
 * Tool 层的统一类型化返回值。
 *
 * @param toolName Tool 稳定名称
 * @param status 执行状态
 * @param data 类型化数据，失败时可为空
 * @param errorMessage 错误说明，成功时为空
 * @param <T> Tool 返回的数据类型
 * @author Rory
 * @since 2026-07-15
 */
public record ToolResult<T>(String toolName, Status status, T data, String errorMessage) {
    /**
     * Tool 的最小执行状态集合。
     */
    public enum Status {
        SUCCESS, PARTIAL, FAILURE
    }

    /**
     * 创建成功的 Tool 结果。
     *
     * @param name Tool 名称
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 成功结果
     */
    public static <T> ToolResult<T> success(String name, T data) {
        return new ToolResult<>(name, Status.SUCCESS, data, null);
    }

    /**
     * 创建失败的 Tool 结果。
     *
     * @param name Tool 名称
     * @param message 错误说明
     * @param <T> 预期数据类型
     * @return 失败结果
     */
    public static <T> ToolResult<T> failure(String name, String message) {
        return new ToolResult<>(name, Status.FAILURE, null, message);
    }
}
