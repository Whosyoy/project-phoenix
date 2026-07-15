package com.projectphoenix.agentcore.model;

import java.util.List;

public record SkillResult(Status status, EvidenceBundle evidence, List<String> messages) {
    public SkillResult {
        messages = List.copyOf(messages);
    }

    public enum Status {
        SUCCESS, NEEDS_INPUT, PARTIAL, UNCERTAIN, FAILED, UNSUPPORTED
    }

    public static SkillResult unsupported(String message) {
        return new SkillResult(Status.UNSUPPORTED, null, List.of(message));
    }

    public static SkillResult needsInput(String message) {
        return new SkillResult(Status.NEEDS_INPUT, null, List.of(message));
    }
}
