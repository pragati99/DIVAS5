package edu.utdallas.mavs.divas.core.sim.common.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents the current state of an environment object in the simulation.
 * <p>
 * It contains properties related to the current state of an environment object in the environment.
 */
public class EnvObjectState extends VirtualState implements Visible, Serializable
{
    private static final long          serialVersionUID = -2950808581541129266L;

    /**
     * Description for the env object.
     */
    protected String                   description      = "";

    /**
     * The type of the env object (i.e. 3DModel).
     */
    protected String                   type             = "";

    /**
     * The material of the env object, if applicable (i.e. brick).
     */
    protected String                   material         = "";

    /**
     * The name of the env object.
     */
    protected String                   name             = "";

    /**
     * The image path for the env object.
     */
    protected String                   image            = "";

    /**
     * List of points surrounding the env object's shape.
     */
    transient protected List<Vector3f> points;

    /**
     * Flag indicating if the env object is on fire or not.
     */
    protected boolean                  onFire           = false;

    /**
     * Creates a new environment object state.
     */
    public EnvObjectState()
    {
        super(-1);
    }

    /**
     * Gets the description of the env object.
     * 
     * @return the env object description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Changes the description of the env object.
     * 
     * @param description
     *        the new description for the env object.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the type of the env object.
     * 
     * @return the env object type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Changes the type of the env object.
     * 
     * @param type
     *        the new type for the env object.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Gets the material of the env object.
     * 
     * @return the env object material.
     */
    public String getMaterial()
    {
        return material;
    }

    /**
     * Changes the material of the env object.
     * 
     * @param material
     *        the new material for the env object.
     */
    public void setMaterial(String material)
    {
        this.material = material;
    }

    /**
     * Gets the name of the env object.
     * 
     * @return the env object name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Changes the name of the env object.
     * 
     * @param name
     *        the new name for the env object.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the thumbnail image of the env object.
     * 
     * @return the env object image.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Changes the thumbnail image of the env object.
     * 
     * @param image
     *        the new image path for the env object.
     */
    public void setImage(String image)
    {
        this.image = image;
    }

    /**
     * Flag indicating if the environment object is on fire.
     * 
     * @return true if the env object is on fire and false otherwise.
     */
    public boolean isOnFire()
    {
        return onFire;
    }

    /**
     * Changes the flag indicating if the environment object is on fire or not.
     * 
     * @param onFire
     *        The flag indicating if the environment object is on fire.
     */
    public void setOnFire(boolean onFire)
    {
        this.onFire = onFire;
    }

    @Override
    public Vector3f getVisiblePosition()
    {
        return position;
    }

    @Override
    public Vector3f getVisibleScale()
    {
        return scale;
    }

    /**
     * Changes the description, type, material, rotation, available, onFire and scale of the environment object with such properties from the
     * given env object state.
     * 
     * @param eo
     *        The environment object state to copy properties from (i.e., description, type, material) to this environment object state.
     */
    public void copyFrom(EnvObjectState eo)
    {
        super.copyFrom(eo);

        description = eo.description;
        type = eo.type;
        material = eo.material;
        rotation = eo.rotation;
        onFire = eo.onFire;
        scale = eo.scale;
    }

    @Override
    public synchronized void updateBoundings()
    {
        super.updateBoundings();
        // updates points
        // calculate the 8 points of the AABB
        if(points == null || points.size() < 9)
        {
            points = new ArrayList<Vector3f>();
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
            points.add(new Vector3f());
        }
        points.get(1).set((position.x - scale.x), (position.y - scale.y), (position.z - scale.z));
        points.get(2).set((position.x - scale.x), (position.y - scale.y), (position.z + scale.z));
        points.get(3).set((position.x - scale.x), (position.y + scale.y), (position.z - scale.z));
        points.get(4).set((position.x - scale.x), (position.y + scale.y), (position.z + scale.z));
        points.get(5).set((position.x + scale.x), (position.y - scale.y), (position.z - scale.z));
        points.get(6).set((position.x + scale.x), (position.y - scale.y), (position.z + scale.z));
        points.get(7).set((position.x + scale.x), (position.y + scale.y), (position.z - scale.z));
        points.get(8).set((position.x + scale.x), (position.y + scale.y), (position.z + scale.z));
    }

    @Override
    public String toString()
    {
        return String.format("%s[%s]", name, type);
    }

    /**
     * Gets a list of points surrounding the env object's shape.
     * 
     * @return the list of points.
     */
    public List<Vector3f> getPoints()
    {
        if(points == null)
        {
            updateBoundings();
        }
        return points;
    }

    /**
     * Gets a point surrounding the env object's shape.
     * 
     * @param number
     *        the point number to get
     * @return the list of points.
     */
    public Vector3f getPoint(int number)
    {
        if(points == null || points.size() < 9)
        {
            updateBoundings();
        }
        return points.get(number);
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
        BoundingVolume BV = getBoundingVolume();
        BV.transform(new Transform(rotation));
        BV.collideWith(collisionRay, results);
    }

    public EnvObjectState copy()
    {
        EnvObjectState copy = new EnvObjectState();
        copy.setPosition(position.clone());
        copy.setModelName(modelName);
        copy.setMaterial(material);
        copy.setScale(scale.clone());
        copy.setRotation(rotation.clone());
        copy.setType(type);
        return copy;
    }

}
