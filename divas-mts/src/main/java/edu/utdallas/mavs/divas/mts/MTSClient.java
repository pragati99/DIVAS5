package edu.utdallas.mavs.divas.mts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.utils.ByteSerializer;

/**
 * <code>MTSClient</code> objects represent a client connection to a JMS broker server. Once connected, a <code>MTSClient</code> can publish and subscribe messages to and from other clients of the
 * same JMS broker.
 * <p>
 * This class provides the necessary operations for connecting to a broker server, subscribing to and receiving messages on certain topics, publishing to and sending messages on certain topics, and disconnecting from the broker server.
 * <p>
 * This class simplifies the task of enabling messaging between clients by providing a well abstracted interface for JMS broker connections. To connect to a broker and enable communication with other clients, simply initialize a
 * <code>MTSClient</code> object, providing the host name and port number of the JMS broker server, add publication and subscription topics, and begin sending and receiving messages.
 * <p>
 * Note that all messages sent by a <code>MTSClient</code> are <code>MTSPayload</code> objects and must be interpreted as such.
 */
public class MTSClient
{
    private final static Logger              logger           = LoggerFactory.getLogger(MTSClient.class);

    /**
     * The name or ip of the server running the JMS broker.
     */
    private String                           hostName;

    /**
     * The server's listening port number.
     */
    private String                           port;

    /**
     * The initial context providing the starting point for name resolutions. Used to open a connection to the proper
     * JMS broker.
     */
    private InitialContext                   context;

    /**
     * An active connection to a publish/subscribe JMS provider. Used to create publish and subscribe <code>TopicSession</code> objects for producing and consuming messages.
     */
    private TopicConnection                  topicConnection  = null;

    /**
     * Message producing session created from <code>topicConnection</code>, used to create <code>TopicPublisher</code>s.
     */
    private TopicSession                     publishSession   = null;

    /**
     * Message consuming session created from <code>topicConnection</code>, used to create <code>TopicSubscriber</code> s.
     */
    private TopicSession                     subscribeSession = null;

    /**
     * contains a list of current <code>TopicPublisher</code>s and the <code>CommunicationModule</code>s publishing on
     * that topic. The <code>HashMap</code> key is the topic name and the value is the <code>PublicationManager</code> for that topic. The <code>PublicationManager</code> contains the <code>TopicPublisher</code> used to publish
     * messages on the topic.
     */
    private Map<String, PublicationManager>  pubManagerMap    = Collections.synchronizedMap(new HashMap<String, PublicationManager>());

    /**
     * contains a list of current <code>TopicSubscriber</code>s and the <code>CommunicationModule</code>s subscribed to
     * that topic. The <code>HashMap</code> key is the topic name and the value is the <code>SubscriptionManager</code> for that topic. The <code>SubscriptionManager</code> contains the <code>TopicSubscriber</code> used to subscribe
     * to messages that have been published to the
     * topic.
     */
    private Map<String, SubscriptionManager> subManagerMap    = Collections.synchronizedMap(new HashMap<String, SubscriptionManager>());

    private boolean                          connected        = false;

    /**
     * The constructor initializes the <code>MTSClient</code> at the given host on the given port. Specifically, this
     * constructor creates an open TCP/IP socket connection to the JMS broker servicing
     * the given <code>port</code> at the given <code>hostName</code>.
     * <p>
     * Once created, <code>MTSClient</code> objects must publish and subscribe to topics before sending and receiving messages.
     * 
     * @param hostName
     *        the name or ip of the server running the JMS broker. The server must be visible to the client executing
     *        the <code>MTSClient</code> for the connection to be created.
     * @param port
     *        the server's listening port number.
     * @throws MTSException
     *         if there is no message broker running at the host on the specified port, or if the connection can not be
     *         created for some other reason.
     */
    public MTSClient(String hostName, String port)
    {
        logger.info("Creating MTSClient instance: " + hostName + ":" + port);

        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Starts a topic connection with the host on the port specified. This method does not through an MTS exception.
     */
    public void startQuietly()
    {
        try
        {
            connect();
        }
        catch(MTSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Starts a topic connection with the host on the port specified
     * 
     * @throws MTSException
     *         if there is no message broker running at the host on the specified port, or if the connection can not be
     *         created for some other reason.
     */
    public void start() throws MTSException
    {
        connect();
    }

    /**
     * Creates a topic connection with the host on the port specified by the <code>port</code> attribute. The connection
     * is created in stopped mode so no messages will be delivered until the <code>startReceiving()</code> method is
     * called.
     * 
     * @throws MTSException
     *         if there is no message broker running at the host on the specified port, or if the connection can not be
     *         created for some other reason.
     */
    protected void connect() throws MTSException
    {
        logger.info("Connecting: " + hostName + ":" + port);

        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        // properties.setProperty(Context.PROVIDER_URL, "tcp://" + hostName + ":" + port);
        properties.setProperty("prefetchPolicy.topicPrefetch", "1000");
        // properties.setProperty(Context.PROVIDER_URL, "tcp://" + hostName + ":" + port + "?socketBufferSize=131072&ioBufferSize=16384");
        properties.setProperty(Context.PROVIDER_URL, "tcp://" + hostName + ":" + port + "?jms.optimizeAcknowledge=true");
        // properties.setProperty("prefetchPolicy.queuePrefetch", "1000");
        // properties.setProperty("prefetchPolicy.queueBrowserPrefetch", "500");
        // properties.setProperty("prefetchPolicy.durableTopicPrefetch", "1000");
        // properties.setProperty("prefetchPolicy.topicPrefetch", "32766");
        // properties.setProperty(Context.SECURITY_PRINCIPAL, "someusername" );
        // properties.setProperty(Context.SECURITY_CREDENTIALS, "somepassword" );

        try
        {
            context = new InitialContext(properties);
        }
        catch(NamingException e)
        {
            throw new MTSException("Failed to create context, make sure the message broker is running.\n", e);
        }

        try
        {
            TopicConnectionFactory tcFactory = (TopicConnectionFactory) context.lookup("ConnectionFactory");

            topicConnection = tcFactory.createTopicConnection();

            startReceiving();
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to create connection, make sure the message broker is running.\n", e);
        }
        catch(NamingException e)
        {
            throw new MTSException("Failed to create connection to " + hostName + ":" + port + "\n", e);
        }

        connected = true;
    }

    /**
     * Terminates all publication topics, all subscription topics, and both the connection and context for this <code>ComunicationModule</code>. Once called, this <code>ComunicationModule</code> can
     * no longer send or receive messages until reconnected using the <code>connect()</code> method.
     * 
     * @throws MTSException
     *         if the publication or subscription topics could not be removed, or if the connection or context could not
     *         be closed.
     */
    public void terminate() throws MTSException
    {
        logger.debug("Terminating MTSClient connection: " + hostName + ":" + port);

        connected = false;

        removeAllPublications();
        removeAllSubscriptions();

        // close the topic connection and naming context
        if(topicConnection != null && context != null)
        {
            try
            {
                topicConnection.close();
                context.close();

                topicConnection = null;
            }
            catch(JMSException e)
            {
                throw new MTSException("Failed to close topic connection.\n", e);
            }
            catch(NamingException e)
            {
                throw new MTSException("Failed to close context.\n", e);
            }
        }

        logger.debug("Terminated MTSClient connection: " + hostName + ":" + port);
    }

    /**
     * Terminates all publication topics, all subscription topics, and both the connection and context for this <code>ComunicationModule</code> quietly. Once called, this <code>ComunicationModule</code> can
     * no longer send or receive messages until reconnected using the <code>connect()</code> method.
     */
    public void terminateQuietly()
    {
        try
        {
            terminate();
        }
        catch(MTSException e)
        {
            logger.error("An error occurred while terminating the MTS client");
        }
    }

    /**
     * Adds the given topic name to the list of publication topics. Once called, this this <code>ComunicationModule</code> may be used to send messages using the given topic. If the list of
     * publication topics is empty, the publish session is automatically created prior to adding the given topic.
     * 
     * @param topicName
     *        the name of the topic to add to the publication list
     * @param comModule
     *        The client connection to a JMS broker server
     * @throws MTSException
     *         if the publication topic could not be added or if the publish session could not be created.
     */
    public void addPublisher(String topicName, CommunicationModule comModule) throws MTSException
    {
        // if there is no publication manager for this topic
        if(pubManagerMap.get(topicName) == null)
        {
            // create one
            try
            {
                if(publishSession == null)
                    publishSession = createSession();

                pubManagerMap.put(topicName, new PublicationManager(topicName, publishSession.createPublisher(createTopic("dynamicTopics/" + topicName))));
            }
            catch(JMSException e)
            {
                throw new MTSException("Failed to add publication topic:  " + topicName + "\n", e);
            }
        }

        // add the comModule to the list of comModules currently publishing on the topic
        pubManagerMap.get(topicName).addPublisher(comModule);
    }

    /**
     * Adds a subscription to the given topic. Messages received on this topic that are not filtered will be sent to the <code>subscriber</code> object. Messages will not be delivered for any topic
     * until the <code>startReceiving()</code> is called. Each topic may have a multiple subscribers. Each subscriber
     * will receive a copy of each message.
     * 
     * @param topicName
     *        the name of the topic to add a subscription to
     * @param subscriber
     *        An implementation of the <code>ISubscriber</code> interface which wants
     *        to subscribe to the given topic.
     * @throws MTSException
     *         if the subscription topic could not be added.
     */
    public void startSubscription(String topicName, Subscriber subscriber) throws MTSException
    {
        // if there is no subscription manager for this topic
        if(subManagerMap.get(topicName) == null)
        {
            // create one
            try
            {
                if(subscribeSession == null)
                    subscribeSession = createSession();

                TopicSubscriber topicSubscriber = subscribeSession.createSubscriber(createTopic("dynamicTopics/" + topicName), null, false);
                SubscriptionManager subManager = new SubscriptionManager(topicName, topicSubscriber);
                subManagerMap.put(topicName, subManager);
                topicSubscriber.setMessageListener(new MTSPayloadListener(subManager));
            }
            catch(JMSException e)
            {
                throw new MTSException("Failed to add subscription topic:  " + topicName + "\n", e);
            }
        }

        // add the subscriber to the list of subscribers currently subscribed to the topic
        subManagerMap.get(topicName).addSubscriber(subscriber);
    }

    /**
     * Creates a topic session from the <code>MTSClient</code>'s topic connection.
     * 
     * @return the created <code>TopicSession</code>
     * @throws MTSException
     *         if the topic session could not be created.
     */
    protected TopicSession createSession() throws MTSException
    {
        try
        {
            return topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            // return topicConnection.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to create session.\n", e);
        }
    }

    /**
     * Creates a topic with the given name.
     * 
     * @param topicName
     *        the name of the topic to create
     * @return the created <code>Topic</code>
     * @throws MTSException
     *         if the topic could not be created.
     */
    protected Topic createTopic(String topicName) throws MTSException
    {
        Topic topic = null;

        try
        {
            topic = (Topic) context.lookup(topicName);
        }
        catch(NamingException e)
        {
            throw new MTSException("Failed to create topic:  " + topicName + "\n", e);
        }

        return topic;
    }

    /**
     * Returns the host name of the message broker connection.
     * 
     * @return the host name of the message broker connection.
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * Returns the port number of the message broker connection.
     * 
     * @return the port number of the message broker connection.
     */
    public String getPort()
    {
        return port;
    }

    /**
     * Returns an array of current publication topic names.
     * 
     * @return a String array of current publication topic names.
     */
    public String[] getPublicationTopics()
    {
        // create array to store results
        String[] topicNames = new String[pubManagerMap.size()];

        // get the iterator for the topic publisher keys (topic names)
        Iterator<String> iter = pubManagerMap.keySet().iterator();

        // initialize array index
        int i = 0;

        // loop through each of the topic publisher keys (topic names)
        while(iter.hasNext())
        {
            // store the next topic name in the results array and increment the array index
            topicNames[i++] = iter.next();
        }

        // return the results
        return topicNames;
    }

    /**
     * Returns an array of current subscription topic names.
     * 
     * @return a String array of current subscription topic names.
     */
    public String[] getSubscriptionTopics()
    {
        // create array to store results
        String[] topicNames = new String[pubManagerMap.size()];

        // get the iterator for the topic subscriber keys (topic names)
        Iterator<String> iter = subManagerMap.keySet().iterator();

        // initialize array index
        int i = 0;

        // loop through each of the topic subscriber keys (topic names)
        while(iter.hasNext())
        {
            // store the next topic name in the results array and increment the array index
            topicNames[i++] = iter.next();
        }

        // return the results
        return topicNames;
    }

    /**
     * Sends <code>payload</code> to the message broker on the given topic. The message broker is responsible for
     * delivering the message payload to any subscribers of the given topic.
     * 
     * @param payload
     *        the message payload to be sent to subscribers of the topic
     * @param topicName
     *        the name of the topic on which the message will be sent
     * @param filter
     *        String value of the message's 'filter' property. This value may be used to determine which messages are
     *        delivered to subscribers. The message is only delivered to subscribers whose
     *        properties match the filter. A value of null or an empty string indicates that there is no filter for the
     *        message consumer and that message will be sent to all subscribers to the topic.
     * @throws MTSException
     *         if there is no publish session open, or if there is no publisher for the given topic.
     */
    public void publishMessage(MTSPayload payload, String topicName) throws MTSException
    {
        if(publishSession == null)
            throw new MTSException("Failed to publish message, no publish session open.", null);

        try
        {
            // prepare the message
            ObjectMessage objMessage = publishSession.createObjectMessage(payload);

            // get the topic publisher which will publish this message
            TopicPublisher topicPublisher = pubManagerMap.get(topicName).getTopicPublisher();

            if(topicPublisher != null)
            {
                topicPublisher.publish(objMessage, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 0);
            }
            else
            {
                throw new MTSException("Failed to publish message, no TopicPublisher for topic:  " + topicName + "\n", null);
            }
        }
        catch(Exception e)
        {
            throw new MTSException("Failed to publish message to topic:  " + topicName + "\n", e);
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
     * @param filter
     *        String value of the message's 'filter' property. This value may be used to determine which messages are
     *        delivered to subscribers. The message is only delivered to subscribers whose
     *        properties match the filter. A value of null or an empty string indicates that there is no filter for the
     *        message consumer and that message will be sent to all subscribers to the topic.
     * @throws MTSException
     *         if there is no publish session open, or if there is no publisher for the given topic.
     */
    public void publishBytesMessage(MTSPayload payload, String topicName) throws MTSException
    {
        if(publishSession == null)
            throw new MTSException("Failed to publish message, no publish session open.", null);

        try
        {
            // prepare the message
            ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();
            byte[] bytes = ByteSerializer.serialize(payload);
            bytesMessage.writeBytes(bytes);

            // get the topic publisher which will publish this message
            TopicPublisher topicPublisher = pubManagerMap.get(topicName).getTopicPublisher();

            if(topicPublisher != null)
            {
                topicPublisher.publish(bytesMessage, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, 0);
            }
            else
            {
                throw new MTSException("Failed to publish message, no TopicPublisher for topic:  " + topicName + "\n", null);
            }
        }
        catch(Exception e)
        {
            throw new MTSException("Failed to publish message to topic:  " + topicName + "\n", e);
        }
    }

    /**
     * Publishes the given message for all the subscribers to the given topic name.
     * 
     * @param payload
     *        The message payload to be sent to subscribers of the topic.
     * @param topicName
     *        The name of the topic on which the message will be sent
     * @param priority
     *        JMS priority values can range from 0 to 9. 0 is the lowest priority and 9 is the highest.
     *        If you do not provide a value, the JMS provider will use the default priority value of 4.
     *        The default priority is considered normal.
     * @throws MTSException
     *         If there is no publish session open, or if there is no publisher for the given topic.
     */
    public void publishMessage(MTSPayload payload, String topicName, int priority) throws MTSException
    {
        if(publishSession == null)
            throw new MTSException("Failed to publish message, no publish session open.", null);

        try
        {
            // prepare the message
            ObjectMessage objMessage = publishSession.createObjectMessage(payload);

            // get the topic publisher which will publish this message
            TopicPublisher topicPublisher = pubManagerMap.get(topicName).getTopicPublisher();

            if(topicPublisher != null)
            {
                topicPublisher.publish(objMessage, DeliveryMode.NON_PERSISTENT, priority, 0);
            }
            else
            {
                throw new MTSException("Failed to publish message, no TopicPublisher for topic:  " + topicName + "\n", null);
            }
        }
        catch(Exception e)
        {
            throw new MTSException("Failed to publish message to topic:  " + topicName + "\n", e);
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
     * @param comModule
     *        The client connection to a JMS broker server
     * @throws MTSException
     *         if the publication topic could not be removed or if the publish session could not be closed.
     */
    public void removePublisher(String topicName, CommunicationModule comModule) throws MTSException
    {
        logger.debug("Removing publisher for " + topicName);

        synchronized(pubManagerMap)
        {
            PublicationManager pm = pubManagerMap.get(topicName);

            if(pm != null)
            {
                pm.removePublisher(comModule);

                // if there are no more comModules publishing to this topic
                if(!pm.hasPublishers())
                {
                    // close the topic's publication manager
                    closePublicationManager(pm);

                    // remove the publication manager from the map
                    pubManagerMap.remove(pm.getTopicName());
                }
            }

            tryClosePublishSession();
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
     * @param subscriber
     *        An implementation of the <code>ISubscriber</code> interface which wants
     *        to subscribe to the given topic.
     * @throws MTSException
     *         if the subscription topic could not be removed or the subscribe session could not be closed.
     */
    public void endSubscription(String topicName, Subscriber subscriber) throws MTSException
    {
        synchronized(subManagerMap)
        {
            SubscriptionManager sm = subManagerMap.get(topicName);

            if(sm != null)
            {
                sm.removeSubscriber(subscriber);

                // if there are no more comModules subscribed to this topic
                if(!sm.hasSubscriptions())
                {
                    // close the topic's subscription manager
                    closeSubscriptionManager(sm);

                    // remove the subscription manager from the map
                    subManagerMap.remove(sm.getTopicName());
                }
            }
        }
    }

    /**
     * Removes all publication topics. Once called, this <code>ComunicationModule</code> can no longer send messages on
     * any topic until a topic is added again using the <code>addPublicationTopic(String)</code> method. This method
     * causes the publish session to close upon removal of the last publication topic.
     * 
     * @throws MTSException
     *         if any of the publication topics could not be removed.
     */
    private void removeAllPublications() throws MTSException
    {
        synchronized(pubManagerMap)
        {
            logger.debug("Removing all publications.");

            Iterator<PublicationManager> iter = pubManagerMap.values().iterator();

            while(iter.hasNext())
            {
                PublicationManager pm = iter.next();
                closePublicationManager(pm);

                // remove the publication manager from the map
                iter.remove();
            }

            tryClosePublishSession();
        }
    }

    /**
     * Removes all subscription topics. Once called, this <code>ComunicationModule</code> can no longer receive messages
     * on any topic until a topic is added again using the <code>addSubscriptionTopic(String, ISubscriber, String)</code> method. This method causes the subscribe session
     * to close upon removal of the last publication topic.
     * 
     * @throws MTSException
     *         if any of the publication topics could not be removed.
     */
    private void removeAllSubscriptions() throws MTSException
    {
        synchronized(subManagerMap)
        {
            Iterator<SubscriptionManager> iter = subManagerMap.values().iterator();

            while(iter.hasNext())
            {
                closeSubscriptionManager(iter.next());

                // remove the subscription manager from the map
                iter.remove();
            }

            tryCloseSubscribeSession();
        }
    }

    /**
     * Closes the topic publisher associated with this topic.
     * 
     * @param pm
     *        <code>PublicationManager</code> to be closed.
     * @throws MTSException
     *         if either the topic publisher or publish session could not be closed.
     */
    protected void closePublicationManager(PublicationManager pm) throws MTSException
    {
        // close the topic publisher for the given topic
        try
        {
            pm.getTopicPublisher().close();
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to close topic publisher:  " + pm.getTopicName() + "\n", e);
        }
    }

    /**
     * Closes the topic subscriber associated with this topic.
     * 
     * @param sm
     *        <code>SubscriptionManager</code> to be closed.
     * @throws MTSException
     *         if either the topic subscriber or subscribe session could not be closed.
     */
    protected void closeSubscriptionManager(SubscriptionManager sm) throws MTSException
    {
        try
        {
            // close the topic subscriber for the given topic
            sm.getTopicSubscriber().close();
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to close topic subscriber:  " + sm.getTopicName() + "\n", e);
        }
    }

    /**
     * If the list of topic subscribers is empty, close the publish session.
     * 
     * @throws MTSException
     */
    protected void tryClosePublishSession() throws MTSException
    {
        logger.debug("Closing publish session.");

        try
        {
            if(pubManagerMap.isEmpty() && publishSession != null)
            {
                publishSession.close();
                publishSession = null;
            }

            logger.debug("Closed publish session.");
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to close publish session.\n", e);
        }
    }

    /**
     * If the list of topic subscribers is empty, close the subscriber session.
     * 
     * @throws MTSException
     */
    protected void tryCloseSubscribeSession() throws MTSException
    {
        logger.debug("Closing subscribe session.");

        try
        {
            if(subManagerMap.isEmpty() && subscribeSession != null)
            {
                subscribeSession.close();
                subscribeSession = null;
            }

            logger.debug("Closed subscribe session.");
        }
        catch(JMSException e)
        {
            throw new MTSException("Failed to close subscribe session.\n", e);
        }
    }

    /**
     * This method starts or resumes the delivery of messages on the subscribed topics. This method must be invoked in
     * order to begin receiving messages.
     * 
     * @throws MTSException
     *         if the topic connection could not be started.
     */
    public void startReceiving() throws MTSException
    {
        if(topicConnection != null)
        {
            try
            {
                // resume message delivery
                topicConnection.start();
            }
            catch(JMSException e)
            {
                throw new MTSException("Failed to start subscriptions, topic connection failed to start.\n", e);
            }
        }
        else
        {
            throw new MTSException("Failed to start subscriptions, not connected.\n", null);
        }
    }

    /**
     * Temporarily stops the delivery of message on subscribed topics. Once called, this <code>ComunicationModule</code> will no longer receive messages on any topic until the connection is started
     * again using the <code>addSubscriptionTopic(String)</code> method.
     * <p>
     * If any message listeners (<code>ISubscriber</code>s) are running when <code>stopReceiving()</code> is invoked, <code>stopReceiving()</code> will not return until all of the listeners have returned. These message listeners have full services of
     * the connection until they return and the subscription is stopped.
     * <p>
     * The <code>startReceiving()</code> method must be invoked to resume delivery of messages.
     * 
     * @throws MTSException
     *         if the topic connection could not be stopped.
     */
    public void stopReceiving() throws MTSException
    {
        if(topicConnection != null)
        {
            try
            {
                // pause message delivery
                topicConnection.stop();
            }
            catch(JMSException e)
            {
                throw new MTSException("Failed to stop subscriptions, topic connection failed to stop.\n", e);
            }
        }
    }

    private class PublicationManager
    {
        protected String            topicName;
        protected ArrayList<Object> publishers;
        private TopicPublisher      tp;

        public PublicationManager(String topicName, TopicPublisher tp)
        {
            this.topicName = topicName;
            this.tp = tp;
            publishers = new ArrayList<Object>();
        }

        public TopicPublisher getTopicPublisher()
        {
            return tp;
        }

        public void addPublisher(Object publisher)
        {
            publishers.add(publisher);
        }

        public void removePublisher(Object publisher)
        {
            publishers.remove(publisher);
        }

        public boolean hasPublishers()
        {
            return !publishers.isEmpty();
        }

        public String getTopicName()
        {
            return topicName;
        }
    }

    /**
     * A <code>SubscriptionManager</code> object represents a mapping between a topic name and a list of subscribers
     * which are subscribed to that topic.
     * <p>
     * Each topic name can have one or more subscriber. This object is stored in a <code>Map</code> inside the <code>MTSClient</code>.
     */
    public class SubscriptionManager
    {
        /**
         * The name of the topic on which the message will be sent
         */
        protected String           topicName;
        /**
         * The <code>List</code> of subscribers to the <code>topicName</code>.
         */
        protected List<Subscriber> subscribers;
        private TopicSubscriber    ts;

        /**
         * Constructs the <code>SubscriptionManager</code> object, by setting the topic name and the <code>TopicSubscriber</code> and initializing the list of subscribers.
         * 
         * @param topicName
         *        The name of the topic to set in the <code>SubscriptionManager</code>
         * @param ts
         *        The <code>TopicSubscriber</code>
         */
        public SubscriptionManager(String topicName, TopicSubscriber ts)
        {
            this.topicName = topicName;
            this.ts = ts;
            subscribers = Collections.synchronizedList(new ArrayList<Subscriber>());
        }

        /**
         * Gets <code>TopicSubscriber</code>. A client uses a TopicSubscriber object to receive messages that have been
         * published to a topic.
         * 
         * @return <code>TopicSubscriber</code> for the topic in this object.
         */
        public TopicSubscriber getTopicSubscriber()
        {
            return ts;
        }

        /**
         * Adds the given subscriber to the list of subscribers to the topic name.
         * 
         * @param subscriber
         *        An implementation of the <code>ISubscriber</code> interface which wants
         *        to subscribe to the given topic.
         */
        public void addSubscriber(Subscriber subscriber)
        {
            subscribers.add(subscriber);
        }

        /**
         * Removes the given subscriber from the list of subscribers to the topic name.
         * 
         * @param subscriber
         *        An implementation of the <code>ISubscriber</code> interface which wants
         *        to subscribe to the given topic.
         */
        public void removeSubscriber(Subscriber subscriber)
        {
            subscribers.remove(subscriber);
        }

        /**
         * @return boolean flag that indicates if there is any current subscribers.
         */
        public boolean hasSubscriptions()
        {
            return !subscribers.isEmpty();
        }

        /**
         * @return The topic name for the current <code>subManager</code> object.
         */
        public String getTopicName()
        {
            return topicName;
        }

        /**
         * @return <code>List</code> of subscribers for the current <code>subManager</code> object.
         */
        public List<Subscriber> getSubscribers()
        {
            return subscribers;
        }
    }

    /**
     * @param topic
     *        The topic name for which we want to get the subscribers.
     * @return <code>List</code> of subscribers for the given topic name.
     */
    public List<Subscriber> getSubscribers(String topic)
    {
        SubscriptionManager sm = subManagerMap.get(topic);

        if(sm != null)
            return sm.getSubscribers();

        return null;
    }

    /**
     * @return boolean flag that indicates if the <code>MTSClient</code> oject is connected to the broker.
     */
    public boolean isConnected()
    {
        return connected;
    }
}
