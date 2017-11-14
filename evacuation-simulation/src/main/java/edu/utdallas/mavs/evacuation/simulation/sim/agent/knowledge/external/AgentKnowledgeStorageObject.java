package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;

/**
 * Agent Knowledge Storage Object - Stores critical information regarding agents.
 */
public class AgentKnowledgeStorageObject extends VirtualKnowledgeStorageObject
{
    private static final long serialVersionUID = 1L;

    Vector3f                  velocity;

    /**
     * GEts the ID, scale and position
     * 
     * @param agent
     *        an agent state to create the agent KSO from
     */
    public AgentKnowledgeStorageObject(AgentState agent)
    {
        super(agent.getID(), agent.getScale(), agent.getPosition(), agent.isCollidable(), agent.getBoundingArea());
        this.velocity = agent.getVelocity();
    }

    public void updateValues(AgentState agent)
    {
        this.scale = agent.getScale();
        this.position = agent.getPosition();
        this.boundingArea = agent.getBoundingArea();
        this.velocity = agent.getVelocity();
    }

    public Vector3f getVelocity()
    {
        return velocity;
    }

    public Vector2f getVelocity2D()
    {
        return new Vector2f(velocity.x, velocity.z);
    }

    public void setVelocity(Vector3f velocity)
    {
        this.velocity = velocity;
    }

    public float getRadius()
    {
        return SimConfig.getInstance().agent_Radius;
    }
}
