package edu.utdallas.mavs.divas.mts;

/**
 * The <code>Subscriber</code> interface must be implemented by any class
 * wishing to receive MTS messages on subscribed topics.
 */
public interface Subscriber
{
    /**
     * This method is called whenever a message is received on any of the
     * subscribed topics.
     * 
     * @param topic
     *        the topic over which the message was sent
     * @param payload
     *        the message payload
     */
    public abstract void messageReceived(String topic, MTSPayload payload);
}
