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

package com.github.grundic.agentPriority.manager;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import jetbrains.buildServer.BuildAgent;
import jetbrains.buildServer.ExtensionsProvider;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.grundic.agentPriority.Constants.*;

/**
 * User: g.chernyshev
 * Date: 12/11/16
 * Time: 23:21
 */
public class AgentPriorityManager {

    @NotNull
    private final ExtensionsProvider extensionsProvider;

    public AgentPriorityManager(@NotNull ExtensionsProvider extensionsProvider) {
        this.extensionsProvider = extensionsProvider;
    }

    @NotNull
    public List<SBuildAgent> sort(@NotNull Collection<SBuildAgent> agents, @NotNull SProject project, @NotNull SBuildType buildType){
        List<AgentPriorityDescriptor> configured = configuredForProjectWithParents(project);
        if (configured.isEmpty()) {
            return new ArrayList<>(agents);
        }

        Ordering<SBuildAgent> agentOrdering = Ordering.allEqual().nullsLast();
        for (AgentPriorityDescriptor priorityDescriptor : configured) {
            AgentPriority priority = priorityDescriptor.getAgentPriority();
            priority.setBuildType(buildType);
            agentOrdering = agentOrdering.compound(Ordering.natural().nullsLast().onResultOf(priority));
        }

        return agentOrdering.sortedCopy(agents);
    }

    @NotNull
    public List<AgentPriority> list() {
        List<AgentPriority> priorities = new ArrayList<>(extensionsProvider.getExtensions(AgentPriority.class));
        Collections.sort(priorities);
        return priorities;
    }

    @Nullable
    public AgentPriority getPriorityByType(@NotNull String type) {
        for (AgentPriority candidate : list()) {
            if (candidate.getType().equalsIgnoreCase(type)) {
                return candidate;
            }
        }
        return null;
    }

    @NotNull
    public List<AgentPriorityDescriptor> configuredForProject(@NotNull SProject project) {
        List<AgentPriorityDescriptor> priorityDescriptors = new ArrayList<>();

        for (SProjectFeatureDescriptor feature : project.getOwnFeaturesOfType(FEATURE_TYPE)) {
            AgentPriorityDescriptor descriptor = new AgentPriorityDescriptor(this, feature, project);
            priorityDescriptors.add(descriptor);
        }

        priorityDescriptors.sort((priority1, priority2) -> {
            String orderStr1 = priority1.getParameters().get(PRIORITY_ORDER);
            String orderStr2 = priority2.getParameters().get(PRIORITY_ORDER);

            orderStr1 = (orderStr1 == null ? "0" : orderStr1);
            orderStr2 = (orderStr2 == null ? "0" : orderStr2);

            Integer order1 = Ints.tryParse(orderStr1);
            Integer order2 = Ints.tryParse(orderStr2);

            order1 = (order1 == null ? 0 : order1);
            order2 = (order2 == null ? 0 : order2);

            return order1.compareTo(order2);
        });

        return priorityDescriptors;
    }


    @NotNull
    public List<AgentPriorityDescriptor> configuredForProjectWithParents(@NotNull SProject project) {
        List<AgentPriorityDescriptor> priorityDescriptors = new ArrayList<>();
        List<SProject> parents = project.getProjectPath();
        Collections.reverse(parents);

        for (SProject p : parents) {
            priorityDescriptors.addAll(configuredForProject(p));
        }

        return priorityDescriptors;
    }

    @NotNull
    public Map<SProject, List<AgentPriorityDescriptor>> configuredPerProject(@NotNull SProject project) {
        Map<SProject, List<AgentPriorityDescriptor>> result = new LinkedHashMap<>();
        List<SProject> projectPath = project.getProjectPath();
        Collections.reverse(projectPath);

        for (SProject p : projectPath) {
            List<AgentPriorityDescriptor> descriptors = configuredForProject(p);
            if (!descriptors.isEmpty()) {
                result.put(p, descriptors);
            }
        }

        return result;
    }

    @Nullable
    public AgentPriorityDescriptor findPriorityById(@NotNull SProject project, @NotNull String priorityId) {
        List<SProject> parents = project.getProjectPath();
        Collections.reverse(parents);

        for (SProject p : parents) {
            SProjectFeatureDescriptor feature = p.findFeatureById(priorityId);
            if (null != feature && feature.getType().equals(FEATURE_TYPE)) {
                return new AgentPriorityDescriptor(this, feature, p);
            }
        }
        return null;
    }

    @NotNull
    public AgentPriorityDescriptor addPriority(@NotNull SProject project, @NotNull String priorityType, @NotNull Map<String, String> parameters) {
        Map<String, String> projectFeatureParams = new HashMap<>();
        projectFeatureParams.putAll(parameters);
        projectFeatureParams.put(TYPE_PARAM, priorityType);
        return new AgentPriorityDescriptor(this, project.addFeature(FEATURE_TYPE, projectFeatureParams), project);
    }

    public boolean updatePriority(@NotNull SProject project, @NotNull String priorityId, @NotNull String priorityType, @NotNull Map<String, String> parameters) {
        Map<String, String> projectFeatureParams = new HashMap<>();
        projectFeatureParams.putAll(parameters);
        projectFeatureParams.put(TYPE_PARAM, priorityType);
        return project.updateFeature(priorityId, FEATURE_TYPE, projectFeatureParams);
    }
}
