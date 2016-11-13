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
import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityBean;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.SimpleView;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.github.grundic.agentPriority.Constants.PLUGIN_NAME;
import static com.github.grundic.agentPriority.Constants.PLUGIN_PATH;

/**
 * User: g.chernyshev
 * Date: 07/11/16
 * Time: 23:37
 */

public class ConfigurationController extends BaseController {
    @NotNull
    private final ProjectManager projectManager;
    @NotNull
    private final AgentPriorityManager priorityManager;
    @NotNull
    private final PluginDescriptor pluginDescriptor;

    public ConfigurationController(
            @NotNull ProjectManager projectManager,
            @NotNull AgentPriorityManager priorityManager,
            @NotNull WebControllerManager controllerManager,
            @NotNull PluginDescriptor pluginDescriptor) {
        this.projectManager = projectManager;
        this.priorityManager = priorityManager;
        this.pluginDescriptor = pluginDescriptor;

        controllerManager.registerController("/admin/" + PLUGIN_NAME + "/configuration.html", this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        final String priorityId = request.getParameter("priorityId");
        final String priorityType = request.getParameter("priorityType");
        SProject project = projectManager.findProjectByExternalId(request.getParameter("projectId"));

        if (project == null) {
            return SimpleView.createTextView("Project not found");
        }

        AgentPriorityBean priorityBean;
        AgentPriority agentPriority;

        if (StringUtil.isEmpty(priorityId)) {
            agentPriority = priorityManager.getPriorityByType(priorityType);
            if (null != agentPriority) {
                Map<String, String> defaultProperties = agentPriority.getDefaultProperties();
                priorityBean = new AgentPriorityBean(defaultProperties, defaultProperties);
            } else {
                return SimpleView.createTextView("Could not find agent priority of type: " + priorityType);
            }
        } else {
            AgentPriorityDescriptor priorityDescriptor = priorityManager.findPriorityById(project, priorityId);
            if (null == priorityDescriptor) {
                return SimpleView.createTextView(String.format("Agent priority with given id '%s' not found!", priorityId));
            }
            agentPriority = priorityDescriptor.getAgentPriority();
            priorityBean = new AgentPriorityBean(priorityDescriptor.getParameters(), agentPriority.getDefaultProperties());
        }

        priorityBean.setPriorityId(priorityId);
        priorityBean.setPriorityType(priorityType);

        String jspPath = pluginDescriptor.getPluginResourcesPath(PLUGIN_PATH + "/jsp/priorityConfiguration.jsp");
        ModelAndView mv = new ModelAndView(jspPath);
        mv.getModel().put("priorityBean", priorityBean);
        mv.getModel().put("agentPriority", agentPriority);
        mv.getModel().put("project", project);
        return mv;
    }
}
