package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;

/**
 * Raw sensed data perceived by the agent's sensors. (NOT COMBINED)
 */
public class SensedData implements Serializable
{
    private static final long   serialVersionUID = 1L;

    private static final Logger logger           = LoggerFactory.getLogger(SensedData.class);

    /**
     * The time the event occurred
     */
    protected long              eventOccurredTime;

    /**
     * The event's ID
     */
    protected int               eventID;

    /**
     * The origin the event occurred (if known)
     */
    protected Vector3f          origin;

    /**
     * The direction the event occurred (if known)
     */
    protected Vector3f          direction;

    /**
     * The type of event
     */
    protected String            type;
    /**
     * The sense that sensed this event
     */
    protected Sense             sense;
    /**
     * The value of sensed data
     */
    protected float             value;

    /**
     * The trust constant of this sensed data
     */
    protected float             trustConstant;

    /**
     * The certainty percent that this event occurred.
     */
    protected float             certaintyPercent;
    /**
     * The value of sensed data
     */
    protected float             intensity;

    /**
     * Get the certainty percent that this event occurred.
     * 
     * @return the certainty percent that this event occurred.
     */
    public float getCertaintyPercent()
    {
        return certaintyPercent;
    }

    /**
     * Set the certainty percent that this event occurred.
     * 
     * @param certaintyPercent
     *        the certainty percent that this event occurred.
     */
    public void setCertaintyPercent(float certaintyPercent)
    {
        this.certaintyPercent = certaintyPercent;
    }

    /**
     * Create a new sensed data
     */
    public SensedData()
    {
        origin = null;
        direction = null;
    }

    /**
     * Create a new sensed data.
     * 
     * @param time
     *        the time the event was sensed
     * @param type
     *        the type of event that was sensed
     * @param sense
     *        the sense that sensed it
     * @param value
     *        the value of the sensed data
     * @param certaintyPercent
     *        the certainty percent that this event occurred.
     * @param trustConstant
     *        the trust constant
     */
    public SensedData(long time, String type, Sense sense, float value, float certaintyPercent, float trustConstant)
    {
        this.eventOccurredTime = time;
        this.type = type;
        this.sense = sense;
        this.value = value;
        this.certaintyPercent = certaintyPercent;
        this.trustConstant = trustConstant;
        origin = null;
        direction = null;
    }

    /**
     * Get the event's origin.
     * 
     * @return origin
     */
    public Vector3f getOrigin()
    {
        return origin;
    }

    /**
     * Set the event's origin
     * 
     * @param origin
     *        origin
     */
    public void setOrigin(Vector3f origin)
    {
        this.origin = origin;
    }

    /**
     * Get the time the event occurred.
     * 
     * @return time
     */
    public long getEventOccurredTime()
    {
        return eventOccurredTime;
    }

    /**
     * Set the time the event occured.
     * 
     * @param eventOccurredTime
     *        The time
     */
    public void setEventOccurredTime(long eventOccurredTime)
    {
        this.eventOccurredTime = eventOccurredTime;
    }

    /**
     * Get the event's ID
     * 
     * @return the ID
     */
    public int getEventID()
    {
        return eventID;
    }

    /**
     * Set the event's ID
     * 
     * @param eventID
     *        the ID
     */
    public void setEventID(int eventID)
    {
        this.eventID = eventID;
    }

    /**
     * Get the event's direction
     * 
     * @return the direction
     */
    public Vector3f getDirection()
    {
        return direction;
    }

    /**
     * Set the event's direction
     * 
     * @param direction
     *        the direction
     */
    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    /**
     * Get the type of event
     * 
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the type of event
     * 
     * @param type
     *        the type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Get the sense that sensed the event
     * 
     * @return the sense
     */
    public Sense getSense()
    {
        return sense;
    }

    /**
     * Set the sense that sensed the event
     * 
     * @param sense
     *        the sense
     */
    public void setSense(Sense sense)
    {
        this.sense = sense;
    }

    /**
     * Get the value of the sensed data
     * 
     * @return the value
     */
    public float getValue()
    {
        return value;
    }

    /**
     * Set the value of the sensed data
     * 
     * @param value
     *        the value
     */
    public void setValue(float value)
    {
        this.value = value;
    }

    /**
     * Get the trust constant
     * 
     * @return The trust constant
     */
    public float getTrustConstant()
    {
        return trustConstant;
    }

    /**
     * set the trust constant
     * 
     * @param trustConstant
     *        the trust constant
     */
    public void setTrustConstant(float trustConstant)
    {
        this.trustConstant = trustConstant;
    }

    /**
     * whether or not the event has an origin.
     * 
     * @return whether or not the event has an origin.
     */
    public boolean hasOrigin()
    {
        boolean rv = false;

        if(origin != null)
        {
            rv = true;
        }

        return rv;
    }

    /**
     * whether or not the event has a direction.
     * 
     * @return whether or not the event has a direction.
     */
    public boolean hasDirection()
    {
        boolean rv = false;

        if(direction != null)
        {
            rv = true;
        }

        return rv;
    }

    /**
     * For testing, print out this event's data
     */
    public void print()
    {
        logger.trace("Type: " + type);
        logger.trace("#1 Time: " + eventOccurredTime + " , Sense: " + sense.toString() + " , value: " + value + " , certainty: " + certaintyPercent);

        if(origin != null)
        {
            logger.trace("org = " + origin);
        }

        if(direction != null)
        {
            logger.trace("dir = " + direction);
        }
    }
    
    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }
    
}
