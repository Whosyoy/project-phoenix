package com.projectphoenix.agentcore.response;

import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.model.UserRequest;

public interface ResponseGenerator {
    String generate(UserRequest request, SkillResult result);
}
