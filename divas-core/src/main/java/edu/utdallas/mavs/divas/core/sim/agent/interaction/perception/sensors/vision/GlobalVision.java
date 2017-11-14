package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;

/**
 * Implementation of a global vision algorithm. Anything sent to this agent is considered visible.
 * <p>
 * Note: this algorithm is truly global only in the case of a single cell environment
 * 
 * @param <S> the agent state type
 */
public class GlobalVision<S extends HumanAgentState> extends AbstractVision<S, HumanKnowledgeModule<S>, HumanPerceptionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new global vision algorithm
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     * @param humanPerceptionModule
     */
    public GlobalVision(HumanKnowledgeModule<S> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
    }

    @Override
    public void receiveAgents(List<AgentState> agents)
    {
        for(AgentState agent : agents)
            receive(agent);
    }

    @Override
    public void receiveEvents(List<EnvEvent> events)
    {
        for(EnvEvent event : events)
        {
            if(event.isCurrentlyVisible())
            {
                receive(event);
            }
        }
    }

    @Override
    public void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        for(EnvObjectState obj : envObjs)
            receive(obj);
    }

    /**
     * @param agentState
     */
    public void receive(AgentState agentState)
    {
        if(agentState.isVisible() && agentState.getID() != knowledgeModule.getId())
        {
            knowledgeModule.addAgent(agentState);
        }
    }

    /**
     * @param event
     */
    public void receive(EnvEvent event)
    {
        // TODO: save events
    }

    /**
     * @param envObj
     */
    public void receive(EnvObjectState envObj)
    {
        if(envObj.isVisible())
        {
            knowledgeModule.addEnvObj(envObj);
        }
    }
}
