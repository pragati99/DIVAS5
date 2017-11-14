package edu.utdallas.mavs.divas.core.sim.common.percept;

public interface SmellPerceptor
{
    /**
     * Flag indicating if the agent olfactory sense is enabled.
     * 
     * @return true if the agent olfactory sense is enabled and false otherwise.
     */
    public boolean isOlfactoryEnabled();

    /**
     * Changes the flag indicating whether the agent olfactory sense is enabled or not enabled.
     * 
     * @param olfactoryEnabled
     *        Flag indicating if the agent olfactory sense is enabled.
     */
    public void setOlfactoryEnabled(boolean olfactoryEnabled);
}
