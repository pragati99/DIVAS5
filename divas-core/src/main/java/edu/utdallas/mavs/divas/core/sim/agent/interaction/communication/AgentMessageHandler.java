package edu.utdallas.mavs.divas.core.sim.agent.interaction.communication;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.LocalMTS;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;
import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * The Post Office! (Agent message handler)
 */
public class AgentMessageHandler
{
    /**
     * Sleep time between calls to empty the agent message queue.
     */
    private static final long                                MESSENGEHANDLER_SLEEP_TIME  = 100;
    /**
     * The most messages processed per call.
     */
    private static final long                                MESSENGEHANDLER_MAX_MESSAGE = 120000;

    /**
     * Instance of agent message handler.
     */
    private static AgentMessageHandler                       instance;

    /**
     * The agent MTS
     */
    private static LocalMTS                                  agentMTS;

    private HashMap<Integer, AgentCommunicationModule>       agentComs                   = new HashMap<Integer, AgentCommunicationModule>();

    private static final ConcurrentLinkedQueue<AgentMessage> outbox                      = new ConcurrentLinkedQueue<AgentMessage>();

    private static Multithreader                             agentMessengerService;

    private AgentMessageHandler()
    {
        startMessageService();
        agentMTS = new LocalMTS();
        agentMTS.addSubscriptionTopic(0, DivasTopic.agentMessageTopic, agentMsgHandler());
    }

    /**
     * Get the instance of agent message handler.
     * 
     * @return the instance
     */
    public static AgentMessageHandler getInstance()
    {
        if(instance == null)
        {
            instance = new AgentMessageHandler();
        }
        return instance;
    }

    /**
     * Add an agent message to the outbox.
     * 
     * @param am
     *        an agent message
     */
    public void addMessageToOutbox(AgentMessage am)
    {
        outbox.add(am);
    }

    /**
     * Add a new agent's com module to give the agent messages
     * 
     * @param id
     *        agent ID
     * @param vacm
     *        agent com module
     */
    public void addAgentComModule(int id, AgentCommunicationModule vacm)
    {
        agentComs.put(id, vacm);
    }

    /**
     * Pass a message to an agent
     * 
     * @param am
     *        the message
     */
    public void passMessageToAgent(AgentMessage am)
    {
        AgentCommunicationModule acm = agentComs.get(am.getDestinationID());
        if(acm != null)
        {
            acm.addMessageToInbox(am);
        }
    }

    /**
     * Send the message to the MTS.
     * 
     * @param mtsPayload
     *        the message data
     * @param topic
     *        topic to send on
     */
    protected void sendMessage(MTSPayload mtsPayload, String topic)
    {
        agentMTS.publishMessage(mtsPayload, topic);
    }

    /**
     * Start the messaging service
     */
    public void startMessageService()
    {
        agentMessengerService = new Multithreader("AgentMessageHandlerService", ThreadPoolType.CACHED, true);
        agentMessengerService.executePeriodicTask(getProcessingTask(), MESSENGEHANDLER_SLEEP_TIME);
    }

    /**
     * The task to process the outbox.
     * 
     * @return the runnable task
     */
    private Runnable getProcessingTask()
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {

                // sh.add((float) outbox.size());
                // System.out.println("------------------------------------------- OUTBOX: "+sh.getAverage());
                processOutbox();

            }
        };
        return task;
    }

    /**
     * Process the outbox until MESSENGEHANDLER_MAX_MESSAGE are sent.
     */
    public void processOutbox()
    {
        AgentMessage am;
        int count = 0;
        while(count < MESSENGEHANDLER_MAX_MESSAGE && ((am = outbox.poll()) != null))
        {
            count++;
            // final AgentMessage data = am;
            // Runnable task = new Runnable()
            // {
            // @Override
            // public void run()
            // {
            sendMessage(new MTSPayload(0, am), DivasTopic.agentMessageTopic);
            // }
            // };
            // multithreader.execute(task);
        }
    }

    // public void processOutbox()
    // {
    // AgentMessage am;
    // while ((am = outbox.poll()) != null)
    // {
    // AgentMessage data = am;
    // sendMessage(new MTSPayload(0, data), DivasTopic.agentMessageTopic);
    // }
    // }

    /**
     * The subscriber for the MTS.
     * 
     * @return the subscriber
     */
    private Subscriber agentMsgHandler()
    {
        return new Subscriber()
        {
            @Override
            public void messageReceived(String topic, MTSPayload payload)
            {
                if(payload.getData() instanceof AgentMessage)
                {
                    AgentMessage am = (AgentMessage) payload.getData();
                    passMessageToAgent(am);
                }
            }
        };
    }

}
