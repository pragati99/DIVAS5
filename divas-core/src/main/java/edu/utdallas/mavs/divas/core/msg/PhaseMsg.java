package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.Phase;

/**
 * This class describes the Phase message.
 * <p>
 * Phase messages are sent at the end of each phase. Phase messages are wrapped with this type of message.
 */
public class PhaseMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	Phase						phase;

	/**
	 * @return The <code>Phase</code> set in this message.
	 */
	public Phase getPhase()
	{
		return phase;
	}

	/**
	 * Sets the phase of this message with the given <code>Phase</code>.
	 * 
	 * @param phase
	 *        The <code>Phase</code> to be set.
	 */
	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}

	/**
	 * Constructs the <code>PhaseMsg</code> object using the given <code>Phase</code>.
	 * 
	 * @param phase
	 *        The <code>Phase</code> to be set in the message.
	 */
	public PhaseMsg(Phase phase)
	{
		super();
		this.phase = phase;
	}

}
