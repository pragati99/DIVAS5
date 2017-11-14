package edu.utdallas.mavs.divas.core.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;

/**
 * An abstract implementation of the agent's reaction module.
 * 
 * @param <KM>
 *        The agent's knowledge module
 * @param <TM>
 *        The agent's task module
 */
public abstract class AbstractReactionModule<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>> implements ReactiveModule<KM, TM>, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's knowledge module
     */
    protected KM              knowledgeModule;
    /**
     * The agent's task module
     */
    protected TM              taskModule;

    /**
     * Create a new agent reaction module using the knowledge module and task module.
     * 
     * @param knowledgeModule
     *        the agent's knowledge module
     * @param taskModule
     *        the agent's task module
     */
    public AbstractReactionModule(KM knowledgeModule, TM taskModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
        this.taskModule = taskModule;
    }
}
