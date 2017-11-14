package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import java.io.Serializable;

/**
 * Store important knowledge only.
 */
public abstract class AbstractKnowledgeStorageObject implements Serializable
{
    /**
     * Serializer
     */
    private static final long serialVersionUID = 9064581745286227871L;
    /**
     * ID of stored Object
     */
    int                       id;

    /**
     * Constructor
     * 
     * @param id
     *        The ID
     */
    public AbstractKnowledgeStorageObject(int id)
    {
        this.id = id;
    }

    /**
     * Get the ID
     * 
     * @return The ID
     */
    public int getID()
    {
        return id;
    }

    /**
     * Set the ID
     * 
     * @param id
     *        The ID
     */
    public void setID(int id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object arg0)
    {
        if(arg0 instanceof NeighborKnowledgeStorageObject)
        {
            return getID() == ((NeighborKnowledgeStorageObject) arg0).getID();
        }
        return false;
    }

    @Override
    public int hashCode()
    {

        int hash = 1 + getID();
        return hash;
    }

    @Override
    public String toString()
    {
        return "Id: " + getID();
    }
}
