package edu.utdallas.mavs.divas.core.sim.agent.interaction;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.AgentCommunicationModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.AgentMessage;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.PerceptionModule;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * This class describes an agent's abstract interaction module
 * 
 * @param <PM> the agent's PlanningModule type
 * @param <ACM> the agent's communication module
 */
public abstract class AbstractInteractionModule<PM extends PerceptionModule, ACM extends AgentCommunicationModule> implements InteractionModule, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's perception module
     */
    protected PM              perceptionModule;

    /**
     * The agent's communication module
     */
    protected ACM             communicationModule;

    /**
     * Constructs a new interaction module
     * 
     * @param perceptionModule
     *        the agent's perception module
     * @param communicationModule
     *        the agent's communication module
     */
    public AbstractInteractionModule(PM perceptionModule, ACM communicationModule)
    {
        super();
        this.perceptionModule = perceptionModule;
        this.communicationModule = communicationModule;
    }

    @Override
    public void perceive(CellState state)
    {
        // configures and prepares the agent's sensors
        perceptionModule.prepareSensors();

        // perceives the environment state
        perceptionModule.perceive(state);

        // combines perceptions
        perceptionModule.combinePerceptions();
    }

    @Override
    public void sendMessage(AgentMessage am)
    {
        communicationModule.addMessageToOutbox(am);
    }

    @Override
    public AgentMessage getMessageFromInbox()
    {
        return communicationModule.getMessageFromInbox();
    }

    @Override
    public void dispatchMessages()
    {
        communicationModule.dispatchMessages();
    }
}
