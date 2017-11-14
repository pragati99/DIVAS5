/*package edu.utdallas.mavs.divas.gui.event;

import com.pennychecker.eventbus.Event;

import edu.utdallas.mavs.divas.core.client.dto.SimDto;

public final class SimSummaryUpdateEvent extends Event<SimSummaryUpdateEventHandler>
{
    public final static Type<SimSummaryUpdateEventHandler> TYPE = new Type<SimSummaryUpdateEventHandler>();
    private final SimDto                                   simSummary;

    public SimSummaryUpdateEvent(SimDto simSummary)
    {
        this.simSummary = simSummary;
    }

    @Override
    public Type<SimSummaryUpdateEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(SimSummaryUpdateEventHandler h)
    {
        h.onSimSummaryUpdateEvent(this);
    }

    public SimDto getSimSummary()
    {
        return simSummary;
    }
}
*/