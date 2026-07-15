package com.projectphoenix.agentcore.router;

import com.projectphoenix.agentcore.model.Intent;
import com.projectphoenix.agentcore.model.UserRequest;

import java.util.List;

public final class RuleBasedIntentRouter implements IntentRouter {
    private static final List<String> STATUS_TERMS = List.of(
            "状态", "进度", "到哪一步", "没跑完", "未完成", "isrunning", "构建中", "测试中");

    @Override
    public Intent route(UserRequest request) {
        String text = request.userText() == null ? "" : request.userText().toLowerCase();
        return STATUS_TERMS.stream().anyMatch(text::contains)
                ? Intent.PIPELINE_STATUS_QUERY
                : Intent.UNKNOWN;
    }
}
