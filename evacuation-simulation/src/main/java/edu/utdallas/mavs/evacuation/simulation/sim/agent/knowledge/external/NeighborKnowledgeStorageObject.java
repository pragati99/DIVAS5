package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;

/**
 * Agent Knowledge Storage Object - Stores critical information regarding agents.
 */
public class NeighborKnowledgeStorageObject extends AgentKnowledgeStorageObject
{
    private static final long serialVersionUID = 1L;

    protected float           distance;

    protected long            cycle;

    public NeighborKnowledgeStorageObject(AgentState agent, float distance, long cycle)
    {
        super(agent);
        this.distance = distance;
        this.cycle = cycle;
    }

    public float getDistance()
    {
        return distance;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public long getCycle()
    {
        return cycle;
    }
}
