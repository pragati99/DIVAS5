/*
 * File URL : $HeadURL: https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/event/EnvEvent.java $
 * Revision : $Rev: 627 $
 * Last modified at: $Date: 2010-11-11 22:21:43 -0600 (Thu, 11 Nov 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector3f;

public class ReasonedData implements Serializable
{
    private static final long   serialVersionUID = 1L;

    private final static Logger logger           = LoggerFactory.getLogger(ReasonedData.class);

    protected long              eventOccurredTime;

    protected int               eventID;

    protected Vector3f          origin;
    protected Vector3f          direction;

    String                      predicatedName;
    String                      predicatedName2;
    String                      predicatedName3;

    String                      reasonedType     = null;
    String                      reasonedType2    = null;
    String                      reasonedType3    = null;

    float                       certaintyPercent;

    float                       certaintyPercent2;
    float                       certaintyPercent3;

    float                       intensity;
    
    // Stores Reasoned Sensed Data

    public ReasonedData(long time, float certaintyPercent, String name, String reasonedType)
    {
        this.eventOccurredTime = time;
        this.certaintyPercent = certaintyPercent;
        this.reasonedType = reasonedType;
        predicatedName = name;
        certaintyPercent2 = 0;
        certaintyPercent3 = 0;
        predicatedName2 = null;
        predicatedName3 = null;
    }

    public void print()
    {
        logger.trace("Type: {}", reasonedType);
        logger.trace("#1 Time: " + eventOccurredTime + " , Name: " + predicatedName + " , certainty: " + certaintyPercent);

        if(predicatedName2 != null)
        {
            logger.trace("#2 Time: " + eventOccurredTime + " , Name: " + predicatedName2 + " , certainty: " + certaintyPercent2);
        }

        if(predicatedName3 != null)
        {
            logger.trace("#3 Time: " + eventOccurredTime + " , Name: " + predicatedName3 + " , certainty: " + certaintyPercent3);
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

    public String getPredicatedName2()
    {
        return predicatedName2;
    }

    public void setPredicatedName2(String predicatedName2)
    {
        this.predicatedName2 = predicatedName2;
    }

    public String getPredicatedName3()
    {
        return predicatedName3;
    }

    public void setPredicatedName3(String predicatedName3)
    {
        this.predicatedName3 = predicatedName3;
    }

    public float getCertaintyPercent()
    {
        return certaintyPercent;
    }

    public void setCertaintyPercent(float certaintyPercent)
    {
        this.certaintyPercent = certaintyPercent;
    }

    public float getCertaintyPercent2()
    {
        return certaintyPercent2;
    }

    public void setCertaintyPercent2(float certaintyPercent2)
    {
        this.certaintyPercent2 = certaintyPercent2;
    }

    public float getCertaintyPercent3()
    {
        return certaintyPercent3;
    }

    public void setCertaintyPercent3(float certaintyPercent3)
    {
        this.certaintyPercent3 = certaintyPercent3;
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

    public boolean hasPredicatedName2()
    {
        boolean rv = false;

        if(predicatedName2 != null)
        {
            rv = true;
        }

        return rv;
    }

    public boolean hasPredicatedName3()
    {
        boolean rv = false;

        if(predicatedName3 != null)
        {
            rv = true;
        }

        return rv;
    }

    public String getReasonedType()
    {
        return reasonedType;
    }

    public void setReasonedType(String reasonedType)
    {
        this.reasonedType = reasonedType;
    }

    public String getReasonedType2()
    {
        return reasonedType2;
    }

    public void setReasonedType2(String reasonedType2)
    {
        this.reasonedType2 = reasonedType2;
    }

    public String getReasonedType3()
    {
        return reasonedType3;
    }

    public void setReasonedType3(String reasonedType3)
    {
        this.reasonedType3 = reasonedType3;
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
