package com.projectphoenix.agentcore.extractor;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用显式格式和已确认上下文提取 {@code apply_bus_id}。
 *
 * <p>用户文本中的显式值优先于会话上下文；无法确认时保留缺参状态。</p>
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class RuleBasedParameterExtractor implements ParameterExtractor {
    private static final Pattern EXPLICIT_ID = Pattern.compile(
            "(?i)apply[_ -]?bus[_ -]?id\\s*[:=]?\\s*([a-z0-9-]+)");

    @Override
    public ExecutionContext extract(UserRequest request, Intent intent) {
        String applyBusId = explicitId(request.userText());
        if (applyBusId == null) {
            applyBusId = request.confirmedContext().get("apply_bus_id");
        }
        List<String> missing = applyBusId == null || applyBusId.isBlank()
                ? List.of("apply_bus_id") : List.of();
        return new ExecutionContext(request, intent, applyBusId, missing);
    }

    private String explicitId(String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = EXPLICIT_ID.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }
}
