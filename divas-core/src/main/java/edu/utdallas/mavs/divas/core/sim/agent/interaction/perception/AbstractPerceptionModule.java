package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.SensedData;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.PhysicalSensor;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes an abstract agent's percpetion module.
 * 
 * @param <KM>
 *        the agent's KnowledgeModule type
 */
public abstract class AbstractPerceptionModule<KM extends KnowledgeModule<?>> implements PerceptionModule, Serializable
{
    private static final long                 serialVersionUID = 1L;

    /**
     * The agent's KnowledgeModule
     */
    protected KM                              knowledgeModule;

    /**
     * The perception combination module, for combining raw perception into knowledge
     */
    protected PerceptionCombinationModule<KM> perceptionCombinationModule;

    /**
     * The agent's sensors
     */
    public List<PhysicalSensor<KM, ?>>        senses;

    /**
     * Creates a new agent perception module
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     */
    public AbstractPerceptionModule(KM knowledgeModule)
    {
        super();
        this.knowledgeModule = knowledgeModule;
        this.perceptionCombinationModule = new PerceptionCombinationModule<KM>(knowledgeModule);
        this.senses = new ArrayList<PhysicalSensor<KM, ?>>();
    }

    /**
     * Updates sensors statuses
     */
    protected abstract void updateSensors();

    @Override
    public void prepareSensors()
    {
        updateSensors();

        // set sensor algorithms
        for(PhysicalSensor<?, ?> sense : senses)
            sense.setAlgorithm();
    }

    /**
     * Add some raw sensed data perceived this tick
     * 
     * @param sd
     *        the raw sense data
     */
    public void addPerceivedData(SensedData sd)
    {
        perceptionCombinationModule.addPerceivedData(sd);
    }

    @Override
    public void combinePerceptions()
    {
        perceptionCombinationModule.combinePerceptions();
    }

    /**
     * Adds a new sensor to this agent's perception module
     * 
     * @param sense
     *        an agent's sensor
     */
    public void addSense(PhysicalSensor<KM, ?> sense)
    {
        if(!senses.contains(sense))
            senses.add(sense);
    }

    /**
     * Removes a sensor from this agent
     * 
     * @param sense
     *        the agent sensor to be removed
     */
    public void removeSense(PhysicalSensor<KM, ?> sense)
    {
        senses.remove(sense);
    }

    @Override
    public void perceive(CellState cell)
    {
        clearPerceptionData();

        perceiveAgents(cell.getAgentStates());

        perceiveEnvObjs(cell.getEnvObjects());

        perceiveEvents(cell.getEvents());

        resolveObstructions();

    }

    /**
     * Clean out old perception data from previous cycles
     */
    private void clearPerceptionData()
    {
        perceptionCombinationModule.clearCombinationData();
        knowledgeModule.clearPerceptionKnowledge();
    }

    /**
     * Perceives agents passed onto this sensor
     * 
     * @param agentStates
     *        a set of agents
     */
    protected void perceiveAgents(List<AgentState> agentStates)
    {
        for(PhysicalSensor<?, ?> sense : senses)
            sense.perceiveAgents(agentStates);
    }

    /**
     * Perceives environment objects passed onto this sensor
     * 
     * @param objs
     *        a set of environment objects
     */
    protected void perceiveEnvObjs(List<EnvObjectState> objs)
    {
        for(PhysicalSensor<?, ?> sense : senses)
            sense.perceiveEnvObjs(objs);
    }

    /**
     * Perceives events passed onto this sensor
     * 
     * @param events
     *        a set of events
     */
    protected void perceiveEvents(List<EnvEvent> events)
    {

        for(PhysicalSensor<?, ?> sense : senses)
            sense.perceiveEvents(events);
    }

    /**
     * Checks for (and remove) obstructed objects
     */
    protected void resolveObstructions()
    {
        for(PhysicalSensor<?, ?> sense : senses)
        {
            sense.resolveObstructions();
        }
    }
}
