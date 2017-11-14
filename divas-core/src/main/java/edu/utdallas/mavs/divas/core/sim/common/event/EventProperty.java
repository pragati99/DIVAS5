package edu.utdallas.mavs.divas.core.sim.common.event;

import java.io.Serializable;

/**
 * This class represents properties an event can have.
 * <p>
 * Events such as {@link BombEvent} can have properties such as "smoke" that is perceivable by smell senses, "boom" that is perceivable by hearing senses and "fire" that is perceivable by vision
 * senses.
 */
public class EventProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String            type;
    private Sense            sense;
    private float             value;

    /**
     * An enumeration of the possible agent senses.
     */
    public enum Sense
    {
        /**
         * Agent vision sense.
         */
        Vision,
        /**
         * Agent hearing sense.
         */
        Hearing,
        /**
         * Agent smell sense.
         */
        Smell;
    }

    /**
     * Creates a new event property object.
     * 
     * @param type
     *        The type of this event property.
     * @param value
     *        The value for this event property.
     * @param sense
     *        The sense that perceives this event property.
     */
    public EventProperty(String type, float value, Sense sense)
    {
        this.type = type;
        this.value = value;
        this.sense = sense;
    }

    /**
     * Gets the type of this event property.
     * 
     * @return the event property type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Changes the type of this event property.
     * 
     * @param type
     *        The new type of this event property.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Returns the sense that can perceive this event property.
     * 
     * @return The sense that perceives this event property.
     */
    public Sense getSense()
    {
        return sense;
    }

    /**
     * Changes the sense that can perceive this event property.
     * 
     * @param sense
     *        The new sense that perceives this event property.
     */
    public void setSense(Sense sense)
    {
        this.sense = sense;
    }

    /**
     * Gets the value for this event property.
     * 
     * @return The value for this event property.
     */
    public float getValue()
    {
        return value;
    }

    /**
     * Changes the value for this event property.
     * 
     * @param value
     *        The new value for this event property.
     */
    public void setValue(float value)
    {
        this.value = value;
    }

}
