package edu.utdallas.mavs.divas.core.sim.env;

import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;

import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * The <code>CellMapNode</code> class contains information about the nodes stored in the <code>CellMap</code>.
 * <p>
 * Each node store a link to it's parent node and to it's children nodes, which forms the shape of the cell map hierarchy.
 */
public class CellMapNode implements Serializable
{
	private static final long	serialVersionUID	= -7559433466289742061L;

	private CellID				id;
	private CellBounds			bounds;

	private CellMapNode			parent;
	private CellMapNode			children[];

	private String				hostID;

	/**
	 * Constructs the <code>CellMapNode</code> object given the <code>CellID</code> for the node, the bounds,
	 * the parent <code>CellMapNode</code> and the host.
	 * 
	 * @param cellID
	 *        The <code>CellID</code> for the cell controller of the node.
	 * @param bounds
	 *        The bounds for the given cell controller.
	 * @param parent
	 *        The parent <code>CellMapNode</code>.
	 * @param hostID
	 *        The hostID of the <code>CellMapNode</code>.
	 */
	public CellMapNode(CellID cellID, CellBounds bounds, CellMapNode parent, String hostID)
	{
		this.setId(cellID);
		this.setBounds(bounds);
		this.parent = parent;
		this.hostID = hostID;
	}

	/**
	 * Constructs the <code>CellMapNode</code> object given the <code>CellState</code>, the parent <code>CellMapNode</code> and the host ID.
	 * 
	 * @param cell
	 *        The <code>CellState</code> to be assigned to the <code>CellMapNode</code>.
	 * @param parent
	 *        The parent <code>CellMapNode</code>.
	 * @param hostID
	 *        The host ID.
	 */
	public CellMapNode(CellState cell, CellMapNode parent, String hostID)
	{
		this(cell.getId(), cell.getBounds(), parent, hostID);
	}

	private void setId(CellID id)
	{
		this.id = id;
	}

	/**
	 * @return The cell controller <code>CellID</code> for this <code>CellMapNode</code>.
	 */
	public CellID getId()
	{
		return id;
	}

	private void setBounds(CellBounds bounds)
	{
		this.bounds = bounds;
	}

	/**
	 * @return The bounds of the cell controller for this <code>CellMapNode</code>.
	 */
	public CellBounds getBounds()
	{
		return bounds;
	}

	/**
	 * @return An Array of <code>CellMapNode</code> that represents this <code>CellMapNode</code> children.
	 */
	public CellMapNode[] getChildren()
	{
		return children;
	}

	/**
	 * @return The host Id for this <code>CellMapNode</code>.
	 */
	public String getHostID()
	{
		return hostID;
	}

	/**
	 * Splits the current <code>CellMapNode</code> into the given children.
	 * 
	 * @param child0
	 *        The <code>CellMapNode</code> for the first child.
	 * @param child1
	 *        The <code>CellMapNode</code> for the second child.
	 */
	public void splitInto(CellMapNode child0, CellMapNode child1)
	{
		children = new CellMapNode[] { child0, child1 };
	}

	/**
	 * Merges the <code>CellMapNode</code> children.
	 */
	public void merge()
	{
		if(children != null)
		{
			for(CellMapNode child : children)
				child.merge();

			children = null;
		}
	}

	/**
	 * Tests if the current <code>CellMapNode</code> is a leaf node.
	 * 
	 * @return Boolean flag that indicates if the current <code>CellMapNode</code> is a leaf node.
	 */
	public boolean isLeaf()
	{
		return children == null;
	}

	/**
	 * Finds a <code>CellMapNode</code> that contains the given coordinates.
	 * 
	 * @param x
	 *        The X coordinate.
	 * @param y
	 *        The Y coordinate.
	 * @param z
	 *        The Z coordinate.
	 * @return The <code>CellMapNode</code> of the cell controller that contains the given coordinates.
	 */
	public CellMapNode findCell(float x, float y, float z)
	{
		CellMapNode result = null;

		if(getBounds().contains(x, y, z))
		{
			result = this;

			if(children != null)
			{
				for(CellMapNode child : children)
				{
					if(child.bounds.contains(x, y, z))
					{
						result = child.findCell(x, y, z);
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * locates the <code>CellMapNode</code> for the cell controller whose bounds fully contain the
	 * agent's vision shape.
	 * 
	 * @param shape
	 *        The agent vision shape.
	 * @return The <code>CellMapNode</code> for the cell controller whose bounds fully contain the
	 *         agent's vision shape.
	 */
	public CellMapNode generalize(Shape shape)
	{
		if(shape.intersects(bounds))
		{
			if(parent != null)
				return parent.generalize(shape);

			return this;
		}

		return this;
	}

	/**
	 * Determines the cell controllers within this shape boundary to which an agent should subscribe.
	 * 
	 * @param shape
	 *        The agent vision shape.
	 * @param cells
	 *        An <code>ArrayList</code> of <code>CellID</code> to which an agent should subscribe.
	 */
	public void localize(Shape shape, ArrayList<CellID> cells)
	{
		if(isLeaf())
			cells.add(id);
		else
			for(CellMapNode child : children)
				if(shape.intersects(child.bounds))
					child.localize(shape, cells);
	}
}
