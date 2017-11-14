package edu.utdallas.mavs.divas.core.sim.agent.task;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;

/**
 * This interface describes the behavior of an agent's task module.
 * 
 * @param <KM> the agent's knowledge module type
 */
public interface TaskModule<KM extends KnowledgeModule<?>>
{
    /**
     * Creates a new task
     * 
     * @param type the type of the new task
     * @param executionCycle the schedule execution cycle of the task
     * @return the newly created task
     */
    public Task createTask(String type, long executionCycle);

    /**
     * Checks if the agent has the definition of a specific type of task in its task module
     * 
     * @param type the type of the task
     * @return a boolean indicating if the agent has this task in its task module or not
     */
    public boolean containsTask(String type);
}
