package edu.utdallas.mavs.divas.core.sim.env;

import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;

/**
 * This class contains information about the agent subscriptions to cell controllers.
 * <p>
 * Each agent is subscribed to a subset of the cell controllers in the environment. This subset is calculated based on the agent location and its field of view and vision distance. The cell subscription helps improve the performance of the simulation
 * as each agent receives only the relevant information of it's interest.
 * 
 * @param <CC>
 *        the cell controller's type
 */
public class CellSubscriptionService<CC extends CellController> implements Serializable
{
    private static final long            serialVersionUID = -2521259091302491919L;

    /**
     * stores the computed agent subscriptions
     */
    protected Map<CellID, List<Integer>> subscriptions;

    /**
     * stores the combined cell state for each agent (merges all perceivable cells into one "environment" for the agent
     * to perceive)
     */
    protected Map<Integer, CellState>    combinedCellStates;

    /**
     * Constructs the cell subscription service by initializing the maps and creating the multithreader.
     */
    public CellSubscriptionService()
    {
        subscriptions = new HashMap<CellID, List<Integer>>();
        combinedCellStates = new HashMap<Integer, CellState>();
    }

    /**
     * Subscribe agents to the cells they should be perceiving data from.
     * 
     * @param cells
     *        the cell controllers to be processed for agent subscription
     * @param cellMap
     *        the environment cell map
     */
    public void processAgentSubscriptions(Collection<CC> cells, CellMap cellMap)
    {
        // resets css
        reset();

        // calculates agent subscriptions
        addSubscriptions(cells, cellMap);

        // disseminates cell state
        for(CellController cell : cells)
        {
            if(cellMap.isLeaf(cell.getCellID()))
            {
                disseminateCellState(cell.getCellState());
            }
        }
    }

    /**
     * Disseminate the given cell state to the agents subscribed to the cell controller with the given state.
     * The given cell state will be combined with other cell states the agent is subscribed to.
     * 
     * @param cell
     */
    protected void disseminateCellState(CellState cell)
    {
        try
        {
            List<Integer> agents = getAgentsFor(cell.getId());
            if(agents != null)
            {
                for(int agentID : agents)
                {
                    // if agent's combined cell doesn't exist, create it
                    if(!combinedCellStates.containsKey(agentID))
                    {
                        combinedCellStates.put(agentID, new CellState(/*
                                                                       * id and bounds don't matter, the agent is
                                                                       * unaware of them
                                                                       */));
                    }
                    combinedCellStates.get(agentID).combineEntitiesFrom(cell);
                    combinedCellStates.get(agentID).setTime(cell.getTime());
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Adds the agents <code>subscriptions</code> Map which stores the <code>CellID</code> for the cell
     * controller along with a list of agents subscribed to this cell.
     * 
     * @param cellMap
     * @param cells
     */
    protected void addSubscriptions(Collection<CC> cells, CellMap cellMap)
    {
        // calculates agent subscriptions
        List<AgentSubscription> subs = getAgentSubscriptions(cells, cellMap);

        // add subscriptions to hash map
        for(AgentSubscription agSub : subs)
        {
            // gentCellAddress.put(agSub.getAgentID(), cellState.getId());

            for(CellID cell : agSub.getCellIDs())
            {
                getAgentsFor(cell).add(agSub.getAgentID());
            }
        }
    }

    private List<AgentSubscription> getAgentSubscriptions(Collection<CC> cells, CellMap map)
    {
        // calculate agent subscriptions
        List<AgentSubscription> subs = new ArrayList<AgentSubscription>();

        for(CC cellController : cells)
        {
            CellState cellState = cellController.getCellState();
            for(AgentState agent : cellState.getAgentStates())
            {
                // create a new list of cellids for the agent to subscribe to
                AgentSubscription sub = new AgentSubscription(agent.getID());

                if(agent.canSee())
                {
                    Shape shape = ((VisionPerceptor) agent).calculateVisibleRegion();
                    // add the potentially perceivable cells to the list
                    for(CellID id : map.getCellsIntersecting(cellState.getId(), shape))
                    {
                        sub.addSubscription(id);
                    }
                }

                if(agent.canHear() || agent.canSmell())
                {
                    // make sure the agent gets subscribed to this cell, regardless of vision
                    if(!sub.getCellIDs().contains(cellState.getId()))
                    {
                        sub.addSubscription(cellState.getId());
                    }
                }

                subs.add(sub);
            }
        }

        return subs;
    }

    private List<Integer> getAgentsFor(CellID cellID)
    {
        // if cell subscription doesn't exist, create it
        if(!subscriptions.containsKey(cellID))
        {
            subscriptions.put(cellID, new ArrayList<Integer>());
        }
        return subscriptions.get(cellID);
    }

    /**
     * Gets the combined cell state for the cell controllers the given agent is subscribed to.
     * 
     * @param agentID
     *        The agent ID
     * @return Combined <code>CellState</code> for the given agent.
     */
    public CellState retrieveCombinedCellStateForAgent(int agentID)
    {
        return combinedCellStates.get(agentID);
    }

    /**
     * Tests if the given agent has a combined cell state for the cell controllers it's subscribed to.
     * 
     * @param agentID
     *        The agent ID.
     * @return Boolean flag that indicates if the given agent has a combined cell state.
     */
    public Boolean hasCombinedCellState(int agentID)
    {
        return combinedCellStates.containsKey(agentID);
    }

    /**
     * Resets the cell subscription service by clearing all the maps.
     */
    public void reset()
    {
        subscriptions.clear();
        combinedCellStates.clear();
    }

    /**
     * Gets the <code>subscriptions</code>.
     * 
     * @return <code>Map</code> that contains the <code>CellID</code> for the cell controller along
     *         with a list of agents that are subscribed to that cell.
     */
    public Map<CellID, List<Integer>> getSubscriptions()
    {
        return subscriptions;
    }

    private Map<Integer, CellState> getCombinedCellStates()
    {
        return combinedCellStates;
    }

    public void copyFrom(CellSubscriptionService<?> css)
    {
        subscriptions.putAll(css.getSubscriptions());
        combinedCellStates.putAll(css.getCombinedCellStates());
    }
}
