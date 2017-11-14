package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import com.jme3.math.Vector2f;

public class RVOObstacle implements Serializable, Comparable<RVOObstacle>
{
    private static final long serialVersionUID = 1L;

    public RVOObstacle(Vector2f point)
    {
        super();
        this.point = point;
    }

    public RVOObstacle(Vector2f point, Vector2f unitdir)
    {
        super();
        this.point = point;
        this.unitDir = unitdir;
    }

    public RVOObstacle(Vector2f point, RVOObstacle prevObstacle, RVOObstacle nextObstacle)
    {
        super();
        this.point = point;
        this.prevObstacle = prevObstacle;
        this.nextObstacle = nextObstacle;
    }

    public RVOObstacle(Vector2f point, Vector2f unitdir, RVOObstacle prevObstacle, RVOObstacle nextObstacle)
    {
        super();
        this.point = point;
        this.unitDir = unitdir;
        this.prevObstacle = prevObstacle;
        this.nextObstacle = nextObstacle;
    }

    private float       distance;
    private Vector2f    point;
    private Vector2f    unitDir;
    private RVOObstacle prevObstacle;
    private RVOObstacle nextObstacle;
    private int         ID;

    public int getID()
    {
        return ID;
    }

    public void setID(int iD)
    {
        ID = iD;
    }

    public Vector2f getPoint()
    {
        return point;
    }

    public void setPoint(Vector2f point)
    {
        this.point = point;
    }

    public Vector2f getUnitdir()
    {
        return unitDir;
    }

    public void setUnitdir(Vector2f unitdir)
    {
        this.unitDir = unitdir;
    }

    public RVOObstacle getPrevObstacle()
    {
        return prevObstacle;
    }

    public void setPrevObstacle(RVOObstacle prevObstacle)
    {
        this.prevObstacle = prevObstacle;
    }

    public RVOObstacle getNextObstacle()
    {
        return nextObstacle;
    }

    public void setNextObstacle(RVOObstacle nextObstacle)
    {
        this.nextObstacle = nextObstacle;
    }

    public void updateUnitVector()
    {
        unitDir = new Vector2f(nextObstacle.getPoint());
        unitDir.subtractLocal(point);
        unitDir.normalizeLocal();
    }

    public float getDistance()
    {
        return distance;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    @Override
    public int compareTo(RVOObstacle o)
    {
        Float f0 = this.distance;
        Float f1 = o.distance;
        return f0.compareTo(f1);
    }

}

