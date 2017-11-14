package edu.utdallas.mavs.divas.core.sim.env;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.core.msg.AssignEventIDMsg;
import edu.utdallas.mavs.divas.core.msg.AssignStateIDMsg;
import edu.utdallas.mavs.divas.core.msg.CreateEventMsg;
import edu.utdallas.mavs.divas.core.msg.CreateStateMsg;
import edu.utdallas.mavs.divas.core.msg.PhaseMsg;
import edu.utdallas.mavs.divas.core.msg.RemoveStateMsg;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg;
import edu.utdallas.mavs.divas.core.msg.StateUpdateMsg;
import edu.utdallas.mavs.divas.core.sim.Phase;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.env.ExternalStimulus.ExternalCommand;
import edu.utdallas.mavs.divas.core.spec.agent.AgentLoader;
import edu.utdallas.mavs.divas.core.spec.env.EnvLoader;
import edu.utdallas.mavs.divas.core.spec.env.EnvSpec;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * The <code>AbstractEnvironment</code> is an abstract class for the environment which oversees the
 * execution of locally-run cell controllers, provides a shared communication module, and contains
 * an Environment Cell Map which is shared by the locally-run cell controllers.
 * The cell map contains an updated view of the cell structure of the entire decentralized virtual environment.
 * 
 * @param <CC>
 *        the CellControlle's type of this environment
 */
public abstract class AbstractEnvironment<CC extends CellController> implements Environment<CC>, Serializable
{
    private static final long                       serialVersionUID = 1L;

    /**
     * A Logger object to log messages.
     */
    private final static Logger                     logger           = LoggerFactory.getLogger(AbstractEnvironment.class);

    /**
     * A multithreader to provide a standard mechanism for developer to handle concurrent programming.
     */
    protected transient Multithreader               multithreader;

    /**
     * A list of agent stimulus. This represents the agents stimulus generated after executing the agent phase. Each
     * stimulus represents agent intention to do something.
     */
    protected transient CommunicationModule         comModule;

    /**
     * The <code>MTSClient</code> object which represents a client connection to a JMS broker server
     */
    protected transient MTSClient                   client;

    /**
     * The cell map object contains an updated view of the cell structure
     * of the entire decentralized virtual environment.
     */
    protected CellMap                               cellMap;

    /**
     * A <code>Map</code> for the <code>CellID</code> along with <code>CellController</code>.
     */
    protected Map<CellID, CC>                       cells;

    /**
     * The current simulation cycle number.
     */
    protected transient long                        cycles;

    /**
     * The current simulation phase.
     */
    protected transient Phase                       phase;

    /**
     * A dummy object used to enforce a synchronization process.
     */
    protected static Object                         restructureLock  = new Object();

    /**
     * The cell subscription service.
     */
    protected transient CellSubscriptionService<CC> css;

    /**
     * Agent loader
     */
    protected AgentLoader                           agentLoader;

    /**
     * Environment loader
     */
    protected EnvLoader                             environmentLoader;

    /**
     * Constructs the abstract environment given the <code>MTSClient</code>.
     * 
     * @param client
     *        <code>MTSClient</code> object.
     */
    public AbstractEnvironment(MTSClient client)
    {
        this.client = client;
        start();
    }

    /**
     * Creates a cell controller for this type of environment given a <code>CellState</code> and a
     * communication module.
     * 
     * @param cellState
     *        The cell controller state.
     * @param commModule
     *        The communication module object.
     * @return Cell controller created with given cell state.
     */
    protected abstract CC createCellController(CellState cellState, CommunicationModule commModule);

    /**
     * Creates an agent loader for the environment
     * 
     * @return the new agent loader
     */
    protected abstract AgentLoader createAgentLoader();

    /**
     * Initializes the environment services by creating the communication module, the multithreader,
     * the cell subscription services, initializing the maps and lists, reset the simulation cycle to -1
     * and setup messages publications and subscriptions.
     */
    public void start()
    {
        comModule = new CommunicationModule(client);
        multithreader = createMultithreader();
        agentLoader = createAgentLoader();
        environmentLoader = createEnvLoader();
        css = new CellSubscriptionService<CC>();
        cells = Collections.synchronizedMap(new HashMap<CellID, CC>());
        // initialize time to -1, meaning the EnvironmentManager hasn't received a tick yet
        cycles = -1;
        try
        {
            setupSubscriptions();
            setupPublications();
        }
        catch(MTSException e)
        {
            logger.error(e.toString());
        }
    }

    /**
     * Setups the publications to message topics.
     * 
     * @throws MTSException
     *         Signals that an MTS exception of some sort has occurred. This class of exceptions
     *         produced by failed or interrupted messaging operations.
     */
    protected void setupPublications() throws MTSException
    {
        comModule.addPublicationTopic(DivasTopic.envTopic);
        comModule.addPublicationTopic(DivasTopic.assignIDTopic);
        comModule.addPublicationTopic(DivasTopic.phaseCompletionTopic);
        comModule.addPublicationTopic(DivasTopic.destroyEntityTopic);
    }

    /**
     * Setups the subscriptions to message topics.
     * 
     * @throws MTSException
     *         Signals that an MTS exception of some sort has occurred. This class of exceptions
     *         produced by failed or interrupted messaging operations.
     */
    protected void setupSubscriptions() throws MTSException
    {
        comModule.addSubscriptionTopic(DivasTopic.externalStimulusTopic, externalStimuliMsgHandler());
        comModule.addSubscriptionTopic(DivasTopic.createEntityTopic, createEntityMsgHandler());
        comModule.addSubscriptionTopic(DivasTopic.runtimeAgentCommandTopic, userCommandHandler());
    }

    /**
     * Creates the multithreader used in concurrent programming in the environment.
     * 
     * @return The <code>Multithreader</code> created.
     */
    protected Multithreader createMultithreader()
    {
        return new Multithreader("EnvironmentThrd", ThreadPoolType.FIXED, 5, false);
    }

    @Override
    public void terminate()
    {
        destroyAllCellControllers();
        multithreader.terminate();
        comModule.forceDisconnect();
    }

    /**
     * Creates an environment loader
     * 
     * @return the new environment loader
     */
    protected EnvLoader createEnvLoader()
    {
        return new EnvLoader();
    }

    @Override
    public void loadEnvironment(File file)
    {
        EnvSpec envSpec = (file == null) ? new EnvSpec(environmentLoader) : new EnvSpec(file.getParent(), file.getName(), environmentLoader);

        try
        {
            loadEnvironment(envSpec.loadFromXMLFile());
        }
        catch(Exception e1)
        {
            try
            {
                environmentLoader.loadDefaultEnvironments();
                loadEnvironment(envSpec.loadFromXMLFile());
            }
            catch(Exception ex)
            {
                logger.error("An error has occurred while loading environment specification file");
            }
        }
    }

    /**
     * Loading a previously saved simulation environment given the cell state.
     * 
     * @param envSpec
     *        Specification for the environment being loaded.
     */
    protected void loadEnvironment(EnvSpec envSpec)
    {
        CellState rootCellState = new CellState(CellID.rootID(), envSpec.getCellState().getBounds());
        reset();
        setCellMap(new CellMap(rootCellState, Host.getHost().getHostID()));
        CC root = createCellController(rootCellState, comModule);
        cells.put(root.getCellID(), root);

        // Load Environment Objects
        for(EnvObjectState state : envSpec.getCellState().getEnvObjects())
        {
            sendMessage(new MTSPayload(-1, new AssignStateIDMsg(state)), DivasTopic.assignIDTopic);
        }
    }

    @Override
    public void saveEnvironment(String fileName)
    {
        environmentLoader.saveEnvironment(fileName);
    }

    @Override
    public <E extends Environment<?>> void copyFrom(final E environment)
    {
        setCellMap(environment.getCellMap());
        setCellControllers(environment.getCellControllers());

        if(this instanceof SelfOrganizingEnvironment)
        {
            ((SelfOrganizingEnvironment<?>) this).setStrategy(((SelfOrganizingEnvironment<?>) environment).getStrategy());
        }
    }

    @SuppressWarnings("unchecked")
    private void setCellControllers(List<?> cellControllers)
    {
        reset();
        // destroyAllCellControllers();

        CC cellController = null;

        for(Object cell : cellControllers)
        {
            cellController = copyFrom((CC) cell);
            cells.put(cellController.getCellID(), cellController);
        }
    }

    private CC copyFrom(CC oldCellController)
    {
        CC cellController = createCellController(oldCellController.getCellState(), comModule);
        cellController.setAgents(oldCellController.getAgents());
        return cellController;
    }

    /**
     * Publishes a message with the given payload and topic to the topic subscribers.
     * 
     * @param payload
     *        The message payload to be sent to subscribers of the topic.
     * @param topic
     *        The name of the topic on which the message will be sent.
     */
    protected void sendMessage(MTSPayload payload, String topic)
    {
        try
        {
            comModule.publishMessage(payload, topic);
        }
        catch(MTSException e)
        {
            logger.error("Environment could not send message {} at cycle {}.", new Object[] { topic, Host.getHost().getCycles() }, e);
        }
    }

    /**
     * eliminates all cell controllers, removing any entities and agents from the simulation
     * (for restarting or terminating a simulation)
     */
    private void destroyAllCellControllers()
    {
        synchronized(cells)
        {
            for(CellController cc : cells.values())
            {
                cc.destroy();
            }
            cells.clear();
        }
    }

    @Override
    public void executePhase(long cycles, Phase phase)
    {
        this.cycles = cycles;
        this.phase = phase;

        switch(phase)
        {
        case ENVIRONMENT:
            executeEnvironmentPhase();
            notifyPhaseCompletion(phase);
            break;
        case AGENT:
            executeAgentPhase();
            notifyPhaseCompletion(phase);
        default:
            break;
        }
    }

    /**
     * Processes the environment reorganization requests which could be a split or merge requests.
     */
    protected void processReorganizationQueue()
    {
        // To be overridden by children
    }

    private void notifyPhaseCompletion(Phase phase)
    {
        sendMessage(new MTSPayload(0, new PhaseMsg(phase)), DivasTopic.phaseCompletionTopic);
    }

    /**
     * In this phase:
     * CCs Combine arriving stimuli from neighbors
     * CCs Respond to arriving stimuli
     * CCs Calculate next state of the environment
     * CCs send updated state (once all requests are addressed)
     * tell each cell to deliberate (in parallel)
     */
    protected void executeEnvironmentPhase()
    {
        // process the reorganization queue before reacting
        processReorganizationQueue();

        // react and deliberate
        List<Runnable> tasks = new ArrayList<Runnable>();

        for(final CellController cell : cells.values())
        {
            if(cellMap.isLeaf(cell.getCellID()))
            {
                tasks.add(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        logger.debug("CC {} Env Objects: {}", cell.toString(), cell.getCellState().getEnvObjects());
                        List<AgentStateModel> agentStates = cell.react();
                        cell.deliberate(agentStates);
                    }
                });
            }
        }

        multithreader.executeAndWait(tasks);

        // after deliberation, all agent transfers have already been processed, so we can proceed to cell
        // subscription without worrying about boundary conditions
        css.processAgentSubscriptions(cells.values(), cellMap);
    }

    /**
     * Executes the agent phase of the simulation.
     * <p>
     * Each cell controller in the environment is responsible for executing the agent phase for the agent it contains.
     */
    protected void executeAgentPhase()
    {
        List<Runnable> tasks = new ArrayList<Runnable>();

        tasks.add(new Runnable()
        {
            @Override
            public void run()
            {
                for(final CellController cell : cells.values())
                {
                    if(cellMap.isLeaf(cell.getCellID()))
                    {
                        cell.executeAgents();
                    }
                }
            }
        });

        multithreader.executeAndWait(tasks);
    }

    /**
     * Creates a new agent in the simulation
     * 
     * @param state
     *        the agent state
     */
    public void createAgent(AgentState state)
    {
        AgentState agent = agentLoader.createAgent(state.getModelName());
        agent.setPosition(state.getPosition());
        createVirtualState(agent);
    }

    /**
     * Creates a new environment object in the simulation
     * 
     * @param state
     *        the environment object state
     */
    public void createEnvObject(EnvObjectState state)
    {
        createVirtualState(state);
    }

    protected void createVirtualState(VirtualState state)
    {
        CC cell = getCellController(getCellMap().findCell(state.getPosition()));

        // Check if environment object can be added before assigning a new ID
        if(cell != null && !cell.hasConflicts(state))
        {
            sendMessage(new MTSPayload(-1, new AssignStateIDMsg(state)), DivasTopic.assignIDTopic);
        }
    }

    /**
     * Triggers an event in the simulation
     * 
     * @param envEvent
     *        The environment event.
     */
    protected void triggerEvent(EnvEvent envEvent)
    {
        sendMessage(new MTSPayload(-1, new AssignEventIDMsg(envEvent)), DivasTopic.assignIDTopic);
    }

    /**
     * Updates the state of an agent in the simulation
     * 
     * @param state
     *        the new state
     */
    public void updateAgent(AgentState state)
    {
        CC cc = null;
        for(CC cell : cells.values())
        {
            if(cell.containsAgent(state.getID()))
            {
                cc = cell;
            }
        }

        if(cc != null)
        {
            cc.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.MODIFY));
        }
    }

    /**
     * Updates the state of an environment object in the simulation
     * 
     * @param state
     *        the new state
     */
    public void updateEnvObject(EnvObjectState state)
    {
        List<CC> intersectingCells = new ArrayList<CC>();
        List<CC> cellsForRemoval = new ArrayList<CC>();

        for(CC cell : cells.values())
        {
            if(cellMap.isLeaf(cell.getCellID()))
            {
                if(cell.getCellState().getBounds().intersects(state.getBoundingArea()))
                {
                    intersectingCells.add(cell);
                }
                else if(cell.containsEnvObject(state.getID()))
                {
                    cellsForRemoval.add(cell);
                }
            }
        }

        boolean canUpdate = true;
        canUpdate = resolveConflicts(state, intersectingCells, canUpdate);
        if(canUpdate)
        {
            for(CC cell : cellsForRemoval)
            {
                cell.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.REMOVE));
            }
            for(CC cell : intersectingCells)
            {
                if(cell.containsEnvObject(state.getID()))
                {
                    cell.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.MODIFY));
                }
                else
                {
                    cell.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.ADD));
                }
            }
        }
    }

    /**
     * Removes an agent from the simulation
     * 
     * @param state
     *        the agent to be removed
     */
    public void removeAgent(AgentState state)
    {
        CC cc = null;

        for(CC cell : cells.values())
            if(cell.containsAgent(state.getID()))
                cc = cell;

        if(cc != null)
            cc.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.DESTROY));
    }

    /**
     * Removes an environment object from the simulation
     * 
     * @param state
     *        the environment object to be removed
     */
    public void removeEnvObject(EnvObjectState state)
    {
        CC cc = null;

        for(CC cell : cells.values())
            if(cell.containsEnvObject(state.getID()))
                cc = cell;

        if(cc != null)
            cc.addExternalStimulus(new ExternalStimulus(state, ExternalCommand.DESTROY));
    }

    protected boolean resolveConflicts(EnvObjectState state, List<CC> intersectingCells, boolean canUpdate)
    {
        for(CC cell : intersectingCells)
        {
            if(cell.hasConflicts(state))
            {
                canUpdate = false;
                break;
            }
        }
        return canUpdate;
    }

    /**
     * Resets the environment by destroying all the cell controllers, clearing the agents for each cell
     * and reseting the cell subscription service.
     */
    public void reset()
    {
        clearAgents();
        destroyAllCellControllers();
        css.reset();
    }

    private void clearAgents()
    {
        synchronized(cells)
        {
            for(CellController cell : cells.values())
            {
                cell.clearAgents();
            }
        }
    }

    @Override
    public CellMap getCellMap()
    {
        return cellMap;
    }

    /**
     * Sets the given <code>CellMap</code> to the environment.
     * 
     * @param map
     *        The <code>CellMap</code> to be set.
     */
    protected void setCellMap(CellMap map)
    {
        cellMap = map;
    }

    @Override
    public CC getCellController(CellID cellID)
    {
        return cells.get(cellID);
    }

    @Override
    public CC getCellController(Vector3f position)
    {
        for(CC cell : cells.values())
        {
            if(cellMap.isLeaf(cell.getCellID()) && cell.getCellState().getBounds().contains(position))
            {
                return cell;
            }
        }
        return null;
    }

    @Override
    public List<CC> getCellControllers()
    {
        return new ArrayList<CC>(cells.values());
    }

    /**
     * @return A <code>List</code> of agent states for all agents in the environment.
     */
    public List<AgentState> getAgentStates()
    {
        List<AgentState> states = new ArrayList<AgentState>();

        for(CellController cell : cells.values())
        {
            states.addAll(cell.getCellState().getAgentStates());
        }

        return states;
    }

    @Override
    public boolean hasCombinedPerceptionDataFor(int agentId)
    {
        return css.hasCombinedCellState(agentId);
    }

    @Override
    public CellState retrievePerceptionDataFor(int agentId)
    {
        return css.retrieveCombinedCellStateForAgent(agentId);
    }

    private Subscriber createEntityMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                Object data = payload.getData();
                handleEntityCreation(data);
            }
        };
    }

    /**
     * Handles entity creation.
     * 
     * @param entityCreationMessage
     *        the entity creation message
     */
    protected void handleEntityCreation(Object entityCreationMessage)
    {
        // do not add external stimuli while the environment is restructuring
        synchronized(restructureLock)
        {
            logger.debug("Create entity message received.");

            if(entityCreationMessage instanceof CreateStateMsg)
            {
                CreateStateMsg msg = (CreateStateMsg) entityCreationMessage;

                if((msg.getState() instanceof EnvObjectState))
                {
                    for(CC c : cells.values())
                    {
                        if((cellMap.isLeaf(c.getCellID())) && (c.getCellState().getBounds().intersects(((EnvObjectState) msg.getState()).getBoundingArea())))
                        {
                            c.addExternalStimulus(new ExternalStimulus(msg.getState(), ExternalCommand.CREATE));
                            logger.debug("Added create environmnet object stimulus for {}[{}] in cell {}.", new Object[] { msg.getState().getModelName(), msg.getState().getID(), c.getCellID() });
                        }
                    }
                }
                else
                {
                    CC cell = getCellController(getCellMap().findCell(msg.getState().getPosition()));
                    cell.addExternalStimulus(new ExternalStimulus(msg.getState(), ExternalCommand.CREATE));
                    logger.debug("Added create agent external stimulus for {}[{}] in cell {}.", new Object[] { msg.getState().getModelName(), msg.getState().getID(), cell.getCellID() });
                }
            }
            else if(entityCreationMessage instanceof CreateEventMsg)
            {
                CreateEventMsg msg = (CreateEventMsg) entityCreationMessage;

                CC cell = getCellController(getCellMap().findCell(msg.getEnvEvent().getOrigin()));
                if(cell != null)
                {
                    cell.addExternalStimulus(new ExternalStimulus(msg.getEnvEvent()));
                    logger.debug("Added create event external stimulus for {}[{}] in cell {}.", new Object[] { msg.getEnvEvent().getClass().getName(), msg.getEnvEvent().getID(), cell.getCellID() });
                }
            }
        }
    }

    private Subscriber externalStimuliMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                Object data = payload.getData();
                handleExternalSimuli(data);
            }
        };
    }

    /**
     * Handles received external stimuli.
     * 
     * @param stimuliMesssage
     *        the stimuli message received
     */
    protected void handleExternalSimuli(Object stimuliMesssage)
    {
        if(stimuliMesssage instanceof AssignStateIDMsg)
        {
            VirtualState state = ((AssignStateIDMsg) stimuliMesssage).getState();

            if(state instanceof AgentState)
            {
                logger.debug("Received agent creation external stimulus for agent in position {}.", state.getPosition());
                System.out.println("creating agent");
                createAgent((AgentState) state);
            }
            else
            {
                logger.debug("Received environment object creation external stimulus for object in position {}.", state.getPosition());
                createEnvObject((EnvObjectState) state);
            }
        }
        else if(stimuliMesssage instanceof AssignEventIDMsg)
        {
            EnvEvent envEvent = ((AssignEventIDMsg) stimuliMesssage).getEnvEvent();
            triggerEvent(envEvent);
        }
        else if(stimuliMesssage instanceof StateUpdateMsg)
        {
            VirtualState state = ((StateUpdateMsg) stimuliMesssage).getState();
            if(state instanceof AgentState)
            {
                logger.debug("Handling Env Object update Stimulus for {}", state.getID());
                updateAgent((AgentState) state);
            }
            else if(state instanceof EnvObjectState)
            {
                logger.debug("Handling Env Object update Stimulus for {}", state.getID());
                updateEnvObject((EnvObjectState) state);
            }

        }
        else if(stimuliMesssage instanceof RemoveStateMsg)
        {
            VirtualState state = ((RemoveStateMsg) stimuliMesssage).getState();

            synchronized(cells)
            {
                if(state instanceof AgentState)
                {
                    logger.debug("Handling Agent Removal Stimulus for {}", state.getID());
                    removeAgent((AgentState) state);
                }
                else if(state instanceof EnvObjectState)
                {
                    logger.debug("Handling Env Object Removal Stimulus for {}", state.getID());
                    removeEnvObject((EnvObjectState) state);
                }
            }
        }
    }

    private Subscriber userCommandHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof RuntimeAgentCommandMsg)
                {
                    RuntimeAgentCommandMsg cmd = (RuntimeAgentCommandMsg) payload.getData();

                    synchronized(cells)
                    {
                        for(CC cell : cells.values())
                            if(cell.containsAgent(cmd.getAgentID()))
                            {
                                cell.addUserCommand(cmd);
                            }
                    }
                }
            }
        };
    }
}
