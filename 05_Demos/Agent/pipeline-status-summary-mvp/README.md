# Pipeline Status Summary MVP-1.1

纯 Java、框架无关的单 Skill 最小闭环。只使用固定 Fixture，不访问网络、MCP、LLM 或公司接口。

## 代码流程图

```mermaid
flowchart TD
    A["UserRequest<br/>用户文本 + 已确认上下文"] --> B["RuleBasedIntentRouter<br/>规则意图识别"]
    B --> C{"Intent 是否受支持？"}

    C -- "UNKNOWN" --> D["SkillResult.UNSUPPORTED"]
    C -- "PIPELINE_STATUS_QUERY" --> E["RuleBasedParameterExtractor<br/>提取 apply_bus_id"]

    E --> F{"apply_bus_id 是否完整？"}
    F -- "否" --> G["SkillResult.NEEDS_INPUT"]
    F -- "是" --> H["SkillRegistry<br/>获取 PipelineStatusSummarySkill"]

    H --> I["SkillExecutor"]
    I --> J["PipelineStatusSummarySkill"]
    J --> K["PipelineStatusSummaryWorkflow<br/>固定执行顺序"]

    K --> L["MockDirectBusStatusTool<br/>查询全局状态"]
    L --> M["MockBuildDetailTool<br/>查询构建明细"]
    M --> N["MockTestDetailTool<br/>查询测试明细"]
    N --> O["过滤 history = false<br/>只保留当前记录"]
    O --> P["聚合事实、Tool 执行摘要、<br/>缺失证据和冲突"]
    P --> Q["EvidenceBundle"]

    Q --> R{"全局状态与明细冲突？"}
    R -- "是" --> S["SkillResult.UNCERTAIN"]
    R -- "否" --> T{"存在 Tool 失败？"}
    T -- "是" --> U["SkillResult.PARTIAL"]
    T -- "否" --> V["SkillResult.SUCCESS"]

    D --> W["TemplateResponseGenerator"]
    G --> W
    S --> W
    U --> W
    V --> W
    W --> X["AgentResponse<br/>结构化结果 + 自然语言回答"]
```

读图重点：

- `UNKNOWN` 和缺少 `apply_bus_id` 都在进入 `SkillRegistry` 前返回，不会调用任何 Tool。
- Workflow 串行调用三个 Mock Tool，并以 `history = false` 作为当前记录过滤规则。
- 最终回答只能消费 `SkillResult` 中的 `EvidenceBundle`，不能绕过证据补充事实。

## 运行测试

需要 JDK 17 或更高版本：

```bash
./run-tests.sh
```

测试脚本将源码编译到临时目录，不在仓库内生成构建产物。
