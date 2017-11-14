/*package edu.utdallas.mavs.divas.gui.event;

import com.pennychecker.eventbus.Event;

public final class SimStatusUpdateEvent extends Event<SimStatusUpdateEventHandler>
{
    public final static Type<SimStatusUpdateEventHandler> TYPE = new Type<SimStatusUpdateEventHandler>();
    private final boolean                                 running;

    public SimStatusUpdateEvent(boolean running)
    {
        this.running = running;
    }

    @Override
    public Type<SimStatusUpdateEventHandler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(SimStatusUpdateEventHandler h)
    {
        h.onSimStatusUpdateEvent(this);
    }

    public boolean isRunning()
    {
        return running;
    }
}
*/