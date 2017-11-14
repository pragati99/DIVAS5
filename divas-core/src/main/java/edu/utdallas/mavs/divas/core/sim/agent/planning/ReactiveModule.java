package edu.utdallas.mavs.divas.core.sim.agent.planning;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;

/**
 * The agent's reaction module. Generates reactions to events when necessary
 * 
 * @param <KM>
 *        The agent's knowledge module.
 * @param <TM>
 *        The agent's task module.
 */
public interface ReactiveModule<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>>
{
    /**
     * Generate plans for the agent.
     * 
     * @return a plan
     */
    public Plan react();
}
