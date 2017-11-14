package edu.utdallas.mavs.divas.core.sim.env;

import java.io.Serializable;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;

/**
 * This class describes the external stimuli information.
 */
public class ExternalStimulus implements Serializable
{
    private static final long serialVersionUID = 1L;
    private Object            stimulus;
    private ExternalCommand   command;

    /**
     * Enumeration of external command types
     */
    public enum ExternalCommand
    {
        /**
         * Create entity command
         */
        CREATE,
        /**
         * Destroy entity command
         */
        DESTROY,
        /**
         * Modify entity command
         */
        MODIFY,
        /**
         * Trigger event command
         */
        TRIGGER,
        /**
         * Remove environment object command
         */
        REMOVE,
        /**
         * Add environment object command
         */
        ADD
    }

    /**
     * Constructs the <code>ExternalStimulus</code> object containing a stimulus object (EnvEvent or VirtualState) and
     * the given <code>command</code> type.
     * 
     * @param stimulus
     *        The stimulus data of the entity.
     * @param command
     *        The <code>command</code> type.
     */
    public ExternalStimulus(VirtualState stimulus, ExternalCommand command)
    {
        super();
        this.stimulus = stimulus;
        this.command = command;
    }

    /**
     * Constructs the <code>ExternalStimulus</code> object containing a stimulus object (EnvEvent, VirtualState, or
     * VirtualAgent) and the given <code>command</code> type.
     * 
     * @param stimulus
     *        The stimulus data of the entity.
     * @param command
     *        The <code>command</code> type.
     */
    public ExternalStimulus(EnvEvent stimulus)
    {
        super();
        this.stimulus = stimulus;
        this.command = ExternalCommand.TRIGGER;
    }

    /**
     * @return The data for this <code>ExternalStimulus</code>.
     */
    public Object getStimulus()
    {
        return stimulus;
    }

    /**
     * @return The <code>ExternalCommand</code> for this <code>ExternalStimulus</code>.
     */
    public ExternalCommand getCommand()
    {
        return command;
    }

    /**
     * The position of this stimulus
     * 
     * @return the stimulus position
     */
    public Vector3f getPosition()
    {
        Vector3f position = new Vector3f();
        if(stimulus instanceof EnvEvent)
        {
            position = ((EnvEvent) stimulus).getOrigin();
        }
        else if(stimulus instanceof VirtualState)
        {
            position = ((VirtualState) stimulus).getPosition();
        }
        return position;
    }

    /**
     * Checks if the stimulus is an event
     * 
     * @return true if the stimulus is an event. Otherwise, false.
     */
    public boolean isEvent()
    {
        return stimulus instanceof EnvEvent;
    }

    /**
     * Checks if the stimulus is a virtual agent
     * 
     * @return true if the stimulus is a virtual agent. Otherwise, false.
     */
    public boolean isAgent()
    {

        return stimulus instanceof AgentState;
    }

    /**
     * Checks if the stimulus is an environment object
     * 
     * @return true if the stimulus is an environment object. Otherwise, false.
     */
    public boolean isEnvObject()
    {
        return stimulus instanceof EnvObjectState;
    }
}
