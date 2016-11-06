package com.github.grundic.agentPriority.config;

import com.github.grundic.agentPriority.prioritisation.AgentPriority;
import jetbrains.buildServer.serverSide.SProject;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

/**
 * User: g.chernyshev
 * Date: 06/11/16
 * Time: 15:53
 */
public class ConfigurationHelper {

    @NotNull
    private final static String CONFIG_NAME = "agent-priorities.xml";

    private static File getProjectConfig(@NotNull SProject project) {
        return new File(project.getConfigDirectory(), CONFIG_NAME);
    }

    public static void save(@NotNull SProject project, @NotNull AgentPriorityConfiguration configuration) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AgentPriorityConfiguration.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxbMarshaller.marshal(configuration, getProjectConfig(project));
    }


    @NotNull
    public static List<AgentPriority> load(@NotNull SProject project) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(AgentPriorityConfiguration.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        AgentPriorityConfiguration configuration = (AgentPriorityConfiguration) jaxbUnmarshaller.unmarshal(getProjectConfig(project));
        return configuration.getPriorities();
    }
}
