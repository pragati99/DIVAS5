package edu.utdallas.mavs.divas.core.sim.agent.interaction.communication;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Basic Agent's Communication Module
 */
public class SimpleAgentCommunicationModule implements AgentCommunicationModule, Serializable
{
    private static final long                                  serialVersionUID = 1L;

    /**
     * The agent's message inbox
     */
    protected static final ConcurrentLinkedQueue<AgentMessage> inbox            = new ConcurrentLinkedQueue<AgentMessage>();
    /**
     * The agent's message outbox
     */
    protected static final ConcurrentLinkedQueue<AgentMessage> outbox           = new ConcurrentLinkedQueue<AgentMessage>();

    /**
     * create a virtual agent com module
     * 
     * @param agentId the ID of the agent
     */
    public SimpleAgentCommunicationModule(int agentId)
    {
        super();
        AgentMessageHandler.getInstance().addAgentComModule(agentId, this);
    }

    @Override
    public void addMessageToOutbox(AgentMessage am)
    {
        outbox.add(am);
    }

    /**
     * Add a message to the agent's inbox.
     * 
     * @param am
     *        an agent message
     */
    @Override
    public void addMessageToInbox(AgentMessage am)
    {
        inbox.add(am);
    }

    @Override
    public AgentMessage getMessageFromInbox()
    {
        return inbox.poll();
    }

    @Override
    public void dispatchMessages()
    {
        AgentMessage am;
        while((am = outbox.poll()) != null)
        {
            AgentMessageHandler.getInstance().addMessageToOutbox(am);
        }
    }
}
