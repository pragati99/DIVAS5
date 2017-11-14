package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.utils.collections.LightWeightBoundedQueue;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.EnvObjectKnowledgeStorageObject;

/**
 * A generic path finding node (For use with Astar path finding algorithm)
 */
public class PathFindingNode implements Serializable
{
    /**
     * 
     */
    private static final int                 MAX_NEIGHBOR     = 15;
    private static final long                serialVersionUID = 1L;
    private int                              nodeID;
    private Vector3f                         point;
    private float                            gscore, fscore;
    private PathFindingNode                  cameFrom;
    private LightWeightBoundedQueue<Integer> neighbors;
    private int                              depth            = 0;
    private boolean                          closed           = false;
    private boolean                          newNode          = true;
    private boolean                          connectedToGoal  = false;
    private int                              parentEnvObjID   = -1;
    private float                            agentSize;

    ArrayDeque<PathFindingNode>              nodesToProcess;

    private static int                       nextNodeID       = 0;

    /**
     * Create a path finding node from a point
     * 
     * @param point
     *        the 3d point
     */
    public PathFindingNode(float agentSize, Vector3f point)
    {
        this.point = point;
        gscore = 0;
        fscore = 0;
        depth = 0;
        cameFrom = null;
        neighbors = new LightWeightBoundedQueue<Integer>(MAX_NEIGHBOR);
        nodesToProcess = new ArrayDeque<PathFindingNode>();
        this.nodeID = nextNodeID++;
        this.agentSize = agentSize;
    }

    /**
     * Create a path finding node from a point and pass in the parent env-obj
     * 
     * @param point
     *        the 3d point
     * @param parentEnvObjID
     *        parent object ID
     */
    public PathFindingNode(float agentSize, Vector3f point, int parentEnvObjID)
    {
        this.point = point;
        gscore = 0;
        fscore = 0;
        cameFrom = null;
        neighbors = new LightWeightBoundedQueue<Integer>(MAX_NEIGHBOR);
        nodesToProcess = new ArrayDeque<PathFindingNode>();
        this.parentEnvObjID = parentEnvObjID;
        this.nodeID = nextNodeID++;
        this.agentSize = agentSize;
    }

    /**
     * Get the node's point
     * 
     * @return the 3d point
     */
    public Vector3f getPoint()
    {
        return point;
    }

    /**
     * Set the nodes point.
     * 
     * @param point
     *        the 3d point
     */
    public void setPoint(Vector3f point)
    {
        this.point = point;
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
    public PathFindingNode getCameFrom()
    {
        return cameFrom;
    }

    /**
     * Set the parent node (Astar)
     * 
     * @param cameFrom
     *        parent node
     */
    public void setCameFrom(PathFindingNode cameFrom)
    {
        this.cameFrom = cameFrom;
    }

    /**
     * Calculate all the neighbors that this node has. Uses all other nodes to check if neighbor. Has a max distance. And has a build in queue of nodes to process
     * so that too much time each tick is not spent processing. (If many nodes are added at once this can cripple the simulation, so the queue and the maximum time processing prevents this
     * and makes sure the simulation runs smoothly.
     * 
     * @param allNodes
     *        all the nodes to check against
     * @param avoidList
     *        the env obs to check for obstruction
     * @param endTime
     *        The cut off time for testing
     * @param distance
     *        the max distance to check for nodes
     */
    public void calculateNeighbors(Collection<PathFindingNode> allNodes, List<EnvObjectKnowledgeStorageObject> avoidList, long endTime, int distance)
    {
        if(nodesToProcess.isEmpty())
        {
            nodesToProcess.addAll(allNodes);
        }
        PathFindingNode node;
        while(System.currentTimeMillis() <= endTime && (node = nodesToProcess.poll()) != null)
        {
            if(node.getNodeID() != this.getNodeID())
            {
                if(!neighbors.contains(node))
                {
                    if(node.getPoint().distance(point) < distance)
                    {
                        boolean neighbor = true;
                        for(EnvObjectKnowledgeStorageObject obj : avoidList)
                        {
                            if(obj.isCollidable() && testIntersectsObject(node, obj))
                            {
                                neighbor = false;
                                break;
                            }
                        }
                        if(neighbor == true)
                        {
                            neighbors.add(node.getNodeID());
                            node.addNeighbor(this.nodeID);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculate all the neighbors that this node has. Same as generic method but this one is made specificly for checking ONLY the goal node. (not normal nodes)
     * Has no queue since only one node.
     * 
     * @param node
     *        all the nodes to check against
     * @param avoidList
     *        the env obs to check for obstruction
     */
    public void calculateNeighborsGoal(PathFindingNode node, List<EnvObjectKnowledgeStorageObject> avoidList)
    {

        if(node != this)
        {
            boolean neighbor = true;
            for(EnvObjectKnowledgeStorageObject obj : avoidList)
            {
                if(obj.isCollidable() && testIntersectsObject(node, obj))
                {
                    neighbor = false;
                    break;
                }
            }
            if(neighbor == true)
            {
                connectedToGoal = true;
            }
        }
    }

    /**
     * Get all neighbors to this node
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
     * Add a new neighbor to this node
     * 
     * @param newNeighbor
     *        the neighbor to be added
     */
    public void addNeighbor(int newNeighbor)
    {
        neighbors.add(newNeighbor);
    }

    /**
     * Calculate all the neighbors that this node has. Same as generic method but this one is made specificly for checking ONLY the start node. (not normal nodes)
     * Has no queue since only one node.
     * 
     * @param allNodes
     *        all the nodes to check against
     * @param envObjects
     *        the env obs to check for obstruction
     * @param distance
     *        the max distance to check for nodes
     */
    public void calculateStartNeighbors(Collection<PathFindingNode> allNodes, List<EnvObjectKnowledgeStorageObject> envObjects, int distance, PathFindingNode goalNode)
    {
        for(PathFindingNode node : allNodes)
        {
            if(node.getPoint().distance(point) < distance)
            {
                boolean neighbor = true;
                for(EnvObjectKnowledgeStorageObject obj : envObjects)
                {
                    if(obj.isCollidable() && testIntersectsObject(node, obj))
                    {
                        neighbor = false;
                        break;
                    }
                }

                if(neighbor == true)
                {
                    neighbors.add(node.getNodeID());
                }
            }
        }
        PathFindingNode node = goalNode;
        if(node.getPoint().distance(point) < distance)
        {
            boolean neighbor = true;
            for(EnvObjectKnowledgeStorageObject obj : envObjects)
            {
                if(obj.isCollidable() && testIntersectsObject(node, obj))
                {
                    neighbor = false;
                    break;
                }
            }

            if(neighbor == true)
            {
                connectedToGoal = true;
            }
        }
    }

    private boolean testIntersectsObject(PathFindingNode node, EnvObjectKnowledgeStorageObject obj)
    {
        return obj.agentPathIntersectsObj2D(node.getPoint(), point, agentSize);
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
     * Get the parent objects ID
     * 
     * @return the parent object's ID (integer)
     */
    public int getParentEnvObjID()
    {
        return parentEnvObjID;
    }

    /**
     * Set the parent object's iD
     * 
     * @param parentEnvObjID
     *        the parent object's ID (integer)
     */
    public void setParentEnvObjID(int parentEnvObjID)
    {
        this.parentEnvObjID = parentEnvObjID;
    }

    public int getNodeID()
    {
        return nodeID;
    }

    public void setNodeID(int nodeID)
    {
        this.nodeID = nodeID;
    }

    public boolean isConnectedToGoal()
    {
        return connectedToGoal;
    }

    public void setConnectedToGoal(boolean connectedToGoal)
    {
        this.connectedToGoal = connectedToGoal;
    }

}
