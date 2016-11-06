package com.github.grundic.agentPriority;

import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterContext;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult;
import jetbrains.buildServer.serverSide.buildDistribution.StartingBuildAgentsFilter;
import org.jetbrains.annotations.NotNull;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 16:00
 */
public class PriorityAgentsFilter implements StartingBuildAgentsFilter {
    @NotNull
    @Override
    public AgentsFilterResult filterAgents(@NotNull AgentsFilterContext context) {
        return new AgentsFilterResult();
    }
}
