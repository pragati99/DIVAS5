package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.ontology.Thing;
import edu.utdallas.mavs.divas.utils.collections.LightWeightBoundedQueue;

/**
 * Store information regarding door's. Each door has exactly two rooms attached to it.
 */
public class Door extends Thing
{

    /**
     * Serializer ID
     */
    private static final long                serialVersionUID = 1L;

    private static final int                 MAX_NEIGHBOR     = 25;

    /**
     * The Door's ID
     */
    private int                              id               = -1;
    private int                              doorCongestion   = 0;
    private boolean                          visited          = false;
    private boolean                          isExit           = false;
    private boolean                          obstacled        = false;

    private LightWeightBoundedQueue<Integer> neighbors;

    private float                            doorflow         = 0;
    private float                            gscore, fscore;
    private Door                             cameFrom;
    private int                              depth            = 0;
    private boolean                          closed           = false;
    private boolean                          newNode          = true;
    private long                             cycleSet         = 0;

    /**
     * Create a door with envobj ID
     * 
     * @param id
     *        ID of envobj
     */
    public Door(int id)
    {
        this.id = id;
        neighbors = new LightWeightBoundedQueue<Integer>(MAX_NEIGHBOR);
    }

    /**
     * Get ID
     * 
     * @return ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set ID
     * 
     * @param id
     *        ID
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get all neighbors to this door
     * 
     * @return list of neighbors
     */
    public LightWeightBoundedQueue<Integer> getNeighbors()
    {
        return neighbors;
    }

    /**
     * clear the neighbor list
     */
    public void clearNeighbors()
    {
        neighbors.clear();
    }

    /**
     * Add a new neighbor to this door
     * 
     * @param newNeighbor
     *        the neighbor to be added
     */
    public void addNeighbor(int newNeighbor)
    {
        neighbors.add(newNeighbor);
    }

    /**
     * Remove a neighbor to this door
     * 
     * @param neighbor
     *        the neighbor to be removed
     */
    public void removeNeighbor(int neighbor)
    {
        neighbors.remove(neighbor);
    }

    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }

    /**
     * Get the depth if this node (Astar)
     * 
     * @return integer depth
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Set the depth of this node (Astar)
     * 
     * @param depth
     *        the integer depth
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * Whether the node is closed or not. (Astar)
     * 
     * @return true if closed, false otherwise
     */
    public boolean isClosed()
    {
        return closed;
    }

    /**
     * Set whether this node is closed or not. (Astar)
     * 
     * @param closed
     *        true if closed, false if not
     */
    public void setClosed(boolean closed)
    {
        this.closed = closed;
    }

    /**
     * Check whether this is a new node or not. (Astar)
     * 
     * @return true if new, false otherwise
     */
    public boolean isNewNode()
    {
        return newNode;
    }

    /**
     * Set whether this is a new node or not. (Astar)
     * 
     * @param newNode
     *        true if new, false otherwise
     */
    public void setNewNode(boolean newNode)
    {
        this.newNode = newNode;
    }

    /**
     * Get the G-score of the node (Astar)
     * 
     * @return the G-score float
     */
    public float getGscore()
    {
        return gscore;
    }

    /**
     * Set the G-score of the node (Astar)
     * 
     * @param gscore
     *        the G-score float
     */
    public void setGscore(float gscore)
    {
        this.gscore = gscore;
    }

    /**
     * Get the F-score of the node (Astar)
     * 
     * @return the F-score float
     */
    public float getFscore()
    {
        return fscore;
    }

    /**
     * Set the F-score of the node (Astar)
     * 
     * @param fscore
     *        the F-score float
     */
    public void setFscore(float fscore)
    {
        this.fscore = fscore;
    }

    /**
     * Get the parent node (Astar)
     * 
     * @return parent node
     */
    public Door getCameFrom()
    {
        return cameFrom;
    }

    /**
     * Set the parent node (Astar)
     * 
     * @param cameFrom
     *        parent node
     */
    public void setCameFrom(Door cameFrom)
    {
        this.cameFrom = cameFrom;
    }

    public boolean isExit()
    {
        return isExit;
    }

    public void setExit(boolean isExit)
    {
        this.isExit = isExit;
    }

    public boolean isObstacled()
    {
        return obstacled;
    }

    public void setObstacled(boolean obstacled)
    {
        this.obstacled = obstacled;
    }

    public int getDoorCongestion()
    {
        return doorCongestion;
    }

    public void setDoorCongestion(int doorCongestion)
    {
        this.doorCongestion = doorCongestion;
    }

    public long getCycleSet()
    {
        return cycleSet;
    }

    public void setCycleSet(long cycleSet)
    {
        this.cycleSet = cycleSet;
    }

    public float getDoorflow()
    {
        return doorflow;
    }

    public void setDoorflow(float doorflow)
    {
        this.doorflow = doorflow;
    }

}
