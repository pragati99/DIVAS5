package edu.utdallas.mavs.divas.gui.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.utdallas.mavs.divas.core.client.dto.CellStateDto;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.gui.mvp.model.CellSummary;
import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity.SimEntityType;

/**
 * This class describes a repository for runtime information about the simulation.
 * <p>
 * It is similar to the playground concept for the visualizers.
 */
public class SimRepository
{
    private static Map<Integer, AgentState>     agents;

    private static Map<Integer, EnvObjectState> envObjs;

    private static Map<Integer, EnvEvent>       events;

    private static Map<CellID, CellSummary>     cells;

    private static Map<Integer, SimEntity>      simEntities;

    public SimRepository()
    {
        agents = Collections.synchronizedMap(new HashMap<Integer, AgentState>());
        envObjs = Collections.synchronizedMap(new HashMap<Integer, EnvObjectState>());
        events = Collections.synchronizedMap(new HashMap<Integer, EnvEvent>());
        cells = Collections.synchronizedMap(new HashMap<CellID, CellSummary>());
        simEntities = Collections.synchronizedMap(new HashMap<Integer, SimEntity>());
    }

    public void update(CellStateDto dto)
    {
        // updates agents
        for(AgentState agentState : dto.getAgentStates())
        {
            simEntities.put(agentState.getID(), new SimEntity(agentState.getID(), SimEntityType.Agent, agentState.getAgentType(), dto.getId().toString(), agentState));
            agents.put(agentState.getID(), agentState);
        }

        // updates envObjs
        for(EnvObjectState env : dto.getEnvObjects())
        {
            simEntities.put(env.getID(), new SimEntity(env.getID(), SimEntityType.EnvObject, env.getType(), dto.getId().toString(), env));
            envObjs.put(env.getID(), env);
        }

        // updates events
        for(EnvEvent event : dto.getEvents())
        {
            events.put(event.getID(), event);
        }

        // updates cell summary TODO
        cells.put(dto.getId(), new CellSummary());
    }

    public Map<Integer, SimEntity> find(String query)
    {
        Map<Integer, SimEntity> results = new HashMap<Integer, SimEntity>();

        synchronized(simEntities)
        {
            if(isInteger(query))
            {
                int id = Integer.valueOf(query);
                if(simEntities.containsKey(id))
                {
                    results.put(id, simEntities.get(id));
                }
            }
            String lcquery = query.toLowerCase();
            for(SimEntity e : simEntities.values())
            {
                if(e.getName().toLowerCase().contains(lcquery) || e.getType().toString().toLowerCase().contains(lcquery) || e.getCellID().toLowerCase().contains(lcquery))
                {
                    results.put(e.getId(), e);
                }
            }
        }

        return results;
    }

    public boolean isInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public AgentState findAgent(int ID)
    {
        return agents.get(ID);
    }

    public EnvObjectState findEnvObj(int ID)
    {
        return envObjs.get(ID);
    }

    public EnvEvent findEvent(int ID)
    {
        return events.get(ID);
    }

    public static void clear()
    {
        agents.clear();
        envObjs.clear();
        events.clear();
        cells.clear();
        simEntities.clear();
    }

    public int getAgentsCount()
    {
        return agents.size();
    }

    public int getEnvObjectsCount()
    {
        return envObjs.size();
    }

    public void removeAgent(AgentState state)
    {
        agents.remove(state.getID());
        simEntities.remove(state.getID());
    }

    public void removeEnvObject(EnvObjectState state)
    {
        envObjs.remove(state.getID());
        simEntities.remove(state.getID());
    }
}
