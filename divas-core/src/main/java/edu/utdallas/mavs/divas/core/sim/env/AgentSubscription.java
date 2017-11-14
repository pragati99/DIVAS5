package edu.utdallas.mavs.divas.core.sim.env;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores agent subscriptions to cell controllers.
 */
public class AgentSubscription
{
    private int                 agentID;
    private Map<CellID, CellID> cellIDs;

    /**
     * Constructs the <code>AgentSubscription</code> object given an agent ID.
     * 
     * @param agentID The agent ID.
     */
    public AgentSubscription(int agentID)
    {
        this.agentID = agentID;
        cellIDs = new HashMap<CellID, CellID>();
    }

    /**
     * Gets the agent ID for this <code>AgentSubscription</code> object.
     * 
     * @return The agent ID
     */
    public int getAgentID()
    {
        return agentID;
    }

    /**
     * Adds a new cell to the subscription
     * 
     * @param cellID
     */
    public void addSubscription(CellID cellID)
    {
        cellIDs.put(cellID, cellID);
    }

    /**
     * Gets the controllers IDs which the agent of this <code>AgentSubscription</code> object is subscribed to.
     * 
     * @return An <code>ArrayList</code> of cell controllers IDs
     */
    public Collection<CellID> getCellIDs()
    {
        return cellIDs.values();
    }
}
