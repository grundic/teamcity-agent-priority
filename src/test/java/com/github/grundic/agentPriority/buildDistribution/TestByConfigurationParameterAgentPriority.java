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
import com.github.grundic.agentPriority.prioritisation.impl.ByConfigurationParameter;
import jetbrains.buildServer.serverSide.SBuildAgent;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Mockito.*;

/**
 * User: g.chernyshev
 * Date: 29/11/16
 * Time: 14:31
 */
public class TestByConfigurationParameterAgentPriority extends AbstractAgentPriorityTest {

    private final static String parameterName = "priority";

    @Mock(name = "agentWithEmptyValue")
    private SBuildAgent agentWithEmptyValue;
    @Mock(name = "agentWithNonInt")
    private SBuildAgent agentWithNonInt;
    @Mock(name = "agentWithEmptyMap")
    private SBuildAgent agentWithEmptyMap;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        when(agent1.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "1001");
        }});

        when(agent2.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "756");
        }});

        when(agent3.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "512");
        }});

        when(agent4.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "311");
        }});

        when(agent5.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "8");
        }});

        when(agent6.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "-4");
        }});

        when(agentWithEmptyValue.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "");
        }});

        when(agentWithNonInt.getConfigurationParameters()).thenReturn(new HashMap<String, String>() {{
            put(parameterName, "foobar");
        }});

        when(agentWithEmptyMap.getConfigurationParameters()).thenReturn(new HashMap<>());

    }


    @DataProvider
    public Object[][] testData() {
        initMocks();

        AgentPriority byConfigurationParameter = spy(new ByConfigurationParameter());

        doReturn(new HashMap<String, String>() {{
            put("parameterName", parameterName);
        }}).when(byConfigurationParameter).getParameters();

        AgentPriorityDescriptor descriptor = mock(AgentPriorityDescriptor.class);
        when(descriptor.getAgentPriority()).thenReturn(byConfigurationParameter);

        return new Object[][]{
                // |    DESCRIPTOR        |  GIVEN AGENTS         |  EXPECTED ORDERED AGENTS    |
                {descriptor, Collections.emptyList(), Collections.emptyList()},
                {descriptor, Collections.singletonList(agent3), Collections.singletonList(agent3)},
                {descriptor, Collections.singletonList(agentWithEmptyValue), Collections.singletonList(agentWithEmptyValue)},
                {descriptor, Collections.singletonList(agentWithNonInt), Collections.singletonList(agentWithNonInt)},
                {descriptor, Collections.singletonList(agentWithEmptyMap), Collections.singletonList(agentWithEmptyMap)},
                {
                        descriptor,
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent2, agent3, agent4, agent5, agent6)
                },
                {
                        descriptor,
                        Arrays.asList(agent3, agentWithEmptyValue, agent1, agent4, agent2),
                        Arrays.asList(agent1, agent2, agent3, agent4, agentWithEmptyValue)
                },
                {
                        descriptor,
                        Arrays.asList(agent3, agentWithNonInt, agent1, agent4, agent2),
                        Arrays.asList(agent1, agent2, agent3, agent4, agentWithNonInt)
                },
                {
                        descriptor,
                        Arrays.asList(agent3, agentWithEmptyMap, agent1, agent4, agent2),
                        Arrays.asList(agent1, agent2, agent3, agent4, agentWithEmptyMap)
                },
                {
                        descriptor,
                        Arrays.asList(agentWithEmptyValue, agent5, agentWithNonInt, agent2, agent6, agentWithEmptyMap, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent2, agent3, agent4, agent5, agent6, agentWithEmptyValue, agentWithNonInt, agentWithEmptyMap)
                },
        };
    }
}
