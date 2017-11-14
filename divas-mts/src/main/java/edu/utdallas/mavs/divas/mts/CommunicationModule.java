package edu.utdallas.mavs.divas.mts;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>CommunicationModule</code> objects represent a client connection to a JMS broker server. Once connected, a <code>CommunicationModule</code> can publish and subscribe messages to and from
 * other clients of the same JMS broker.
 * <p>
 * This class provides the necessary operations for connecting to a broker server, subscribing to and receiving messages on certain topics, publishing to and sending messages on certain topics, and disconnecting from the broker server.
 * <p>
 * This class simplifies the task of enabling messaging between clients by providing a well abstracted interface for JMS broker connections. To connect to a broker and enable communication with other clients, simply initialize a
 * <code>CommunicationModule</code> object, providing the host name and port number of the JMS broker server, add publication and subscription topics, and begin sending and receiving messages.
 * <p>
 * Note that all messages sent by a <code>CommunicationModule</code> are <code>MTSPayload</code> objects and must be interpreted as such.
 */
public class CommunicationModule
{
    private final static Logger         logger   = LoggerFactory.getLogger(CommunicationModule.class);

    /**
     * The name or IP address of the server running the JMS broker.
     */
    private MTSClient                   client;

    private ArrayList<String>           publications;
    private HashMap<String, Subscriber> subscribers;

    private static LocalMTS             localMTS = new LocalMTS();
    private int                         id;
    private static int                  nextid   = 0;

    /**
     * The constructor initializes the <code>CommunicationModule</code>. This constructor assumes that the <MTSType> is
     * TCP.
     * <p>
     * Once created, <code>CommunicationModule</code> objects must publish and subscribe to topics before sending and receiving messages.
     * 
     * @param client
     *        <code>MTSClient</code> instance to use.
     */
    public CommunicationModule(MTSClient client)
    {
        this.client = client;
        this.id = nextid;
        nextid++;
        publications = new ArrayList<String>();
        subscribers = new HashMap<String, Subscriber>();
    }

    /**
     * Initiates all publication topics, all subscription topics, and both the connection and context for this <code>ComunicationModule</code>.
     * 
     * @param client the client for to which this communication module should be associated
     * @throws MTSException
     *         if the publication or subscription topics could not be removed, or if the connection or context could not
     *         be closed.
     */
    public void reconnect(MTSClient client) throws MTSException
    {
        logger.debug("Connecting.");

        this.client = client;

        for(String topic : publications)
            client.addPublisher(topic, this);

        for(String topic : subscribers.keySet())
            client.startSubscription(topic, subscribers.get(topic));

        logger.debug("Connected.");
    }

    /**
     * Terminates all publication topics, all subscription topics, and both the connection and context for this <code>ComunicationModule</code>.
     * 
     * @throws MTSException
     *         if the publication or subscription topics could not be removed, or if the connection or context could not
     *         be closed.
     */
    public void disconnect() throws MTSException
    {
        logger.debug("Disconnecting.");

        client.stopReceiving();

        for(String topic : publications)
            client.removePublisher(topic, this);

        for(String topic : subscribers.keySet())
            client.endSubscription(topic, subscribers.get(topic));

        localMTS.terminate();

        logger.debug("Disconnected.");
    }

    /**
     * Terminates all publication topics, all subscription topics, and both the connection and context for this <code>ComunicationModule</code>. Once called, this <code>ComunicationModule</code> can
     * no longer send or receive messages until reconnected using the <code>connect()</code> method.
     */
    public void forceDisconnect()
    {
        try
        {
            disconnect();
        }
        catch(MTSException e)
        {
            logger.error("An error occurred while disconnecting from message broker. Perhaps the broker is not running.");
        }
    }

    /**
     * Adds the given topic name to the list of publication topics. Once called, this this <code>ComunicationModule</code> may be used to send messages using the given topic. If the list of
     * publication topics is empty, the publish session is automatically created prior to adding the given topic.
     * 
     * @param topicName
     *        the name of the topic to add to the publication list
     * @throws MTSException
     *         if the publication topic could not be added or if the publish session could not be created.
     */
    public void addPublicationTopic(String topicName) throws MTSException
    {
        if(!topicIsLocal(topicName))
        {
            client.addPublisher(topicName, this);
            publications.add(topicName);
        }
    }

    /**
     * Adds a subscription to the given topic. Messages will not be delivered for any topic until the <code>startReceiving()</code> is called. Each topic may only have a single subscriber. If a second
     * subscriber tries to subscribe to a topic, an exception is thrown, and the
     * subscription fails.
     * 
     * @param topicName
     *        the name of the topic to add a subscription to
     * @param subscriber
     *        the object that should be notified when a message is received for this topic.
     * @throws MTSException
     *         if the subscription topic could not be added.
     */
    public void addSubscriptionTopic(String topicName, Subscriber subscriber) throws MTSException
    {
        if(subscribers.get(topicName) != null)
            throw new MTSException("Already subscribed to topic: " + topicName, null);

        if(topicIsLocal(topicName))
        {
            localMTS.addSubscriptionTopic(id, topicName, subscriber);
        }
        else
        {
            client.startSubscription(topicName, subscriber);
            subscribers.put(topicName, subscriber);
        }
    }

    /**
     * Sends <code>payload</code> to the message broker on the given topic. The message broker is responsible for
     * delivering the message payload to any subscribers of the given topic.
     * 
     * @param payload
     *        the message payload to be sent to subscribers of the topic
     * @param topicName
     *        the name of the topic on which the message will be sent
     * @throws MTSException
     *         if there is no publish session open, or if there is no publisher for the given topic.
     */
    public void publishMessage(MTSPayload payload, String topicName) throws MTSException
    {
        logger.debug("Sending message, topic " + topicName);

        if(topicIsLocal(topicName))
        {
            localMTS.publishMessage(payload, topicName);
        }
        else
        {
            client.publishMessage(payload, topicName);
        }
    }

    /**
     * @param payload
     *        The message payload to be sent to subscribers of the topic.
     * @param topicName
     *        the name of the topic for which to close the publisher.
     * @param priority
     *        JMS priority values can range from 0 to 9. 0 is the lowest priority and 9 is the highest.
     *        If you do not provide a value, the JMS provider will use the default priority value of 4.
     *        The default priority is considered normal.
     * @throws MTSException
     *         if the publication topic could not be removed or if the publish session could not be closed.
     */
    public void publishMessage(MTSPayload payload, String topicName, int priority) throws MTSException
    {
        logger.debug("Sending message, topic " + topicName);

        if(topicIsLocal(topicName))
        {
            localMTS.publishMessage(payload, topicName);
        }
        else
        {
            client.publishMessage(payload, topicName, priority);
        }
    }

    /**
     * Sends <code>payload</code> to the message broker on the given topic. The message broker is responsible for
     * delivering the message payload to any subscribers of the given topic.
     * 
     * @param payload
     *        the message payload to be sent to subscribers of the topic
     * @param topicName
     *        the name of the topic on which the message will be sent
     * @throws MTSException
     *         if there is no publish session open, or if there is no publisher for the given topic.
     */
    public void publishBytesMessage(MTSPayload payload, String topicName) throws MTSException
    {
        logger.debug("Sending message, topic " + topicName);

        if(topicIsLocal(topicName))
        {
            localMTS.publishMessage(payload, topicName);
        }
        else
        {
            client.publishBytesMessage(payload, topicName);
        }
    }

    /**
     * Removes the publisher for the given topic. Once removed, messages can no longer be sent for the given topic. If
     * the removal of the topic leaves the list of publication topics empty, the publish
     * session is closed.
     * <p>
     * Restore the topic publication by sending the topic name to the <code>addPublicationTopic(String)</code> method.
     * 
     * @param topicName
     *        the name of the topic for which to close the publisher.
     * @throws MTSException
     *         if the publication topic could not be removed or if the publish session could not be closed.
     */
    public void removePublicationTopic(String topicName) throws MTSException
    {
        if(!topicIsLocal(topicName))
        {
            client.removePublisher(topicName, this);
            publications.remove(topicName);
        }
    }

    /**
     * Removes the subscription for the given topic. Once removed, messages of the given topic will no longer be
     * delivered to the topic's subscriber. If the removal of the topic leaves the list of
     * subscription topics empty, the subscription session is closed.
     * <p>
     * Restore the topic subscription by sending the topic name to the <code>addSubscriptionTopic(String, ISubscriber, String)</code> method.
     * 
     * @param topicName
     *        the name of the topic whose messages will no longer be delivered.
     * @throws MTSException
     *         if the subscription topic could not be removed or the subscribe session could not be closed.
     */
    public void removeSubscriptionTopic(String topicName) throws MTSException
    {
        Subscriber subscriber = subscribers.get(topicName);

        if(subscriber != null && !topicIsLocal(topicName))
        {
            client.endSubscription(topicName, subscriber);
            subscribers.remove(topicName);
        }
    }

    /**
     * @return the host name of the message broker connection.
     */
    public String getHostName()
    {
        return client.getHostName();
    }

    /**
     * @return the port number of the message broker connection.
     */
    public String getPort()
    {
        return client.getPort();
    }

    /**
     * Gets the current publication topics of this communication module
     * 
     * @return a list of publication topics
     */
    public ArrayList<String> getPublications()
    {
        return publications;
    }

    /**
     * Gets the current subscriptions of this communication module
     * 
     * @return a list of subscriptions
     */
    public HashMap<String, Subscriber> getSubscribers()
    {
        return subscribers;
    }

    private boolean topicIsLocal(String topicName)
    {
        if(topicName.equals(DivasTopic.hostConfigTopic) || topicName.equals(DivasTopic.envTopic) || topicName.equals(DivasTopic.destroyEntityTopic) || topicName.equals(DivasTopic.externalStimulusTopic)
                || topicName.equals(DivasTopic.runtimeAgentCommandTopic) || topicName.equals(DivasTopic.simulationControlTopic) || topicName.equals(DivasTopic.simSummaryTopic) || topicName.equals(DivasTopic.reorganizationTopic)
                || topicName.equals(DivasTopic.cellStructureTopic) || topicName.equals(DivasTopic.simStatusTopic) || topicName.equals(DivasTopic.simulationPropertiesTopic) || topicName.equals(DivasTopic.simSnapshotTopic))
            return false;
        return true;
    }
}
