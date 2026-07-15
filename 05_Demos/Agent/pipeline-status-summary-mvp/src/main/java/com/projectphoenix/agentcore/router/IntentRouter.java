package com.projectphoenix.agentcore.router;

import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

/**
 * 将用户请求路由到受支持的顶层意图。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface IntentRouter {
    /**
     * 识别用户请求的意图。
     *
     * @param request 用户请求
     * @return 支持的意图或 {@link Intent#UNKNOWN}
     */
    Intent route(UserRequest request);
}
