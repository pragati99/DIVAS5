package edu.utdallas.mavs.divas.core.sim.agent.interaction.communication;

import java.io.Serializable;

/**
 * An agent message
 */
public class AgentMessage implements Serializable
{
    /**
     * serializer ID
     */
    private static final long serialVersionUID = 4717603722837858220L;

    /**
     * Source agent ID
     */
    int                       sourceID;
    /**
     * Destination agent ID
     */
    int                       destinationID;

    /**
     * The message itself
     */
    String                    message;

    /**
     * Type of message
     */
    MessageType               messageType;

    /**
     * Priority of message
     */
    MessagePriority           messagePriority;

    /**
     * Time message was sent.
     */
    long                      timeCycle;

    /**
     * Agent Message Type
     */
    public enum MessageType
    {
        /**
         * Information message
         */
        INFO,
        /**
         * Request Message
         */
        REQUEST
    }

    /**
     * Priority of agent message
     */
    public enum MessagePriority
    {
        /**
         * Low priority
         */
        LOW,
        /**
         * Medium priority
         */
        MEDIUM,
        /**
         * High priority
         */
        HIGH
    }

    /**
     * Create an agent message
     * 
     * @param sourceID
     *        The source agent ID
     * @param destinationID
     *        The Destination agent ID
     * @param message
     *        The message itself
     */
    public AgentMessage(int sourceID, int destinationID, String message)
    {
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.message = message;
    }

    /**
     * Create an agent message
     * 
     * @param sourceID
     *        The source agent ID
     * @param destinationID
     *        The Destination agent ID
     * @param timeCycle
     *        Simulaiton Time Cycle
     * @param message
     *        The message itself
     */
    public AgentMessage(int sourceID, int destinationID, long timeCycle, String message)
    {
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.message = message;
        this.timeCycle = timeCycle;
    }

    /**
     * Create an agent message
     * 
     * @param sourceID
     *        The source agent ID
     * @param destinationID
     *        The Destination agent ID
     * @param timeCycle
     *        Simulaiton Time Cycle
     * @param message
     *        The message itself
     * @param messageType
     *        The message type
     */
    public AgentMessage(int sourceID, int destinationID, long timeCycle, String message, MessageType messageType)
    {
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.message = message;
        this.messageType = messageType;
        this.timeCycle = timeCycle;
    }

    /**
     * Create an agent message
     * 
     * @param sourceID
     *        The source agent ID
     * @param destinationID
     *        The Destination agent ID
     * @param timeCycle
     *        Simulaiton Time Cycle
     * @param message
     *        The message itself
     * @param messageType
     *        The message type
     * @param messagePriority
     *        The message Priority
     */
    public AgentMessage(int sourceID, int destinationID, long timeCycle, String message, MessageType messageType, MessagePriority messagePriority)
    {
        this.sourceID = sourceID;
        this.destinationID = destinationID;
        this.message = message;
        this.messageType = messageType;
        this.messagePriority = messagePriority;
        this.timeCycle = timeCycle;
    }

    /**
     * Get source agent ID
     * 
     * @return Source agent ID
     */
    public int getSourceID()
    {
        return sourceID;
    }

    /**
     * Set source agent ID
     * 
     * @param sourceID
     *        Source agent ID
     */
    public void setSourceID(int sourceID)
    {
        this.sourceID = sourceID;
    }

    /**
     * Get destination agent ID
     * 
     * @return Destination agent ID
     */
    public int getDestinationID()
    {
        return destinationID;
    }

    /**
     * Set Destination agent ID
     * 
     * @param destinationID
     *        Destination agent ID
     */
    public void setDestinationID(int destinationID)
    {
        this.destinationID = destinationID;
    }

    /**
     * Get The message itself
     * 
     * @return The message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Set The message itself
     * 
     * @param message
     *        The message itself
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

}
