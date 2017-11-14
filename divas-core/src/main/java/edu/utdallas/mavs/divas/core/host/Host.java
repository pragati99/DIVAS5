package edu.utdallas.mavs.divas.core.host;

import java.io.File;

import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.config.Config;
import edu.utdallas.mavs.divas.core.sim.Phase;
import edu.utdallas.mavs.divas.core.sim.Simulation;
import edu.utdallas.mavs.divas.core.sim.env.Environment;
import edu.utdallas.mavs.divas.mts.MTSBroker;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * This class describes the DIVAs host.
 * <p>
 * This is the top-level component in DIVAs. It is a virtual platform which instantiates and hosts DIVAs simulation components on a given machine.
 */
public abstract class Host
{
    private static final Logger    logger = LoggerFactory.getLogger(Host.class);

    /**
     * The singleton instance of the Host
     */
    protected static Host          instance;

    /**
     * The id of the Host
     */
    protected String               hostID;

    /**
     * The thread executor instance of the Host
     */
    protected static Multithreader multithreader;

    /**
     * The configuration manager of the Host
     */
    protected HostConfigManager    configManager;

    /**
     * The local MTS broker of the Host
     */
    protected MTSBroker            localBroker;

    /**
     * The MTS client of the Host
     */
    protected MTSClient            client;

    /**
     * The simulation time keeper
     */
    protected Heartbeat            heartbeat;

    /**
     * The simulation id manager
     */
    protected IdManager            idManager;

    /**
     * The simulation component
     */
    protected Simulation<?>        simulation;

    /**
     * Creates and initializes the simulation
     * 
     * @param client
     *        the MTS client
     */
    protected abstract void createSimulation(MTSClient client);

    /**
     * Creates a new instance of DIVAs host
     * 
     * @param simConfig
     *        the simulation configuration settings
     */
    protected Host()
    {
        logger.debug("Creating Host instance");
        hostID = "name";
        multithreader = new Multithreader("HostThrd", ThreadPoolType.FIXED);
        instance = this;
    }

    /**
     * Gets the singleton instance of the {@link Host} component
     * 
     * @return the singleton instance of HOST
     */
    public static Host getHost()
    {
        return instance;
    }

    /**
     * Starts the {@link Host} local MTS broker
     */
    public void start()
    {
        createLocalBrokerAndWait(Config.getDefaultHostPort());
        createClientAndWait(localBroker.getBrokerHost(), localBroker.getBrokerPort());
    }

    /**
     * Destroys the MTS client and local MTS broker
     */
    protected void disconnect()
    {
        destroyClientAndWait();
        destroyLocalBrokerAndWait();
    }

    private void createLocalBrokerAndWait(final String port)
    {
        multithreader.executeAndWait(createLocalBrokerTask(port));
    }

    private Runnable createLocalBrokerTask(final String port)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Creating local broker.");
                createLocalBroker(port);
            }
        };
    }

    /**
     * Creates the {@link Host} local MTS broker
     * 
     * @param port
     *        the port nunber to bind the local borker service
     */
    protected void createLocalBroker(final String port)
    {
        localBroker = new MTSBroker(port);
        try
        {
            localBroker.start();
        }
        catch(Exception e)
        {
            logger.warn("Terminating Host");
            System.exit(0);
        }
    }

    private void destroyLocalBrokerAndWait()
    {
        multithreader.executeAndWait(destroyLocalBrokerTask());
    }

    private Runnable destroyLocalBrokerTask()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Destroying local broker.");
                destroyLocalBroker();
            }
        };
    }

    /**
     * Destroys the {@link Host} local MTS broker
     */
    protected void destroyLocalBroker()
    {
        if(localBroker != null)
        {
            localBroker.terminate();
            localBroker = null;
        }
    }

    private void createClientAndWait(final String hostname, final String port)
    {
        multithreader.executeAndWait(createClientTask(hostname, port));
    }

    private Runnable createClientTask(final String hostname, final String port)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Creating client.");
                createClient(hostname, port);
            }
        };
    }

    /**
     * Creates the MTS client component and initialize components depending on
     * it
     * 
     * @param hostname
     *        the host id
     * @param port
     *        the broker listener port
     */
    protected void createClient(final String hostname, final String port)
    {
        client = new MTSClient(hostname, port);

        client.startQuietly();

        configManager = new HostConfigManager(client);

        heartbeat = new Heartbeat(client);
        idManager = new IdManager(client);

        createSimulationAndWait();

        loadEnvironment(null);
    }

    private void destroyClientAndWait()
    {
        multithreader.executeAndWait(destroyClientTask());
    }

    private Runnable destroyClientTask()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Destroying client.");
                destroyClient();
            }
        };
    }

    /**
     * Destroys the MTS client of the simulation and terminates the components
     * depending on it
     */
    protected void destroyClient()
    {
        if(client != null)
        {
            destroySimulationAndWait();

            heartbeat.terminate();

            idManager.terminate();

            if(configManager != null)
            {
                configManager.sendClearAllMsg();
                configManager.terminate();
            }

            configManager = null;

            if(client != null)
            {
                try
                {
                    client.terminate();
                    client = null;
                }
                catch(MTSException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createSimulationAndWait()
    {
        multithreader.executeAndWait(createSimulationTask());
    }

    private Runnable createSimulationTask()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Creating simulation.");
                createSimulation(client);
            }
        };
    }

    /**
     * Loads the simulation environment
     * 
     * @param file
     *        the environment specification
     */
    public void loadEnvironment(File file)
    {
        if(simulation == null)
            createSimulationAndWait();

        logger.info("Loading environment.");

        // Reset the idManger
        idManager.reset();

        heartbeat.reset();

        simulation.loadEnvironment(file);

        // notify visualizer that the simulation is loading/restarting
        configManager.sendClearAllMsg();
    }

    /**
     * Loads an existing simulation from a simulation snapshot file.
     * 
     * @param <E>
     *        the simulation environment type.
     * @param simSnapshot
     *        the simulation snapshot file.
     */
    public <E extends Environment<?>> void loadSimulation(SimSnapshot<E> simSnapshot)
    {
        heartbeat.copyFrom(simSnapshot.getHeartbeat());
        idManager.copyFrom(simSnapshot.getIdManager());
        simulation.copyFrom(simSnapshot.getSimulation());

        // notify visualizer that the simulation is loading/restarting
        configManager.sendClearAllMsg();
    }

    private void destroySimulationAndWait()
    {
        multithreader.executeAndWait(destroySimulationTask());
    }

    private Runnable destroySimulationTask()
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                logger.info("Destroying simulation.");
                destroySimulation();
            }
        };
    }

    /**
     * Destroys the simulation
     */
    protected void destroySimulation()
    {
        // notifies that the simulation is loading/restarting
        configManager.sendClearAllMsg();

        if(simulation != null)
            simulation.terminate();

        simulation = null;
    }

    /**
     * Gets current simulation {@link Phase}
     * 
     * @return the simulation phase
     */
    public Phase getPhase()
    {
        return simulation.getPhase();
    }

    /**
     * Gets this {@link Host} local MTS broker
     * 
     * @return the local MTS broker
     */
    public MTSBroker getLocalBroker()
    {
        return localBroker;
    }

    /**
     * Gets this {@link Host} MTS client
     * 
     * @return the MTS client
     */
    public MTSClient getClient()
    {
        return client;
    }

    /**
     * Gets the instance of {@link Simulation} of this {@link Host}
     * 
     * @return the simulation instance
     */
    public Simulation<?> getSimulation()
    {
        return simulation;
    }

    /**
     * Gets the {@link ConfigurationManager} of this {@link Host}
     * 
     * @return the instance of configuration manager
     */
    public HostConfigManager getConfigManager()
    {
        return configManager;
    }

    /**
     * Gets the id of the {@link Host}
     * 
     * @return the Host id
     */
    public String getHostID()
    {
        return hostID;
    }

    /**
     * Gets the {@link IdManager} of this Host
     * 
     * @return the id manager
     */
    public IdManager getIdManager()
    {
        return idManager;
    }

    /**
     * Gets the current simulation cycle period
     * 
     * @return the simulation cycle period in ms
     */
    public int getPeriod()
    {
        return heartbeat.getPeriod();
    }

    /**
     * Gets the current simulation cycle number
     * 
     * @return the simulation cycle number
     */
    public long getCycles()
    {
        return simulation.getCycles();
    }

    /**
     * Gets the heartbeat of the simulation
     * 
     * @return the simulation heartbeat
     */
    public Heartbeat getHeartbeat()
    {
        return heartbeat;
    }

    /**
     * Gets the simulation environment
     * 
     * @return the simulation environment
     */
    public Environment<?> getEnvironment()
    {
        return simulation.getEnvironment();
    }

    /**
     * Terminates and tear down simulation host resources
     */
    public void terminate()
    {
        disconnect();

        multithreader.terminate();

        System.exit(0);
    }
}
