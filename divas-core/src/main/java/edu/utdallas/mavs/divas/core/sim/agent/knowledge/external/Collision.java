package edu.utdallas.mavs.divas.core.sim.agent.knowledge.external;

import java.io.Serializable;

/**
 * This class represents a collision information for the agent.
 * <p>
 * It contains the id and types of the entity to which the agent has collided.
 */
public class Collision implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The id of the collidable entity
     */
    private int               collidableId;

    /**
     * The type of the collidable entity
     */
    private CollisionType     collidableType;

    /**
     * What was collided with
     */
    public enum CollisionType
    {
        /**
         * An agent
         */
        AGENT,
        /**
         * An env object
         */
        ENVOBJ
    }

    /**
     * Creates a new collision information for the agent
     * 
     * @param collidableId
     * @param collidableType
     */
    public Collision(int collidableId, CollisionType collidableType)
    {
        super();
        this.collidableId = collidableId;
        this.collidableType = collidableType;
    }

    /**
     * Gets the id of the collidable entity
     * 
     * @return id of collidable
     */
    public int getCollidableId()
    {
        return collidableId;
    }

    /**
     * Gets the type of the collidable entity
     * 
     * @return the type of the collidable
     */
    public CollisionType getCollidableType()
    {
        return collidableType;
    }
}
