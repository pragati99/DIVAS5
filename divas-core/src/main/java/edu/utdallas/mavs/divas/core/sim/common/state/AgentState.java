package edu.utdallas.mavs.divas.core.sim.common.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.Collision;
import edu.utdallas.mavs.divas.core.sim.common.percept.AudioPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.SmellPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.utils.collections.LinkedBoundedQueue;

/**
 * This class represents the current state of an agent in the simulation.
 * <p>
 * It contains properties related to the current state of an agent in the environment.
 */
public class AgentState extends VirtualState implements Serializable
{
    private static final long  serialVersionUID = 1L;

    /**
     * The heading of the agent based on its head position.
     */
    // TODO check if we can use rotation instead
    protected Vector3f         heading          = new Vector3f(1, 0, 0);

    /**
     * The type of the agent (i.e. human)
     */
    // FIXME: remove, replace with abstractions
    protected String           agentType        = "";

    /**
     * The type of control used to drive the agent. The agent is autonomous by default but can be driven by the keyboard
     * or a Wiimote.
     */
    protected AgentControlType controlType      = AgentControlType.Autonomous;

    /**
     * A set of tasks the agent knows how to perform.
     */
    protected Set<String>      taskNames        = new HashSet<String>();

    /**
     * Flag indicating if the agent is alive.
     */
    protected boolean          alive            = true;

    /**
     * A buffer of entities to which the agent has collided to recently
     */
    protected Queue<Collision> collisions;

    /**
     * Creates a new agent state.
     */
    public AgentState()
    {
        super(-1);
        this.collisions = new LinkedBoundedQueue<Collision>(5);
    }

    /**
     * Gets the agent type such as human or car.
     * 
     * @return the agent type.
     */
    public String getAgentType()
    {
        return agentType;
    }

    /**
     * Gets the type of control used to drive the agent.
     * 
     * @return the agent control type.
     */
    public AgentControlType getControlType()
    {
        return controlType;
    }

    /**
     * Gets the heading of the agent based on its head position.
     * 
     * @return the agent heading.
     */
    public Vector3f getHeading()
    {
        return heading;
    }

    /**
     * Changes the type of the agent.
     * 
     * @param agentType
     *        The new agent type.
     */
    public void setAgentType(String agentType)
    {
        this.agentType = agentType;
    }

    /**
     * Changes the agent control type.
     * 
     * @param controlType
     *        The new agent control type.
     */
    public void setControlType(AgentControlType controlType)
    {
        this.controlType = controlType;
    }

    /**
     * Changes the heading of the agent.
     * 
     * @param heading
     *        The new heading of the agent.
     */
    public void setHeading(Vector3f heading)
    {
        this.heading = heading;
    }

    /**
     * Changes the agent type, control type, heading, max speed, desired speed, visible distance, field of view, min
     * audible threshold, acoustic emission,
     * smell sensitivity, reach distance, posture, vision algorithm, olfactory enabled and auditory enabled of the agent
     * with such properties from the
     * given agent state.
     * 
     * @param agent
     *        The agent state to copy properties from (i.e., agent type, control type, heading) to this agent state.
     */
    public void copyFrom(AgentState agent)
    {
        super.copyFrom(agent);
        agentType = agent.agentType;
        controlType = agent.controlType;
        heading = agent.heading.clone();
    }

    /**
     * Gets a 2D projection of the position of the agent
     * 
     * @return a 2D vector
     */
    public Vector2f getPosition2D()
    {
        return new Vector2f(position.x, position.z);
    }

    /**
     * Gets a set of tasks the agent knows how to perform.
     * 
     * @return a set of agent tasks.
     */
    public Set<String> getTaskNames()
    {
        return taskNames;
    }

    /**
     * Replace the set of agent tasks with the given set of tasks.
     * 
     * @param taskNames
     *        The new set of agent tasks.
     */
    public void setTaskNames(Set<String> taskNames)
    {
        this.taskNames = taskNames;
    }

    /**
     * A flag indicating if the agent is alive or not.
     * 
     * @return the live status of the agent.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Changes the alive status of the agent.
     * 
     * @param alive
     *        The new alive status for the agent.
     */
    public void setAlive(boolean alive)
    {
        this.alive = alive;
    }

    /**
     * Adds a collision information to the agent state
     * 
     * @param collision
     *        a collision information
     */
    public void addCollision(Collision collision)
    {
        collisions.add(collision);
    }

    /**
     * Return the list of collisions and clean it from the agent state
     * 
     * @return the list of collisions from last cycle
     */
    public List<Collision> getCollisions()
    {
        List<Collision> collisionList = new ArrayList<>();
        Collision c = null;
        while((c = collisions.poll()) != null)
        {
            collisionList.add(c);
        }
        return collisionList;
    }

    /**
     * Checks if this agent can see
     * 
     * @return true if it can see
     */
    public boolean canSee()
    {
        return this instanceof VisionPerceptor;
    }

    /**
     * Checks if this agent can hear
     * 
     * @return true if it can hear
     */
    public boolean canHear()
    {
        return this instanceof AudioPerceptor;
    }

    /**
     * Checks if this agent can smell
     * 
     * @return true if it can smell
     */
    public boolean canSmell()
    {
        return this instanceof SmellPerceptor;
    }
}
