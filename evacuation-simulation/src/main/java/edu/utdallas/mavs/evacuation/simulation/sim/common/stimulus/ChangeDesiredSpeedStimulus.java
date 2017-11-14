package edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;

/**
 * This class describes a stimulus for changing the speed of an agent.
 */
public class ChangeDesiredSpeedStimulus extends AgentStimulus
{
    private static final long serialVersionUID = 1L;

    private float             desiredSpeed;

    /**
     * Creates a new change desired speed stimulus
     * 
     * @param id
     *        the agent's ID
     * @param desiredSpeed
     *        the agent's desired speed
     */
    public ChangeDesiredSpeedStimulus(int id, float desiredSpeed)
    {
        super(id);
        this.setdesiredSpeed(desiredSpeed);
    }

    /**
     * Sets the agent's desired speed
     * 
     * @param desiredSpeed
     *        the agent's desired speed
     */
    public void setdesiredSpeed(float desiredSpeed)
    {
        this.desiredSpeed = desiredSpeed;
    }

    /**
     * Gets the agent's desired speed
     * 
     * @return the agent's desired speed
     */
    public float getdesiredSpeed()
    {
        return desiredSpeed;
    }
}
