package edu.utdallas.mavs.divas.core.sim.agent.knowledge.external;

import java.io.Serializable;

import com.jme3.math.FastMath;

import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;

public class EventPropertyKnowledge implements Serializable
{
    private static final long serialVersionUID = 1L;

    String                    eventName;

    String                    type;
    Sense                     sense;
    float                     min, max;
    float                     alpha;
    float                     R;
    float                     epsilon;

    public EventPropertyKnowledge(String type, Sense sense, float min, float max)
    {
        this.type = type;
        this.sense = sense;
        this.min = min;
        this.max = max;
        alpha = (min + max) / 2;
        R = FastMath.abs(min - max);
        epsilon = R / 2;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Sense getSense()
    {
        return sense;
    }

    public void setSense(Sense sense)
    {
        this.sense = sense;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public float getMin()
    {
        return min;
    }

    public void setMin(float min)
    {
        this.min = min;
    }

    public float getMax()
    {
        return max;
    }

    public void setMax(float max)
    {
        this.max = max;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    public float getR()
    {
        return R;
    }

    public void setR(float r)
    {
        R = r;
    }

    public float getEpsilon()
    {
        return epsilon;
    }

    public void setEpsilon(float epsilon)
    {
        this.epsilon = epsilon;
    }
}
