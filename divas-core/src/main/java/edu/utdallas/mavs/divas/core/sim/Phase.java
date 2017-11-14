package edu.utdallas.mavs.divas.core.sim;

/**
 * This enumeration describes the Simulation phases.
 */
public enum Phase
{
    /**
     * dummy phase, the phase before agent when the heartbeat hasn't run yet
     */
    INIT,

    /**
     * agent phase, where agents are processed
     */
    AGENT,

    /**
     * environment phase, where the environment react to agent's stimuli
     */
    ENVIRONMENT
}
