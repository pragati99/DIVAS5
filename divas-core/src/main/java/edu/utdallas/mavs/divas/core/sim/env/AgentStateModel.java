package edu.utdallas.mavs.divas.core.sim.env;

import java.io.Serializable;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.utils.physics.ActualObjectPolygon;

/**
 * AgentStateModel allows the cell to store additional information about an agent while it is combining actions.
 */
public class AgentStateModel implements Serializable
{
    private static final long   serialVersionUID = -6398344229190777460L;

    private AgentState          state;

    private Vector3f            nextPosition;
    private Vector3f            nextVelocity;
    private ActualObjectPolygon nextBoundingArea;

    /**
     * Constructs the <code>AgentStateModel</code> object given the <code>AgentState</code>.
     * 
     * @param state
     *        The <code>AgentState</code>.
     */
    public AgentStateModel(AgentState state)
    {
        this.state = state;
        nextPosition = state.getPosition().clone();
        nextVelocity = state.getVelocity().clone();
    }

    /**
     * @return The <code>AgentState</code> Id.
     */
    public int getId()
    {
        return state.getID();
    }

    /**
     * Gets the next position for the agent.
     * 
     * @return A <code>Vector3f</code> the represent the next position of the agent.
     */
    public Vector3f getNextPosition()
    {
        return nextPosition;
    }

    /**
     * Gets the <code>AgentState</code> for this agent state model.
     * 
     * @return The <code>AgentState</code>.
     */
    public AgentState getState()
    {
        return state;
    }

    /**
     * Gets the scale of the agent.
     * 
     * @return A <code>Vector3f</code> that represent the agent scale.
     */
    public Vector3f getScale()
    {
        return state.getScale();
    }

    /**
     * Sets the next position and the next bounding area for this agent state.
     * 
     * @param nextPosition
     *        A <code>Vector3f</code> that represent the next position for the agent in
     *        the simulation.
     */
    public void move(Vector3f nextPosition)
    {
        this.nextPosition = nextPosition; 
        this.nextBoundingArea = new ActualObjectPolygon(new Vector2f(nextPosition.x, nextPosition.y), state.getScale());
    }

    /**
     * Undo the last move operation for this agent state.
     * 
     * @see edu.utdallas.mavs.divas.core.sim.env.AgentStateModel#move(Vector3f)
     */
    public void undoMove()
    {
        this.nextPosition = state.getPosition().clone(); // .add(state.getHeading().normalize().mult(-.3f));
        this.nextBoundingArea = state.getBoundingArea();

        // this.nextPosition = state.getPosition().clone().add(state.getHeading().normalize().mult(-.3f));
        // this.nextBoundingArea = PhysicsHelper.createBoundingArea(nextPosition, state.getScale());
    }

    /**
     * Gets the nextBoundingArea.
     * 
     * @return A <code>Rectangle2D</code> for the next bounding area.
     */
    public ActualObjectPolygon getNextBoundingArea()
    {
        return nextBoundingArea;
    }

    /**
     * Update the <code>AgentStateModel</code> with the next state.
     * 
     * @return An <code>AgentState</code> that represents the computed next state.
     */
    public AgentState commitState()
    {
        // store the original position and velocity to calculate velocity and acceleration
        Vector3f origPosition = state.getPosition().clone();
        Vector3f origVelocity = state.getVelocity().clone();

        // TODO: to be more accurate, use tick interval to determine velocity and acceleration wrt seconds
        state.setPosition(nextPosition.clone());
        state.setVelocity(nextPosition.subtract(origPosition));
        state.setAcceleration(nextVelocity.subtract(origVelocity));
        state.updateBoundings();
        return state;
    }
}
