package edu.utdallas.mavs.divas.core.spec.agent;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;

/**
 * Helper method for creating an agent.
 */
public abstract class AgentLoader
{
    /**
     * Loads an agent specification
     * 
     * @param modelName
     *        The agent model name.
     * @return AgentState
     */
    public abstract AgentState createAgent(String modelName);
}
