package edu.utdallas.mavs.divas.mts;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.utils.Multithreader;
import edu.utdallas.mavs.divas.utils.Multithreader.ThreadPoolType;

/**
 * <code>LocalMTS</code> object is used by the <code>CommunicationModule</code> to publish messages using local
 * memory reference instead of using the TCP protocol.
 * <p>
 * This technique is faster for publishing the messages than using the activeMQ, but it can only be used when we send messages within the same java process.
 */
public class LocalMTS extends Observable
{
    private final static Logger  logger        = LoggerFactory.getLogger(LocalMTS.class);
    private static Multithreader multithreader = new Multithreader("LocalMTS", ThreadPoolType.FIXED);

    /**
     * Sends <code>payload</code> to the subscribers of the given topic.
     * 
     * @param payload
     *        the message payload to be sent to subscribers of the topic
     * @param topicName
     *        the name of the topic on which the message will be sent
     */
    public synchronized void publishMessage(MTSPayload payload, String topicName)
    {
        logger.debug("Publishing message {}", topicName);
        setChanged();
        notifyObservers(new Message(topicName, payload));
    }

    /**
     * Adds a subscription to the given topic. Messages received on this topic will be sent to the <code>subscriber</code> object.
     * 
     * @param id
     * @param topicName
     *        The name of the topic to add a subscription to
     * @param subscriber
     *        An implementation of the <code>ISubscriber</code> interface which wants
     *        to subscribe to the given topic.
     */
    public void addSubscriptionTopic(int id, String topicName, Subscriber subscriber)
    {
        Topic topic = new Topic(id, topicName);
        logger.debug("Adding observer: " + topic);
        addObserver(new LocalObserver(subscriber, topicName));
    }

    /**
     * Terminates the <code>LocalMTS</code> services, by deleting the observers list and terminating the multithreader.
     */
    public void terminate()
    {
        deleteObservers();
        multithreader.terminate();
    }

    class Topic
    {

        int    id;
        String topicName;

        public Topic(int id, String topicName)
        {
            this.id = id;
            this.topicName = topicName;
        }

        @Override
        public boolean equals(Object o)
        {
            Topic t = (Topic) o;
            return t.id == id && t.topicName.equals(topicName);
        }

        @Override
        public int hashCode()
        {
            int hash = 1;
            hash = hash * 17 + id;
            hash = hash * 31 + topicName.hashCode();
            return hash;
        }

        @Override
        public String toString()
        {
            return "Id: " + id + " topic name: " + topicName;
        }

    }

    class Message
    {
        public String     topicName;
        public MTSPayload payload;

        public Message(String topicName, MTSPayload payload)
        {
            this.topicName = topicName;
            this.payload = payload;
        }

        public String getTopicName()
        {
            return this.topicName;
        }

        public MTSPayload getPayload()
        {
            return payload;
        }
    }

    class LocalObserver implements Observer
    {

        Subscriber subscriber;
        String     topicName;

        public LocalObserver(Subscriber subscriber, String topicName)
        {
            super();
            this.subscriber = subscriber;
            this.topicName = topicName;
        }

        @Override
        public void update(Observable o, Object arg)
        {
            Message m = (Message) arg;

            if(m.getTopicName().equals(this.topicName))
            {
                multithreader.execute(getExecutableTask(m));
            }
        }

        private Runnable getExecutableTask(final Message message)
        {
            Runnable task = new Runnable()
            {
                @Override
                public void run()
                {
                    if(subscriber == null)
                    {
                        deleteObserver(LocalObserver.this);
                        return;
                    }
                    logger.debug("Relaying message {}.", message.topicName);
                    subscriber.messageReceived(message.getTopicName(), message.getPayload());
                }
            };

            return task;
        }
    }
}
