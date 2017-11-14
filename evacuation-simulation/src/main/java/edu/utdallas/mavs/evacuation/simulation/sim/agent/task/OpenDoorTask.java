package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTask;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.OpenDoorStimulus;

/**
 * This class describes a open door task.
 */
public class OpenDoorTask extends AbstractTask
{
    private static final long  serialVersionUID = 1L;

    /**
     * The name of this task
     */
    public static final String NAME             = "OpenDoor";

    /**
     * The type of this task
     */
    public static final String TYPE             = "OpenDoor";

    /**
     * The door ID
     */
    protected int              id;

    /**
     * Creates a new open door task
     * 
     * @param enabled
     *        the enabled status of the task
     */
    public OpenDoorTask(boolean enabled)
    {
        this(-1, enabled);
    }

    /**
     * Creates a new open door task
     * 
     * @param executionCycle
     *        the schedule execution cycle of the task
     * @param enabled
     *        the enabled status of the task
     */
    public OpenDoorTask(long executionCycle, boolean enabled)
    {
        super(NAME, TYPE, executionCycle, enabled);
    }

    @Override
    public Stimuli execute(int agentId)
    {
        Stimuli stimuli = new Stimuli();
        stimuli.add(new OpenDoorStimulus(agentId, id));
        return stimuli;
    }

    @Override
    public Task createTask(long executionCycle)
    {
        return new OpenDoorTask(executionCycle, enabled);
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
