package edu.utdallas.mavs.divas.core.sim.agent.knowledge;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;

/**
 * An Abstract Knowledge Module
 * 
 * @param <S>
 *        the agent state type
 */
public abstract class AbstractKnowledgeModule<S extends AgentState> implements KnowledgeModule<S>, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Simulation Cycle
     */
    protected long            cycle;

    /**
     * Agent's own state
     */
    protected S               self;

    public AbstractKnowledgeModule(S self)
    {
        this.self = self;
    }

    @Override
    public S getSelf()
    {
        return self;
    }

    @Override
    public void setSelf(S state)
    {
        this.self = state;
    }

    @Override
    public void setTime(long cycle)
    {
        this.cycle = cycle;
    }

    @Override
    public long getTime()
    {
        return cycle;
    }

    @Override
    public int getId()
    {
        return self.getID();
    }

    @Override
    public void setId(int id)
    {
        self.setID(id);
    }
}
