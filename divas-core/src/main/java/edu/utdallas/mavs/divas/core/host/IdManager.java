package edu.utdallas.mavs.divas.core.host;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.msg.AssignEventIDMsg;
import edu.utdallas.mavs.divas.core.msg.AssignStateIDMsg;
import edu.utdallas.mavs.divas.core.msg.CreateEventMsg;
import edu.utdallas.mavs.divas.core.msg.CreateStateMsg;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.divas.mts.MTSException;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.mts.Subscriber;

/**
 * This class describes the simulation ID manager.
 * <p>
 * The id manager provides local components with means to assign unique IDs to any agents, objects, or environment events that are created.
 */
public class IdManager implements Subscriber, Serializable
{
    private static final long             serialVersionUID = 1L;

    private static final Logger           logger           = LoggerFactory.getLogger(IdManager.class);

    private transient CommunicationModule comModule;

    private int                           stateID;
    private int                           eventID;

    /**
     * Creates a new instance of {@link IdManager}
     * 
     * @param client
     *        the MTS client
     */
    public IdManager(MTSClient client)
    {
        comModule = new CommunicationModule(client);

        this.stateID = 0;
        this.eventID = 0;

        try
        {
            comModule.addPublicationTopic(DivasTopic.createEntityTopic);
            comModule.addSubscriptionTopic(DivasTopic.assignIDTopic, this);
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates the id manager with properties from the given id manager.
     * 
     * @param idManager
     *        The idManager from which values will be retrieved to update this idManager with.
     */
    public void copyFrom(IdManager idManager)
    {
        stateID = idManager.getStateID();
        eventID = idManager.getEventID();
    }

    @Override
    public void messageReceived(String topic, MTSPayload payload)
    {
        // this object should not be subscribed and should not receive messages, but just in case
        if(payload.getData() instanceof AssignStateIDMsg)
        {
            AssignStateIDMsg msg = (AssignStateIDMsg) payload.getData();

            CreateStateMsg createMsg = new CreateStateMsg(msg.getState());
            createMsg.getState().setID(getNextStateID());

            logger.debug("Id {} assigned to {}", createMsg.getState().getID(), createMsg.getState().getModelName());

            // resend the create message with the ID assigned
            sendCreateMsg(new MTSPayload(0, createMsg));
        }
        else if(payload.getData() instanceof AssignEventIDMsg)
        {
            AssignEventIDMsg msg = (AssignEventIDMsg) payload.getData();

            CreateEventMsg createMsg = new CreateEventMsg(msg.getEnvEvent());
            createMsg.getEnvEvent().setEventID(getNextEventID());

            logger.debug("Id {} assigned to {}", createMsg.getEnvEvent().getID(), createMsg.getEnvEvent().getClass().getName());

            // resend the create message with the ID assigned
            sendCreateMsg(new MTSPayload(0, createMsg));
        }
    }

    private synchronized int getNextEventID()
    {
        return eventID++;
    }

    private synchronized int getNextStateID()
    {
        return stateID++;
    }

    private void sendCreateMsg(MTSPayload payload)
    {
        try
        {
            comModule.publishMessage(payload, DivasTopic.createEntityTopic);
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the latest state ID assigned by this manager
     * 
     * @return an ID
     */
    public int getStateID()
    {
        return stateID;
    }

    /**
     * Gets the latest event ID assigned by this manager
     * 
     * @return an ID
     */
    public int getEventID()
    {
        return eventID;
    }

    /**
     * Terminates the IdManager
     */
    public void terminate()
    {
        comModule.forceDisconnect();
    }

    /**
     * Set the state ID and event ID to 0.
     */
    public synchronized void reset()
    {
        this.stateID = 0;
        this.eventID = 0;
    }
}
