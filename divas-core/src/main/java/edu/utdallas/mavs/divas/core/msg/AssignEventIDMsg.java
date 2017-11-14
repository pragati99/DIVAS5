package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;

/**
 * This class describes the message for assigning an ID for an event.
 * <p>
 * Assigning event ID requests are wrapped with this type of message.
 */
public class AssignEventIDMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private EnvEvent			event;

	/**
	 * Constructs the <code>AssignEventIDMsg</code> object given the environment event.
	 * 
	 * @param event
	 *        External environment event to assign ID to.
	 */
	public AssignEventIDMsg(EnvEvent event)
	{
		this.setEnvEvent(event);
	}

	/**
	 * Sets the given environment event for this message.
	 * 
	 * @param event
	 *        Environment event to be set.
	 */
	public void setEnvEvent(EnvEvent event)
	{
		this.event = event;
	}

	/**
	 * @return The environment event for this message.
	 */
	public EnvEvent getEnvEvent()
	{
		return event;
	}
}
