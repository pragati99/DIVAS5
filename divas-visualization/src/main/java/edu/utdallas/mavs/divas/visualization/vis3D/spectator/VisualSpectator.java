package edu.utdallas.mavs.divas.visualization.vis3D.spectator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.dto.CellStateDto;
import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.core.msg.RemoveEventMsg;
import edu.utdallas.mavs.divas.core.msg.RemoveStateMsg;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * This class handles the messages received from the simulation host
 * <p>
 * This class setups the subscriptions to message topics, and defines how each message topic is handled. It's responsible for updating the PlayGround information by extracting information from messages received from the simulation and dump it in the
 * PlayGround.
 */
public class VisualSpectator
{
    private final static Logger  logger          = LoggerFactory.getLogger(VisualSpectator.class);

    private SimAdapter           simClientAdapter;

    private static PlayGround    playground      = null;

    private static Multithreader multithreader;

    private static long          cycles;

    private static int           period;

    private static Object        playgroundMutex = new Object();

    /**
     * Constructs the VisualSpectator by injecting the SimAdapter and PlayGround
     * dependencies
     * 
     * @param simClientAdapter
     *        The SimAdapter which communicate with the simulation core
     * @param playGround
     *        The place to store the current status of the simulation
     */
    @Inject
    public VisualSpectator(SimAdapter simClientAdapter, PlayGround playGround)
    {
        this.simClientAdapter = simClientAdapter;
        VisualSpectator.playground = playGround;
        multithreader = new Multithreader("VisualSpectatorThrd", ThreadPoolType.FIXED);
    }

    /**
     * Gets the current simulation cycle number
     * 
     * @return The cycle number of the last received message
     */
    public static long getCycles()
    {
        return cycles;
    }

    /**
     * Gets the current simulation cycle period
     * 
     * @return The cycle period of the last received message
     */
    public static int getPeriod()
    {
        return period;
    }

    /**
     * Gets the current number of objects (agents and environment objects) in the simulation
     * 
     * @return the number of objects in the last received message
     */
    public static int getNumberOfObjects()
    {
        return playground.getEntitiesCount();
    }

    /**
     * Gets the current playground
     * 
     * @return PlayGround which represent the current status of the simulation
     */
    public static PlayGround getPlayGround()
    {
        return playground;
    }

    /**
     * Setups the subscription to message topics
     */
    public void start()
    {
        simClientAdapter.addSubscription(DivasTopic.envTopic, cellUpdateMsgHandler());
        simClientAdapter.addSubscription(DivasTopic.destroyEntityTopic, destroyEntityMsgHandler());
        simClientAdapter.addSubscription(DivasTopic.hostConfigTopic, simEndingMsgHandler());
        simClientAdapter.addSubscription(DivasTopic.simulationPropertiesTopic, simPropertiesUpdateMsgHandler());
    }

    private Subscriber cellUpdateMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof CellStateDto)
                {
                    final CellStateDto cell = (CellStateDto) payload.getData();
                    if(cycles < cell.getCycleNumber())
                        cycles = cell.getCycleNumber();
                    period = cell.getPeriod();

                    Runnable task = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            playground.updateCellBounds(cell.getCellMap(), cell.getCycleNumber());
                            for(AgentState agent : cell.getAgentStates())
                            {
                                playground.updateAgent(agent, cell.getCycleNumber());
                            }
                            for(EnvObjectState obj : cell.getEnvObjects())
                            {
                                playground.updateEnvObject(obj, cell.getCycleNumber());
                            }
                            for(EnvEvent event : cell.getEvents())
                            {
                                playground.triggerEvent(event, cell.getCycleNumber());
                            }
                        }

                    };

                    synchronized(playgroundMutex)
                    {
                        multithreader.executeAndWait(task);
                    }
                }
            }
        };
    }

    private Subscriber destroyEntityMsgHandler()
    {
        logger.info("Inside destroyEntityMsgHandler");

        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof RemoveStateMsg)
                {
                    logger.debug("Instance of RemoveStateMsg");

                    RemoveStateMsg msg = (RemoveStateMsg) payload.getData();

                    if(msg.getState() instanceof AgentState)
                    {
                        synchronized(playgroundMutex)
                        {
                            playground.removeAgent(msg.getState().getID());
                        }
                    }
                    else if(msg.getState() instanceof EnvObjectState)
                    {
                        synchronized(playgroundMutex)
                        {
                            playground.removeEnvObject(msg.getState().getID());
                        }
                    }
                }
                if(payload.getData() instanceof RemoveEventMsg)
                {
                    logger.debug("Instance of RemoveEventMsg");

                    RemoveEventMsg msg = (RemoveEventMsg) payload.getData();

                    if(msg.getEvent() != null)
                    {
                        synchronized(playgroundMutex)
                        {
                            playground.removeEvent(msg.getEvent().getID());
                        }
                    }
                }
            }

        };
    }

    private Subscriber simPropertiesUpdateMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                SimConfig.refresh();
                VisConfig.refresh();
            }
        };
    }

    private Subscriber simEndingMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                switch(payload.getKey())
                {
                case 2:
                    synchronized(playgroundMutex)
                    {
                        playground.reset();
                    }
                    break;
                }
            }
        };
    }

    @Override
    protected void finalize() throws Throwable
    {
        multithreader.terminate();
        super.finalize();
    }

    /**
     * Stops the visual spectator and terminate the thead executor
     */
    public void stop()
    {
        try
        {
            finalize();
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}
