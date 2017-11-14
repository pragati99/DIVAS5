package edu.utdallas.mavs.divas.core.sim.common.stimulus;

import java.io.Serializable;
import java.util.ArrayList;

import edu.utdallas.mavs.divas.core.sim.env.CellID;

/**
 * This class describes a collection of agent stimulus.
 */
public class Stimuli extends ArrayList<AgentStimulus> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long              time;
    private CellID            cellID;

    public void setTime(long time)
    {
        this.time = time;
    }

    public long getTime()
    {
        return time;
    }

    public CellID getCellID()
    {
        return cellID;
    }

    public void setCellID(CellID cellID)
    {
        this.cellID = cellID;
    }
}
