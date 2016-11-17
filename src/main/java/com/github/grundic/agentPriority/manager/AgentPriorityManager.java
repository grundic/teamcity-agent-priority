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
import jetbrains.buildServer.ExtensionsProvider;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.grundic.agentPriority.Constants.FEATURE_TYPE;
import static com.github.grundic.agentPriority.Constants.TYPE_PARAM;

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
    public List<AgentPriorityDescriptor> configured(@NotNull SProject project) {
        List<AgentPriorityDescriptor> priorityDescriptors = new ArrayList<>();

        List<SProject> parents = project.getProjectPath();

        for (SProject p : parents) {
            for (SProjectFeatureDescriptor feature : p.getOwnFeaturesOfType(FEATURE_TYPE)) {
                AgentPriorityDescriptor descriptor = new AgentPriorityDescriptor(this, feature, p);
                priorityDescriptors.add(descriptor);
            }
        }

        return priorityDescriptors;
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
