package com.github.grundic.agentPriority.config;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import com.github.grundic.agentPriority.prioritisation.ByConfigurationParameter;
import com.github.grundic.agentPriority.prioritisation.ByName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: g.chernyshev
 * Date: 06/11/16
 * Time: 17:14
 */

@XmlRootElement()
public class AgentPriorityConfiguration {
    public AgentPriorityConfiguration() {
    }

    private List<AgentPriority> priorities;

    @XmlElementWrapper(name = "priorities")
    @XmlElements({
            @XmlElement(name = "byName", type = ByName.class),
            @XmlElement(name = "byConfigurationParameter", type = ByConfigurationParameter.class)
    })
    public List<AgentPriority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<AgentPriority> priorities) {
        this.priorities = priorities;
    }
}
