package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This message describes the state updates messages sent to the simulation.
 */
public class StateUpdateMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private VirtualState		state;

	/**
	 * Constructs the <code>StateUpdateMsg</code> object with the given <code>VirtualState</code>
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set.
	 */
	public StateUpdateMsg(VirtualState state)
	{
		this.setState(state);
	}

	/**
	 * Sets the given <code>VirtualState</code> to this message.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> to be set.
	 */
	public void setState(VirtualState state)
	{
		this.state = state;
	}

	/**
	 * @return The <code>VirtualState</code> stored in this message.
	 */
	public VirtualState getState()
	{
		return state;
	}
}
