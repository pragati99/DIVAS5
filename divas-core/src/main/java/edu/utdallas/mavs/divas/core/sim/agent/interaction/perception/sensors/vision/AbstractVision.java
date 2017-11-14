package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.PerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * Abstract agent vision algorithm interface.
 * 
 * @param <S> the agent state type
 * @param <KM> the agent's knowledge module type
 * @param <PM> the agent's perception module
 */
public abstract class AbstractVision<S extends AgentState, KM extends KnowledgeModule<S>, PM extends PerceptionModule> implements Serializable
{
    private static final long   serialVersionUID = 1L;

    private final static Logger logger           = LoggerFactory.getLogger(AbstractVision.class);

    /**
     * the agent's KnowledgeModule
     */
    protected KM                knowledgeModule;

    /**
     * the agent's perception module
     */
    protected PM                perceptionModule;

    /**
     * Creates a new vision algorithm
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     * @param perceptionModule
     */
    public AbstractVision(KM knowledgeModule, PM perceptionModule)
    {
        this.knowledgeModule = knowledgeModule;
        this.perceptionModule = perceptionModule;
    }

    /**
     * Process perception of agents passed onto this sensor
     * 
     * @param agents
     *        a set of agents
     */
    public abstract void receiveAgents(List<AgentState> agents);

    /**
     * Process perception of environment objects passed onto this sensor
     * 
     * @param envObjs
     *        a set of environment objects
     */
    public abstract void receiveEnvObjs(List<EnvObjectState> envObjs);

    /**
     * Process perception of events passed onto this sensor
     * 
     * @param events
     *        a set of events
     */
    public abstract void receiveEvents(List<EnvEvent> events);

    /**
     * Checks for (and remove) obstructed objects
     */
    public void resolveObstructions()
    {
        logger.debug("Not Divas Vision Algorithm. Obstruction check ignored");
    }
}
