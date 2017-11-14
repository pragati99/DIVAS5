package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.Phase;

/**
 * This class describes the <code>Tick</code> message.
 * <p>
 * The <code>Tick</code> message is sent at the end of each simulation phase in the <code>heartbeat</code> to indicate the end of that phase and to enter a new phase.
 */
public class TickMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private long				cycles;
	private Phase				phase;
	private long				simTime;
	private int					period;

	/**
	 * Constructs the <code>TickMsg</code> object.
	 * 
	 * @param ticks
	 *        The cycle number.
	 * @param phase
	 *        The new phase.
	 * @param simTime
	 *        The simulation time.
	 * @param period
	 *        The elapsed time of the cycle.
	 */
	public TickMsg(long ticks, Phase phase, long simTime, int period)
	{
		this.cycles = ticks;
		this.phase = phase;
		this.simTime = simTime;
		this.period = period;
	}

	/**
	 * @return The cycle number in this message.
	 */
	public long getCycles()
	{
		return cycles;
	}

	/**
	 * Sets the cycle number in this message.
	 * 
	 * @param cycles
	 *        The cycle number to be set.
	 */
	public void setCycles(long cycles)
	{
		this.cycles = cycles;
	}

	/**
	 * @return The simulation phase stored in the message.
	 */
	public Phase getPhase()
	{
		return phase;
	}

	/**
	 * Sets the message <code>phase</code> with the given phase.
	 * 
	 * @param phase
	 *        The <code>phase</code> to be set.
	 */
	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}

	/**
	 * @return The simulation time.
	 */
	public long getSimTime()
	{
		return simTime;
	}

	/**
	 * Sets the simulation in the message.
	 * 
	 * @param simTime
	 *        The simulation time.
	 */
	public void setSimTime(long simTime)
	{
		this.simTime = simTime;
	}

	/**
	 * @return The cycle elapsed time.
	 */
	public int getPeriod()
	{
		return period;
	}

	/**
	 * Sets the period or the cycle time with the given period.
	 * 
	 * @param period
	 *        The elapsed time to be set in the message.
	 */
	public void setPeriod(int period)
	{
		this.period = period;
	}
}
