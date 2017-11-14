package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.env.CellID;

/**
 * This class describes the message for cell controller restructure request.
 * <p>
 * Merge or split cell controller requests are wrapped with this type of message.
 */
public class CellStructureMsg implements Serializable
{
	private static final long	serialVersionUID	= 3646720983240718367L;

	private Operation			operation;
	private CellID				cellID;

	/**
	 * Enumeration of the cell controller restructure operations.
	 */
	public enum Operation
	{
		/**
		 * Split cell controller operation
		 */
		SPLIT,
		/**
		 * Merge cell controller operation
		 */
		MERGE
	}

	/**
	 * Gets a <code>CellStructureMsg</code> for cell controller merge request.
	 * This sets the operation of the message to <code>MERGE</code>.
	 * 
	 * @param cellID
	 *        The <code>CellID</code> for the cell controller to be split
	 * @return A <code>CellStructureMsg</code> object.
	 */
	public static CellStructureMsg getSplitMsg(CellID cellID)
	{
		CellStructureMsg msg = new CellStructureMsg(Operation.SPLIT, cellID);
		return msg;
	}

	/**
	 * Gets a <code>CellStructureMsg</code> for cell controller merge request.
	 * This sets the operation of the message to <code>MERGE</code>.
	 * 
	 * @param cellID
	 *        The <code>CellID</code> for the cell controller to be merged
	 * @return A <code>CellStructureMsg</code> object.
	 */
	public static CellStructureMsg getMergeMsg(CellID cellID)
	{
		CellStructureMsg msg = new CellStructureMsg(Operation.MERGE, cellID);
		return msg;
	}

	private CellStructureMsg(Operation operation, CellID cellID)
	{
		this.operation = operation;
		this.cellID = cellID;
	}

	/**
	 * @return The message operation which could be: SPLIT or MERGE
	 */
	public Operation getOperation()
	{
		return operation;
	}

	/**
	 * @return The <code>CellID</code> for the cell controller wrapped in the message.
	 */
	public CellID getCellID()
	{
		return cellID;
	}

}
