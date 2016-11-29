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
import com.github.grundic.agentPriority.prioritisation.impl.ByCpuIndex;
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
public class TestByCpuIndexAgentPriority extends AbstractAgentPriorityTest {
    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        when(agent1.getCpuBenchmarkIndex()).thenReturn(1001);
        when(agent2.getCpuBenchmarkIndex()).thenReturn(999);
        when(agent3.getCpuBenchmarkIndex()).thenReturn(710);
        when(agent4.getCpuBenchmarkIndex()).thenReturn(-10);
        when(agent5.getCpuBenchmarkIndex()).thenReturn(311);
        when(agent6.getCpuBenchmarkIndex()).thenReturn(0);
    }


    @DataProvider
    public Object[][] testData() {
        initMocks();

        AgentPriority byCpuIndex = spy(new ByCpuIndex());

        AgentPriorityDescriptor descriptor = mock(AgentPriorityDescriptor.class);
        when(descriptor.getAgentPriority()).thenReturn(byCpuIndex);

        return new Object[][]{
                // |    DESCRIPTOR        |  GIVEN AGENTS         |  EXPECTED ORDERED AGENTS    |
                {descriptor, Collections.emptyList(), Collections.emptyList()},
                {descriptor, Collections.singletonList(agent3), Collections.singletonList(agent3)},
                {
                        descriptor,
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent2, agent3, agent5, agent6, agent4)
                },
        };
    }
}
