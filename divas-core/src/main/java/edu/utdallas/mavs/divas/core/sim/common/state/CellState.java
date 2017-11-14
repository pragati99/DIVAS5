package edu.utdallas.mavs.divas.core.sim.common.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;

/**
 * This class stores information about the cell controller contents.
 * <p>
 * This information includes: 1)CellBounds. 2)Agents in the cell controller. 3)Environment objects in the Cell Controller. 4)Events in the Cell Controller.
 */
public class CellState implements Serializable
{
    private static final long            serialVersionUID = 2595604100870567724L;

    private CellID                       id;

    private CellBounds                   bounds;

    private long                         time;

    private Map<Integer, AgentState>     agents;
    private Map<Integer, EnvObjectState> envObjects;
    private Map<Integer, EnvEvent>       events;

    /**
     * Creates a new cell state.
     */
    public CellState()
    {
        agents = Collections.synchronizedMap(new HashMap<Integer, AgentState>());
        envObjects = Collections.synchronizedMap(new HashMap<Integer, EnvObjectState>());
        events = Collections.synchronizedMap(new HashMap<Integer, EnvEvent>());
    }

    /**
     * Creates a new cell state with the given id and bounds.
     * 
     * @param id
     *        The id of the cell state.
     * @param bounds
     *        The boundaries of the cell state.
     */
    public CellState(CellID id, CellBounds bounds)
    {
        this();
        this.setId(id);
        this.setBounds(bounds);
    }

    /**
     * This method splits the cell state into two new children cell states. First, the boundary of this cell state is
     * split into two. Then, all agents,
     * environment objects and events in this cell state is added to the newly-created children cell states only if they
     * intersect with the bounds of
     * the children cell states.
     * 
     * @return an array containing the two newly-created children cell states.
     */
    public CellState[] split()
    {
        CellState subCells[] = new CellState[2];

        CellBounds parts[] = (SimConfig.getInstance().use_demographic_splitting) ? bounds.splitDemographically(agents.values()) : bounds.split();

        subCells[0] = new CellState(id.createChild(0), parts[0]);
        subCells[1] = new CellState(id.createChild(1), parts[1]);

        // forward entities to newly-created children
        synchronized(envObjects)
        {
            for(EnvObjectState e : envObjects.values())
                for(CellState subCell : subCells)
                    if(subCell.getBounds().intersects(e.getBoundingArea()))
                        subCell.addEnvObject(e);
        }

        synchronized(events)
        {
            for(EnvEvent e : events.values())
                for(CellState subCell : subCells)
                    if(subCell.getBounds().contains(e.getOrigin()))
                        subCell.addEvent(e);
            events.clear();
        }
        return subCells;
    }

    /**
     * This method merges the current cell state with the given cell state. First, checks if the two cells are allowed
     * to be merged.
     * If yes, then add all entities from the given cell state to this cell state and eliminate duplicate of large
     * environment objects.
     * Then, reset the id and update the boundaries of this cell state.
     * 
     * @param cell
     *        The cell state to merge with the current cell state.
     */
    public void mergeWith(CellState cell)
    {
        // check parent ID to be sure the cells can be merged
        if(!cell.getId().getParentID().equals(id.getParentID()))
            return;

        // Combine the entities before updating the bounds in order to check for the large environment objects
        // to prevent adding the large object twice in the merged cell
        combineEntitiesFrom(cell);

        CellBounds b0 = bounds;
        CellBounds b1 = cell.bounds;

        // make sure the cells are next to each other
        // TODO: check for third dimension as well
        // are the cells aligned vertically?
        // if(!(b0.getX() == b1.getX() && b0.getWidth() == b1.getWidth() && ((b0.getY() < b1.getY()) ? (b0.getY() +
        // b0.getHeight() == b1.getY()) : (b1.getY() + b1.getHeight() == b0.getY())))
        // && !(b0.getY() == b1.getY() && b0.getHeight() == b1.getHeight() && ((b0.getX() < b1.getX()) ? (b0.getX() +
        // b0.getWidth() == b1.getX()) : (b1.getX() + b1.getWidth() == b0.getX()))))
        // return null;

        float x_min = (float) Math.min(b0.getX(), b1.getX());
        float x_max = (float) Math.max(b0.getMaxX(), b1.getMaxX());
        float y_min = (float) Math.min(b0.getY(), b1.getY());
        float y_max = (float) Math.max(b0.getMaxY(), b1.getMaxY());

        setId(id.getParentID());
        setBounds(new CellBounds(x_min, x_max, b0.getMin_Y(), b0.getMax_Y(), y_min, y_max));

    }

    /**
     * Adds all entities of the given state to the current cell state.
     * 
     * @param cell
     *        The cell state of the cell being merged with this cell state.
     */
    public void combineEntitiesFrom(CellState cell)
    {
        envObjects.putAll(cell.envObjects);
        agents.putAll(cell.agents);
        events.putAll(cell.events);
    }

    /**
     * Changes the id of this cell state.
     * 
     * @param id
     *        the new id for this cell state.
     */
    public void setId(CellID id)
    {
        this.id = id;
    }

    /**
     * Gets the id of this cell state.
     * 
     * @return the cell state id.
     */
    public CellID getId()
    {
        return id;
    }

    /**
     * Changes the boundaries for this cell state.
     * 
     * @param bounds
     *        the new boundaries for this cell state.
     */
    public void setBounds(CellBounds bounds)
    {
        this.bounds = bounds;
    }

    /**
     * Gets the {@link CellBounds} of this cell state.
     * 
     * @return the cell state boundaries.
     */
    public CellBounds getBounds()
    {
        return bounds;
    }

    /**
     * Changes the time of the cell state based on the simulation cycle.
     * 
     * @param time
     *        the new time for this cell state.
     */
    public void setTime(long time)
    {
        this.time = time;
    }

    /**
     * Gets the time for this cell state.
     * 
     * @return the cell state time.
     */
    public long getTime()
    {
        return time;
    }

    /**
     * Gets a list of {@link AgentState} in this cell state. This represents the agents in the cell controller.
     * 
     * @return list of agent states.
     */
    public List<AgentState> getAgentStates()
    {
        return new ArrayList<AgentState>(agents.values());
    }

    /**
     * Returns the agent in the list that has the given id. If the agent doesn't exist in the agent list, returns null.
     * 
     * @param agentID
     *        The id of the agent to be retrieved from the list of agent states in this cell state.
     * @return the {@link AgentState} for the given agentID.
     */
    public AgentState getAgentState(int agentID)
    {
        return agents.get(agentID);
    }

    /**
     * Gets a list of {@link EnvObjectState} in this cell state. This represents the env objects in the cell controller.
     * 
     * @return list of environment object states.
     */
    public List<EnvObjectState> getEnvObjects()
    {
        return new ArrayList<EnvObjectState>(envObjects.values());
    }

    /**
     * Returns the envObject in the list that has the given ID. If the envObject doesn't exist in the envObject list,
     * return null.
     * 
     * @param envObjectID
     *        The id of the env object to be retrieved from the list of env object states in this cell state.
     * @return the {@link EnvObjectState} for the given envObjectID.
     */
    public EnvObjectState getEnvObjectState(int envObjectID)
    {
        return envObjects.get(envObjectID);
    }

    /**
     * Gets a list of {@link EnvEvent} in this cell state. This represents the external events in the cell controller.
     * 
     * @return list of external environment events.
     */
    public List<EnvEvent> getEvents()
    {
        return new ArrayList<EnvEvent>(events.values());
    }

    /**
     * Removes all agents, environment objects and environment events from this cell state.
     */
    public void destroy()
    {
        agents.clear();
        envObjects.clear();
        events.clear();
    }

    /**
     * Removes all agents from this cell state.
     */
    public void clearAgents()
    {
        agents.clear();
    }

    /**
     * Adds a new agent state to this cell state. This represents adding a new agent in the cell controller.
     * 
     * @param a
     *        The new agent state to be added in the list of agent states in this cell state.
     * @return true if the new agent state replaced an old agent state in this cell state with the same id and false
     *         otherwise.
     */
    public boolean addAgent(AgentState a)
    {
        return agents.put(a.getID(), a) != null;
    }

    /**
     * Returns a flag indicating whether this cell state contains the agent state for the given.
     * 
     * @param agentID
     *        The id of the agent to check whether this cell state contains.
     * @return true if this cell state contains the agent state with the given id and false otherwise.
     */
    public boolean containsAgent(int agentID)
    {
        return agents.containsKey(agentID);
    }

    /**
     * Removes an environment object state from this cell state. This represents removing an environment object from the
     * cell controller.
     * 
     * @param state
     *        the environment object state to be removed.
     * @return true if the list of environment object states contained the state to be removed and false otherwise.
     */
    public boolean removeEnvObject(EnvObjectState state)
    {
        return envObjects.remove(state.getID()) != null;
    }

    /**
     * Removes the agent state with the given id from this cell state. This represents removing an agent from the cell
     * controller.
     * 
     * @param agentId
     *        The id for the agent to be removed.
     * @return true if the agent state was removed and false if no agent state was found with the given id.
     */
    public boolean removeAgent(int agentId)
    {
        return agents.remove(agentId) != null;
    }

    /**
     * Adds a new environment object state to this cell state. This represents adding a new environment object in the
     * cell controller.
     * 
     * @param state
     *        The new environment object state to be added in the list of environment object states in this cell state.
     * @return true if the environment object state was added to the list of environment object states and false
     *         otherwise.
     */
    public boolean addEnvObject(EnvObjectState state)
    {
        return envObjects.put(state.getID(), state) != null;
    }

    /**
     * Adds a new environment event to this cell state. This represents adding a new event in the
     * cell controller.
     * 
     * @param event
     *        The new event object to be added in the list of events in this cell state.
     * @return true if the event object was added to the list of events and false
     *         otherwise.
     */
    public boolean addEvent(EnvEvent event)
    {
        return events.put(event.getID(), event) != null;
    }

    /**
     * Removes event from cell
     * 
     * @param event
     *        the event to be removed
     * @return true if removed. Otherwise, false.
     */
    public boolean removeEvent(EnvEvent event)
    {
        return events.remove(event.getID()) != null;
    }

    /**
     * Returns a map of agent states in this cell state. This represents the agents in the cell controller.
     * 
     * @return A mapping of agent id with agent states.
     */
    public Map<Integer, AgentState> getAgents()
    {
        return agents;
    }

    @Override
    public String toString()
    {
        return getId().toString();
    }
}
