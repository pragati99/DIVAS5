package edu.utdallas.mavs.divas.core.sim.common.percept;

public interface AudioPerceptor
{
    /**
     * Flag indicating if the agent auditory sense is enabled.
     * 
     * @return true if the agent auditory sense is enabled and false otherwise.
     */
    public boolean isAuditoryEnabled();

    /**
     * Changes the flag indicating whether the agent auditory sense is enabled or not enabled.
     * 
     * @param auditoryEnabled
     *        Flag indicating if the agent auditory sense is enabled.
     */
    public void setAuditoryEnabled(boolean auditoryEnabled);
    
    /**
     * Gets the minimum audible threshold the agent can perceive using its hearing sense.
     * 
     * @return the minimum audible threshold of the agent.
     */
    public float getMinAudibleThreshold();
}
