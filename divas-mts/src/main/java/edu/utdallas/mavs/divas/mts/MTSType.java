package edu.utdallas.mavs.divas.mts;

/**
 * This class contains an enumeration of message transportation system types.
 */
public enum MTSType
{
	/**
	 * Shared MTS type uses local memory references to send messages. This method is very fast since
	 * it uses local memory calls, and is used when we send messages within the same java process.
	 */
	SHARED,
	/**
	 * TCP MTS type uses TCP protocol to send message. This method is slower than Shared MTS, and its used
	 * when we send messages between two different java processes.
	 */
	TCP
}
