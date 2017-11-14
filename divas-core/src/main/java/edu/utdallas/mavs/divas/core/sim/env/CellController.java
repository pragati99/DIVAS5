package edu.utdallas.mavs.divas.core.sim.env;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg;
import edu.utdallas.mavs.divas.core.sim.agent.Agent;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;

/**
 * The <code>CellController</code> interface must be implemented by any class wishing to fulfill a
 * cell controller functionalities.
 */
public interface CellController
{
    /**
     * @return <code>CellID</code> of the current cell controller.
     */
    public CellID getCellID();

    /**
     * Reacts to the agent, external and user influences and stimuli by combining these stimuli and
     * applying it to the current agent states to compute the new agent states.
     * 
     * @return List of <code>AgentStateModel</code> generated after environment react phase.
     */
    public List<AgentStateModel> react();

    /**
     * Deliberates the environment by updating the <code>AgentStateModel</code> with the computed next
     * state and Publishing a message with the cell controller state to the subscribers.
     * 
     * @param agentStates
     *        List of <code>AgentStateModel</code>.
     */
    public void deliberate(List<AgentStateModel> agentStates);

    /**
     * Executes the agent phase.
     * <p>
     * This involves retrieving the combined cell state for each agent according to it's cells subscription in order to be perceived. And generating the agent stimuli based on what agent perceived and storing it in the cell controller agent stimuli
     * list.
     */
    public void executeAgents();

    /**
     * @return <code>CellState</code> of the current cell controller.
     */
    public CellState getCellState();

    /**
     * clear the map of agents in the cell controller and the list of agents in the cell state.
     */
    public void clearAgents();

    /**
     * Adds the agent with the given state to the map of agent in the cell controller and the list
     * of agents in the cell state.
     * 
     * @param agent
     *        Agent state to be added.
     */
    public void addAgent(Agent agent);

    /**
     * Adds the given environment object state to the list of environment object state in the cell state.
     * 
     * @param eo
     *        <code>EnvObjectState</code> to be added.
     * @return boolean flag that indicates that the addition process completed successfully.
     */
    public boolean addEnvObject(EnvObjectState eo);

    /**
     * Adds the given event to the list of events in the cell state.
     * 
     * @param e
     *        <code>EnvEvent</code> to be added.
     * @return boolean flag that indicates that the addition process completed successfully.
     */
    public boolean addEvent(EnvEvent e);

    /**
     * @return Agents count in the cell controller.
     */
    public int getAgentsCount();

    /**
     * @return Environment objects count in the cell controller.
     */
    public int getEnvObjectsCount();

    /**
     * Checks if the agent with the given agent Id is in the cell controller state agents map.
     * 
     * @param agentID
     *        Agent Id to look for.
     * @return boolean flag that indicates if the agent with the given Id exists in the cell state agents map.
     */
    public boolean containsAgent(int agentID);

    /**
     * Checks if the environment object with given environment object Id exists in the cell state
     * environment objects list.
     * 
     * @param envStateID
     *        environment object state Id to look for.
     * @return boolean flag that indicates if the environment object exist in the environment objects list.
     */
    public boolean containsEnvObject(int envStateID);

    /**
     * Gets the agent with the given agent Id from the agents map.
     * 
     * @param agentId
     *        Agent Id for agent state to be found.
     * @return <code>VirtualAgent</code> for the agent with the given agent Id.
     */
    public Agent getAgent(int agentId);

    /**
     * @return Map of agent id along with agent states.
     */
    public Map<Integer, Agent> getAgents();

    /**
     * Reset the map of agents in the cell controller with the given map of agents.
     * 
     * @param agents
     */
    public void setAgents(Map<Integer, Agent> agents);

    /**
     * Removes the agent state with given Id from the map of agents in the cell controller and the
     * list of agents in the cell state.
     * 
     * @param agentId
     *        Agent Id for the agent to be removed.
     */
    public void removeAgent(int agentId);

    /**
     * Removes the environment object state from the list of environment object states in the cell controller.
     * 
     * @param state
     *        Environment object state to be removed.
     */
    public void removeEnvObject(EnvObjectState state);

    /**
     * Adds the given <code>ExternalStimulus</code> to the queue of the External Stimulus in the
     * cell controller.
     * 
     * @param stateStimulus
     *        external stimulus to be added.
     */
    public void addExternalStimulus(ExternalStimulus stateStimulus);

    /**
     * Add a user runtime agent command to the queue of user commands in the cell controllers.
     * 
     * @param cmd
     *        Runtime agent command to be added.
     */
    public void addUserCommand(RuntimeAgentCommandMsg cmd);

    /**
     * Merge the given cell controller with its direct sibling.
     * <p>
     * The sibling cell controller will be found and merged with the given cell controller. The new parent cell controller will have the boundaries and the contents of the combined merged cell controllers.
     * 
     * @param sibling
     *        Cell controller to be merged.
     */
    public void merge(CellController sibling);

    /**
     * Splits the cell controller into two child cell controllers.
     * <p>
     * This method involves: 1)Creating two child cell controllers with boundaries that equally split the parent boundaries. 2)The cell controller contents like the agents, environment objects, events, agent stimuli, user commands and external
     * stimulus will be distributed on the child cell controllers according to the position of each of them.
     * 
     * @param <CC>
     *        The cell controller type.
     * @return Array of child cell controllers.
     */
    public <CC extends CellController> List<CC> split();

    /**
     * @return The external stimuli queue.
     */
    public Collection<ExternalStimulus> getExternalStimuliBuffer();

    /**
     * @return The users agent runtime command list.
     */
    public Collection<RuntimeAgentCommandMsg> getUserCommandBuffer();

    /**
     * @return The list with agent stimuli.
     */
    public List<AgentStimulus> getAgentStimuli();

    /**
     * Determines if the given virtual state can be added to the simulation according to the the environment
     * rules.
     * 
     * @param state
     *        <code>VirtualState</code> to be tested for addition.
     * @return boolean flag that indicates if the given virtual state can be added to the simulation.
     */
    public boolean hasConflicts(VirtualState state);

    /**
     * Determines if an agent collides with another agent or environment object in the simulation.
     * 
     * @param agentStateModel
     *        The agent state model containing next position and next bounding area.
     * @return true is the conflict was resolved and false otherwise.
     */
    public boolean resolveConflicts(AgentStateModel agentStateModel);

    /**
     * Destroy the cell controller by clearing the cell state contents and clearing all the lists
     * in the cell controllers and terminating the multithreader and disconnecting the communication module.
     */
    public void destroy();
}
