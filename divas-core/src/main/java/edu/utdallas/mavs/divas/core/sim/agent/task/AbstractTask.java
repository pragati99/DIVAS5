package edu.utdallas.mavs.divas.core.sim.agent.task;

import java.io.Serializable;

/**
 * This class describes an abstract agent task.
 * <p>
 * The outcome of an executed task represents an intention, which must pass through the environment constraints
 * validation such as the environment's physical laws.
 */
public abstract class AbstractTask implements Task, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The name of the task
     */
    protected String          name;

    /**
     * The type of the task
     */
    protected String          type;

    /**
     * The enabled status of the task
     */
    protected boolean         enabled;

    /**
     * The schedule execution cycle of the task
     */
    protected long            executionCycle;

    /**
     * Constructs a new agent task
     * 
     * @param name the name of the task
     * @param type the type of the task
     * @param executionCycle the schedule execution cycle of the task
     * @param enabled the enabled status of the task
     */
    public AbstractTask(String name, String type, long executionCycle, boolean enabled)
    {
        super();
        this.name = name;
        this.type = type;
        this.executionCycle = executionCycle;
        this.enabled = enabled;
    }

    /**
     * Gets the schedule execution cycle number of this task
     * 
     * @return the cycle number of this task
     */
    public long getTickNumber()
    {
        return executionCycle;
    }

    /**
     * Updates this task execution cycle number
     * 
     * @param l the scheduled cycle number
     */
    public void setTickNumber(long l)
    {
        this.executionCycle = l;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String toString()
    {
        return name;
    }

}
