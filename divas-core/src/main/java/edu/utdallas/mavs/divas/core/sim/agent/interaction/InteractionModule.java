package edu.utdallas.mavs.divas.core.sim.agent.interaction;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.AgentMessage;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * This interface describes an agent interaction module.
 */
public interface InteractionModule
{
    /**
     * Process perception of a cell state, which represents a portion (total or partial) of the situated environment.
     * 
     * @param state
     *        a cell state
     */
    public void perceive(CellState state);

    /**
     * Send an agent message.
     * 
     * @param am
     */
    public void sendMessage(AgentMessage am);

    /**
     * Get the next message from the agent's inbox
     * 
     * @return the message
     */
    public AgentMessage getMessageFromInbox();

    /**
     * Send all messages
     */
    public void dispatchMessages();
}
