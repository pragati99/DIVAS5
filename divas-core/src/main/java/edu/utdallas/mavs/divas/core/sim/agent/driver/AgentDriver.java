package edu.utdallas.mavs.divas.core.sim.agent.driver;

/**
 * This interface describes the agent driver used for controlling an agent.
 * <p>
 * An agent can be controlled by the end user using the Keyboard driver or the Wiimote driver.
 */
public interface AgentDriver
{
    /**
     * Gets the current forward delta and then reset it to 0.
     * 
     * @return forward delta - positive is forward
     */
    public float grabForwardDelta();

    /**
     * Gets the current lateral delta and then reset it to 0.
     * 
     * @return lateral delta - positive is right
     */
    public float grabLateralDelta();

    /**
     * Gets the current rotation delta and then reset it to 0.
     * 
     * @return rotation delta - positive is rightward
     */
    public float grabRotationDelta();

    /**
     * Closes and destroys this agent driver.
     */
    public void close();
}
