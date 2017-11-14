package edu.utdallas.mavs.divas.core.sim.env;

import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * The <code>CellMap</code> contains an updated view of the cell structure
 * of the entire decentralized virtual environment.
 * <p>
 * As the environment is self organizing the cell controller are continuously split and merged dynamically to achieve load balancing and improve performance. As a result the hierarchy of cell
 * controllers in the environment changes accordingly.
 */
public class CellMap extends Observable implements Serializable
{
    private final static Logger logger           = LoggerFactory.getLogger(CellMap.class);

    private static final long   serialVersionUID = -3454036692491920124L;
    private CellMapNode         root;

    /**
     * Constructs the <code>CellMap</code> class by assigning the root cell controller and the host
     * of the root.
     * 
     * @param rootCell
     *        <code>CellState</code> for the root cell controller.
     * @param rootHost
     *        The host for the root cell controller.
     */
    public CellMap(CellState rootCell, String rootHost)
    {
        logger.debug("Creating CellMap");
        this.root = new CellMapNode(rootCell, null, rootHost);
    }

    /**
     * Merge the <code>CellMapNode</code> for the given cell controller id with it's sibling.
     * 
     * @param id
     *        <code>CellId</code> for the cell controller to be merged.
     */
    public synchronized void merge(CellID id)
    {
        getCellNode(id).merge();

        setChanged();
        notifyObservers();
    }

    /**
     * Split the given <code>CellMapNode</code> into the given child's.
     * 
     * @param id
     *        <code>CellID</code> for the <code>CellMapNode</code> to be split.
     * @param child0
     *        <code>CellState</code> for the first child.
     * @param child1
     *        <code>CellState</code> for the second child.
     */
    public void split(CellID id, CellState child0, CellState child1)
    {
        split(id, child0.getId(), child0.getBounds(), Host.getHost().getHostID(), child1.getId(), child1.getBounds(), Host.getHost().getHostID());
    }

    private synchronized void split(CellID id, CellID child0id, CellBounds child0bounds, String child0host, CellID child1id, CellBounds child1bounds, String child1host)
    {
        CellMapNode node = getCellNode(id);

        if(node != null && node.isLeaf())
        {
            node.splitInto(new CellMapNode(child0id, child0bounds, node, child0host), new CellMapNode(child1id, child1bounds, node, child1host));

            setChanged();
            notifyObservers();
        }
    }

    private void getLeavesBounds(CellMapNode node, Map<CellID, CellBounds> leafList)
    {
        if(node.isLeaf())
            leafList.put(node.getId(), node.getBounds());
        else
            for(CellMapNode child : node.getChildren())
                getLeavesBounds(child, leafList);
    }

    /**
     * Gets the <code>CellBounds</code> for each one of the leaves cell controllers.
     * 
     * @return <code>Map</code> of leaves cell controllers id and bounds.
     */
    public Map<CellID, CellBounds> getLeafBounds()
    {
        Map<CellID, CellBounds> leafbounds = new HashMap<CellID, CellBounds>();
        getLeavesBounds(root, leafbounds);
        return leafbounds;
    }

    /**
     * @return <code>CellMapNode</code> for the root cell controller.
     */
    public CellMapNode getRoot()
    {
        return root;
    }

    /**
     * Finds the <code>CellID</code> for the cell controller that contains the given position.
     * 
     * @param v
     *        <code>Vector3f</code> that represents the position to be looked for.
     * @return <code>CellID</code> that contains the given position.
     */
    public CellID findCell(Vector3f v)
    {
        return findCell(v.x, v.y, v.z);
    }

    /**
     * Finds the <code>CellID</code> for the cell controller that contains the given coordinates.
     * 
     * @param x
     *        X coordinate.
     * @param y
     *        Y coordinate.
     * @param z
     *        Z coordinate.
     * @return <code>CellID</code> for the cell controller that contains the given coordinates.
     */
    public CellID findCell(float x, float y, float z)
    {
        CellMapNode node = root.findCell(x, y, z);
        if(node != null)
            return node.getId();
        return null;
    }

    /**
     * Finds the <code>CellMapNode</code> for the given coordinates.
     * 
     * @param x
     *        X coordinate.
     * @param y
     *        Y coordinate.
     * @param z
     *        Z coordinate.
     * @return <code>CellMapNode</code> for the cell controller that contains the given coordinates.
     */
    public CellMapNode findMapNode(float x, float y, float z)
    {
        return root.findCell(x, y, z);
    }

    /**
     * Checks if the cell controller with given <code>CellID</code> is leaf.
     * 
     * @param id
     *        <code>CellID</code> for the cell controller to be tested.
     * @return Boolean flag that indicates if the cell controller with given <code>CellID</code> is leaf.
     */
    public boolean isLeaf(CellID id)
    {
        CellMapNode node = getCellNode(id);

        if(node == null)
            return false;

        return node.getChildren() == null;
    }

    /**
     * Finds the <code>CellMapNode</code> for the cell controller that has the given <code>CellID</code>.
     * 
     * @param id
     *        The <code>CellID</code> for the cell controller.
     * @return <code>CellMapNode</code> for the cell controller with given <code>CellID</code>.
     */
    private CellMapNode getCellNode(CellID id)
    {   
        CellMapNode node = root;

        if(node != null)
        {
            for(int part : id.getParts())
            {
                if(node.getChildren() != null && node.getChildren().length >= (part - 1))
                {
                    node = node.getChildren()[part];
                }
            }
        }

        return node;
    }

    /**
     * Gets the list of cell controllers that intersects with the given shape starting from a given cell
     * controller.
     * 
     * @param startingCell
     *        The <code>CellID</code> of the cell controller to start look from.
     * @param shape
     *        The <code>Shape</code> that will span an area of cell controllers in the environment.
     * @return An <code>ArrayList</code> of the <code>CellID</code>.
     */
    public ArrayList<CellID> getCellsIntersecting(CellID startingCell, Shape shape)
    {
        // get the smallest cell that fully contains the shape
        CellMapNode scc = getCellNode(startingCell).generalize(shape);

        ArrayList<CellID> cells = new ArrayList<CellID>();

        // starting at the smallest containing cell, traverse the subtree, adding a cell's id if the shape intersects
        // the bounds of that cell
        scc.localize(shape, cells);

        return cells;
    }

    /**
     * Tests if the cell controller with the given <code>CellID</code> is the root cell controller
     * in the <code>CellMap</code>.
     * 
     * @param cellID
     *        The <code>CellID</code> of the cell controller to be tested.
     * @return Boolean flag that indicates if the given cell controller is the root cell controller.
     */
    public boolean isRoot(CellID cellID)
    {
        return getRoot().getId().equals(cellID);
    }

    /**
     * @param cellID
     * @return An integer that represents the cell map depth starting from the given cell controller.
     */
    public int getCellDepth(CellID cellID)
    {
        return cellID.getIdString().length();
    }
}
