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

package com.github.grundic.agentPriority.controllers;

import com.github.grundic.agentPriority.manager.AgentPriorityManager;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import jetbrains.buildServer.controllers.admin.projects.EditProjectTab;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.grundic.agentPriority.Constants.*;

/**
 * User: g.chernyshev
 * Date: 06/11/16
 * Time: 21:57
 */
public class ProjectConfigurationTab extends EditProjectTab {

    @NotNull
    private final AgentPriorityManager priorityManager;
    @NotNull
    private final PluginDescriptor pluginDescriptor;


    public ProjectConfigurationTab(
            @NotNull PagePlaces pagePlaces,
            @NotNull AgentPriorityManager priorityManager,
            @NotNull PluginDescriptor pluginDescriptor
    ) {
        super(pagePlaces, PLUGIN_NAME, PLUGIN_PATH + "/jsp/projectTab.jsp", PLUGIN_TITLE);
        this.priorityManager = priorityManager;
        this.pluginDescriptor = pluginDescriptor;

        register();
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        super.fillModel(model, request);

        SProject project = getProject(request);
        if (null != project) {
            model.put("configuredPriorities", priorityManager.configuredPerProject(project));
            model.put("availableBuilds", project.getOwnBuildTypes());
        }

        model.put("availablePriorities", priorityManager.list());
    }

    @NotNull
    @Override
    public String getTabTitle(@NotNull HttpServletRequest request) {
        SProject project = this.getProject(request);
        String tabTitle = super.getTabTitle(request);

        if (project != null) {
            int count = 0;
            Map<SProject, List<AgentPriorityDescriptor>> priorities = priorityManager.configuredPerProject(project);
            for (List<AgentPriorityDescriptor> descriptors : priorities.values()) {
                count += descriptors.size();
            }

            if (count > 0) {
                tabTitle += String.format(" (%d)", count);
            }
        }

        return tabTitle;
    }

    @NotNull
    @Override
    public List<String> getJsPaths() {
        return Arrays.asList(
                pluginDescriptor.getPluginResourcesPath(PLUGIN_PATH + "/js/agentPriorityDialog.js"),
                pluginDescriptor.getPluginResourcesPath(PLUGIN_PATH + "/js/agentPriorityOrderDialog.js")
        );
    }
}
