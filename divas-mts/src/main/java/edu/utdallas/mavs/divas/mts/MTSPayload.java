package edu.utdallas.mavs.divas.mts;

import java.io.Serializable;

/**
 * This class contains the message payload for MTS messages. An <code>MTSPayload</code> object contains a single data object and supporting
 * fields to facilitate the receiver's job in parsing the object. Instances of <code>MTSPayload</code> are encoded (instantiated) by the sender, exchanged
 * by the Java Message Broker, and decoded to extract information. The <code>data</code> attribute contains the primary content of the payload
 * while <code>key</code> informs the receiver how to process the message.
 */
public class MTSPayload implements Serializable
{
    private static final long serialVersionUID = 750200936200151153L;

    /**
     * The message key, an integer used to help the receiver interpret the
     * message. e.g. ACK = 0, ADD = 1, REMOVE = 2, DUPLICATE = 3, etc...
     */
    private int               key              = -1;

    /**
     * The message data, the primary content of the message.
     */
    private Serializable      data             = null;

    /**
     * The default constructor contains no implementation. Use <code>MTSPayload(int, Object)</code> to populate the message
     * payload.
     */
    public MTSPayload()
    {}

    /**
     * Initializes the message payload using the provided information.
     * 
     * @param key
     *        an integer key that may used to pass commands or provide the
     *        receiver with information regarding the handling of the
     *        message
     * @param data
     *        payload object, the primary conent of the message
     */
    public MTSPayload(int key, Serializable data)
    {
        this.key = key;
        this.data = data;
    }

    /**
     * Returns the value of the message's key attribute.
     * 
     * @return the message key
     */
    public int getKey()
    {
        return this.key;
    }

    /**
     * Returns the message's data object.
     * 
     * @return the message data
     */
    public Serializable getData()
    {
        return this.data;
    }

    /**
     * Sets the message's data attribute.
     * 
     * @param data
     *        the new message data
     */
    public void setData(Serializable data)
    {
        this.data = data;
    }

    /**
     * Sets the message's key attribute.
     * 
     * @param key
     *        the new message key
     */
    public void setKey(int key)
    {
        this.key = key;
    }

    /**
     * Returns a formatted string containing the values of the message's
     * attributes.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String dataStr = "null";
        if(data != null)
            dataStr = data.toString();
        return "\n" + "MTS Payload:\n" + "  key: " + key + "\n" + "  value: " + dataStr + "\n";
    }
}
