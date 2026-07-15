package com.projectphoenix.agentcore.response;

import com.projectphoenix.agentcore.model.EvidenceBundle;
import com.projectphoenix.agentcore.model.SkillResult;
import com.projectphoenix.agentcore.model.UserRequest;

import java.util.stream.Collectors;

/**
 * 使用固定模板将结构化证据生成为可追溯回答。
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class TemplateResponseGenerator implements ResponseGenerator {
    @Override
    public String generate(UserRequest request, SkillResult result) {
        if (result.evidence() == null) {
            return String.join("；", result.messages());
        }

        EvidenceBundle bundle = result.evidence();
        String facts = bundle.evidenceItems().stream()
                .map(item -> item.factName() + "=" + item.factValue()
                        + " [" + item.sourceTool() + "." + item.sourceField() + "]")
                .collect(Collectors.joining("；"));
        String missing = bundle.missingEvidence().isEmpty()
                ? ""
                : "；缺失证据：" + String.join("；", bundle.missingEvidence());
        String conflicts = bundle.conflicts().isEmpty() ? "" : "；不确定：" + String.join("；", bundle.conflicts());
        return "查询范围 apply_bus_id=" + bundle.applyBusId() + "；状态=" + result.status()
                + "；证据：" + facts + missing + conflicts;
    }
}
