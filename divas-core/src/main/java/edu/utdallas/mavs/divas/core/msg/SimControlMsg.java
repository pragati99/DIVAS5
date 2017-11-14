package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

/**
 * This class describes the simulation control message.
 */
public class SimControlMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	private SimControlCommand	simControlCommand;

	/**
	 * Constructs the <code>SimControlMsg</code> with the given <code>SimControlCommand</code>.
	 * 
	 * @param simControlCommand
	 *        The simulation control command to be set in the message.
	 */
	public SimControlMsg(SimControlCommand simControlCommand)
	{
		super();
		this.simControlCommand = simControlCommand;
	}

	/**
	 * Enumeration of the simulation control commands
	 */
	public enum SimControlCommand
	{
		/**
		 * Start simulation command
		 */
		START,
		/**
		 * Pause simulation command
		 */
		PAUSE
	}

	/**
	 * @return the simulation control command stored in this message
	 */
	public SimControlCommand getSimControlCommand()
	{
		return simControlCommand;
	}

}
