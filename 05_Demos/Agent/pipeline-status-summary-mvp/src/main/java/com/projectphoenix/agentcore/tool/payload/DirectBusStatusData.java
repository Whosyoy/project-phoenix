package com.projectphoenix.agentcore.tool.payload;

public record DirectBusStatusData(String applyBusId, boolean running, int stageStatus, String nodeStatus) {}
