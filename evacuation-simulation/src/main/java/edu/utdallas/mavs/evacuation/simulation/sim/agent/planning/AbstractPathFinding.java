package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;

public abstract class AbstractPathFinding<KM extends KnowledgeModule<?>> implements PathFinding<KM>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    protected KM knowledgeModule;

    public AbstractPathFinding(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
    }
}
