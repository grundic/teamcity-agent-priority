package com.github.grundic.agentPriority.prioritisation;

import jetbrains.buildServer.serverSide.SBuildAgent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: g.chernyshev
 * Date: 02/11/16
 * Time: 21:07
 */
public class ByConfigurationParameter implements AgentPriority<String> {

    @NotNull
    @XmlElement
    private final String name; // TODO parameter could be int

    public ByConfigurationParameter(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }


    @Nullable
    @Override
    public String apply(@Nullable SBuildAgent buildAgent) {
        if (null != buildAgent) {
            return buildAgent.getConfigurationParameters().get(name);
        }
        return null;
    }

}
