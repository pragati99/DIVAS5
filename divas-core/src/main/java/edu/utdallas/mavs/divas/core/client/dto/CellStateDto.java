package edu.utdallas.mavs.divas.core.client.dto;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.CellMap;

/**
 * This class describes a Data Transfer Object for CellState
 */
public class CellStateDto implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long              cycles;
    private int               period;
    private CellID            id;
    private CellBounds        bounds;
    // List<AgentDto> agents = new ArrayList<AgentDto>();
    List<AgentState>          agents;
    List<EnvObjectState>      envObjects;
    List<EnvEvent>            events;
    private CellMap           cellMap;

    public CellMap getCellMap()
    {
        return cellMap;
    }

    public CellStateDto(CellState cellState, long cycles, int period, CellMap cellMap)
    {
        this.cycles = cycles;
        this.period = period;
        id = cellState.getId();
        bounds = cellState.getBounds();
        agents = cellState.getAgentStates();
        envObjects = cellState.getEnvObjects();
        events = cellState.getEvents();
        this.cellMap = cellMap;
        // for(AgentState s : cellState.getAgentStates())
        // {
        // agents.add(new AgentDto(s));
        // }
    }

    /**
     * 
     */
    public CellStateDto()
    {
        // TODO Auto-generated constructor stub
    }

    public long getCycleNumber()
    {
        return cycles;
    }

    public int getPeriod()
    {
        return period;
    }

    public List<AgentState> getAgentStates()
    {
        // List<AgentState> agentStates = new ArrayList<AgentState>();
        // for(AgentDto s : agents)
        // {
        // agentStates.add(s.getAgentState());
        // }
        // return agentStates;
        return agents;
    }

    // public List<AgentDto> getAgents()
    // {
    // return agents;
    // }

    public List<EnvObjectState> getEnvObjects()
    {
        return envObjects;
    }

    public List<EnvEvent> getEvents()
    {
        return events;
    }

    public CellID getId()
    {
        return id;
    }

    public CellBounds getBounds()
    {
        return bounds;
    }

    public int getNumberOfObjects()
    {
        return agents.size() + envObjects.size() + events.size();
    }

    public void setPeriod(int period)
    {
        this.period = period;

    }
}
