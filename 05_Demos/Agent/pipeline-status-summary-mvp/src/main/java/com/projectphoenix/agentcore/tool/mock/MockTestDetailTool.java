package com.projectphoenix.agentcore.tool.mock;

import com.projectphoenix.agentcore.tool.TestDetailTool;
import com.projectphoenix.agentcore.tool.ToolResult;
import com.projectphoenix.agentcore.tool.payload.TestDetailData;

import java.util.List;

/**
 * 使用固定 Fixture 模拟测试明细查询及部分失败场景。
 *
 * @author Rory
 * @since 2026-07-15
 */
public final class MockTestDetailTool implements TestDetailTool {
    @Override
    public ToolResult<List<TestDetailData>> query(String id) {
        if (FixtureData.PARTIAL.equals(id)) {
            return ToolResult.failure("query_test_detail", "fixture: test detail unavailable");
        }
        return ToolResult.success("query_test_detail", List.of(
                new TestDetailData("phoenix-api", "main", "RUNNING", false)));
    }
}
