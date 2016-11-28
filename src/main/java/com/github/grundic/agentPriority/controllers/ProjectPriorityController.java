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
import com.google.common.base.Splitter;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.controllers.FormUtil;
import jetbrains.buildServer.controllers.SimpleView;
import jetbrains.buildServer.controllers.admin.projects.PluginPropertiesUtil;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.jdom.Text;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.grundic.agentPriority.Constants.*;

/**
 * User: g.chernyshev
 * Date: 14/11/16
 * Time: 22:40
 */
public class ProjectPriorityController extends BaseFormXmlController {

    @NotNull
    private final ProjectManager projectManager;
    @NotNull
    private final AgentPriorityManager priorityManager;
    @NotNull
    private final ConfigActionFactory actionFactory;

    public ProjectPriorityController(
            @NotNull ProjectManager projectManager,
            @NotNull AgentPriorityManager priorityManager,
            @NotNull WebControllerManager controllerManager,
            @NotNull ConfigActionFactory actionFactory) {
        this.projectManager = projectManager;
        this.priorityManager = priorityManager;
        this.actionFactory = actionFactory;

        controllerManager.registerController("/admin/" + PLUGIN_NAME + "/priorities.html", this);
    }

    @NotNull
    @Override
    protected ModelAndView doGet(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        return SimpleView.createTextView("Method is not supported!");
    }

    @Override
    protected void doPost(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Element xmlResponse) {
        ActionErrors errors = new ActionErrors();

        SProject project = projectManager.findProjectByExternalId(request.getParameter("projectId"));

        if (project == null) {
            errors.addError("projectId", String.format("Can't find project by given '%s' project id!", request.getParameter("projectId")));
            errors.serialize(xmlResponse);
            return;
        }

        String operation = request.getParameter("operation");
        if (operation == null) {
            errors.addError("operation", "Operation was not provided!");
            errors.serialize(xmlResponse);
            return;
        }

        switch (operation) {
            case "savePriority":
                doSave(request, xmlResponse, project, errors);
                break;
            case "deletePriority":
                doRemove(request, project);
                break;
            case "reorderPriority":
                doReorder(request, project);
                break;
            case "getAgentsForBuild":
                getAgentsForBuild(request, response, xmlResponse, project);
                break;
        }
        errors.serialize(xmlResponse);
    }

    private void doSave(@NotNull HttpServletRequest request, @NotNull Element xmlResponse, @NotNull SProject project, @NotNull ActionErrors errors) {
        AgentPriorityBean priorityBean = new AgentPriorityBean();
        bindFromRequest(request, project, priorityBean);
        validate(priorityBean, errors);

        if (errors.hasErrors()) {
            return;
        }

        if (StringUtil.isEmpty(priorityBean.getPriorityId())) {
            doCreate(request, project, priorityBean);
        } else {
            doUpdate(request, project, priorityBean);
        }
    }

    private void bindFromRequest(@NotNull HttpServletRequest request, @NotNull SProject project, @NotNull AgentPriorityBean priorityBean) {
        FormUtil.bindFromRequest(request, priorityBean);
        PluginPropertiesUtil.bindPropertiesFromRequest(request, priorityBean);

        String priorityId = priorityBean.getPriorityId();
        if (StringUtil.isNotEmpty(priorityId)) {
            AgentPriorityDescriptor descriptor = priorityManager.findPriorityById(project, priorityId);
            if (null != descriptor) {
                priorityBean.setPriorityType(descriptor.getAgentPriority().getType());
            }
        }
    }

    private void validate(@NotNull AgentPriorityBean priorityBean, @NotNull ActionErrors errors) {
        String priorityType = priorityBean.getPriorityType();

        if (StringUtil.isNotEmpty(priorityType)) {
            AgentPriority priority = priorityManager.getPriorityByType(priorityType);
            if (null != priority) {
                errors.fillErrors(priority.getPropertiesProcessor(), priorityBean.getProperties());
                return;
            }
        }
        errors.addError(TYPE_PARAM, String.format("Unknown priority type '%s'!", priorityType));
    }

    private void doCreate(@NotNull HttpServletRequest request, @NotNull SProject project, @NotNull AgentPriorityBean priorityBean) {
        assert null != priorityBean.getPriorityType();

        List<AgentPriorityDescriptor> descriptors = priorityManager.configuredForProject(project);
        int priority_order = descriptors.size() + 1;
        Map<String, String> properties = new HashMap<>(priorityBean.getProperties());
        properties.put(PRIORITY_ORDER, Integer.toString(priority_order));

        priorityManager.addPriority(project, priorityBean.getPriorityType(), properties);
        project.persist(actionFactory.createAction(project, String.format("Agent priority %s created.", priorityBean.getPriorityType())));

        getOrCreateMessages(request).addMessage("priorityAdded", "Agent priority successfully created.");
    }

    private void doUpdate(@NotNull HttpServletRequest request, @NotNull SProject project, @NotNull AgentPriorityBean priorityBean) {
        assert null != priorityBean.getPriorityId();
        assert null != priorityBean.getPriorityType();
        priorityManager.updatePriority(project, priorityBean.getPriorityId(), priorityBean.getPriorityType(), priorityBean.getProperties());
        project.persist(actionFactory.createAction(project, String.format("Agent priority %s updated.", priorityBean.getPriorityType())));
        getOrCreateMessages(request).addMessage("priorityUpdated", "Agent priority was updated.");
    }

    private void doRemove(@NotNull HttpServletRequest request, @NotNull SProject project) {
        final String priorityId = request.getParameter("priorityId");
        assert null != priorityId;
        SProjectFeatureDescriptor feature = project.removeFeature(priorityId);
        if (null != feature) {
            final String actionDescription = "Agent priority removed";
            project.persist(actionFactory.createAction(project, actionDescription));
            getOrCreateMessages(request).addMessage("priorityRemove", actionDescription);
        }
    }

    private void doReorder(HttpServletRequest request, SProject project) {
        String order = request.getParameter(PRIORITY_ORDER);
        if (null == order) {
            return;
        }

        Iterable<String> ids = Splitter.on(';')
                .trimResults()
                .omitEmptyStrings()
                .split(order);

        int index = 0;
        for (String id : ids) {
            AgentPriorityDescriptor descriptor = priorityManager.findPriorityById(project, id);
            if (null == descriptor) {
                // TODO: log error
                continue;
            }

            Map<String, String> params = new HashMap<>(descriptor.getParameters());
            params.put(PRIORITY_ORDER, Integer.toString(index));
            priorityManager.updatePriority(project, descriptor.getId(), descriptor.getAgentPriority().getType(), params);
            index++;
        }

        project.persist(actionFactory.createAction(project, "Agent priority order was updated."));
        getOrCreateMessages(request).addMessage("priorityUpdated", "Agent priority was updated.");
    }

    private void getAgentsForBuild(HttpServletRequest request, HttpServletResponse response, Element xmlResponse, SProject project) {
        String buildTypeId = request.getParameter("buildTypeId");
        if (null == buildTypeId) {
            return;
        }

        SBuildType buildType = projectManager.findBuildTypeById(buildTypeId);
        if (null == buildType) {
            return;
        }

        List<SBuildAgent> originalAgents = buildType.getCanRunAndCompatibleAgents(false);
        List<SBuildAgent> sortedAgents = priorityManager.sort(originalAgents, buildType.getProject(), buildType);

        for (SBuildAgent agent : sortedAgents) {
            final Element buildAgent = new Element("buildAgent");
            buildAgent.setContent(new Text(agent.getName()));
            buildAgent.setAttribute("id", Integer.toString(agent.getId()));

            xmlResponse.addContent(buildAgent);
        }
    }
}
