package edu.utdallas.mavs.divas.core.sim.agent;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.driver.AgentDriver;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.InteractionModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.KnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.planning.PlanningModule;
import edu.utdallas.mavs.divas.core.sim.agent.task.TaskModule;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;

/**
 * This class describes an abstract virtual agent.
 * <p>
 * A virtual agent is one that is deployed in the simulation situated environment and is part of the virtual simulation. It is not a design concept. For example, a virtual agent could be a human, a car, or a blood cell.
 * 
 * @param <S> the agent state type
 * @param <KM> the agent's knowledge module type
 * @param <IM> the agent's interaction module type
 * @param <PCM> the agent's planning and control module type
 * @param <TM> the agent's task module type
 */
public abstract class AbstractAgent<S extends AgentState, KM extends KnowledgeModule<S>, IM extends InteractionModule, PCM extends PlanningModule<KM, TM>, TM extends TaskModule<KM>> implements Agent, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The <code>KnowledgeModule</code> of the agent
     */
    protected KM              knowledgeModule;

    /**
     * The <code>InteractionModule</code> of the agent
     */
    protected IM              interactionModule;

    /**
     * The <code>PlanningModule</code> of the agent
     */
    protected PCM             planningModule;

    /**
     * The <code>TaskModule</code> of the agent
     */
    protected TM              taskModule;

    /**
     * The agent's driver, which is used to manually drive the agent.
     */
    protected AgentDriver     agentDriver      = null;

    /**
     * Constructs a new agent.
     * 
     * @param state
     *        the state of the new agent
     */
    public AbstractAgent(S state)
    {
        assert (state != null);
        knowledgeModule = createKnowledgeModule(state);
        interactionModule = createInteractionModule(knowledgeModule);
        taskModule = createTaskModule(knowledgeModule);
        planningModule = createPlanningModule(knowledgeModule, taskModule, interactionModule);
    }

    /**
     * Creates the agent's <code>KnowledgeModule</code>
     * 
     * @param state
     *        the agent's state
     * @return the newly created <code>KnowledgeModule</code>
     */
    protected abstract KM createKnowledgeModule(S state);

    /**
     * Creates the agent's <code>InteractionModule</code>
     * 
     * @param knowledgeModule
     *        the agent's <code>KnowledgeModule</code>
     * @return the newly created <code>InteractionModule</code>
     */
    protected abstract IM createInteractionModule(KM knowledgeModule);

    /**
     * Creates the agent's <code>PlanningModule</code>
     * 
     * @param knowledgeModule
     *        the agent's <code>KnowledgeModule</code>
     * @param taskModule
     *        the agent's <code>taskModule</code>
     * @param interactionModule
     *        the agent's <code>InteractionModule</code>
     * @return the newly created <code>PlanningModule</code>
     */
    protected abstract PCM createPlanningModule(KM knowledgeModule, TM taskModule, IM interactionModule);

    /**
     * Creates the agent's <code>TaskModule</code>
     * 
     * @param knowledgeModule
     *        the agent's <code>KnowledgeModule</code>
     * @return the newly created <code>TaskModule</code>
     */
    protected abstract TM createTaskModule(KM knowledgeModule);

    /**
     * Generate the agent's stimuli
     * 
     * @return agent stimuli
     */
    protected abstract Stimuli generateStimuli();

    @Override
    public S getState()
    {
        return knowledgeModule.getSelf();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setState(AgentState newState)
    {
        knowledgeModule.setSelf((S) newState);
    }

    @Override
    public int getId()
    {
        return knowledgeModule.getId();
    }

    @Override
    public void perceive(CellState cell)
    {
        // sets the current simulation cycle associated with this agent's state
        knowledgeModule.setTime(cell.getTime());
        interactionModule.perceive(cell);
    }

    @Override
    public Stimuli execute()
    {
        Stimuli stimuli = generateStimuli();
        interactionModule.dispatchMessages();
        return stimuli;
    }

    @Override
    public void addGoal(Goal newGoal)
    {
        planningModule.addGoal(newGoal);
    }

    @Override
    public String toString()
    {
        return "Agent " + knowledgeModule.getId();
    }

    @Override
    public boolean equals(Object o)
    {
        AbstractAgent<?, ?, ?, ?, ?> t = (AbstractAgent<?, ?, ?, ?, ?>) o;
        return t.getId() == getId();
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 31 + getId();
        return hash;
    }
}
