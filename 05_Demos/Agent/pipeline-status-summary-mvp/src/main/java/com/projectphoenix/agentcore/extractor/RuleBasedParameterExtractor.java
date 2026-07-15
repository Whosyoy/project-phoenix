package com.projectphoenix.agentcore.extractor;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (text == null) return null;
        Matcher matcher = EXPLICIT_ID.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }
}
