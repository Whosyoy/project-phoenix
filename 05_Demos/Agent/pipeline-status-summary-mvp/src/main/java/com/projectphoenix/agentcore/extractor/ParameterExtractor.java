package com.projectphoenix.agentcore.extractor;

import com.projectphoenix.agentcore.model.ExecutionContext;
import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

public interface ParameterExtractor {
    ExecutionContext extract(UserRequest request, Intent intent);
}
