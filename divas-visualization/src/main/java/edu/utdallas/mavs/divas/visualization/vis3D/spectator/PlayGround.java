package edu.utdallas.mavs.divas.visualization.vis3D.spectator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.CellMap;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.CellBoundsVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EventVO;

/**
 * This class stores the current simulation status as received from the simulation.
 * <p>
 * The information stored in this class is the only information needed by the 2D visualizer to visualize the simulation.
 */
public abstract class PlayGround
{
    private final static Logger     logger = LoggerFactory.getLogger(PlayGround.class);

    /**
     * A HashMap that stores the agent Id along with it's agent state for all agents in the simulation.
     */
    protected Map<Integer, AgentVO<?>> agents;

    /**
     * A List of explosion events that currently exist in the simulation
     */
    protected List<EnvObjectVO>     envObjects;

    /**
     * A List of visualized events that currently exist in the simulation
     */
    protected List<EventVO>         events;

    /**
     * The current environment cell controller bounds
     */
    protected CellBoundsVO          cellBounds;

    /**
     * Default Constructor for the playGround that initialize an empty lists and Maps
     * to be used to store information later.
     */
    public PlayGround()
    {
        agents = Collections.synchronizedMap(new HashMap<Integer, AgentVO<?>>());
        envObjects = Collections.synchronizedList(new ArrayList<EnvObjectVO>());
        events = Collections.synchronizedList(new ArrayList<EventVO>());
        cellBounds = new CellBoundsVO();
    }

    protected abstract AgentVO<?> createAgentVO(AgentState state, long cycle);

    protected abstract EnvObjectVO createEnvObjectVO(EnvObjectState state, long cycle);

    protected abstract EventVO createEventVO(EnvEvent event, long cycle);

    /**
     * Calculates the current VO count in the playground
     * 
     * @return the VO count
     */
    public int getEntitiesCount()
    {
        return agents.size() + envObjects.size() + events.size();
    }

    /**
     * Gets the list of events
     * 
     * @return the events
     */
    public List<EventVO> getEvents()
    {
        return events;
    }

    /**
     * Gets the list of env objects
     * 
     * @return the objects
     */
    public List<EnvObjectVO> getEnvObjects()
    {
        return envObjects;
    }

    /**
     * Finds and retrieves a visualized environment object given its id. Returns <code>null</code> if not found.
     * 
     * @param envObjectID
     *        the id of the environment object
     * @return the visualized environment object found
     */
    public EnvObjectVO findEnvObject(int envObjectID)
    {
        // prevent concurrent modification of envObjects
        synchronized(envObjects)
        {
            for(EnvObjectVO envObject : envObjects)
                if(envObject.getState().getID() == envObjectID)
                    return envObject;
        }
        return null;
    }

    /**
     * Finds and retrieves a visualized agent given its id. Returns <code>null</code> if not found.
     * 
     * @param agentID
     *        the id of the agent
     * @return the visualized agent found
     */
    public AgentVO<?> findAgent(int agentID)
    {
        synchronized(agents)
        {
            return agents.get(agentID);
        }
    }

    /**
     * Finds and retrieves all visualized agents.
     * 
     * @return the visualized agents found
     */
    public Collection<AgentVO<?>> findAllAgents()
    {
        return agents.values();
    }

    /**
     * Finds and retrieves all visualized objects.
     * 
     * @return the visualized ojects found
     */
    public Collection<EnvObjectVO> findAllEnvObjects()
    {
        return envObjects;
    }

    /**
     * Finds and retrieves a visualized event given its id. Returns <code>null</code> if not found.
     * 
     * @param eventId
     *        the id of the event
     * @return the visualized event found
     */
    public EventVO findEvent(int eventId)
    {
        synchronized(events)
        {
            for(EventVO event : events)
                if(event.getEvent().getID() == eventId)
                    return event;
        }
        return null;
    }

    /**
     * Finds and retrieves a visualized cell bound given its id. Returns <code>null</code> if not found.
     * 
     * @param cellID
     *        the id of the cell bound
     * @return the visualized cell bound
     */
    public CellBoundsVO findCellBounds(CellID cellID)
    {
        return cellBounds;
    }

    /**
     * Updates a simulated environment object state in the visualization. If this is the first time the environment
     * object is updated, a new visualized object is created. Otherwise, the corresponding visualized object is updated
     * with the received environment object's state. If the state of the received environment object has a flag to be
     * removed from the visualization, the corresponding visualized object is removed from the list of visualized
     * environment objects.
     * 
     * @param obj
     *        the environment object state to be updated.
     * @param cycle
     *        the current cycle number of the simulation
     */
    public void updateEnvObject(EnvObjectState obj, long cycle)
    {
        synchronized(envObjects)
        {
            EnvObjectVO knownEnvObject = findEnvObject(obj.getID());
            // if the object is not known
            if(knownEnvObject == null)
            {
                if(obj.isVisualized()) // if the object is alive
                    envObjects.add(createEnvObjectVO(obj, cycle)); // create and add
                                                                   // the object
                // to the list
                // otherwise, ignore, don't need to add a dead object
            }
            else
            // if the object is known
            {
                if(obj.isVisualized()) // if the object is alive
                {
                    knownEnvObject.setState(obj, cycle); // update its state
                }
                else
                {
                    // if the object is dead
                    removeEnvObjectVO(knownEnvObject);
                }
            }
        }
    }

    /**
     * Removes a visualized environment object from the simulation.
     * 
     * @param id
     *        the id of the environment object to be removed
     */
    public void removeEnvObject(int id)
    {
        EnvObjectVO knownEnvObject = findEnvObject(id);

        // if the object is known
        if(knownEnvObject != null)
        {
            removeEnvObjectVO(knownEnvObject);
        }
    }

    /**
     * Removes a visualized event from the simulation.
     * 
     * @param id
     *        the id of the event to be removed
     */
    public void removeEvent(int id)
    {
        EventVO knownEvent = findEvent(id);

        // if the object is known
        if(knownEvent != null)
        {
            removeEventVO(knownEvent);
        }
    }

    /**
     * Updates a simulated agent state in the visualization. If this is the first time the agent is updated, a new
     * visualized object is created. Otherwise, the corresponding visualized object is updated with the received agent
     * state. If the state of the received agent has a flag to be removed from the visualization, the corresponding
     * visualized object is removed from the list of visualized agents.
     * 
     * @param agentState
     *        the agent state to be updated
     * @param cycle
     *        the current cycle number of the simulation
     */
    public synchronized void updateAgent(AgentState agentState, long cycle)
    {
        // AgentState agentState = agentDto.getState();

        synchronized(agents)
        {
            // if the agent is not known
            if(!agents.containsKey(agentState.getID()))
            {
                if(agentState.isVisualized())
                {
                    agents.put(agentState.getID(), createAgentVO(agentState, cycle));
                }
            }
            // if the agent is known
            else
            {
                AgentVO knownAgent = agents.get(agentState.getID());
                if(agentState.isVisualized())
                {
                    knownAgent.setState(agentState, cycle); // update its state
                }
                else
                {
                    // if the agent is dead
                    removeAgentVO(knownAgent);
                }
            }
        }
    }

    /**
     * Removes a visualized agent from the simulation.
     * 
     * @param id
     *        the id of the agent to be removed
     */
    public void removeAgent(int id)
    {
        AgentVO<?> agent = findAgent(id);

        // if the agent is known
        if(agent != null)
            removeAgentVO(agent);
    }

    /**
     * Trigger an event in the visualization
     * 
     * @param event
     *        the event state containing information about the event
     * @param cycle
     *        the current cycle number of the simulation
     */
    public void triggerEvent(EnvEvent event, long cycle)
    {
        synchronized(events)
        {
            if(event.isCurrentlyVisible())
            {
                if(event.getAge() <= 1)
                {
                    events.add(createEventVO(event, cycle));
                }
                else if(event.getAge() >= event.getMaxAge())
                {
                    EventVO vo = findEvent(event.getID());
                    removeEventVO(vo);
                }
            }
        }
    }

    /**
     * Updates the cell bounds of the visualized simulation environment.
     * 
     * @param cellMap the bounds of the cell to be updated
     * @param cycle
     *        the current cycle number of the simulation
     */
    public void updateCellBounds(CellMap cellMap, long cycle)
    {
        synchronized(cellBounds)
        {
            cellBounds.setBounds(cellMap, cycle);
        }
    }

    /**
     * Removes a visualized cell of the visualized environment.
     * 
     * @param id
     *        the cell id
     */
    public void removeCellBounds(CellID id)
    {
        synchronized(cellBounds)
        {
            cellBounds.destroy();
        }
    }

    private void removeAgentVO(AgentVO<?> agent)
    {
        synchronized(agents)
        {
            agent.destroy(); // destroy the agent
            agents.remove(agent); // remove the agent from the list
        }
    }

    private void removeEnvObjectVO(EnvObjectVO eoVO)
    {
        synchronized(envObjects)
        {
            eoVO.destroy(); // destroy the eo
            envObjects.remove(eoVO); // remove the eo from the list
        }
    }

    private void removeEventVO(EventVO event)
    {
        event.destroy();
        events.remove(event);
    }

    /**
     * Resets the playground. Cleans the list of agent VOs, environment object VOs, and event VOs
     */
    public void reset()
    {
        logger.info("Resetting playground.");

        synchronized(agents)
        {
            for(AgentVO<?> agentVO : agents.values())
            {
                agentVO.enqueueDetachSpatial();
            }
            agents.clear();
        }
        synchronized(envObjects)
        {
            for(EnvObjectVO eoVO : envObjects)
            {
                eoVO.enqueueDetachSpatial();
            }
            envObjects.clear();
        }
        synchronized(events)
        {
            for(EventVO eventVO : events)
            {
                eventVO.enqueueDetachSpatial();
            }
            events.clear();
        }
        synchronized(cellBounds)
        {
            cellBounds.destroy();
            cellBounds = new CellBoundsVO();
        }
    }
}
