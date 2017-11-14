package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This class describes the message for creating a state for an agent or an environment object.
 * <p>
 * Creating states are wrapped with this type of message.
 */
public class CreateStateMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private VirtualState		state;

	/**
	 * Constructs the <code>CreateStateMsg</code> object with the given <code>VirtualState</code>.
	 * 
	 * @param state
	 *        The initial <code>VirtualState</code> to be set in the message.
	 */
	public CreateStateMsg(VirtualState state)
	{
		this.setState(state);
	}

	/**
	 * Sets the state of the message with the given <code>VirtualState</code>.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set in the message.
	 */
	public void setState(VirtualState state)
	{
		this.state = state;
	}

	/**
	 * @return The <code>VirtualState</code> on this message.
	 */
	public VirtualState getState()
	{
		return state;
	}
}
