package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * A quick sort implementation for path finding nodes
 */
public class NodeSorter implements Serializable
{
    private static final long                 serialVersionUID = 1L;
    private List<Integer>                     nodeIDs;
    private int                               number;
    private HashMap<Integer, PathFindingNode> nodeStore;
    private PathFindingNode                   startNode;
    private PathFindingNode                   goalNode;

    /**
     * Create a node sorter. (Using integer IDs and this storage map.)
     * 
     * @param nodeStore
     *        the node storage map
     * @param goalNode
     * @param startNode
     */
    public NodeSorter(HashMap<Integer, PathFindingNode> nodeStore, PathFindingNode startNode, PathFindingNode goalNode)
    {
        this.nodeStore = nodeStore;
        this.startNode = startNode;
        this.goalNode = goalNode;
    }

    /**
     * Method to sort these path finding nodes (using quick sort)
     * 
     * @param values2
     *        nodes to be sorted
     */
    public void sort(List<Integer> values2)
    {
        // Check for empty or null array
        if(values2 == null || values2.size() == 0)
        {
            return;
        }
        this.nodeIDs = values2;
        number = values2.size();
        quicksort(0, number - 1);
    }

    private void quicksort(int low, int high)
    {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        int pivot = nodeIDs.get(low + (high - low) / 2);

        // Divide into two lists
        while(i <= j)
        {
            // If the current value from the left list is smaller then the pivot
            // element then get the next element from the left list
            while(getProperNode(nodeIDs.get(i)).getFscore() < getProperNode(pivot).getFscore())
            {
                i++;
            }
            // If the current value from the right list is larger then the pivot
            // element then get the next element from the right list
            while(getProperNode(nodeIDs.get(j)).getFscore() > getProperNode(pivot).getFscore())
            {
                j--;
            }

            // If we have found a values in the left list which is larger then
            // the pivot element and if we have found a value in the right list
            // which is smaller then the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if(i <= j)
            {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if(low < j)
            quicksort(low, j);
        if(i < high)
            quicksort(i, high);
    }

    protected PathFindingNode getProperNode(Integer nodeID)
    {
        if(nodeID == startNode.getNodeID())
        {
            return startNode;
        }
        else if(nodeID == goalNode.getNodeID())
        {
            return goalNode;
        }
        else
        {
            return nodeStore.get(nodeID);
        }
    }

    private void exchange(int i, int j)
    {
        int temp = nodeIDs.get(i);
        nodeIDs.set(i, nodeIDs.get(j));
        nodeIDs.set(j, temp);
    }
}
