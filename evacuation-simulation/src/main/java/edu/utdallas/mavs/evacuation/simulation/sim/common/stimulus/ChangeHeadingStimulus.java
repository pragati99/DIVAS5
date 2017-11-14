package edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;

/**
 * This class describes a stimulus for changing the heading direction of an agent.
 */
public class ChangeHeadingStimulus extends AgentStimulus
{
    private static final long serialVersionUID = 1L;
    private float             x;
    private float             y;
    private float             z;

    /**
     * Creates a new change heading stimulus
     * 
     * @param id the agent's ID
     * @param heading the agent's heading direction
     */
    public ChangeHeadingStimulus(int id, Vector3f heading)
    {
        this(id, heading.x, heading.y, heading.z);
    }

    /**
     * Creates a new change heading stimulus
     * 
     * @param id the agent's ID
     * @param x the x coordinate of the agent's heading direction
     * @param y the y coordinate of the agent's heading direction
     * @param z the z coordinate of the agent's heading direction
     */
    public ChangeHeadingStimulus(int id, float x, float y, float z)
    {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the x coordinate of the heading direction
     * 
     * @return the x coordinate of the heading direction
     */
    public float getX()
    {
        return x;
    }

    /**
     * Sets the x coordinate of the heading direction
     * 
     * @param x the x coordinate of the heading direction
     */
    public void setX(float x)
    {
        this.x = x;
    }

    /**
     * Gets the y coordinate of the heading direction
     * 
     * @return the y coordinate of the heading direction
     */
    public float getY()
    {
        return y;
    }

    /**
     * Sets the y coordinate of the heading direction
     * 
     * @param y the y coordinate of the heading direction
     */
    public void setY(float y)
    {
        this.y = y;
    }

    /**
     * Gets the z coordinate of the heading direction
     * 
     * @return the z coordinate of the heading direction
     */
    public float getZ()
    {
        return z;
    }

    /**
     * Sets the z coordinate of the heading direction
     * 
     * @param z the z coordinate of the heading direction
     */
    public void setZ(float z)
    {
        this.z = z;
    }
}
