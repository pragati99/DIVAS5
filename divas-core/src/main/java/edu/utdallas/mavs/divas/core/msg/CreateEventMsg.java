package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;

/**
 * This class describes the message for create environment events.
 * <p>
 * Creating environment events requests are wrapped with this type of message.
 */
public class CreateEventMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private EnvEvent			event;

	/**
	 * Constructs the <code>CreateEventMsg</code> with the given environment event.
	 * 
	 * @param event
	 *        The environment event to be set in the message.
	 */
	public CreateEventMsg(EnvEvent event)
	{
		this.setEnvEvent(event);
	}

	/**
	 * Sets the given environment event in this message.
	 * 
	 * @param event
	 *        The environment event to be set in the message.
	 */
	public void setEnvEvent(EnvEvent event)
	{
		this.event = event;
	}

	/**
	 * @return The environment event set in the message.
	 */
	public EnvEvent getEnvEvent()
	{
		return event;
	}
}
