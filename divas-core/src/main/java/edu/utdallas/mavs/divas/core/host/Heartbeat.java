package edu.utdallas.mavs.divas.core.host;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.msg.HeartbeatCommandMsg;
import edu.utdallas.mavs.divas.core.msg.PhaseMsg;
import edu.utdallas.mavs.divas.core.msg.TickMsg;
import edu.utdallas.mavs.divas.core.sim.Phase;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * This class describes the simulator heatbeat.
 * <p>
 * The heartbeat acts as a time keeper for the simulation and provides local components with information regarding the simulation time.
 */
public class Heartbeat implements Serializable
{
    private static final long             serialVersionUID = 1L;

    private final static Logger           logger           = LoggerFactory.getLogger(Heartbeat.class);

    /**
     * A lower-bound for the simulation cycle time. If the actual simulation cycle time completes below this time, the
     * cycle is delayed by the remaining difference.
     */
    private static int                    minCycleTime     = SimConfig.getInstance().min_Cycle_Interval;

    /**
     * The communication module.
     */
    private transient CommunicationModule comModule;

    /**
     * Payload message to be sent to local components.
     */
    private transient MTSPayload          tickPayload;

    /**
     * The current cycle number of the simulation. Initially set to 0.
     */
    private long                          cycles           = 0;

    /**
     * The current simulation phase. Initially set to INIT.
     */
    private Phase                         phase            = Phase.INIT;

    /**
     * Time in milliseconds heartbeats
     */
    private int                           period           = 0;

    /**
     * Total running time of the heartbeat timer in milliseconds
     */
    private long                          runTime          = 0;

    /**
     * Status of the time keeper ON/OFF
     */
    private boolean                       beating          = false;

    /**
     * Timestamps the beginning of a simulation cycle
     */
    private long                          cycleTimestamp;

    /**
     * Thread executor
     */
    private transient Multithreader       multithreader;

    /**
     * Creates a new heartbeat instance.
     * 
     * @param client
     *        the MTS client to be used by the heart beat to communicate with other components.
     */
    public Heartbeat(MTSClient client)
    {
        comModule = new CommunicationModule(client);

        multithreader = new Multithreader("HeartbeatThrd", ThreadPoolType.FIXED, 5, false);

        // create canned tick payload to be modified and published at every tick
        // key = 0 indicates that this is a tick message
        tickPayload = new MTSPayload(0, null);

        // configures the time keeper
        initializeHeartBeat();
    }

    /**
     * Initializes the time keeper
     */
    private void initializeHeartBeat()
    {
        cycles = 0;
        period = 0;
        phase = Phase.INIT;
        runTime = 0;

        try
        {
            // setups time control of the simulation
            setupTimeControl();

            // setups heartbeat control
            //setupHeartbeatControl();

            // setups simulation phase execution ordering
            setupSimulationPhaseControl();

            // setup publications
            comModule.addPublicationTopic(DivasTopic.heartbeatTopic);
            comModule.addPublicationTopic(DivasTopic.phaseCompletionTopic);
            comModule.addPublicationTopic(DivasTopic.simStatusTopic);
        }
        catch(MTSException e)
        {
            logger.error("An error has occurred while initializing heartbeat.", e);
        }
    }

    private void setupSimulationPhaseControl() throws MTSException
    {
        comModule.addSubscriptionTopic(DivasTopic.phaseCompletionTopic, new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                Phase p = ((PhaseMsg) payload.getData()).getPhase();
                logger.debug("Receiveing Phase {}", p);

                if(beating)
                {
                    // determine which phase is next (this is very important!)
                    switch(p)
                    {
                    case INIT:
                        phase = Phase.AGENT;
                        cycles++;
                        cycleTimestamp = System.currentTimeMillis();
                        break;
                    case AGENT:
                        phase = Phase.ENVIRONMENT;
                        break;
                    case ENVIRONMENT:
                        phase = Phase.AGENT;
                        cycles++;
                        long elapsedTime = System.currentTimeMillis() - cycleTimestamp;
                        period = (int) elapsedTime;

                        if(elapsedTime < minCycleTime)
                        {
                            try
                            {
                                Thread.sleep(minCycleTime - elapsedTime);
                                elapsedTime = minCycleTime;
                            }
                            catch(InterruptedException e)
                            {
                                logger.error("An error has occurred while delaying heartbeat", e);
                            }
                        }

                        if(elapsedTime > 150)
                            logger.info("Simulation period: {}", elapsedTime);
                        // period = (int) elapsedTime;
                        runTime += elapsedTime;
                        cycleTimestamp = System.currentTimeMillis();
                        break;
                    }

                    // notifies phase change
                    tick(cycles, phase, runTime, period);
                    sendStatusMessage(beating);
                }
            }
        });
    }

//    private void setupHeartbeatControl() throws MTSException
//    {
//        comModule.addSubscriptionTopic(DivasTopic.heartbeatTopic, new Subscriber()
//        {
//            @Override
//            public void messageReceived(String topic, MTSPayload payload)
//            {
//                if(payload.getKey() < 0)
//                {
//                    // if heartbeat has stopped
//                    beating = false;
//                }
//                else if(payload.getKey() > 0)
//                {
//                    // if the heartbeat has started
//                    beating = true;
//                    // period = payload.getKey();
//                }
//
//                sendStatusMessage(beating);
//            }
//        });
//    }

    private void setupTimeControl() throws MTSException
    {
        comModule.addSubscriptionTopic(DivasTopic.timeControlTopic, new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof HeartbeatCommandMsg)
                {
                    HeartbeatCommandMsg command = (HeartbeatCommandMsg) payload.getData();

                    if(command.getCommand() == HeartbeatCommandMsg.Command.RUN)
                        start();
                    else if(command.getCommand() == HeartbeatCommandMsg.Command.PAUSE)
                        pause();
                    else if(command.getCommand() == HeartbeatCommandMsg.Command.RESET)
                        reset();
                }
            }
        });
    }

    private void tick(long cycles, Phase phase, long simTime, int period)
    {
        // update tick payload data with new tick and simTime
        tickPayload.setData(new TickMsg(cycles, phase, simTime, period));
        sendHeartBeatMessage(tickPayload);
    }

    /**
     * Updates this heartbeat with properties from the given heartbeat instance.
     * 
     * @param heartbeat
     *        The heartbeat from which values will be retrieved in order to update this heartbeat with.
     */
    public void copyFrom(Heartbeat heartbeat)
    {
        cycles = heartbeat.cycles;
        phase = heartbeat.phase;
        period = heartbeat.period;
        runTime = heartbeat.runTime;
        beating = heartbeat.beating;
        cycleTimestamp = System.currentTimeMillis();
    }

    private void sendPhaseMessage(MTSPayload msg)
    {
        try
        {
            comModule.publishMessage(msg, DivasTopic.phaseCompletionTopic);
        }
        catch(MTSException e)
        {
            logger.error("An error has occurred while sending phase completion message at cycle number {}", cycles, e);
        }
    }

    private void sendHeartBeatMessage(MTSPayload msg)
    {
        try
        {
            comModule.publishMessage(msg, DivasTopic.heartbeatTopic);
        }
        catch(MTSException e)
        {
            logger.error("An error has occurred while sending heartbeat message at cycle number {}", cycles, e);
        }
    }

    private void sendStatusMessage(final boolean running)
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    comModule.publishMessage(new MTSPayload(0, running), DivasTopic.simStatusTopic);
                }
                catch(MTSException e)
                {
                    logger.error("Failed to send simulation status message at cycle number {}", cycles, e);
                }
            }
        };
        multithreader.execute(task);
    }

    /**
     * Gets the current simulation cycle period
     * 
     * @return the simulation cycle period
     */
    public int getPeriod()
    {
        return period;
    }

    /**
     * Gets the current cycle number of the simulation
     * 
     * @return the cycle number
     */
    public long getCycles()
    {
        return cycles;
    }

    /**
     * Checks if the time keeper is running
     * 
     * @return the time keeping running status
     */
    public boolean isBeating()
    {
        return beating;
    }

    /**
     * Pauses the simulation by stopping the heartbeat
     */
    public void pause()
    {
        beating = false;
        // key < 0 indicates that is not beating
        //sendHeartBeatMessage(new MTSPayload(-1, null));
        sendStatusMessage(beating);
    }

    /**
     * Starts or Resumes the simulation by starting the heartbeat
     */
    public void start()
    {
        if(!beating)
        {
            logger.info("Starting heartbeat");

            beating = true;

            // resumes beating from current phase
            sendPhaseMessage(new MTSPayload(0, new PhaseMsg(phase)));

            // key > 0 indicates that is beating
            //sendHeartBeatMessage(new MTSPayload(1, null));
            
            sendStatusMessage(beating);
        }
        else
        {
            logger.info("Heartbeat already beating at cycle number {}", cycles);
        }
    }

    /**
     * Resets the heartbeat
     */
    public void reset()
    {
        cycles = 0;
        period = 0;
        phase = Phase.INIT;
        runTime = 0;
    }

    /**
     * Causes the heartbeat to update its configuration parameters.
     */
    public static void refresh()
    {
        minCycleTime = SimConfig.getInstance().min_Cycle_Interval;
    }

    /**
     * Handles the time keeper shutdown
     */
    public void terminate()
    {
        comModule.forceDisconnect();
    }
}
