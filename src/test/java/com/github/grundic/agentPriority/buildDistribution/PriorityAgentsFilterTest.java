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
import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import com.github.grundic.agentPriority.prioritisation.impl.ByBuildStatus;
import com.github.grundic.agentPriority.prioritisation.impl.ByConfigurationParameter;
import com.github.grundic.agentPriority.prioritisation.impl.ByName;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterContext;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult;
import jetbrains.buildServer.util.ItemProcessor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.*;


/**
 * User: g.chernyshev
 * Date: 19/11/16
 * Time: 21:32
 */
public class PriorityAgentsFilterTest {

    private AgentPriorityManager priorityManager;
    private ProjectManager projectManager;
    private AgentsFilterContext context;

    private List<AgentPriorityDescriptor> descriptors;

    private SBuildAgent agent1;
    private SBuildAgent agent2;
    private SBuildAgent agent3;
    private SBuildAgent agent4;
    private SBuildAgent agent5;

    @BeforeMethod
    public void setUp() throws Exception {
        priorityManager = mock(AgentPriorityManager.class);
        projectManager = mock(ProjectManager.class);
        context = mock(AgentsFilterContext.class, RETURNS_DEEP_STUBS);
        SBuildType build = mock(SBuildType.class);
        SProject project = mock(SProject.class);


        Collection<SBuildAgent> agents = new ArrayList<>();
        agent1 = mock(SBuildAgent.class, RETURNS_DEEP_STUBS);
        agent2 = mock(SBuildAgent.class, RETURNS_DEEP_STUBS);
        agent3 = mock(SBuildAgent.class, RETURNS_DEEP_STUBS);
        agent4 = mock(SBuildAgent.class, RETURNS_DEEP_STUBS);
        agent5 = mock(SBuildAgent.class, RETURNS_DEEP_STUBS);
        agents.addAll(Arrays.asList(agent2, agent5, agent1, agent4, agent3));

        descriptors = new ArrayList<>();

        when(projectManager.findBuildTypeById(anyString())).thenReturn(build);
        when(build.getProject()).thenReturn(project);
        when(priorityManager.configuredForProjectWithParents(any(SProject.class))).thenReturn(descriptors);

        when(context.getAgentsForStartingBuild()).thenReturn(agents);

        Answer<Integer> compareTo = new Answer<Integer>() {

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                SBuildAgent other = (SBuildAgent) args[0];
                return ((SBuildAgent) invocation.getMock()).getName().compareTo(other.getName());
            }
        };

        when(agent1.compareTo(any(SBuildAgent.class))).then(compareTo);
        when(agent2.compareTo(any(SBuildAgent.class))).then(compareTo);
        when(agent3.compareTo(any(SBuildAgent.class))).then(compareTo);
        when(agent4.compareTo(any(SBuildAgent.class))).then(compareTo);
        when(agent5.compareTo(any(SBuildAgent.class))).then(compareTo);

        when(agent1.getName()).thenReturn("agent1");
        when(agent2.getName()).thenReturn("agent2");
        when(agent3.getName()).thenReturn("agent3");
        when(agent4.getName()).thenReturn("agent4");
        when(agent5.getName()).thenReturn("agent5");

        when(agent1.getConfigurationParameters().get(anyString())).thenReturn("10");
        when(agent2.getConfigurationParameters().get(anyString())).thenReturn("4");
        when(agent3.getConfigurationParameters().get(anyString())).thenReturn("3");
        when(agent4.getConfigurationParameters().get(anyString())).thenReturn("2");
        when(agent5.getConfigurationParameters().get(anyString())).thenReturn("1");
    }

    @Test
    public void testFilterAgentsByName() throws Exception {
        AgentPriorityDescriptor byName = mock(AgentPriorityDescriptor.class);
        descriptors.add(byName);
        when(byName.getAgentPriority()).thenReturn(new ByName());

        PriorityAgentsFilter filter = new PriorityAgentsFilter(priorityManager, projectManager);
        AgentsFilterResult result = filter.filterAgents(context);

        List<String> names = new ArrayList<>();
        Assert.assertNotNull(result.getFilteredConnectedAgents());
        for (SBuildAgent agent : result.getFilteredConnectedAgents()) {
            names.add(agent.getName());
        }

        Assert.assertEquals(names, Arrays.asList("agent1", "agent2", "agent3", "agent4", "agent5"));
    }

    @Test
    public void testFilterAgentsByConfigurationParameter() throws Exception {
        AgentPriorityDescriptor byConfigurationParameter = mock(AgentPriorityDescriptor.class);
        descriptors.add(byConfigurationParameter);
        AgentPriority priority = new ByConfigurationParameter();
        Map<String, String> params = new HashMap<>();
        params.put("parameterName", "priority");
        priority.setParameters(params);
        when(byConfigurationParameter.getAgentPriority()).thenReturn(priority);


        PriorityAgentsFilter filter = new PriorityAgentsFilter(priorityManager, projectManager);
        AgentsFilterResult result = filter.filterAgents(context);

        List<String> names = new ArrayList<>();
        Assert.assertNotNull(result.getFilteredConnectedAgents());
        for (SBuildAgent agent : result.getFilteredConnectedAgents()) {
            names.add(agent.getName());
        }

        Assert.assertEquals(names, Arrays.asList("agent5", "agent4", "agent3", "agent2", "agent1"));
    }

    @Test
    public void testFilterAgentsByBuildStatus() throws Exception {
        AgentPriorityDescriptor byConfigurationParameter = mock(AgentPriorityDescriptor.class);
        descriptors.add(byConfigurationParameter);
        BuildHistory buildHistory = mock(BuildHistory.class);

        SFinishedBuild finishedBuild1 = mock(SFinishedBuild.class);
        SFinishedBuild finishedBuild2 = mock(SFinishedBuild.class);
        SFinishedBuild finishedBuild3 = mock(SFinishedBuild.class);
        Status buildStatus1 = Status.FAILURE;
        Status buildStatus2 = Status.ERROR;
        Status buildStatus3 = Status.NORMAL;

        when(finishedBuild1.getAgent()).thenReturn(agent1);
        when(finishedBuild2.getAgent()).thenReturn(agent1);
        when(finishedBuild3.getAgent()).thenReturn(agent1);

        when(finishedBuild1.getBuildStatus()).thenReturn(buildStatus1);
        when(finishedBuild2.getBuildStatus()).thenReturn(buildStatus2);
        when(finishedBuild3.getBuildStatus()).thenReturn(buildStatus3);

        List<SFinishedBuild> history = new ArrayList<>();
        history.addAll(Arrays.asList(finishedBuild1, finishedBuild2, finishedBuild3));

        Stubber stubber = doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ItemProcessor<SFinishedBuild> processor = (ItemProcessor<SFinishedBuild>) args[5];

            for (SFinishedBuild build: history) {
                boolean result = processor.processItem(build);
                if (!result) {
                    break;
                }
            }

            return null;

        });

        stubber.when(buildHistory).processEntries(anyString(), any(), anyBoolean(), anyBoolean(), anyBoolean(), any());

        AgentPriority priority = new ByBuildStatus(buildHistory);
        when(byConfigurationParameter.getAgentPriority()).thenReturn(priority);

        PriorityAgentsFilter filter = new PriorityAgentsFilter(priorityManager, projectManager);
        AgentsFilterResult result = filter.filterAgents(context);

        List<String> names = new ArrayList<>();
        Assert.assertNotNull(result.getFilteredConnectedAgents());
        for (SBuildAgent agent : result.getFilteredConnectedAgents()) {
            names.add(agent.getName());
        }

        Assert.assertEquals(names, Arrays.asList("agent2", "agent5", "agent4", "agent3", "agent1"));
    }
}