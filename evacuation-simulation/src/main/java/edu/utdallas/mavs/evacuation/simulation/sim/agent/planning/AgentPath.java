package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.ontology.Thing;

/**
 * An evacuation path
 */
public class AgentPath implements Serializable
{
    private static final long serialVersionUID = 1L;

    List<Thing>               path;

    float                     value;
    Thing                     next;
    float                     distanceFactor;

    public AgentPath()
    {
        super();
        path = null;
    }

    public List<Thing> getPath()
    {
        return path;
    }

    public void setPath(List<Thing> path)
    {
        this.path = path;
    }

    public void removeLast()
    {
        path.remove(0);
    }

    public Thing getFirstStep()
    {

        if(path.size() > 1)
        {
            next = path.get(0);
            return next;
        }
        if(path.size() == 1)
        {
            Thing retVal = path.get(0);
            path = null;
            return retVal;
        }
        return null;
    }

    public Thing getFinalStep()
    {
        if(path.size() > 0)
        {
            next = path.get(path.size() - 1);
            return next;
        }
        return null;
    }

    public Thing getNextStep()
    {
        removeLast();
        if(path.size() > 0)
        {
            next = path.get(0);
            return next;
        }
        path = null;
        return null;
    }

    public Thing getNextStepNoRemove()
    {
        if(path.size() > 0)
        {
            next = path.get(0);
            return next;
        }
        path = null;
        return null;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue(float value)
    {
        this.value = value;
    }

    public float getDistanceFactor()
    {
        return distanceFactor;
    }

    public void setDistanceFactor(float distanceFactor)
    {
        this.distanceFactor = distanceFactor;
    }

}
