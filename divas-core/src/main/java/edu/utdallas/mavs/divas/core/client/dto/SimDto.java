package edu.utdallas.mavs.divas.core.client.dto;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;

/**
 * This class describes a data transfer object for simulation summary data.
 */
public class SimDto implements Serializable
{
    private static final long      serialVersionUID       = 1L;

    private int                    period;

    private long                   cycles;

    private long                   simTime;

    private boolean                selfOrganized;

    private ReorganizationStrategy reorganizationStrategy = ReorganizationStrategy.No_Reorganization;

    public SimDto(int period, long cycles, long simTime, boolean selfOrganized)
    {
        super();
        this.period = period;
        this.cycles = cycles;
        this.simTime = simTime;
        this.selfOrganized = selfOrganized;
    }

    public SimDto(int period, long cycles, long simTime, boolean selfOrganized, ReorganizationStrategy reorganizationStrategy)
    {
        this(period, cycles, simTime, selfOrganized);
        this.reorganizationStrategy = reorganizationStrategy;
    }

    public SimDto()
    {
        this.cycles = -1;
    }

    public int getPeriod()
    {
        return period;
    }

    public long getCycles()
    {
        return cycles;
    }

    public long getSimTime()
    {
        return simTime;
    }

    public boolean isSelfOrganized()
    {
        return selfOrganized;
    }

    public ReorganizationStrategy getReorganizationStrategy()
    {
        return reorganizationStrategy;
    }

}
