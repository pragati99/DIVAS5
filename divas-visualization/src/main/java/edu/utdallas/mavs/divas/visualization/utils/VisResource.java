package edu.utdallas.mavs.divas.visualization.utils;

import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes a visualizer resource.
 * 
 * @param <T> the type of the object associated with the visualizer resource
 */
public class VisResource<T>
{
    /**
     * The image path for resource.
     */
    protected String           image       = "";

    /**
     * The name of the resource.
     */
    protected String           name        = "";

    /**
     * The type of the resource.
     */
    protected String           type        = "";

    /**
     * The description associated with the resource.
     */
    protected String           description = "";

    /**
     * The category of this resource.
     */
    protected ResourceCategory category;

    /**
     * Some object associated with this resource.
     */
    protected T                object;

    /**
     * An enumeration of categories for visualizer resources
     */
    public enum ResourceCategory
    {
        AGENT, ENV_OBJ, EVENT
    }

    /**
     * Constructs a new visualizer resource
     * 
     * @param image the image associated with the resource
     * @param name the name of the resource
     * @param type the type of the resource
     * @param description the description of the resource
     * @param object the object associated with the resource
     */
    public VisResource(String image, String name, String type, String description, T object)
    {
        assert (object instanceof AgentState);
        assert (object instanceof EnvObjectState);
        assert (object instanceof EnvEvent);

        this.image = image;
        this.name = name;
        this.type = type;
        this.description = description;
        this.object = object;
        this.category = getCategory(object);
    }

    /**
     * Gets the image of this resource
     * 
     * @return the resource's image
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Gets the name of this resource
     * 
     * @return the resource's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the type of this resource
     * 
     * @return the resource's type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets the description of this resource
     * 
     * @return the resource's description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets the category of this resource
     * 
     * @return the resource's category
     */
    public ResourceCategory getCategory()
    {
        return category;
    }

    /**
     * Gets the object of this resource
     * 
     * @return the resource's object
     */
    public T getObject()
    {
        return object;
    }

    /**
     * Infers this object category from its object's type
     * 
     * @param object
     * @return
     */
    private ResourceCategory getCategory(T object)
    {
        if(object instanceof AgentState)
            return ResourceCategory.AGENT;
        if(object instanceof EnvObjectState)
            return ResourceCategory.ENV_OBJ;
        if(object instanceof EnvEvent)
            return ResourceCategory.EVENT;
        return null;
    }
}
