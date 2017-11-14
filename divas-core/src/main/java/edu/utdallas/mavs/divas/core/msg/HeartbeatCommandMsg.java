package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

/**
 * This class describes the heart beat command message.
 */
public class HeartbeatCommandMsg implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	/**
	 * Heartbeat command
	 */
	protected Command			command;

	/**
	 * Enumeration of heartbeat command types
	 */
	public enum Command
	{
		/**
		 * Run the heartbeat
		 */
		RUN,
		/**
		 * Pause the heartbeat
		 */
		PAUSE,
		/**
		 * Reset the heartbeat
		 */
		RESET
	}

	private HeartbeatCommandMsg(Command command)
	{
		this.command = command;
	}

	/**
	 * @return A <code>HeartbeatCommandMsg</code> object with command set to <code>RUN</code>
	 */
	public static HeartbeatCommandMsg getRunCommand()
	{
		return new HeartbeatCommandMsg(Command.RUN);
	}

	/**
	 * @return A <code>HeartbeatCommandMsg</code> object with command set to <code>PAUSE</code>
	 */
	public static HeartbeatCommandMsg getPauseCommand()
	{
		return new HeartbeatCommandMsg(Command.PAUSE);
	}

	/**
	 * @return A <code>HeartbeatCommandMsg</code> object with command set to <code>RESET</code>
	 */
	public static HeartbeatCommandMsg getResetCommand()
	{
		return new HeartbeatCommandMsg(Command.RESET);
	}

	/**
	 * @return The <code>Command</code> type of this <code>HeartbeatCommandMsg</code>.
	 */
	public Command getCommand()
	{
		return command;
	}
}
