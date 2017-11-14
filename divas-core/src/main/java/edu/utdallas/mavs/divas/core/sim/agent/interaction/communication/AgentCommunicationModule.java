package edu.utdallas.mavs.divas.core.sim.agent.interaction.communication;

/**
 * Agent communication module.
 * Handles all agent to agent communication.
 */
public interface AgentCommunicationModule
{
    /**
     * Get an agent message from the inbox
     * 
     * @return the next message in the inbox
     */
    public AgentMessage getMessageFromInbox();

    /**
     * Send all agent messages to post office
     */
    public void dispatchMessages();

    /**
     * Add a new agent message to outbox
     * 
     * @param am agent message
     */
    public void addMessageToOutbox(AgentMessage am);

    /**
     * Add a new agent message to inbox
     * 
     * @param am agent message
     */
    public void addMessageToInbox(AgentMessage am);
}
