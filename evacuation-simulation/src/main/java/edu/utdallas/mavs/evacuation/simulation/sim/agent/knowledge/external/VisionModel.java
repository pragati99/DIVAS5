package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import java.io.Serializable;

/**
 * Store information about something (anything) that is visible.
 */
public class VisionModel implements Serializable
{
    /**
     * Serializer ID
     */
    private static final long serialVersionUID = 1L;
    /**
     * The type of the thing that is visible.
     */
    String                    type;
    /**
     * The ID of the visible thing.
     */
    int                       id;
    /**
     * Indicates how visible the thing is that is visible.
     */
    double                    visibility;           // 1 = 100%, 0 = 0% vision quality

    /**
     * Create a new visible thing.
     * 
     * @param id
     *        the id
     * @param type
     *        the type
     */
    public VisionModel(int id, String type)
    {
        this.type = type;
        this.id = id;
        this.visibility = 1; // default is perfect visibility
    }

    /**
     * Set the visible quality
     * 
     * @param visibility
     *        the visible quality
     */
    public void setVisionQuality(double visibility)
    {
        this.visibility = visibility;
    }

    /**
     * Get the type
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the type
     * 
     * @param type
     *        the type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Get the ID
     * 
     * @return The ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the ID
     * 
     * @param id
     *        the ID
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the visibility
     * 
     * @return the visibility
     */
    public double getVisibility()
    {
        return visibility;
    }
}
