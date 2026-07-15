package com.projectphoenix.agentcore.model;

import java.util.List;

public record ExecutionContext(UserRequest request, Intent intent, String applyBusId, List<String> missingParameters) {
    public ExecutionContext {
        missingParameters = List.copyOf(missingParameters);
    }

    public boolean isComplete() {
        return missingParameters.isEmpty();
    }
}
