package edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;

public class OpenDoorStimulus extends AgentStimulus
{
    private static final long serialVersionUID = 1L;
    private int               envObjID;

    public OpenDoorStimulus(int agentID, int envObjID)
    {
        super(agentID);

        this.envObjID = envObjID;
    }

    public int getEnvObjID()
    {
        return envObjID;
    }

    public void setEnvObjID(int envObjID)
    {
        this.envObjID = envObjID;
    }
}
