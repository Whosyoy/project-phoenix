package com.projectphoenix.agentcore.router;

import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

public interface IntentRouter {
    Intent route(UserRequest request);
}
