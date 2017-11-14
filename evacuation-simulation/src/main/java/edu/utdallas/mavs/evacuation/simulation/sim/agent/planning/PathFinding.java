package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;

/**
 * The Agent's Path Finding Module
 * 
 * @param <KM>
 *        the agent's knowledge module
 */
public interface PathFinding<KM extends KnowledgeModule>
{
    /**
     * Find a path to the specified goal
     * 
     * @param vecGoal
     *        the vector point to the goal
     * @param generatedPlan
     *        the plan to add path planning to
     */
    public void pathplan(Vector3f vecGoal, Plan generatedPlan);

    /**
     * A method to notify the path finding that the agent has reacted to an event without planning.
     */
    public void notifyReact();

    public void notifyUserGoalChange();
}
