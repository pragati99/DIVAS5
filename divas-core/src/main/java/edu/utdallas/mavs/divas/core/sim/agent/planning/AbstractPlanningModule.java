package edu.utdallas.mavs.divas.core.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;

/**
 * An abstract implementation of the agent's PCM. (Planning and Control Module)
 * 
 * @param <KM>
 *        The agent's knowledge module
 * @param <TM>
 *        The agent's Task module
 */
public abstract class AbstractPlanningModule<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>> implements PlanningModule<KM, TM>, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's reactionModule
     */
    protected KM              knowledgeModule;

    /**
     * Create a new PCM module using the plan generator and plan executor.
     * 
     * @param knowledgeModule the agent's knowledge module
     */
    public AbstractPlanningModule(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
    }
}
