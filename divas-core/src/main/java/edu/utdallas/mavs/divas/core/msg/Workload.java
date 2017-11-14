package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.env.CellID;

public class Workload implements Serializable
{
    private static final long serialVersionUID = 1L;

    CellID                    cellID;
    float                     workload;

    public Workload(CellID cellID, float workload)
    {
        super();
        this.cellID = cellID;
        this.workload = workload;
    }

    public CellID getCellID()
    {
        return cellID;
    }

    public float getWorkload()
    {
        return workload;
    }
}
