package com.github.grundic.agentPriority.prioritisation;

import com.google.common.base.Function;
import jetbrains.buildServer.serverSide.SBuildAgent;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 21:03
 */

public interface AgentPriority<T> extends Function<SBuildAgent, T> {

}
