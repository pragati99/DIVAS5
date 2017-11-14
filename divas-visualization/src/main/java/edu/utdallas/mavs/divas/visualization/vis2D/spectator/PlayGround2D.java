package edu.utdallas.mavs.divas.visualization.vis2D.spectator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utdallas.mavs.divas.core.client.dto.CellStateDto;
import edu.utdallas.mavs.divas.core.sim.common.event.BombEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.FireworkEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.CellMap;
import edu.utdallas.mavs.divas.visualization.vis2D.vo.ExplosionVO2D;
import edu.utdallas.mavs.divas.visualization.vis2D.vo.FireworkVO2D;

/**
 * This class stores the current simulation status as received from the simulation.
 * <p>
 * The information stored in this class is the only information needed by the 2D visualizer to visualize the simulation.
 */
public class PlayGround2D
{
    /**
     * A HashMap that stores the agent Id along with it's agent state for all agents in the simulation.
     */
    protected Map<Integer, AgentState>  agents = new HashMap<Integer, AgentState>();
    /**
     * A List of the environment objects state currently exist in the simulation.
     */
    protected List<EnvObjectState>      envObjects;
    /**
     * A List of explosion events that currently exist in the simulation
     */
    protected List<ExplosionVO2D>       explosions;
    /**
     * A List of fireworks events that currently exist in the simulation
     */
    protected List<FireworkVO2D>        fireworks;
    /**
     * A list of the current environment cell controller stats
     */
    protected Map<CellID, CellStateDto> cells;
    /**
     * The current cell Map of the environment
     */
    protected CellMap                   cellMap;

    /**
     * Default Constructor for the playGround2D that initialize an empty lists and Maps
     * to be used to store information later.
     */
    public PlayGround2D()
    {
        agents = Collections.synchronizedMap(new HashMap<Integer, AgentState>());
        envObjects = Collections.synchronizedList(new ArrayList<EnvObjectState>());
        explosions = Collections.synchronizedList(new ArrayList<ExplosionVO2D>());
        fireworks = Collections.synchronizedList(new ArrayList<FireworkVO2D>());
        cells = Collections.synchronizedMap(new HashMap<CellID, CellStateDto>());
    }

    // /**
    // * A copy constructor, that creates a copy instance of the given playGround
    // *
    // * @param playGround2D
    // * A playGround2D that needs to be copied from
    // */
    // public PlayGround2D(PlayGround2D playGround2D)
    // {
    // agents = Collections.synchronizedMap(new HashMap<Integer, AgentState>(playGround2D.getAgents()));
    // envObjects = Collections.synchronizedList(new ArrayList<EnvObjectState>(playGround2D.getEnvObjects()));
    // cellBounds = Collections.synchronizedList(new ArrayList<CellBoundsVO>(playGround2D.getCellBounds()));
    // explosions = Collections.synchronizedList(new ArrayList<ExplosionVO2D>(playGround2D.getExplosions()));
    // fireworks = Collections.synchronizedList(new ArrayList<FireworkVO2D>(playGround2D.getFireworks()));
    //
    // cellMap = playGround2D.getCellMap();
    // }

    /**
     * Gets the current explosion events in the simulation
     * 
     * @return List of explosions
     */
    public synchronized List<ExplosionVO2D> getExplosions()
    {
        return explosions;
    }

    /**
     * Gets the current fireworks events in the simulation
     * 
     * @return the Fireworks
     */
    public synchronized List<FireworkVO2D> getFireworks()
    {
        return fireworks;
    }

    /**
     * Find an environment object in the playGround given the environment object ID.
     * 
     * @param envObjectID
     *        The environment object ID to be find.
     * @return Environment object state for the given environment object ID.
     */
    public EnvObjectState findEnvObject(int envObjectID)
    {
        // prevent concurrent modification of envObjects
        synchronized(envObjects)
        {
            for(EnvObjectState envObject : envObjects)
                if(envObject.getID() == envObjectID)
                    return envObject;
        }
        return null;
    }

    /**
     * Find an agent in the playGround given the agent ID.
     * 
     * @param agentID
     *        The agent Id to be find.
     * @return Agent state for the given agent ID.
     */
    public AgentState findAgent(int agentID)
    {
        return agents.get(agentID);
    }

    /**
     * Find a fireworks event in the playGround given an fireworks event ID.
     * 
     * @param fireworkID
     *        The event ID for the fireworks to be find.
     * @return FireworkVO2D for the given fireworks event id.
     */
    public FireworkVO2D findFirework(int fireworkID)
    {
        // prevent concurrent modification of explosions
        synchronized(explosions)
        {
            for(FireworkVO2D firework : fireworks)
                if(firework.getEventID() == fireworkID)
                    return firework;
        }
        return null;
    }

    /**
     * Find a explosion event in the playGround given an explosion event ID.
     * 
     * @param explosionID
     *        The event ID for the explosion to be find.
     * @return ExplosionVO2D for the given explosion event id.
     */
    public ExplosionVO2D findExplosion(int explosionID)
    {
        // prevent concurrent modification of explosions
        synchronized(explosions)
        {
            for(ExplosionVO2D explosion : explosions)
                if(explosion.getEventID() == explosionID)
                    return explosion;
        }
        return null;
    }

    /**
     * Updates the environment object states list in the playground by adding the new
     * environment object state or updating the existing one.
     * 
     * @param o
     *        Environment object state to be updated in the list
     */
    public synchronized void updateEnvObject(EnvObjectState o)
    {
        EnvObjectState knownEnvObject = findEnvObject(o.getID());

        // if the object is not known
        if(knownEnvObject == null)
        {
            if(o.isVisualized()) // if the object is alive
                envObjects.add(o); // create and add
                                   // the object to the list
            // otherwise, ignore, don't need to add a dead object
        }
        else
        // if the object is known
        {
            if(o.isVisualized()) // if the object is alive
            {
                knownEnvObject = o; // update its state
                removeEnvObject(knownEnvObject);
                envObjects.add(o);
            }
            else
            {
                // if the object is dead
                removeEnvObject(knownEnvObject);
            }
        }
    }

    /**
     * Removes the given environment object state from the list of environment objects in the playGround
     * 
     * @param o
     *        The environment object state to be removed
     */
    public synchronized void removeEnvObject(EnvObjectState o)
    {
        EnvObjectState knownEnvObject = findEnvObject(o.getID());

        // if the object is known
        if(knownEnvObject != null)
            envObjects.remove(knownEnvObject);
    }

    /**
     * Update the agents map in the playGround by adding the new agent state to the Map
     * or updating the the existing agent state.
     * 
     * @param agentState
     *        Agent State to be updated
     */
    public synchronized void updateAgent(AgentState agentState)
    {
        // if the agent is not known
        if(!agents.containsKey(agentState.getID()))
        {
            if(agentState.isVisualized())
            {
                agents.put(agentState.getID(), agentState);
            }
        }
        // if the agent is known
        else
        {

            AgentState knownAgent = agents.get(agentState.getID());
            if(agentState.isVisualized())
            {
                knownAgent = agentState;
                agents.put(agentState.getID(), agentState);
            }
            else
            {
                // if the agent is dead
                removeAgent(knownAgent);
            }
        }
    }

    /**
     * Removes the given agent state from the agent map in the PlayGround
     * 
     * @param agentState
     *        Agent state to be removed from the map
     */
    public synchronized void removeAgent(AgentState agentState)
    {
        AgentState agent = findAgent(agentState.getID());

        // if the agent is known
        if(agent != null)
            agents.remove(agent);
    }

    /**
     * Adds the list of events received from the simulation to the designated list of either
     * the explosion or fireworks events, and assigning an age of 0 to each new event.
     * 
     * @param eventList
     *        List of event needs to be classified and added to the list of explosion or fireworks
     */
    public synchronized void triggerEvent(List<EnvEvent> eventList)
    {
        for(EnvEvent event : eventList)
        {
            if(event instanceof BombEvent)
            {
                // only visualize explosion once.
                if(((BombEvent) event).isCurrentlyVisible())
                {
                    ExplosionVO2D knownExplosion = findExplosion(event.getID());
                    if(knownExplosion == null)
                    {
                        explosions.add(new ExplosionVO2D(event.getID(), event.getOrigin(), 0, ((BombEvent) event).getIntensity(), ((BombEvent) event).isSmoke()));
                    }
                }
            }
            else if(event instanceof FireworkEvent)
            {
                if(((FireworkEvent) event).isCurrentlyVisible())
                {
                    FireworkVO2D knownFirework = findFirework(event.getID());
                    if(knownFirework == null)
                    {
                        fireworks.add(new FireworkVO2D(event.getID(), event.getOrigin(), 0));
                    }
                }
            }
        }
    }

    /**
     * Remove the given explosionVo from the list in the playGround
     * 
     * @param exp
     *        ExplosionVO to be removed from the list
     */
    @SuppressWarnings("unused")
    private synchronized void removeExplosionVO(ExplosionVO2D exp)
    {
        explosions.remove(exp);
    }

    /**
     * Reset the Playground by emptying all the list in the playGroound
     */
    public synchronized void reset()
    {
        synchronized(agents)
        {
            agents.clear();
        }
        synchronized(envObjects)
        {
            envObjects.clear();
        }
        synchronized(explosions)
        {
            explosions.clear();
        }
        synchronized(cells)
        {
            cells.clear();
        }
    }

    /**
     * Updates the current cellMap in the playGround with the given cellMap
     * 
     * @param cell
     *        CellMap to be set in the playGround
     */
    public synchronized void updateCell(CellStateDto cell)
    {
        this.cellMap = cell.getCellMap();
        this.cells.put(cell.getId(), cell);
    }

    /**
     * Gets the current map of agents in the playGround
     * 
     * @return Map of agent Id and agent state
     */
    public synchronized Map<Integer, AgentState> getAgents()
    {
        return agents;
    }

    /**
     * Gets the current list of environment object states in the playGround
     * 
     * @return List of environment object states in the playGround
     */
    public synchronized List<EnvObjectState> getEnvObjects()
    {
        return envObjects;
    }

    /**
     * Gets the current list of CEllBoundsVO in the playGround
     * 
     * @return A list of CEllBoundsVo in the playGround
     */
    public synchronized Map<CellID, CellStateDto> getCells()
    {
        return cells;
    }

    /**
     * Gets the current cellMap stored in the playGround
     * 
     * @return The cellMap stored in the playGround
     */
    public synchronized CellMap getCellMap()
    {
        return cellMap;
    }

    /**
     * Updates the explosion and fireworks events stored in the playGround by increasing the age of each of
     * these events every cycle and delete the events that exceeds the age limits.
     */
    public synchronized void updateEvents()
    {
        for(ExplosionVO2D e : explosions)
        {
            if(e.isAttached())
            {
                if(e.getAge() > 20)
                {
                    explosions.remove(e);
                }
                else
                {
                    e.setAge(e.getAge() + 1);
                }
            }
        }

        for(FireworkVO2D f : fireworks)
        {
            if(f.isAttached())
            {
                if(f.getAge() > 20)
                {
                    fireworks.remove(f);
                }
                else
                {
                    f.setAge(f.getAge() + 1);
                }
            }
        }

    }

    /**
     * Return an agent or environment object that is selected with the picker
     * 
     * @param state
     *        VirtualState that represent the picker
     * @return Virtual State of any agent or environment object that picked with the picker
     */
    public synchronized VirtualState getSelectedObject(VirtualState state)
    {
        // for each env obj state check whether it collides with given state
        for(EnvObjectState envObjectState : envObjects)
        {
            if(isCollidableWith(state, envObjectState))
            {
                return envObjectState;
            }
        }

        // for each agent state check whether it collides with the given state
        /*
         * synchronized(state)
         * {
         * for(AgentState agentState : agents.values())
         * {
         * if(isCollidableWith(state, agentState))
         * {
         * return agentState;
         * }
         * }
         * }
         */
        return null;
    }

    /**
     * Determines if virtual state s1 collides with virtual state s2
     * 
     * @param s1
     *        The first VirtualState to be tested for collision
     * @param s2
     *        The second VirtualState to be tested for collision
     * @return Boolean flag that determines if two given objects are colliding.
     */
    private boolean isCollidableWith(VirtualState s1, VirtualState s2)
    {
        // check whether two virtual states intersect each other
        if(s1.isCollidable() && s2.isCollidable())
        {
            if(s1.getBoundingArea().intersects(s2.getBoundingArea()))
            {
                return true;
            }
        }
        return false;
    }

    private void setCellMap(CellMap cellMap)
    {
        this.cellMap = cellMap;
    }

    @Override
    public synchronized PlayGround2D clone()
    {
        PlayGround2D playGround2D = new PlayGround2D();
        playGround2D.getAgents().putAll(this.getAgents());
        playGround2D.getCells().putAll(this.getCells());
        playGround2D.getEnvObjects().addAll(this.getEnvObjects());
        playGround2D.setCellMap(this.getCellMap());
        playGround2D.getExplosions().addAll(this.getExplosions());
        playGround2D.getFireworks().addAll(this.getFireworks());
        return playGround2D;
    }

    public synchronized int getAgentsCount(CellID cell)
    {
        CellStateDto dto = cells.get(cell);
        return (dto != null) ? dto.getAgentStates().size() : 0;
    }

    public synchronized int getEnvObjCount(CellID cell)
    {
        CellStateDto dto = cells.get(cell);
        return (dto != null) ? dto.getEnvObjects().size() : 0;
    }
}
