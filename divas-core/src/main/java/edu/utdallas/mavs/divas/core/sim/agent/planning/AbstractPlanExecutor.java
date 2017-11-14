package edu.utdallas.mavs.divas.core.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;

/**
 * An abstract implementation of the agent plan executor.
 * 
 * @param <KM>
 *        The Agent's Knowledge Module
 * @param <TM>
 *        The Agent's Task Module
 */
public abstract class AbstractPlanExecutor<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>> implements PlanExecutor<KM, TM>, Serializable
{
    private static final long serialVersionUID = 1L;
    /**
     * The agent's knowledge module
     */
    protected KM              knowledgeModule;

    /**
     * Create a new plan executor and set the agent's knowledge module.
     * 
     * @param knowledgeModule
     *        the agent's knowledge module
     */
    public AbstractPlanExecutor(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
    }

}
