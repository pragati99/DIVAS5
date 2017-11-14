package edu.utdallas.mavs.divas.core.sim.agent.knowledge;

import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventKnowledge;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventPropertyKnowledge;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * Agent Knowledge Module
 * 
 * @param <S> the agent state type
 */
public interface KnowledgeModule<S extends AgentState>
{
    /**
     * Get own agent state.
     * 
     * @return agent's own state
     */
    public S getSelf();

    /**
     * Set the agent's state.
     * 
     * @param state
     *        agent's state
     */
    public void setSelf(S state);

    /**
     * Set simulation cycle
     * 
     * @param time
     *        the sim cycle
     */
    public void setTime(long time);

    /**
     * Get the simulation cycle
     * 
     * @return sim cycle
     */
    public long getTime();

    /**
     * Get Agent's Id
     * 
     * @return The agent's ID
     */
    public int getId();

    /**
     * Set the agents ID
     * 
     * @param id
     *        Agent ID
     */
    public void setId(int id);

    /**
     * Get Event Knowledge
     * 
     * @param type
     *        The type of knowledge you wish to obtain information about.
     * @return A list of event property knowledge that have the listed type
     */
    public List<EventPropertyKnowledge> getEventKnowledgeFromType(String type);

    /**
     * Get event knowledge with name.
     * 
     * @param name
     *        The name.
     * @return Event knowledge regarding events with this name.
     */
    public EventKnowledge getEventKnowledgeByName(String name);

    /**
     * Add final event knowledge of what perceived this cycle.
     * 
     * @param crd
     *        combined event knowledge
     */
    public void addEventsThisTick(CombinedReasonedData crd);

    /**
     * Get final list of event knowledge perceived during this cycle.
     * 
     * @return the list of event knowledge
     */
    public List<CombinedReasonedData> getEventsThisTick();

    /**
     * Add perceived agents to this KM
     * 
     * @param agent
     *        the perceived agent
     */
    public void addAgent(AgentState agent);

    /**
     * Add perceived environment objects to this KM
     * 
     * @param obj
     *        the perceived object
     */
    public void addEnvObj(EnvObjectState obj);

    /**
     * Clear temporary knowledge associated with perception
     */
    public void clearPerceptionKnowledge();
}
