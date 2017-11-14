package edu.utdallas.mavs.divas.core.config;

import java.io.Serializable;

/**
 * HostConfig stores configuration attributes for the DIVAs host.
 */
public class HostConfig implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The skybox type
     */
    protected String          skybox           = "default";

    /**
     * Flag for enabling/diabling water in the simulation environment
     */
    protected boolean         waterEnabled     = false;

    /**
     * Constructs a new instance of Host configuration
     */
    public HostConfig()
    {
        useDefault();
    }

    /**
     * Checks if water is enabled in the simulation
     * 
     * @return flag indicating if water is enabled
     */
    public boolean isWaterEnabled()
    {
        return waterEnabled;
    }

    /**
     * Sets the status of water in the simulation
     * 
     * @param water enabled/disabled status
     */
    public void setWater(boolean water)
    {
        this.waterEnabled = water;
    }

    /**
     * Gets the skybox type
     * 
     * @return the skybox type
     */
    public String getSkybox()
    {
        return skybox;
    }

    /**
     * Sets the skybox type
     * 
     * @param skybox the skybox type
     */
    public void setSkybox(String skybox)
    {
        this.skybox = skybox;
    }

    /**
     * Resets to the default configuration
     */
    public void useDefault()
    {
        skybox = "default";
        waterEnabled = false;
    }
}
