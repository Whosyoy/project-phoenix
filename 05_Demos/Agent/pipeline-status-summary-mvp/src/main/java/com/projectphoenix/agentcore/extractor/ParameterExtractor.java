package com.projectphoenix.agentcore.extractor;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

/**
 * 从用户请求及已确认上下文中提取 Skill 执行参数。
 *
 * @author Rory
 * @since 2026-07-15
 */
public interface ParameterExtractor {
    /**
     * 构造执行上下文，不猜测无法确认的参数。
     *
     * @param request 用户请求
     * @param intent 已确认意图
     * @return 包含已确认参数和缺失项的执行上下文
     */
    ExecutionContext extract(UserRequest request, Intent intent);
}
