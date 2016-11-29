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
import jetbrains.buildServer.ExtensionsProvider;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import static org.mockito.Mockito.*;


/**
 * User: g.chernyshev
 * Date: 29/11/16
 * Time: 10:20
 */
public abstract class AbstractAgentPriorityTest {

    @Mock(name = "agent1")
    SBuildAgent agent1;
    @Mock(name = "agent2")
    SBuildAgent agent2;
    @Mock(name = "agent3")
    SBuildAgent agent3;
    @Mock(name = "agent4")
    SBuildAgent agent4;
    @Mock(name = "agent5")
    SBuildAgent agent5;
    @Mock(name = "agent6")
    SBuildAgent agent6;


    AgentPriorityManager priorityManager = spy(new AgentPriorityManager(mock(ExtensionsProvider.class)));
    AgentsFilterContext context = mock(AgentsFilterContext.class, RETURNS_DEEP_STUBS);

    private ProjectManager projectManager = mock(ProjectManager.class);

    PriorityAgentsFilter filter = new PriorityAgentsFilter(priorityManager, projectManager);

    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        SBuildType buildType = mock(SBuildType.class);
        SProject project = mock(SProject.class);

        when(projectManager.findBuildTypeById(anyString())).thenReturn(buildType);
        when(buildType.getProject()).thenReturn(project);
    }
}
