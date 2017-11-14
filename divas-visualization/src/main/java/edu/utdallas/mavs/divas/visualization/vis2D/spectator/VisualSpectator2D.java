package edu.utdallas.mavs.divas.visualization.vis2D.spectator;

import java.util.Observable;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.dto.CellStateDto;
import edu.utdallas.mavs.divas.core.msg.RemoveStateMsg;
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
 * This class setup the subscriptions to message topics, and defines how each message topic is handled. It's responsible for updating the PlayGround information by extracting information from messages received from the simulation and dump it in the
 * PlayGround.
 */
public class VisualSpectator2D extends Observable
{
    private SimAdapter               simClientAdapter;

    private static PlayGround2D      playground2D = null;

    private static Multithreader     multithreader;

    private static long              cycles       = -1;

    private static VisualSpectator2D instance;

    private static int               period;

    /**
     * Constructs the VisualSpectator2D by injecting the SimAdapter and PlayGround2D
     * dependencies
     * 
     * @param simClientAdapter
     *        The SimAdapter which communicate with the simulation core
     * @param playGround2D
     *        The place to store the current status of the simulation
     */
    @Inject
    public VisualSpectator2D(SimAdapter simClientAdapter, PlayGround2D playGround2D)
    {
        this.simClientAdapter = simClientAdapter;
        VisualSpectator2D.playground2D = playGround2D;
        VisualSpectator2D.instance = this;
        multithreader = new Multithreader("VisualSpectator2DThrd", ThreadPoolType.FIXED);
    }

    /**
     * Gets the current simulation cycle
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
     * Gets the current playground
     * 
     * @return PlayGround which represent the current status of the simulation
     */
    public static PlayGround2D getPlayGround()
    {
        return playground2D;
    }

    /**
     * Gets a singleton instance of the visual spectator
     * 
     * @return Instance of the visual spectator
     */
    public static VisualSpectator2D getInstance()
    {
        return instance;
    }

    /**
     * Setups the subscription to message topics
     */
    public void start()
    {
        simClientAdapter.addSubscription(DivasTopic.envTopic, cellUpdateMsgHandler());
        simClientAdapter.addSubscription(DivasTopic.destroyEntityTopic, destroyEntityMsgHandler());
        simClientAdapter.addSubscription(DivasTopic.hostConfigTopic, simEndingMsgHandler());
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
                    cycles = cell.getCycleNumber();
                    period = cell.getPeriod();

                    Runnable task = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            playground2D.updateCell(cell);

                            playground2D.triggerEvent(cell.getEvents());
                            for(AgentState agent : cell.getAgentStates())
                            {
                                playground2D.updateAgent(agent);
                            }
                            for(EnvObjectState obj : cell.getEnvObjects())
                            {
                                playground2D.updateEnvObject(obj);
                            }

                            playground2D.updateEvents();
                        }
                    };

                    multithreader.executeAndWait(task);

                    setChanged();
                    notifyObservers();

                }
            }
        };
    }

    private Subscriber destroyEntityMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof RemoveStateMsg)
                {
                    RemoveStateMsg msg = (RemoveStateMsg) payload.getData();

                    if(msg.getState() instanceof AgentState)
                        playground2D.removeAgent((AgentState) msg.getState());
                    else if(msg.getState() instanceof EnvObjectState)
                        playground2D.removeEnvObject((EnvObjectState) msg.getState());
                }
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
                    playground2D.reset();
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
     * Stops the visual spectator and terminate the multithreader
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
