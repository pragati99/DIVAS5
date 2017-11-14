package edu.utdallas.mavs.divas.core.client;

import java.io.File;

import com.google.inject.Inject;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.client.dto.GeneralAgentProperties;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg.RuntimeAgentCommand;
import edu.utdallas.mavs.divas.core.sim.common.event.BombEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.DrumsEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.DynamiteEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.FireworkEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.GrillingFoodEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.SpotlightEvent;
import edu.utdallas.mavs.divas.core.sim.common.percept.AudioPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.SmellPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This class adapts the uses of {@link SimFacade} to make it simpler for clients of the simulation to interact
 * with it.
 */
public class SimAdapter
{
    protected SimFacade simFacade;

    /**
     * Creates a new instance of {@link SimAdapter} and injects the necessary dependencies.
     * 
     * @param simFacade
     *        the simulation facade
     */
    @Inject
    public SimAdapter(SimFacade simFacade)
    {
        this.simFacade = simFacade;
    }

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
    public void sendRuntimeAgentCommand(int agentID, RuntimeAgentCommand command, Object arg)
    {
        simFacade.sendRuntimeAgentCommand(agentID, command, arg);
    }

    /**
     * Creates an agent at the given location in the simulation. The Y component of the location will be set to 0.
     * 
     * @param state
     *        agent state to be created
     * @param location
     *        the location of the agent.
     */
    public void createAgent(AgentState state, Vector3f location)
    {
        if(state == null)
            state = new AgentState();
        location.setY(0);
        state.setPosition(location);
        simFacade.createAgent(state);
    }

    /**
     * Creates an environment object in the simulation.
     * 
     * @param envObject
     */
    public void createEnvObject(EnvObjectState envObject)
    {
        envObject.getPosition().setY(0);
        simFacade.createEnvObject(envObject);
    }

    /**
     * Triggers an explosion in the simulation.
     * 
     * @param location
     *        the location of the explosion
     * @param intensity
     *        the intensity of the explosion
     * @param smoke
     *        flag indicating if it produces smoke
     */
    public void createExplosion(Vector3f location, float intensity, boolean smoke)
    {
        BombEvent explosion = new BombEvent();
        location.setY(0.5f);
        explosion.setOrigin(location);
        explosion.setIntensity(intensity);
        explosion.setCurrentlyAudible(true);
        explosion.setCurrentlySmellable(true);
        explosion.setCurrentlyVisible(true);
        explosion.setSmoke(smoke);
        simFacade.createEvent(explosion);
    }

    /**
     * Triggers a dynamite in the simulation.
     * 
     * @param location
     *        the location of the explosion
     * @param intensity
     *        the intensity of the explosion
     * @param smoke
     *        flag indicating if it produces smoke
     */
    public void createDynamite(Vector3f location, float intensity, boolean smoke)
    {
        DynamiteEvent dynamite = new DynamiteEvent();
        location.setY(0.5f);
        dynamite.setOrigin(location);
        dynamite.setIntensity(intensity);
        dynamite.setCurrentlyAudible(true);
        dynamite.setCurrentlySmellable(true);
        dynamite.setCurrentlyVisible(true);
        dynamite.setSmoke(smoke);
        simFacade.createEvent(dynamite);
    }

    /**
     * Triggers fireworks in the simulation.
     * 
     * @param location
     *        the location of the fireworks
     * @param intensity
     *        the intensity of the fireworks
     * @param smoke
     *        flag indicating if it produces smoke
     */
    public void createFireworks(Vector3f location, float intensity, boolean smoke)
    {
        FireworkEvent firework = new FireworkEvent();
        location.setY(50);
        firework.setOrigin(location);
        firework.setIntensity(intensity);
        firework.setCurrentlyAudible(true);
        firework.setCurrentlySmellable(true);
        firework.setCurrentlyVisible(true);
        simFacade.createEvent(firework);
    }

    /**
     * Creates a drums object in the simulation
     * 
     * @param location
     *        the location of the drums
     */
    public void createDrums(Vector3f location)
    {
        DrumsEvent drums = new DrumsEvent();
        drums.setOrigin(location);
        drums.setIntensity(1);
        drums.setCurrentlyAudible(true);
        drums.setCurrentlySmellable(false);
        drums.setCurrentlyVisible(true);
        simFacade.createEvent(drums);
    }

    /**
     * Creates a grill in the simulation
     * 
     * @param location
     *        the location of the grill
     */
    public void createGrill(Vector3f location)
    {
        GrillingFoodEvent grill = new GrillingFoodEvent();
        grill.setOrigin(location);
        grill.setIntensity(1);
        grill.setCurrentlyAudible(true);
        grill.setCurrentlySmellable(true);
        grill.setCurrentlyVisible(true);
        simFacade.createEvent(grill);
    }

    /**
     * Creates a spotlight in the simulation
     * 
     * @param location
     *        the location of the spotlight
     */
    public void createSpotlight(Vector3f location)
    {
        SpotlightEvent spotlight = new SpotlightEvent();
        spotlight.setOrigin(location);
        spotlight.setIntensity(1);
        spotlight.setCurrentlyAudible(false);
        spotlight.setCurrentlySmellable(false);
        spotlight.setCurrentlyVisible(true);
        simFacade.createEvent(spotlight);
    }

    /**
     * Updates the state of an agent or environment object. The Y component of the location will be set to 0.
     * 
     * @param state
     *        the state to be updated.
     */
    public void sendStateUpdate(VirtualState state)
    {
        simFacade.sendStateUpdate(state);
    }

    /**
     * Sends a runtime agent state update command to the simulation.
     * 
     * @param state
     *        the updated state of the agent
     * @param properties
     *        the new properties of the agent
     */
    public void sendAgentStateUpdate(AgentState state, GeneralAgentProperties properties)
    {
        if(state.canSee())
        {
            VisionPerceptor v = (VisionPerceptor) state;
            v.setVisionAlgorithm(properties.getVisionAlgorithm());
            v.setVisibleDistance(properties.getVisibleDistance());
            v.setFOV(properties.getFov());
        }

        if(state.canHear())
        {
            AudioPerceptor a = (AudioPerceptor) state;
            a.setAuditoryEnabled(properties.isAuditoryEnabled());
        }

        if(state.canSmell())
        {
            SmellPerceptor s = (SmellPerceptor) state;
            s.setOlfactoryEnabled(properties.isOlfactoryEnabled());
        }

        sendStateUpdate(state);
    }

    /**
     * Adds a client subscription to the message broker
     * 
     * @param topic
     *        the topic to be registered for
     * @param subscriber
     *        the subscriber handler
     */
    public void addSubscription(String topic, Subscriber subscriber)
    {
        simFacade.addSubscription(topic, subscriber);
    }

    /**
     * Configures publication topics to the message broker
     */
    public void start()
    {
        simFacade.setupPublications();
    }

    /**
     * Starts the simulation
     */
    public void startSimulation()
    {
        simFacade.startSimulation();
    }

    /**
     * Pauses the simulation
     */
    public void pauseSimulation()
    {
        simFacade.pauseSimulation();
    }

    /**
     * Changes the reorganization strategy to be used in the simulation
     * 
     * @param str
     *        the reorganization strategy
     */
    public void setReorganizationStrategy(ReorganizationStrategy str)
    {
        simFacade.setReorganizationStrategy(str);
    }

    /**
     * Notifies the simulation configuration settings change
     */
    public void notifySimPropertiesChange()
    {
        simFacade.notifySimPropertiesChange();
    }

    /**
     * Splits a cell with its neighboring cell
     * 
     * @param cellID
     *        the id of the cell
     */
    public void splitCell(final CellID cellID)
    {
        simFacade.splitCell(cellID);
    }

    /**
     * Merges a cell with its neighboring cell
     * 
     * @param cellID
     *        the id of the cell
     */
    public void mergeCell(final CellID cellID)
    {
        simFacade.mergeCell(cellID);
    }

    /**
     * Save a simulation snapshot
     * 
     * @param file
     *        The file the simulation snapshot will be saved to.
     */
    public void saveSimulationSnapshot(File file)
    {
        simFacade.saveSimulationSnapshot(file);
    }

    /**
     * Load a simulation snapshot
     * 
     * @param file
     *        The file the simulation snapshot will be loaded from.
     */
    public void loadSimulationSnapshot(File file)
    {
        simFacade.loadSimulationSnapshot(file);
    }

    // /**
    // * Load a new environment to the simulation
    // *
    // * @param file
    // * The environment file
    // */
    // public void loadEnvironment(File file)
    // {
    // simFacade.loadEnvironment(file);
    // }

    /**
     * Load a new environment from XML file to the simulation
     * 
     * @param file
     *        The environment XML file
     */
    public void loadEnvironmentXML(File file)
    {
        simFacade.loadEnvironmentXML(file);
    }

    /**
     * Saves the current simulation environment
     * 
     * @param file
     *        The environment file
     */
    public void saveEnvironment(File file)
    {
        simFacade.saveEnvironment(file);
    }

    /**
     * Removes the given agent state
     * 
     * @param agentState
     *        The agent state to to be destroyed.
     */
    public void destroyAgent(AgentState agentState)
    {
        simFacade.destroyAgent(agentState);
    }

    /**
     * Removes the given environment object state
     * 
     * @param envObjState
     *        The environment object state to to be destroyed.
     */
    public void destroyEnvObject(EnvObjectState envObjState)
    {
        simFacade.destroyEnvObject(envObjState);
    }
}
