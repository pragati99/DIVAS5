package edu.utdallas.mavs.evacuation.simulation.sim.agent.task;

import edu.utdallas.mavs.divas.core.sim.agent.task.AbstractTask;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.IdleStimulus;

/**
 * This class describes a open door task.
 */
public class IdleTask extends AbstractTask
{
    private static final long  serialVersionUID = 1L;

    /**
     * The name of this task
     */
    public static final String NAME             = "Idle";

    /**
     * The type of this task
     */
    public static final String TYPE             = "Idle";

    /**
     * Creates a new open door task
     * 
     * @param enabled
     *        the enabled status of the task
     */
    public IdleTask(boolean enabled)
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
    public IdleTask(long executionCycle, boolean enabled)
    {
        super(NAME, TYPE, executionCycle, enabled);
    }

    @Override
    public Stimuli execute(int agentId)
    {
        Stimuli stimuli = new Stimuli();
        stimuli.add(new IdleStimulus(agentId));
        return stimuli;
    }

    @Override
    public Task createTask(long executionCycle)
    {
        return new IdleTask(executionCycle, enabled);
    }

    @Override
    public String toString()
    {
        return "IDLEING";
    }


}
