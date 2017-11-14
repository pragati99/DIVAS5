package edu.utdallas.mavs.divas.core.sim.common.state;

import java.io.Serializable;

/**
 * This class describes an abstract state.
 */
public abstract class AbstractState implements State, Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;

    /**
     * The id of the state.
     */
    protected int             id;

    /**
     * Construct a new abstract state.
     * 
     * @param id
     *        The id of the state.
     */
    public AbstractState(int id)
    {
        super();
        this.id = id;
    }

    /**
     * Gets the id of the state.
     * 
     * @return the state id.
     */
    @Override
    public int getID()
    {
        return id;
    }

    /**
     * Changes the id of the state.
     * 
     * @param id
     *        The new state id.
     */
    @Override
    public void setID(int id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object arg0)
    {
        if(arg0 instanceof AbstractState)
            return this.id == ((AbstractState) arg0).id;
        return false;
    }

}
