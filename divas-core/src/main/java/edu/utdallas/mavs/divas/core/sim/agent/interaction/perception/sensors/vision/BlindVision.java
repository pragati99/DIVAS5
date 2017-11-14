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
 * Implementation of a blind vision algorithm. The agent will see nothing.
 * 
 * @param <S> the agent state type
 */
public class BlindVision<S extends HumanAgentState> extends AbstractVision<S, HumanKnowledgeModule<S>, HumanPerceptionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new blind vision alforithm
     * 
     * @param knowledgeModule the agent's KnowledgeModule
     * @param humanPerceptionModule
     */
    public BlindVision(HumanKnowledgeModule<S> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
    }

    @Override
    public void receiveAgents(List<AgentState> agents)
    {
        // empty
    }

    @Override
    public void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        // empty
    }

    @Override
    public void receiveEvents(List<EnvEvent> events)
    {
        // empty
    }
}
