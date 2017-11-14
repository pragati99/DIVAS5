package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTask;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.MoveStimulus;

/**
 * This class describes a move task.
 * <p>
 * A move task allows an agent to move to a specific location in the environment.
 */
public class MoveTask extends AbstractTask
{
    private static final long  serialVersionUID = 1L;

    /**
     * The name of this task
     */
    public static final String NAME             = "Move";

    /**
     * The type of this task
     */
    public static final String TYPE             = "Position";

    /**
     * The position of the agent after this task is executed
     */
    private Vector3f           position;

    /**
     * Creates a new move task
     * 
     * @param enabled
     *        the enabled status of the task
     */
    public MoveTask(boolean enabled)
    {
        this(-1, enabled);
    }

    /**
     * Creates a new move task
     * 
     * @param executionCycle
     *        the schedule execution cycle of the task
     * @param enabled
     *        the enabled status of the task
     */
    public MoveTask(long executionCycle, boolean enabled)
    {
        super(NAME, TYPE, executionCycle, enabled);
    }

    @Override
    public Stimuli execute(int agentId)
    {
        Stimuli stimuli = new Stimuli();
        stimuli.add(new MoveStimulus(agentId, position));
        return stimuli;
    }

    @Override
    public Task createTask(long executionCycle)
    {
        return new MoveTask(executionCycle, enabled);
    }

    /**
     * Updates the position to which the agent should be moved
     * 
     * @param position
     *        the new position
     */
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%.1f,%.1f,%.1f]", getName(), position.x, position.y, position.z);
    }
}
