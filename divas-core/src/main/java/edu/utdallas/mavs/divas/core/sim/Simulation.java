package edu.utdallas.mavs.divas.core.sim;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.client.dto.SimDto;
import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.host.Heartbeat;
import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.core.host.SimLoader;
import edu.utdallas.mavs.divas.core.host.Status;
import edu.utdallas.mavs.divas.core.msg.HeartbeatCommandMsg;
import edu.utdallas.mavs.divas.core.msg.SimControlMsg;
import edu.utdallas.mavs.divas.core.msg.SimLoaderMsg;
import edu.utdallas.mavs.divas.core.msg.TickMsg;
import edu.utdallas.mavs.divas.core.sim.env.Environment;
import edu.utdallas.mavs.divas.core.sim.env.SelfOrganizingEnvironment;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * This class describes the abstract DIVAs simulation.
 * <p>
 * This is a Multi-agent Based Simulator, implementing the APR (Action Potential Result) model. It is based on a decentralized environment and situated agents that realistically perceive and stimulate
 * their environment and act independently in response to their perception.
 * <p>
 * Concrete simulations should extend this class.
 * 
 * @param <E>
 *        the simulation environment type
 */
public abstract class Simulation<E extends Environment<?>> implements Serializable
{
    private static final long                           serialVersionUID = 1L;

    private static final Logger                         logger           = LoggerFactory.getLogger(Simulation.class);

    /**
     * The MTS client
     */
    protected transient MTSClient                       client;

    /**
     * The communication module
     */
    protected transient CommunicationModule             comModule;

    /**
     * The simulation situated environment
     */
    protected E                                         environment;

    /**
     * The current simulation cycle number
     */
    protected transient long                            cycles           = -1;

    /**
     * The current elapsed time of the simulation
     */
    protected transient long                            simTime          = -1;

    /**
     * The current simulation cycle period
     */
    protected transient int                             period           = -1;

    /**
     * The simulation status
     */
    protected transient Status                          status;

    /**
     * The current phase of the simulation
     */
    protected transient Phase                           phase            = Phase.INIT;

    /**
     * Buffer of requests for simulation snapshots
     */
    protected transient ConcurrentLinkedQueue<Runnable> simSnapshotRequestBuffer;

    /**
     * Thread executor
     */
    protected transient Multithreader                   multithreader;

    /**
     * Creates the simulation situated environment
     * 
     * @param client
     *        the MTS client
     */
    protected abstract void createEnvironment(MTSClient client);

    /**
     * Creates a new instance of {@link Simulation}
     * 
     * @param client
     *        the MTS client
     */
    public Simulation(MTSClient client)
    {
        this.client = client;
        this.setStatus(Status.STOPPED);
        this.simSnapshotRequestBuffer = new ConcurrentLinkedQueue<Runnable>();
        this.multithreader = new Multithreader("SimThrd", ThreadPoolType.FIXED, 5, false);

        this.comModule = new CommunicationModule(client);

        try
        {
            setupSubscriptions();
            setupPublications();
        }
        catch(MTSException e)
        {
            logger.error("An error has occured during subscriptions/publications setup for the simulation", e);
        }

        createEnvironment(client);

        sendMessage(new MTSPayload(-1, HeartbeatCommandMsg.getResetCommand()), DivasTopic.timeControlTopic);

        logger.info("Simulator initialized for simulation @ {}:{}", client.getHostName(), client.getPort());
    }

    /**
     * Setups MTS publications for the {@link Simulation}
     * 
     * @throws MTSException
     */
    protected void setupPublications() throws MTSException
    {
        comModule.addPublicationTopic(DivasTopic.timeControlTopic);
        comModule.addPublicationTopic(DivasTopic.assignIDTopic);
        comModule.addPublicationTopic(DivasTopic.simSummaryTopic);
    }

    /**
     * Setups MTS subscriptions for the {@link Simulation}
     * 
     * @throws MTSException
     */
    protected void setupSubscriptions() throws MTSException
    {
        comModule.addSubscriptionTopic(DivasTopic.heartbeatTopic, heartbeatHandler());
        comModule.addSubscriptionTopic(DivasTopic.simulationPropertiesTopic, configHandler());
        comModule.addSubscriptionTopic(DivasTopic.simulationControlTopic, simControlHandler());
        comModule.addSubscriptionTopic(DivasTopic.simSnapshotTopic, simSnapshotHandler());
    }

    /**
     * Loads the simulation environment from file.
     * 
     * @param file
     *        The file that contains the saved environment
     * @param isXML
     *        A boolean flag to indicate that we are loading the environment from an XML file not a serialized envSpec object
     */
    public void loadEnvironment(File file)
    {
        environment.loadEnvironment(file);
    }

    /**
     * Saves the simulation environment to the given name.
     * 
     * @param name
     *        the name of the file to save the environment to.
     */
    public void saveEnvironment(String name)
    {
        environment.saveEnvironment(name);
    }

    /**
     * Runs the simulation by starting the heartbeat
     */
    public void start()
    {
        try
        {
            // tell the time keeper to start the heartbeat
            comModule.publishMessage(new MTSPayload(-1, HeartbeatCommandMsg.getRunCommand()), DivasTopic.timeControlTopic);
            setStatus(Status.RUNNING);

            logger.info("Simulation running.");
        }
        catch(MTSException e)
        {
            logger.error("An error has occured while starting the simulation at cycle number {}", cycles, e);
        }
    }

    /**
     * Pauses the simulation by pausing the heartbeat
     */
    public void pause()
    {
        try
        {
            // tell the time keeper to pause the heartbeat
            comModule.publishMessage(new MTSPayload(-1, HeartbeatCommandMsg.getPauseCommand()), DivasTopic.timeControlTopic);
            setStatus(Status.STOPPED);

            logger.info("Simulation paused.");
        }
        catch(MTSException e)
        {
            logger.error("An error has occured while pausing the simulation at cycle number {}", cycles, e);
        }
    }

    /**
     * Disconnects the simulation communication module
     */
    public void terminate()
    {
        if(status == Status.RUNNING)
        {
            pause();
        }

        environment.terminate();

        // zero the heartbeat's tick count
        // sendMessage(new MTSPayload(-1,
        // HeartbeatCommandMsg.getResetCommand()), DivasTopic.timeControlTopic);
        if(comModule != null)
        {
            try
            {
                comModule.disconnect();
            }
            catch(MTSException e)
            {
                logger.error("An error has occured while disconnecting simulation communication module", cycles, e);
            }
        }
    }

    /**
     * Updates this simulation with properties from the given simulation instance.
     * 
     * @param simulation
     *        The simulation from which values will be retrieved to update this simulation with.
     */
    public void copyFrom(Simulation<?> simulation)
    {
        environment.copyFrom(simulation.getEnvironment());
    }

    /**
     * Updates the status of the {@link Simulation}
     * 
     * @param status
     *        the new status of the simulation
     */
    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * Gets the current simulation status
     * 
     * @return the simulation status
     */
    public Status getStatus()
    {
        return status;
    }

    /**
     * Gets the situated environment of the simulation
     * 
     * @return the simulation environment
     */
    public E getEnvironment()
    {
        return environment;
    }

    /**
     * Ticks the simulation, triggering its execution. It also notifies the simulation summary.
     * 
     * @param tick
     *        the tick message
     */
    public void tick(TickMsg tick)
    {
        this.cycles = tick.getCycles();
        this.simTime = tick.getSimTime();
        this.phase = tick.getPhase();
        this.period = tick.getPeriod();
        if(tick.getPhase().equals(Phase.AGENT))
        {
            sendSimSummary();
            processSimSnapshotRequestBuffer();
        }
        environment.executePhase(cycles, tick.getPhase());

    }

    private void processSimSnapshotRequestBuffer()
    {
        Runnable task = simSnapshotRequestBuffer.poll();

        if(task != null)
        {
            multithreader.executeAndWait(task);
        }
    }

    private void sendSimSummary()
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                sendMessage(createSimSummary(), DivasTopic.simSummaryTopic);
            }
        };
        multithreader.execute(task);
    }

    /**
     * Creates the simulation summary message
     * 
     * @return the simulation summary payload
     */
    protected MTSPayload createSimSummary()
    {
        MTSPayload simSummary;
        if(environment instanceof SelfOrganizingEnvironment)
        {
            simSummary = new MTSPayload(0, new SimDto(period, cycles, simTime, true, ((SelfOrganizingEnvironment<?>) environment).getStrategy()));
        }
        else
        {
            simSummary = new MTSPayload(0, new SimDto(period, cycles, simTime, false));
        }
        return simSummary;
    }

    /**
     * Publishes a message to the message broker
     * 
     * @param payload
     *        the message to be published
     * @param topic
     *        the message topic
     */
    protected void sendMessage(MTSPayload payload, String topic)
    {
        try
        {
            comModule.publishMessage(payload, topic);
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current simulaion phase
     * 
     * @return the simulation phase
     */
    public Phase getPhase()
    {
        return phase;
    }

    /**
     * Gets the current simulation cycle number
     * 
     * @return the simulation cycle number
     */
    public long getCycles()
    {
        return cycles;
    }

    /**
     * Gets the current simulation time
     * 
     * @return the simulation time
     */
    public long getSimTime()
    {
        return simTime;
    }

    /**
     * Gets the current simulation period
     * 
     * @return the simulation period
     */
    public int getPeriod()
    {
        return period;
    }

    private Subscriber heartbeatHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, final MTSPayload payload)
            {
                if(payload.getData() instanceof TickMsg)
                {
                    logger.debug("Receiving tick message from the heartbeat");
                    tick((TickMsg) payload.getData());
                }
            }
        };
    }

    private Subscriber configHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                SimConfig.refresh();
                Heartbeat.refresh();
            }
        };
    }

    private Subscriber simControlHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                SimControlMsg msg = (SimControlMsg) payload.getData();
                switch(msg.getSimControlCommand())
                {
                case START:
                    start();
                    break;
                case PAUSE:
                    pause();
                    break;
                default:
                    break;
                }
            }
        };
    }

    private Subscriber simSnapshotHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                SimLoaderMsg msg = (SimLoaderMsg) payload.getData();

                // stop heartbeat
                Host.getHost().getHeartbeat().pause();

                switch(msg.getSimSnapshotCommand())
                {
                case SAVE_SIMULATION:
                    SimLoader.saveSimulation(msg.getFile());
                    break;
                case LOAD_SIMULATION:
                    SimLoader.loadSimulation(msg.getFile());
                    sendMessage(createSimSummary(), DivasTopic.simSummaryTopic);
                    break;
                case LOAD_ENVIRONMENT_XML:
                    Host.getHost().loadEnvironment(msg.getFile());
                    sendMessage(createSimSummary(), DivasTopic.simSummaryTopic);
                    break;
                case SAVE_ENVIRONMENT:
                    saveEnvironment(msg.getFile().getPath());
                    break;
                default:
                    break;
                }
            }
        };
    }

    @Override
    public String toString()
    {
        if(comModule != null)
            return "Simulation[" + comModule.getHostName() + ":" + comModule.getPort() + "]";
        return "Simulation[disconnected]";
    }
}
