package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.PerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes an agent's abstract sensor.
 * <p>
 * Sensors are used by agents to perceive their situated environment.
 * 
 * @param <KM> the agent's KnowledgeModule type
 * @param <PM> the agent's perceptionmodule type
 */
public abstract class PhysicalSensor<KM extends KnowledgeModule<?>, PM extends PerceptionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's knowledge module
     */
    protected KM              knowledgeModule;

    /**
     * The agent's perception module
     */
    protected PM              perceptionModule;

    /**
     * The enabled status of this sensor
     */
    protected boolean         enabled;

    /**
     * Creates a new sensor
     * 
     * @param knowledgeModule the agent's KnowledgeModule
     * @param perceptionModule the agent's perceptionmodule
     */
    public PhysicalSensor(KM knowledgeModule, PM perceptionModule)
    {
        this.knowledgeModule = knowledgeModule;
        this.perceptionModule = perceptionModule;
        this.enabled = true;
    }

    /**
     * Perceives agents passed onto this sensor
     * 
     * @param agents a set of agents
     */
    public void perceiveAgents(List<AgentState> agents)
    {
        if(isEnabled())
            receiveAgents(agents);
    }

    /**
     * Perceives events passed onto this sensor
     * 
     * @param events a set of events
     */
    public void perceiveEvents(List<EnvEvent> events)
    {
        if(isEnabled())
            receiveEvents(events);
    }

    /**
     * Perceives environment objects passed onto this sensor
     * 
     * @param envObjs a set of environment objects
     */
    public void perceiveEnvObjs(List<EnvObjectState> envObjs)
    {
        if(isEnabled())
            receiveEnvObjs(envObjs);
    }

    /**
     * Checks the name of the algorithm from the agent's state to ensure we are using the correct one. If not, the
     * correct algorithm will be selected.
     */
    public abstract void setAlgorithm();

    /**
     * Process perception of agents passed onto this sensor
     * 
     * @param agents a set of agents
     */
    protected abstract void receiveAgents(List<AgentState> agents);

    /**
     * Process perception of events passed onto this sensor
     * 
     * @param events a set of events
     */
    protected abstract void receiveEvents(List<EnvEvent> events);

    /**
     * Process perception of environment objects passed onto this sensor
     * 
     * @param envObjs a set of environment objects
     */
    protected abstract void receiveEnvObjs(List<EnvObjectState> envObjs);

    /**
     * Checks for (and remove) obstructed objects
     */
    public abstract void resolveObstructions();

    /**
     * Checks if this sensor is enabled
     * 
     * @return the status of the sensor
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Enables/disables this sensor
     * 
     * @param enabled the new status of the sensor
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
