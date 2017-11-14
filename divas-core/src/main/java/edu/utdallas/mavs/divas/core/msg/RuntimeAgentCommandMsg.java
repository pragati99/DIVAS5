package edu.utdallas.mavs.divas.core.msg;

import java.io.Serializable;

/**
 * This class describes the messages for runtime agent commands.
 */
public class RuntimeAgentCommandMsg implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Enumeration of runtime agent commands
     */
    public enum RuntimeAgentCommand
    {
        /**
         * Set the maximum velocity command
         */
        SET_MAX_VELOCITY,
        /**
         * Set the desired velocity command
         */
        SET_DESIRED_VELOCITY,
        /**
         * Set the vision algorithm command
         */
        SET_VISION_ALG,
        /**
         * Set the visible distance command
         */
        SET_VISIBLE_DISTANCE,
        /**
         * Set the field of view command
         */
        SET_FOV,
        /**
         * Enable the auditory sensor command
         */
        ENABLE_AUDITORY_SENSOR,
        /**
         * Disable the auditory sensor command
         */
        DISABLE_AUDITORY_SENSOR,
        /**
         * Set the minimum audible threshold command
         */
        SET_MIN_AUDIBLE_THRESHOLD,
        /**
         * Enable the olfactory sensor command
         */
        ENABLE_OLFACTORY_SENSOR,
        /**
         * Disable the olfactory sensor command
         */
        DISABLE_OLFACTORY_SENSOR,
        /**
         * Set the smell sensitivity command
         */
        SET_SMELL_SENSITIVITY,
        /**
         * Set agent posture command
         */
        SET_POSTURE,
        /**
         * Set agent control type command
         */
        SET_CONTROL_TYPE,

        /**
         * Update the agent goal
         */
        UPDATE_AGENT_GOAL
    }

    private RuntimeAgentCommand command;
    private int                 agentID;
    private Object              data;

    /**
     * Constructs the <code>RuntimeAgentCommandMsg</code> object.
     * 
     * @param command
     *        The <code>RuntimeAgentCommand</code> to be set in the message.
     * @param agentID
     *        The agent ID
     * @param data
     *        Any data to be passed along with the command, depending on the command type.
     */
    public RuntimeAgentCommandMsg(RuntimeAgentCommand command, int agentID, Object data)
    {
        this.command = command;
        this.agentID = agentID;
        this.data = data;
    }

    /**
     * @return The <code>Command</code> stored in the message.
     */
    public RuntimeAgentCommand getCommand()
    {
        return command;
    }

    /**
     * Sets the given <code>Command</code> in this message.
     * 
     * @param command
     *        The <code>Command</code> to be set.
     */
    public void setCommand(RuntimeAgentCommand command)
    {
        this.command = command;
    }

    /**
     * @return The agent ID in this message.
     */
    public int getAgentID()
    {
        return agentID;
    }

    /**
     * Sets the agent Id to this message.
     * 
     * @param agentID
     *        The agent ID to be set in this message.
     */
    public void setAgentID(int agentID)
    {
        this.agentID = agentID;
    }

    /**
     * @return The data stored in this message.
     */
    public Object getData()
    {
        return data;
    }

    /**
     * @return The data stored in this message.
     */
    public String getDataAsString()
    {
        return data.toString();
    }

    /**
     * Sets the data to this message.
     * 
     * @param data
     */
    public void setData(Object data)
    {
        this.data = data;
    }
}
