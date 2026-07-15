package com.projectphoenix.agentcore.model;

import java.util.List;

public record EvidenceBundle(
        String requestId,
        Intent intent,
        String skillName,
        String applyBusId,
        List<EvidenceItem> evidenceItems,
        List<ToolExecution> toolExecutions,
        List<String> missingEvidence,
        List<String> conflicts,
        boolean uncertain) {

    public EvidenceBundle {
        evidenceItems = List.copyOf(evidenceItems);
        toolExecutions = List.copyOf(toolExecutions);
        missingEvidence = List.copyOf(missingEvidence);
        conflicts = List.copyOf(conflicts);
    }

    public record EvidenceItem(String factName, String factValue, String sourceTool, String sourceField) {}

    public record ToolExecution(String toolName, String status, String errorMessage) {}
}
