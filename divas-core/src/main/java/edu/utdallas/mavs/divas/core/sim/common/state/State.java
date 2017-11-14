package edu.utdallas.mavs.divas.core.sim.common.state;

/**
 * This interface describes a state in the simulation.
 * <p>
 * The environment has a state that is constantly changing due to agent, environment object and external events changes during the simulation. Also agents and environment objects have states that are
 * constantly changing during the simulation.
 */
public interface State
{
    /**
     * Gets the id of the state.
     * 
     * @return the state id.
     */
    public int getID();

    /**
     * Changes the id of the state.
     * 
     * @param id
     *        The new state id.
     */
    public void setID(int id);
}
