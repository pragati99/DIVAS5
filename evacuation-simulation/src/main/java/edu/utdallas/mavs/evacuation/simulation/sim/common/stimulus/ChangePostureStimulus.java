package edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;

/**
 * This class describes a stimulus for changing the posture of an agent.
 */
public class ChangePostureStimulus extends AgentStimulus
{
    private static final long serialVersionUID = 1L;

    private Posture           posture;

    /**
     * Creates a new change posture stimulus
     * 
     * @param id
     *        the agent's ID
     * @param posture
     *        the agent's posture
     */
    public ChangePostureStimulus(int id, Posture posture)
    {
        super(id);
        this.posture = posture;
    }

    /**
     * Gets the agent's posture
     * 
     * @return the agent's posture
     */
    public Posture getPosture()
    {
        return posture;
    }

    /**
     * Sets the agent's posture
     * 
     * @param posture
     *        the agent's posture
     */
    public void setPosture(Posture posture)
    {
        this.posture = posture;
    }
}
