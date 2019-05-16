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
import com.github.grundic.agentPriority.prioritisation.impl.ByPool;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.agentPools.AgentPool;
import jetbrains.buildServer.serverSide.agentPools.AgentPoolManager;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * User: AlexanderJReid
 * Date: 14/05/2019
 */
public class TestByPoolAgentPriority extends AbstractAgentPriorityTest {

    private final static String poolName = "poolName";

    @Mock(name = "agentWithEmptyValue")
    private AgentPoolManager agentPoolManager;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        setupTestAgent(agent1, "agent1", 0, "Default");
        setupTestAgent(agent2, "agent2", 1, "Foo");
        setupTestAgent(agent3, "agent3", 2, "Bar");
        setupTestAgent(agent4, "agent4", 3, "bar");

        ArrayList<AgentPool> allPools = new ArrayList<>();
        allPools.add(createTestAgentPool(0, "Default"));
        allPools.add(createTestAgentPool(1, "Foo"));
        allPools.add(createTestAgentPool(2, "Bar"));
        allPools.add(createTestAgentPool(3, "bar"));

        when(agentPoolManager.getAllAgentPools()).thenReturn(allPools);
    }

    private AgentPool createTestAgentPool(int agentPoolIndex, String agentPoolName)
    {
        AgentPool mock = mock(AgentPool.class);
        when(mock.getAgentPoolId()).thenReturn(agentPoolIndex);
        when(mock.getName()).thenReturn(agentPoolName);
        when(agentPoolManager.findAgentPoolById(agentPoolIndex)).thenReturn(mock);
        return mock;
    }

    private void setupTestAgent(SBuildAgent agent, String agentName, int agentPoolIndex, String agentPoolName)
    {
        when(agent.getName()).thenReturn(agentName);
        when(agent.getAgentPoolId()).thenReturn(agentPoolIndex);
        when(agent.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(poolName, agentPoolName);
        }});
    }

    private AgentPriorityDescriptor getTestDescriptor(boolean isCaseSensitiveTest)
    {
        AgentPriority byPool = spy(new ByPool(agentPoolManager));
        String poolNameValue = (isCaseSensitiveTest) ? "Bar" : "Default";
        doReturn(new HashMap<String, String>() {{
            put("poolName", poolNameValue);
        }}).when(byPool).getParameters();
        AgentPriorityDescriptor descriptor = mock(AgentPriorityDescriptor.class);
        when(descriptor.getAgentPriority()).thenReturn(byPool);
        return descriptor;
    }

    @DataProvider
    public Object[][] testData() {
        initMocks();

        AgentPriorityDescriptor defaultDescriptor = getTestDescriptor(false);
        AgentPriorityDescriptor caseSensitiveDescriptor = getTestDescriptor(true);

        return new Object[][]{
                // |    DESCRIPTOR        |  GIVEN AGENTS         |  EXPECTED ORDERED AGENTS    |
                {defaultDescriptor, Collections.emptyList(), Collections.emptyList()},
                {defaultDescriptor, Collections.singletonList(agent3), Collections.singletonList(agent3)},
                //If the pool is provided, it should be increased in priority but others should stay the same
                {
                        defaultDescriptor,
                        Arrays.asList(agent3, agent1, agent2),
                        Arrays.asList(agent1, agent3, agent2)
                },
                //If the pool param is provided but not found, then the order should not be affected
                {
                        defaultDescriptor,
                        Arrays.asList(agent3, agent2),
                        Arrays.asList(agent3, agent2)
                },
                //The poolName should be treated as case sensitive
                {
                        caseSensitiveDescriptor,
                        Arrays.asList(agent4, agent3),
                        Arrays.asList(agent3, agent4)
                },
        };
    }
}
