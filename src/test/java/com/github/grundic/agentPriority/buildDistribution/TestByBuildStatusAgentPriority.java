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
import com.github.grundic.agentPriority.prioritisation.impl.ByBuildStatus;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildHistory;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import jetbrains.buildServer.util.ItemProcessor;
import org.mockito.Mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * User: g.chernyshev
 * Date: 29/11/16
 * Time: 14:31
 */
public class TestByBuildStatusAgentPriority extends AbstractAgentPriorityTest {

    @Mock
    private BuildHistory buildHistory;

    @DataProvider
    public Object[][] testData() {
        initMocks();

        return new Object[][]{};
    }

    private Object[][] agent1Failed5Times() {
        return new Object[][]{
                {agent1, Status.FAILURE},
                {agent1, Status.FAILURE},
                {agent1, Status.FAILURE},
                {agent1, Status.FAILURE},
                {agent1, Status.FAILURE},
        };
    }

    private Object[][] agent1Succeed5Times() {
        return new Object[][]{
                {agent1, Status.NORMAL},
                {agent1, Status.NORMAL},
                {agent1, Status.NORMAL},
                {agent1, Status.NORMAL},
                {agent1, Status.NORMAL},
        };
    }

    private Object[][] agent1FailedThenSucceed() {
        return new Object[][]{
                {agent1, Status.NORMAL},
                {agent1, Status.FAILURE},
        };
    }

    private Object[][] agent1SucceedThenFailed() {
        return new Object[][]{
                {agent1, Status.FAILURE},
                {agent1, Status.NORMAL},
        };
    }

    private Object[][] complexHistory() {
        return new Object[][]{
                {agent1, Status.FAILURE},  // 1
                {agent2, Status.NORMAL},   // 2
                {agent1, Status.NORMAL},   // 3
                {agent5, Status.NORMAL},   // 4
                {agent5, Status.FAILURE},  // 5
                {agent2, Status.NORMAL},   // 6
                {agent1, Status.FAILURE},  // 7
                {agent1, Status.FAILURE},  // 8
                {agent3, Status.WARNING},  // 9
                {agent4, Status.NORMAL},   // 10  << should not be counted
                {agent4, Status.NORMAL},   // 11  << should not be counted
        };
    }

    private List<SFinishedBuild> buildHistory(Object[][] data) {
        List<SFinishedBuild> history = new ArrayList<>();

        for (Object item : data) {
            SBuildAgent agent = (SBuildAgent) ((Object[]) item)[0];
            Status status = (Status) ((Object[]) item)[1];

            SFinishedBuild finishedBuild = mock(SFinishedBuild.class);
            when(finishedBuild.getAgent()).thenReturn(agent);
            when(finishedBuild.getBuildStatus()).thenReturn(status);

            history.add(finishedBuild);
        }

        return history;
    }

    @DataProvider
    public Object[][] testCustomData() {
        initMocks();

        AgentPriority byCpuIndex = spy(new ByBuildStatus(buildHistory));

        AgentPriorityDescriptor descriptor = mock(AgentPriorityDescriptor.class);
        when(descriptor.getAgentPriority()).thenReturn(byCpuIndex);

        return new Object[][]{
                // |    DESCRIPTOR    |   HISTORY  |  GIVEN AGENTS         |  EXPECTED ORDERED AGENTS    |

                // Empty history
                {
                        descriptor,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        Collections.emptyList(),
                        Collections.singletonList(agent3),
                        Collections.singletonList(agent3)
                },
                {
                        descriptor,
                        Collections.emptyList(),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                },

                // agent1Failed5Times
                {
                        descriptor,
                        buildHistory(agent1Failed5Times()),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        buildHistory(agent1Failed5Times()),
                        Collections.singletonList(agent1),
                        Collections.singletonList(agent1)
                },
                {
                        descriptor,
                        buildHistory(agent1Failed5Times()),
                        Collections.singletonList(agent3),
                        Collections.singletonList(agent3)
                },
                {
                        descriptor,
                        buildHistory(agent1Failed5Times()),
                        Arrays.asList(agent5, agent1, agent2),
                        Arrays.asList(agent5, agent2, agent1),
                },
                {
                        descriptor,
                        buildHistory(agent1Failed5Times()),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent5, agent2, agent6, agent3, agent4, agent1)
                },

                // agent1Succeed5Times
                {
                        descriptor,
                        buildHistory(agent1Succeed5Times()),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        buildHistory(agent1Succeed5Times()),
                        Collections.singletonList(agent1),
                        Collections.singletonList(agent1)
                },
                {
                        descriptor,
                        buildHistory(agent1Succeed5Times()),
                        Collections.singletonList(agent3),
                        Collections.singletonList(agent3)
                },
                {
                        descriptor,
                        buildHistory(agent1Succeed5Times()),
                        Arrays.asList(agent5, agent1, agent2),
                        Arrays.asList(agent1, agent5, agent2),
                },
                {
                        descriptor,
                        buildHistory(agent1Succeed5Times()),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent5, agent2, agent6, agent3, agent4)
                },

                // agent1FailedThenSucceed
                {
                        descriptor,
                        buildHistory(agent1FailedThenSucceed()),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        buildHistory(agent1FailedThenSucceed()),
                        Collections.singletonList(agent1),
                        Collections.singletonList(agent1)
                },
                {
                        descriptor,
                        buildHistory(agent1FailedThenSucceed()),
                        Collections.singletonList(agent3),
                        Collections.singletonList(agent3)
                },
                {
                        descriptor,
                        buildHistory(agent1FailedThenSucceed()),
                        Arrays.asList(agent5, agent1, agent2),
                        Arrays.asList(agent1, agent5, agent2),
                },
                {
                        descriptor,
                        buildHistory(agent1FailedThenSucceed()),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent1, agent5, agent2, agent6, agent3, agent4)
                },

                // agent1SucceedThenFailed
                {
                        descriptor,
                        buildHistory(agent1SucceedThenFailed()),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        buildHistory(agent1SucceedThenFailed()),
                        Collections.singletonList(agent1),
                        Collections.singletonList(agent1)
                },
                {
                        descriptor,
                        buildHistory(agent1SucceedThenFailed()),
                        Collections.singletonList(agent3),
                        Collections.singletonList(agent3)
                },
                {
                        descriptor,
                        buildHistory(agent1SucceedThenFailed()),
                        Arrays.asList(agent5, agent1, agent2),
                        Arrays.asList(agent5, agent2, agent1),
                },
                {
                        descriptor,
                        buildHistory(agent1SucceedThenFailed()),
                        Arrays.asList(agent5, agent2, agent6, agent1, agent3, agent4),
                        Arrays.asList(agent5, agent2, agent6, agent3, agent4, agent1)
                },

                // complexHistory
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Collections.emptyList(),
                        Collections.emptyList()
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Collections.singletonList(agent1),
                        Collections.singletonList(agent1)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Collections.singletonList(agent4),
                        Collections.singletonList(agent4)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Collections.singletonList(agent6),
                        Collections.singletonList(agent6)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Arrays.asList(agent1, agent2),
                        Arrays.asList(agent2, agent1)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Arrays.asList(agent1, agent2, agent3),
                        Arrays.asList(agent2, agent3, agent1)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Arrays.asList(agent1, agent2, agent3, agent4), // agent's #4 history should not be considered as it > build limit.
                        Arrays.asList(agent2, agent3, agent4, agent1)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Arrays.asList(agent1, agent2, agent3, agent4, agent5),
                        Arrays.asList(agent2, agent3, agent5, agent4, agent1)
                },
                {
                        descriptor,
                        buildHistory(complexHistory()),
                        Arrays.asList(agent1, agent2, agent3, agent4, agent5,agent6),
                        Arrays.asList(agent2, agent3, agent5, agent4, agent6, agent1)
                },
        };
    }

    @Test(dataProvider = "testCustomData")
    void testFromCustomDataProvider(
            AgentPriorityDescriptor descriptor,
            List<SFinishedBuild> history,
            List<SBuildAgent> agentsForStartingBuild,
            List<SBuildAgent> expectedAgents
    ) {

        // Mock ItemProcessor invocation
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            @SuppressWarnings("unchecked")
            ItemProcessor<SFinishedBuild> processor = (ItemProcessor<SFinishedBuild>) args[5];

            for (SFinishedBuild build : history) {
                boolean result = processor.processItem(build);
                if (!result) {
                    break;
                }
            }

            return null;

        }).when(buildHistory).processEntries(anyString(), any(), anyBoolean(), anyBoolean(), anyBoolean(), any());

        testFromTestDataProvider(descriptor, agentsForStartingBuild, expectedAgents);
    }
}
