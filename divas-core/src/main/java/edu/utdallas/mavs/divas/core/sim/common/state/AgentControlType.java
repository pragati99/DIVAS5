package edu.utdallas.mavs.divas.core.sim.common.state;

/**
 * An enumeration of available agent drivers. Selected agent can be driven by the end user using the keyboard or a Wiimote. 
 */
public enum AgentControlType
{
    /**
     * Agent is autonomous. Agents are autonomous by default.
     */
    Autonomous, 
    /**
     * Agent is driven by the end user using the keyboard.
     */
    Keyboard, 
    /**
     * Agent is driven by the end user using a Wiimote.
     */
    Wiimote
}
