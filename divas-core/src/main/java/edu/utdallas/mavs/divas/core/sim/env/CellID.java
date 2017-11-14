package edu.utdallas.mavs.divas.core.sim.env;

import java.io.Serializable;

/**
 * This class is for representing the Cell controller Id.
 */
public class CellID implements Serializable
{
	private static final long	serialVersionUID	= -2101519537881559318L;

	private String				idString;

	/**
	 * Constructs the <code>CellID</code> by initializing the idString to be empty.
	 */
	public CellID()
	{
		idString = "";
	}

	private CellID(String id)
	{
		this.idString = id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof CellID)
		{
			if(idString.equals(((CellID) obj).idString))
				return true;
		}
		else
		{
			if(idString.toString().equals(obj.toString()))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		int hash = 1;
		for(char c : idString.toCharArray())
		{
			hash = hash * 31 + c;
		}
		return hash;
	}

	/**
	 * Creates a child of this cell ID with the given sub-index.
	 * 
	 * @param index
	 *        the index of the child.
	 * @return the cell ID of the child.
	 */
	public CellID createChild(int index)
	{
		return new CellID(idString + (char) index);
	}

	/**
	 * Calculates the hierarchy depth of the cell ID based on the length of the id string. (1 is the root)
	 * 
	 * @return depth of the cell ID in the cell hierarchy
	 */
	public int getLevel()
	{
		return idString.length();
	}

	/**
	 * Determines if this cell ID is an ancestor of descendantID.
	 * 
	 * @param descendantID
	 * @return <code>true</code> if cell ID is an ancestor of descendantID and <code>false</code> otherwise
	 */
	public boolean isAncestorOf(CellID descendantID)
	{
		return descendantID.idString.startsWith(idString) && !descendantID.equals(this);
	}

	/**
	 * Determines if this cell ID is a descendant of ancestorID.
	 * 
	 * @param ancestorID
	 * @return <code>true</code> if cell ID is a descendant of ancestorID and <code>false</code> otherwise
	 */
	public boolean isDescendantOf(CellID ancestorID)
	{
		return idString.startsWith(ancestorID.idString) && !ancestorID.equals(this);
	}

	/**
	 * Gets the parent of this cell ID.
	 * 
	 * @return this cell ID's parent
	 */
	public CellID getParentID()
	{
		return new CellID(idString.substring(0, idString.length() - 1));
	}

	/**
	 * @return The last character in the <code>CellID</code>.
	 */
	public int getLastPart()
	{
		return idString.isEmpty() ? -1 : ((int) idString.charAt(idString.length() - 1));
	}

	@Override
    public String toString()
	{
		String printID = "";

		if(idString.isEmpty())
			return "[root]";

		for(char c : idString.toCharArray())
			printID += (int) c + ".";

		if(printID.length() > 1)
			return printID.substring(0, printID.length() - 1);

		return printID;
	}

	/**
	 * @return Array of integers that represent the <code>CellID</code>.
	 */
	public int[] getParts()
	{
		int[] parts = new int[idString.length()];

		for(int i = 0; i < idString.length(); i++)
			parts[i] = idString.charAt(i);

		return parts;
	}

	/**
	 * @return <code>CellID</code> of the root cell controller.
	 */
	public static CellID rootID()
	{
		return new CellID();
	}

	/**
	 * @return <code>String</code> of the cell controller <code>CellID</code>.
	 */
	public String getIdString()
	{
		return idString;
	}

}
