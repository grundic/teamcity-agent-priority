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

package com.github.grundic.agentPriority.prioritisation;

import com.github.grundic.agentPriority.manager.AgentPriorityManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.github.grundic.agentPriority.Constants.TYPE_PARAM;

/**
 * User: g.chernyshev
 * Date: 12/11/16
 * Time: 23:43
 */
public class AgentPriorityDescriptor {

    @NotNull
    private final SProjectFeatureDescriptor featureDescriptor;
    @NotNull
    private final SProject project;
    @NotNull
    private final AgentPriority agentPriority;

    public AgentPriorityDescriptor(@NotNull AgentPriorityManager manager, @NotNull SProjectFeatureDescriptor featureDescriptor, @NotNull SProject project) {
        this.featureDescriptor = featureDescriptor;
        this.project = project;

        final String type = featureDescriptor.getParameters().get(TYPE_PARAM);
        AgentPriority priority = manager.getPriorityByType(type);
        if (null == priority) {
            // TODO: fixme please!
            agentPriority = null;
        } else {
            agentPriority = priority;
        }
    }

    @NotNull
    public String getId() {
        return featureDescriptor.getId();
    }

    @NotNull
    public Map<String, String> getParameters() {
        return featureDescriptor.getParameters();
    }

    @NotNull
    public AgentPriority getAgentPriority() {
        return agentPriority;
    }

    @NotNull
    public SProject getProject() {
        return project;
    }
}
