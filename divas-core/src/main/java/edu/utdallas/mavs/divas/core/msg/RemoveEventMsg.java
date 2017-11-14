package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;

/**
 * This class describes the message for removing EnvEvent from the environment.
 * <p>
 * Removing states are wrapped with this type of message.
 */
public class RemoveEventMsg implements Serializable
{
    private static final long serialVersionUID = 1L;
    private EnvEvent          event;

    /**
     * Constructs the <code>EnvEvent</code> object with the given <code>EnvEvent</code>.
     * 
     * @param event
     * @param state
     *        The <code>EnvEvent</code> to be set in the message.
     */
    public RemoveEventMsg(EnvEvent event)
    {
        this.event = event;
    }

    /**
     * Sets the given <code>EnvEvent</code> in this message.
     * 
     * @param event
     * @param state
     *        The <code>EnvEvent</code> to be set in the message.
     */
    public void setEvent(EnvEvent event)
    {
        this.event = event;
    }

    /**
     * @return The <code>EnvEvent</code> of the message.
     */
    public EnvEvent getEvent()
    {
        return event;
    }
}
