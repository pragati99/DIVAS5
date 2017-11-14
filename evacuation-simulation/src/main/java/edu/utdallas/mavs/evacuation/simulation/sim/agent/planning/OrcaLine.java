package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import com.jme3.math.Vector2f;

public class OrcaLine implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public Vector2f point;
    public Vector2f direction;

    public OrcaLine()
    {
        super();
        this.point = new Vector2f();
        this.direction = new Vector2f();
    }

    public OrcaLine(OrcaLine orcaLine)
    {
        super();
        this.point = new Vector2f(orcaLine.point);
        this.direction = new Vector2f(orcaLine.direction);
    }
}
