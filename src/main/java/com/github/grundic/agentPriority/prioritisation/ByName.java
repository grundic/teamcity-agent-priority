package com.github.grundic.agentPriority.prioritisation;

import jetbrains.buildServer.serverSide.SBuildAgent;

import javax.annotation.Nullable;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 21:06
 */
public class ByName implements AgentPriority<String> {
    @Nullable
    @Override
    public String apply(@Nullable SBuildAgent buildAgent) {
        if (null != buildAgent) {
            return buildAgent.getName();
        }
        return null;
    }
}
