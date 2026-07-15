package com.projectphoenix.agentcore.model;

import java.util.Map;

public record UserRequest(String requestId, String userText, Map<String, String> confirmedContext) {
    public UserRequest {
        confirmedContext = confirmedContext == null ? Map.of() : Map.copyOf(confirmedContext);
    }
}
