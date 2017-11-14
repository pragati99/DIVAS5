package edu.utdallas.mavs.divas.core.sim.env;

import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This <code>Interface</code> is for handling a <code>VirtualState</code> addition or deletion listener.
 */
public interface StateEventListener
{
	/**
	 * Fires the state added handler.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> added.
	 */
	public void stateAdded(VirtualState state);

	/**
	 * Fires the state removed handler.
	 * 
	 * @param state
	 *        The <code>VirtualState</code> removed.
	 */
	public void stateRemoved(VirtualState state);
}
