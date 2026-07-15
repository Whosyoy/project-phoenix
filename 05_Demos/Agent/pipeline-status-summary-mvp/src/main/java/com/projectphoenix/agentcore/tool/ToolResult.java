package com.projectphoenix.agentcore.tool;

public record ToolResult<T>(String toolName, Status status, T data, String errorMessage) {
    public enum Status { SUCCESS, PARTIAL, FAILURE }

    public static <T> ToolResult<T> success(String name, T data) {
        return new ToolResult<>(name, Status.SUCCESS, data, null);
    }

    public static <T> ToolResult<T> failure(String name, String message) {
        return new ToolResult<>(name, Status.FAILURE, null, message);
    }
}
