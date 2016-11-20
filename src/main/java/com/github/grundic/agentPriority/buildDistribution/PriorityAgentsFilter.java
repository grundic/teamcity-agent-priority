/*
 * The MIT License
 *
 * Copyright (c) 2016 Grigory Chernyshev.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.grundic.agentPriority.buildDistribution;

import com.github.grundic.agentPriority.manager.AgentPriorityManager;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import com.google.common.collect.Ordering;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterContext;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult;
import jetbrains.buildServer.serverSide.buildDistribution.StartingBuildAgentsFilter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 16:00
 */
public class PriorityAgentsFilter implements StartingBuildAgentsFilter {

    @NotNull
    private final AgentPriorityManager priorityManager;
    @NotNull
    private final ProjectManager projectManager;

    public PriorityAgentsFilter(
            @NotNull AgentPriorityManager priorityManager,
            @NotNull ProjectManager projectManager
    ) {
        this.priorityManager = priorityManager;

        this.projectManager = projectManager;
    }

    @NotNull
    @Override
    public AgentsFilterResult filterAgents(@NotNull AgentsFilterContext context) {

        SBuildType buildType = projectManager.findBuildTypeById(context.getStartingBuild().getBuildConfiguration().getId());
        if (null == buildType) {
            // TODO add logging here.
            return new AgentsFilterResult();
        }

        SProject project = buildType.getProject();
        List<AgentPriorityDescriptor> configured = priorityManager.configured(project);
        if (configured.isEmpty()) {
            return new AgentsFilterResult();
        }

        Ordering<SBuildAgent> agentOrdering = Ordering.allEqual().nullsLast();
        for (AgentPriorityDescriptor priorityDescriptor : priorityManager.configured(project)) {
            agentOrdering = agentOrdering.compound(Ordering.natural().nullsLast().onResultOf(priorityDescriptor.getAgentPriority()));
        }

        final AgentsFilterResult result = new AgentsFilterResult();
        result.setFilteredConnectedAgents(agentOrdering.sortedCopy(context.getAgentsForStartingBuild()));
        return result;
    }
}
