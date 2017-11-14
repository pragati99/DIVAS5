package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data;

import java.io.Serializable;

import com.jme3.math.Vector3f;

public class EventData implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected Vector3f        origin;
    protected Vector3f        direction;
    
    float                     intensity;

    public Vector3f getOrigin()
    {
        return origin;
    }

    public void setOrigin(Vector3f origin)
    {
        this.origin = origin;
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

    public Vector3f getDirection()
    {
        return direction;
    }

    public void setDirection(Vector3f direction)
    {
        this.direction = direction;
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
