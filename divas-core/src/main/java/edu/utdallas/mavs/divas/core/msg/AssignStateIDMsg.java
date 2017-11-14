package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This class describes the message for assigning an ID for a <code>VirtualState</code>.
 * <p>
 * Assigning state ID requests are wrapped with this type of message.
 */
public class AssignStateIDMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private VirtualState		state;

	/**
	 * Constructs <code>AssignStateIDMsg</code> object given the <code>VirtualState</code>.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be assigned an ID.
	 */
	public AssignStateIDMsg(VirtualState state)
	{
		this.setState(state);
	}

	/**
	 * Sets the given <code>VirtualState</code> to the message.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set.
	 */
	public void setState(VirtualState state)
	{
		this.state = state;
	}

	/**
	 * @return The <code>VirtualState</code> for the current message.
	 */
	public VirtualState getState()
	{
		return state;
	}
}
