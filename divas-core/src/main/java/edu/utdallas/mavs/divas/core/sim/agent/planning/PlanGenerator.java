package edu.utdallas.mavs.divas.core.sim.agent.planning;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;

/**
 * The agent's plan generation module. Generates new plans which are then evaluated, stored and possibly executed.
 * 
 * @param <KM>
 *        The agent's knowledge module.
 * @param <TM>
 *        The agent's task module.
 */
public interface PlanGenerator<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>>
{
    /**
     * Generate plans for the agent.
     * 
     * @return TODO
     */
    public Plan plan();

    /**
     * Add a new goal to the agent
     * 
     * @param newGoal the new goal
     */
    public void addGoal(Goal newGoal);
}
