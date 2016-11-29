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

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.github.grundic.agentPriority.prioritisation.AgentPriorityDescriptor;
import com.github.grundic.agentPriority.prioritisation.impl.ByName;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.buildDistribution.AgentsFilterResult;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * User: g.chernyshev
 * Date: 29/11/16
 * Time: 14:31
 */
public class TestByNameAgentPriority extends AbstractAgentPriorityTest {
    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        when(agent1.getName()).thenReturn("agent1");
        when(agent2.getName()).thenReturn("agent2");
        when(agent3.getName()).thenReturn("agent3");
        when(agent4.getName()).thenReturn("Agent4"); // capital
        when(agent5.getName()).thenReturn("agent5");
        when(agent6.getName()).thenReturn("agent6");
    }


    @DataProvider
    public Object[][] testData() {
        initMocks();

        AgentPriority byNameCaseSensitive = spy(new ByName());
        AgentPriority byNameCaseInsensitive = spy(new ByName());

        doReturn(new HashMap<String, String>() {{
            put("caseInsensitive", "true");
        }}).when(byNameCaseInsensitive).getParameters();

        AgentPriorityDescriptor descriptorCaseSensitive = mock(AgentPriorityDescriptor.class);
        when(descriptorCaseSensitive.getAgentPriority()).thenReturn(byNameCaseSensitive);

        AgentPriorityDescriptor descriptorCaseInsensitive = mock(AgentPriorityDescriptor.class);
        when(descriptorCaseInsensitive.getAgentPriority()).thenReturn(byNameCaseInsensitive);

        return new Object[][]{
                // |    DESCRIPTOR        |  GIVEN AGENTS         |  EXPECTED ORDERED AGENTS    |
                {descriptorCaseSensitive, Collections.emptyList(), Collections.emptyList()},
                {descriptorCaseSensitive, Collections.singletonList(agent3), Collections.singletonList(agent3)},
                {
                        descriptorCaseSensitive,
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent4, agent1, agent2, agent3, agent5, agent6)
                },

                {descriptorCaseInsensitive, Collections.emptyList(), Collections.emptyList()},
                {descriptorCaseInsensitive, Collections.singletonList(agent3), Collections.singletonList(agent3)},
                {
                        descriptorCaseInsensitive,
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent2, agent3, agent4, agent5, agent6)
                }
        };
    }


    @Test(dataProvider = "testData")
    void testByName(AgentPriorityDescriptor descriptor, List<SBuildAgent> agentsForStartingBuild, List<SBuildAgent> expectedAgents) {
        doReturn(Collections.singletonList(descriptor)).when(priorityManager).configuredForProjectWithParents(any(SProject.class));

        when(context.getAgentsForStartingBuild()).thenReturn(agentsForStartingBuild);
        AgentsFilterResult result = filter.filterAgents(context);

        List<SBuildAgent> actualAgents = result.getFilteredConnectedAgents();
        Assert.assertEquals(actualAgents, expectedAgents);
    }
}
