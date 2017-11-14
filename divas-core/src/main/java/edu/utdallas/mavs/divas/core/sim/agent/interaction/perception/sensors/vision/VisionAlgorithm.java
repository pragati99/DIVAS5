package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

/**
 * Vision algorithms
 */
public enum VisionAlgorithm
{
    /**
     * Blind vision. Agent has no eye sight.
     */
    BlindVision,

    /**
     * Global vision. Agent has global vision of the entire environment.
     */
    GlobalVision,

    /**
     * Divas vision. Agent can perceive only things within its field of vision.
     */
    DivasVision,

    /**
     * Non deterministic vision.
     */
    NDDivasVision
}
