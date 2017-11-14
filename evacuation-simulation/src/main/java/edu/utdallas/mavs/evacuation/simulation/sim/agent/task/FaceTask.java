package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTask;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangeHeadingStimulus;

/**
 * This class describes a face task.
 * <p>
 * A face task allows an agent to look into a specific direction.
 */
public class FaceTask extends AbstractTask
{
    private static final long  serialVersionUID = 1L;

    /**
     * The name of this task
     */
    public static final String NAME             = "Face";

    /**
     * The type of this task
     */
    public static final String TYPE             = "Heading";

    /**
     * The heading direction of the agent after this task is executed
     */
    protected Vector3f         heading;

    /**
     * Creates a new face task
     * 
     * @param enabled
     *        the enabled status of the task
     */
    public FaceTask(boolean enabled)
    {
        this(-1, enabled);
    }

    /**
     * Creates a new face task
     * 
     * @param executionCycle
     *        the schedule execution cycle of the task
     * @param enabled
     *        the enabled status of the task
     */
    public FaceTask(long executionCycle, boolean enabled)
    {
        super(NAME, TYPE, executionCycle, enabled);
    }

    @Override
    public Stimuli execute(int agentId)
    {
        Stimuli stimuli = new Stimuli();
        stimuli.add(new ChangeHeadingStimulus(agentId, heading));
        return stimuli;
    }

    @Override
    public Task createTask(long executionCycle)
    {
        return new FaceTask(executionCycle, enabled);
    }

    /**
     * Updates the direction to which the agent should be headed
     * 
     * @param heading
     *        the new heading direction
     */
    public void setHeading(Vector3f heading)
    {
        this.heading = heading;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%.1f,%.1f,%.1f]", getName(), heading.x, heading.y, heading.z);
    }

}
