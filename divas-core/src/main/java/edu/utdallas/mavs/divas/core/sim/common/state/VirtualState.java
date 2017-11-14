package edu.utdallas.mavs.divas.core.sim.common.state;

import java.io.Serializable;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;
import edu.utdallas.mavs.divas.utils.physics.ActualObjectPolygon;
import edu.utdallas.mavs.divas.utils.physics.BoundingShape;
import edu.utdallas.mavs.divas.utils.physics.PhysicsHelper;

/**
 * This class represents a state of a virtual entity in the simulation.
 * <p>
 * It is responsible for containing the properties such as position, velocity, acceleration and scale of the current state for a virtual entity in the simulation. Such properties This class inherits
 * {@link AbstractState}.
 */
public abstract class VirtualState extends AbstractState implements Serializable
{
    private static final long               serialVersionUID = 5674473462178299139L;

    /**
     * Flag indicating if the virtual entity in this state is visualized by the visualizer.
     */
    protected boolean                       visualized       = true;

    /**
     * The position of the virtual entity in this state.
     */
    protected Vector3f                      position         = new Vector3f(0, 0, 0);

    /**
     * The velocity of the virtual entity in this state.
     */
    protected Vector3f                      velocity         = new Vector3f(0, 0, 0);

    /**
     * The acceleration of the virtual entity in this state.
     */
    protected Vector3f                      acceleration     = new Vector3f(0, 0, 0);

    /**
     * The scale of the virtual entity in this state.
     */
    protected Vector3f                      scale            = new Vector3f(0, 0, 0);

    /**
     * The bounding shape of the virtual entity in this state.
     */
    protected BoundingShape                 boundingShape    = BoundingShape.BOX;

    /**
     * The bounding volume of the virtual entity in this state.
     */
    transient protected BoundingVolume      boundingVolume;

    /**
     * The bounding area of the virtual entity in this state.
     */
    transient protected ActualObjectPolygon boundingArea;

    /**
     * Flag indicating if the virtual entity in this state is collidable or not.
     */
    protected boolean                       collidable       = true;

    /**
     * The name of model of the virtual entity in this state.
     */
    protected String                        modelName;

    /**
     * The rotation for the object.
     */
    protected Quaternion                    rotation         = new Quaternion(0, 0, 0, 1);

    /**
     * Creates a new virtual state with the given id.
     * 
     * @param id
     *        The id of the virtual state.
     */
    public VirtualState(int id)
    {
        super(id);
        boundingArea = new ActualObjectPolygon();
    }

    /**
     * Checks if the virtual entity in this state is being visualized by the visualizer.
     * 
     * @return true, if the virtual entity in this state is visualized and false otherwise.
     */
    public boolean isVisualized()
    {
        return visualized;
    }

    /**
     * Changes the virtual entity in this state to visualized or not visualized.
     * 
     * @param alive
     *        a flag indicating if this state is visualized by the visualizer or not.
     */
    public void setVisualized(boolean alive)
    {
        this.visualized = alive;
    }

    /**
     * Gets the model name of the virtual entity in this state.
     * 
     * @return the model name.
     */
    public String getModelName()
    {
        return modelName;
    }

    /**
     * Changes the model name of the virtual entity in this state.
     * 
     * @param modelName
     *        the name for the model of the virtual entity in this state.
     */
    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    /**
     * Gets the velocity of the virtual entity in this state.
     * 
     * @return the velocity.
     */
    public Vector3f getVelocity()
    {
        return velocity;
    }

    /**
     * Gets the 2d velocity of the virtual entity in this state.
     * 
     * @return the 2d velocity.
     */
    public Vector2f getVelocity2D()
    {
        return new Vector2f(velocity.x, velocity.z);
    }

    /**
     * Changes the velocity of the virtual entity in this state.
     * 
     * @param velocity
     *        the velocity of the virtual entity in this state.
     */
    public void setVelocity(Vector3f velocity)
    {
        this.velocity = velocity;
    }

    /**
     * Gets the acceleration of the virtual entity in this state.
     * 
     * @return the acceleration.
     */
    public Vector3f getAcceleration()
    {
        return acceleration;
    }

    /**
     * Changes the acceleration of the virtual entity in this state.
     * 
     * @param acceleration
     *        the acceleration of the virtual entity in this state.
     */
    public void setAcceleration(Vector3f acceleration)
    {
        this.acceleration = acceleration;
    }

    /**
     * Gets the position of the virtual entity in this state.
     * 
     * @return the position.
     */
    public Vector3f getPosition()
    {
        return position;
    }

    /**
     * Changes the position of the virtual entity in this state.
     * 
     * @param position
     *        the position of the virtual entity in this state.
     */
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    /**
     * Gets the scale of the virtual entity in this state.
     * 
     * @return the scale.
     */
    public Vector3f getScale()
    {
        return scale;
    }

    /**
     * Changes the scale of the virtual entity in this state.
     * 
     * @param scale
     *        the scale of the virtual entity in this state.
     */
    public void setScale(Vector3f scale)
    {
        this.scale = scale;
    }

    @Override
    public String toString()
    {
        return Integer.toString(id);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    /**
     * Changes the id, visualized flag, model name, position, velocity, acceleration, bounding area and bounding volume of the
     * virtual entity in this state with such properties from the given virtual state.
     * 
     * @param eo
     *        The virtual state to be used to copy properties from (i.e., id, visualized flag, model name) to this state.
     */
    public void copyFrom(VirtualState eo)
    {
        id = eo.id;
        visualized = eo.visualized;
        modelName = eo.modelName;
        position = eo.position.clone();
        velocity = eo.velocity.clone();
        acceleration = eo.acceleration.clone();
        boundingArea = eo.getBoundingArea();
        boundingVolume = eo.getBoundingVolume();
    }

    /**
     * Updates the bounding volume and bounding area of the virtual entity in this state.
     */
    public void updateBoundings()
    {

        boundingVolume = PhysicsHelper.createBoundingVolume(boundingShape, position, scale);

        if(boundingArea == null)
        {
            boundingArea = new ActualObjectPolygon();
        }
        boundingArea.UpdatePolygon(this.rotation, this.scale, new Vector2f(position.x, position.z));
    }

    /**
     * Gets the bounding area of the virtual entity in this state.
     * 
     * @return the bounding area.
     */
    public ActualObjectPolygon getBoundingArea()
    {
        if(boundingArea == null)
        {
            boundingArea = new ActualObjectPolygon();
        }
        boundingArea.UpdatePolygon(this.rotation, this.scale, new Vector2f(position.x, position.z));
        return boundingArea;
    }

    /**
     * Gets the bounding volume of the virtual entity in this state.
     * 
     * @return the bounding volume.
     */
    public BoundingVolume getBoundingVolume()
    {
        if(boundingVolume == null)
        {
            boundingVolume = PhysicsHelper.createBoundingVolume(boundingShape, position, scale);
        }
        return boundingVolume;
    }

    /**
     * Sets the {@link BoundingShape} of the object
     * 
     * @param boundingShape
     */
    public void setBoundingShape(BoundingShape boundingShape)
    {
        this.boundingShape = boundingShape;
    }

    /**
     * Determines if the environment object intersects a collision {@link Ray}.
     * The collisions results are stored in the results argument.
     * 
     * @param collisionRay
     *        a collision segment to test intersection with the environment object
     * @param results
     *        stores the collision results
     */
    public void intersects(Ray collisionRay, CollisionResults results)
    {
        getBoundingVolume().collideWith(collisionRay, results);
    }

    /**
     * Changes the virtual entity in this state to be collidable or not collidable.
     * 
     * @param collidable
     *        Flag indicating if the virtual entity in this state is collidable or not
     */
    public void setCollidable(boolean collidable)
    {
        this.collidable = collidable;
    }

    /**
     * Flag indicating if the virtual entity in this state is collidable or not
     * 
     * @return true, if the virtual entity in this state is collidable and false, otherwise.
     */
    public boolean isCollidable()
    {
        return collidable;
    }

    /**
     * Checks if the bounding area of this entity contains the x and z coordinates of a 3D vector representing a position
     * 
     * @param vec
     *        a vector representing a position
     * @return true if the bounding area contains the vector. Otherwise, false.
     */
    public boolean contains2D(Vector3f vec)
    {
        Point p = new GeometryFactory().createPoint(new Coordinate(vec.x, vec.z));
        return boundingArea.contains(p);
    }

    /**
     * Checks if this state is visible
     * 
     * @return true if visible
     */
    public boolean isVisible()
    {
        return this instanceof Visible;
    }

    /**
     * Checks if this state is audible
     * 
     * @return true if audible
     */
    public boolean isAudible()
    {
        return this instanceof Audible;
    }

    /**
     * Checks if this state is smellable
     * 
     * @return true if smellable
     */
    public boolean isSmellable()
    {
        return this instanceof Smellable;
    }

    /**
     * Gets the rotation of the environment object.
     * 
     * @return the env object rotation.
     */
    public Quaternion getRotation()
    {
        return rotation;
    }

    /**
     * Changes the rotation of the environment object. This is used when modifying the environment object rotation using the visualizer.
     * 
     * @param rotation
     *        the new env object rotation.
     */
    public void setRotation(Quaternion rotation)
    {
        this.rotation = rotation;
    }
}
