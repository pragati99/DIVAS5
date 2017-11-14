package edu.utdallas.mavs.divas.core.sim.agent.planning;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;

/**
 * The agent's planning and control module (PCM). Oversees the plan generator, evaluator, storage and executor.
 * 
 * @param <KM>
 *        The Agent's Knowledge Module
 * @param <TM>
 *        The Agent's Task Module
 */
public interface PlanningModule<KM extends KnowledgeModule<?>, TM extends TaskModule<KM>>
{
    /**
     * Have the agent plan.
     * 
     * @return The resultant Stimuli from the plan to be executed
     */
    public Stimuli plan();

    /**
     * add new goal
     * 
     * @param newGoal
     */
    public void addGoal(Goal newGoal);
}
