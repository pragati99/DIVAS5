package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This class describes the message for removing the agent state or environment object state from the environment.
 * <p>
 * Removing states are wrapped with this type of message.
 */
public class RemoveStateMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private VirtualState		state;

	/**
	 * Constructs the <code>RemoveStateMsg</code> object with the given <code>VirtualState</code>.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set in the message.
	 */
	public RemoveStateMsg(VirtualState state)
	{
		this.state = state;
	}

	/**
	 * Sets the given <code>VirtualState</code> in this message.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set in the message.
	 */
	public void setState(VirtualState state)
	{
		this.state = state;
	}

	/**
	 * @return The <code>VirtualState</code> of the message.
	 */
	public VirtualState getState()
	{
		return state;
	}
}
