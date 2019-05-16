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

package com.github.grundic.agentPriority.prioritisation.impl;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import jetbrains.buildServer.serverSide.SBuildAgent;
import jetbrains.buildServer.serverSide.agentPools.AgentPool;
import jetbrains.buildServer.serverSide.agentPools.AgentPoolManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * User: AlexanderJReid
 * Date: 14/05/2019
 */
public class ByPool extends AgentPriority {
    private final static String TYPE = "byPool";
    private final static String POOL_NAME = "poolName";

    private final AgentPoolManager agentPoolManager;

    public ByPool(@NotNull final AgentPoolManager agentPoolManager)
    {
        this.agentPoolManager = agentPoolManager;
    }

    @NotNull
    @Override
    public String getType() {
        return TYPE;
    }

    @NotNull
    @Override
    public String getName() {
        return "By pool";
    }

    @Nullable
    @Override
    public String apply(@Nullable SBuildAgent buildAgent) {
        if (null == buildAgent) {
            return null;
        }

        String poolNameParameter = getParameters().get(POOL_NAME);
        if (null == poolNameParameter) {
            return null;
        }

        int currentPoolId = buildAgent.getAgentPoolId();
        AgentPool currentPool = agentPoolManager.findAgentPoolById(currentPoolId);
        if (null == currentPool) {
            return null;
        }
        if (!agentPoolManager.getAllAgentPools().contains(currentPool)) {
            return null;
        }

        String currentPoolName = currentPool.getName();
        if (!currentPoolName.equals(poolNameParameter)) {
            return null;
        }
        return currentPoolName;
    }
}
