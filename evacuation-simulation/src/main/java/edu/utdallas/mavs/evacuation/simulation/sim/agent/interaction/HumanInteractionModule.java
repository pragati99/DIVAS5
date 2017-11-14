package edu.utdallas.mavs.evacuation.simulation.sim.agent.interaction;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.AbstractInteractionModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.AgentCommunicationModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;

/**
 * This class describes a human agent's abstract interaction module
 */
public class HumanInteractionModule extends AbstractInteractionModule<HumanPerceptionModule, AgentCommunicationModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new human interaction module
     * 
     * @param perceptionModule
     *        the agent's perception module
     * @param communicationModule
     *        The agent's communication module
     */
    public HumanInteractionModule(HumanPerceptionModule perceptionModule, AgentCommunicationModule communicationModule)
    {
        super(perceptionModule, communicationModule);
    }
}
