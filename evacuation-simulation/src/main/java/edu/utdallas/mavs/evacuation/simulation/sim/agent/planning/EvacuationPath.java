package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology.Door;

/**
 * An evacuation path
 */
public class EvacuationPath implements Serializable
{
    List<Door> path;

    public EvacuationPath()
    {
        super();
        path = null;
    }

    public List<Door> getPath()
    {
        return path;
    }

    public void setPath(List<Door> path)
    {
        this.path = path;
    }

    public void addToPath(Door d)
    {
        path.add(d);
    }

    public Door getNextStep()
    {
        if(path.size() > 0)
        {
            return path.remove(0);
        }
        return null;
    }

}
