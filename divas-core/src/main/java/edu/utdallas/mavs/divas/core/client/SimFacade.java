package edu.utdallas.mavs.divas.core.client;

import java.io.File;

import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg.RuntimeAgentCommand;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This interface describes a facade for clients to interact with the simulation.
 */
public interface SimFacade
{
    /**
     * Sends a runtime agent command to the simulation.
     * 
     * @param agentID
     *        the id of the agent to which the command is to be applied
     * @param command
     *        the command type
     * @param arg
     *        any argument or data to be passed along with the command, depending on the command type
     */
    public void sendRuntimeAgentCommand(int agentID, RuntimeAgentCommand command, Object arg);

    /**
     * Creates an agent in the simulation.
     * 
     * @param agent
     *        the agent state of the agent to be created
     */
    public void createAgent(AgentState agent);

    /**
     * Creates an environment object in the simulation
     * 
     * @param envObject
     *        the environment object state of the environment object to be created
     */
    public void createEnvObject(EnvObjectState envObject);

    /**
     * Creates an event in the simulation
     * 
     * @param event
     *        the event to be created
     */
    public void createEvent(EnvEvent event);

    /**
     * Updates the state of an agent or environment object
     * 
     * @param state
     *        the state to be updated
     */
    public void sendStateUpdate(VirtualState state);

    /**
     * Starts the simulation
     */
    public void startSimulation();

    /**
     * Pauses the simulation
     */
    public void pauseSimulation();

    /**
     * Adds a client subscription to the message broker
     * 
     * @param topic
     *        the topic to be registered for
     * @param subscriber
     *        the subscriber handler
     */
    public void addSubscription(String topic, Subscriber subscriber);

    /**
     * Adds publication topics to the message broker
     */
    public void setupPublications();

    /**
     * Changes the reorganization strategy to be used in the simulation
     * 
     * @param str
     *        the reorganization strategy
     */
    public void setReorganizationStrategy(ReorganizationStrategy str);

    /**
     * Splits a cell
     * 
     * @param cellID
     *        the id of the cell
     */
    public void splitCell(final CellID cellID);

    /**
     * Merges a cell with its neigboring cell
     * 
     * @param cellID
     *        the id of the cell
     */
    public void mergeCell(final CellID cellID);

    /**
     * Notifies the simulation configuration settings change
     */
    public void notifySimPropertiesChange();

    /**
     * Save a simulation snapshot
     * 
     * @param file
     *        The file the simulation snapshot will be saved to.
     */
    public void saveSimulationSnapshot(File file);

    /**
     * Load a simulation snapshot
     * 
     * @param file
     *        The file the simulation snapshot will be loaded from.
     */
    public void loadSimulationSnapshot(File file);

//    /**
//     * Load a new environment to the simulation
//     * 
//     * @param file
//     *        The environment file
//     */
//    public void loadEnvironment(File file);

    /**
     * Load a new environment to the simulation
     * 
     * @param file
     *        The environment file
     */
    public void loadEnvironmentXML(File file);

    /**
     * Saves the current simulation environment only
     * 
     * @param file
     *        The XML file that contains complete information about environment objects
     */
    public void saveEnvironment(File file);

    /**
     * Removes the given agent state
     * 
     * @param agentState
     *        The agent state to to be destroyed.
     */
    public void destroyAgent(AgentState agentState);

    /**
     * Removes the given environment object state
     * 
     * @param envObjState
     *        The environment object state to to be destroyed.
     */
    public void destroyEnvObject(EnvObjectState envObjState);

}
