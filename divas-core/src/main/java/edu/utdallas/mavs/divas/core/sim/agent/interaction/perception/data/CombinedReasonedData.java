package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class CombinedReasonedData implements Serializable
{
    private static final long   serialVersionUID = 1L;

    private final static Logger logger           = LoggerFactory.getLogger(CombinedReasonedData.class);

    protected long              eventOccurredTime;

    protected int               eventID;

    protected int               eventCounter;
    protected int               maxEvents;

    protected Vector3f          origin;
    protected Vector3f          direction;

    String                      predicatedName;

    float                       certaintyPercent;

    float                       intensity;

    // Stores Raw Sensed Data

    public CombinedReasonedData(long time, float certaintyPercent, String name)
    {
        this.eventOccurredTime = time;
        this.certaintyPercent = certaintyPercent;
        predicatedName = name;
    }

    public void print()
    {

        if(hasOrigin())
        {
            logger.trace("#1 Time: " + eventOccurredTime + " , Name: " + predicatedName + " , Origin: " + getOrigin() + " , certainty: " + certaintyPercent + " , count: " + eventCounter + " , max: "
                    + maxEvents);
        }
        else if(hasDirection())
        {
            logger.trace("#1 Time: " + eventOccurredTime + " , Name: " + predicatedName + " , Direction: " + getDirection() + " , certainty: " + certaintyPercent + " , count: " + eventCounter
                    + " , max: " + maxEvents);
        }
        else
        {
            logger.trace("#1 Time: " + eventOccurredTime + " , Name: " + predicatedName + " , certainty: " + certaintyPercent + " , count: " + eventCounter + " , max: " + maxEvents);
        }
    }

    public Vector3f getOrigin()
    {
        return origin;
    }

    public void setOrigin(Vector3f origin)
    {
        this.origin = origin;
    }

    public long getEventOccurredTime()
    {
        return eventOccurredTime;
    }

    public void setEventOccurredTime(long eventOccurredTime)
    {
        this.eventOccurredTime = eventOccurredTime;
    }

    public int getEventID()
    {
        return eventID;
    }

    public void setEventID(int eventID)
    {
        this.eventID = eventID;
    }

    public Vector3f getDirection()
    {
        return direction;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
    }

    public String getPredicatedName()
    {
        return predicatedName;
    }

    public void setPredicatedName(String predicatedName)
    {
        this.predicatedName = predicatedName;
    }

    public float getCertaintyPercent()
    {
        return certaintyPercent;
    }

    public void setCertaintyPercent(float certaintyPercent)
    {
        this.certaintyPercent = certaintyPercent;
    }

    public boolean hasOrigin()
    {
        boolean rv = false;

        if(origin != null)
        {
            rv = true;
        }

        return rv;
    }

    public boolean hasDirection()
    {
        boolean rv = false;

        if(direction != null)
        {
            rv = true;
        }

        return rv;
    }

    public int getEventCounter()
    {
        return eventCounter;
    }

    public void setEventCounter(int eventCounter)
    {
        this.eventCounter = eventCounter;
    }

    public int getMaxEvents()
    {
        return maxEvents;
    }

    public void setMaxEvents(int maxEvents)
    {
        this.maxEvents = maxEvents;
    }

    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    /**
     * Get Virtual Object's Position
     * 
     * @return Position
     */
    public Vector2f getPosition2D()
    {
        if(origin != null)
        {
            return new Vector2f(origin.x, origin.z);
        }
        return null;
    }

}
