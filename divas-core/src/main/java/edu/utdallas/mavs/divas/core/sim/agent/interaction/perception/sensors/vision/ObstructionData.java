package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;

public class ObstructionData
{

    private int id;

    public ObstructionData(int id, BoundingVolume boundingVolume)
    {
        super();
        this.id = id;
        this.boundingVolume = boundingVolume;
    }

    /**
     * The bounding volume of the virtual entity in this state.
     */
    transient protected BoundingVolume boundingVolume;

    /**
     * Gets the bounding volume of the virtual entity in this state.
     * 
     * @return the bounding volume.
     */
    public BoundingVolume getBoundingVolume()
    {
        return boundingVolume;
    }

    /**
     * Determines if the environment object collides with a collision {@link Ray}.
     * The collisions results are stored in the results argument.
     * 
     * @param collisionRay
     *        a collision segment to test intersection with the environment object
     * @param results
     *        stores the collision results
     */
    public void collideWith(Ray collisionRay, CollisionResults results)
    {
        getBoundingVolume().collideWith(collisionRay, results);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

}
