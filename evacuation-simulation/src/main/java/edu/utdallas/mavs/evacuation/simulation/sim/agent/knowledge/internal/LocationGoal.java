package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.internal;

import java.io.Serializable;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;

public class LocationGoal extends Goal implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected Vector3f        location;

    public LocationGoal(int utilityValue, Vector3f location)
    {
        super(utilityValue);
        this.location = location;
    }

    public void setGoal(Vector3f location)
    {
        this.location = location;
    }

    public Vector3f getLocation()
    {
        return location;
    }

}
