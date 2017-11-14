package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTask;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.CloseDoorStimulus;

/**
 * This class describes a close door task.
 */
public class CloseDoorTask extends AbstractTask
{
    private static final long  serialVersionUID = 1L;

    /**
     * The name of this task
     */
    public static final String NAME             = "CloseDoor";

    /**
     * The type of this task
     */
    public static final String TYPE             = "CloseDoor";

    /**
     * The door ID
     */
    protected int              id;

    /**
     * Creates a new close door task
     * 
     * @param enabled
     *        the enabled status of the task
     */
    public CloseDoorTask(boolean enabled)
    {
        this(-1, enabled);
    }

    /**
     * Creates a new close door task
     * 
     * @param executionCycle
     *        the schedule execution cycle of the task
     * @param enabled
     *        the enabled status of the task
     */
    public CloseDoorTask(long executionCycle, boolean enabled)
    {
        super(NAME, TYPE, executionCycle, enabled);
    }

    @Override
    public Stimuli execute(int agentId)
    {
        Stimuli stimuli = new Stimuli();
        stimuli.add(new CloseDoorStimulus(agentId, id));
        return stimuli;
    }

    @Override
    public Task createTask(long executionCycle)
    {
        return new CloseDoorTask(executionCycle, enabled);
    }

    @Override
    public String toString()
    {
        return "DoorID: " + id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

}
