package edu.utdallas.mavs.divas.mts;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.mts.MTSClient.SubscriptionManager;
import edu.utdallas.mavs.divas.utils.ByteSerializer;

/**
 * The message listener for JMS messages which interprets JMS messages and
 * forwards them as <code>MTSPayload</code> objects to the <code>subscriber</code>.
 */
public class MTSPayloadListener implements MessageListener
{
    private final static Logger logger = LoggerFactory.getLogger(MTSPayloadListener.class);

    /**
     * The object that has subscribed to the topic and wishes to receive
     * messages sent on that topic.
     */
    SubscriptionManager         subManager;

    /**
     * The default constructor sets the subscriber for the subscribed topic.
     * 
     * @param subManager
     *        the subscriber wishing to receive messages sent on the
     *        subscribed topic
     */
    public MTSPayloadListener(SubscriptionManager subManager)
    {
        this.subManager = subManager;
    }

    /**
     * This method is is an implementation of the <code>javax.jms.MessageListener</code> interface and is invoked by the
     * JMS broker when a message is received on a subscribed topic. When
     * invoked, this method forwards the message as a <code>MTSPayload</code> to the <code>subscriber</code>'s <code>messageReceived(MTSPayload)</code> method.
     * <p>
     * If the message is not of the <code>javax.jms.ObjectMessage</code> type, an error message is printed.
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(Message message)
    {
        try
        {
            if(message instanceof javax.jms.ObjectMessage)
            {
                Object obj = ((ObjectMessage) message).getObject();
                sendToConsumers(message, obj);
            }
            else if(message instanceof ActiveMQBytesMessage)
            {
                ActiveMQBytesMessage msg = (ActiveMQBytesMessage) message;
                byte[] bytes = new byte[(int) msg.getBodyLength()];
                msg.readBytes(bytes);
                try
                {
                    Object obj = ByteSerializer.deserialize(bytes);
                    sendToConsumers(message, obj);
                }
                catch(ClassNotFoundException | IOException e)
                {
                    logger.error("Error deserealizing byte array message.", e);
                }
            }
            else
            {
                logger.error("Message of wrong type: {}", message.getClass().getName());
            }
        }
        catch(JMSException e)
        {
            logger.error("JMSException in onMessage(): {}", e.toString());
        }
    }

    /**
     * Relay the message to consumers
     * 
     * @param message the message received
     * @param obj the content of the message
     * @throws JMSException
     */
    protected void sendToConsumers(Message message, Object obj) throws JMSException
    {
        if(obj instanceof MTSPayload)
        {
            String topicName = "";
            if(message.getJMSDestination() instanceof javax.jms.Topic)
                topicName = ((Topic) (message.getJMSDestination())).getTopicName();

            synchronized(subManager.getSubscribers())
            {
                for(Subscriber subscriber : subManager.getSubscribers())
                    subscriber.messageReceived(topicName, (MTSPayload) obj);
            }            
        }
    }
}
