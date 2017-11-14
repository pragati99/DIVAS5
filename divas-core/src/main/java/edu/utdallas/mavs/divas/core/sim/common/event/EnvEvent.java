package edu.utdallas.mavs.divas.core.sim.common.event;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math.util.FastMath;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;
import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;
import edu.utdallas.mavs.divas.utils.physics.PhysicsHelper;

/**
 * This class represents an external environment event.
 * <p>
 * Classes such as {@link BombEvent} and {@link FireworkEvent} inherits from this class.
 */
public abstract class EnvEvent implements Serializable
{
    private static final long                serialVersionUID      = 1L;
    /**
     * The time the event occurred.
     */
    protected long                           eventOccurredTime;

    /**
     * The current length of time the event has existed for. Measured in simulation cycles.
     */
    protected int                            age;

    /**
     * The maximum length of time the event should exist in the simulation. Measured in simulation cycles.
     */
    protected int                            maxAge                = 100;

    /**
     * The id of the event.
     */
    protected int                            eventID;

    /**
     * The origin of the event.
     */
    protected Vector3f                       origin;

    /**
     * Enabled/disabled flags for smellable sense
     */
    protected boolean                        currentlySmellable;

    /**
     * Enabled/disabled flags for audible sense
     */
    protected boolean                        currentlyAudiable;

    /**
     * Enabled/disabled flags for visible sense
     */
    protected boolean                        currentlyVisible;

    /**
     * The current sound radius of this event
     */
    protected float                          currentSoundRadius;

    /**
     * the current smell radius of this event
     */
    protected float                          currentSmellRadius;

    /**
     * The acoustic intensity of this event
     */
    protected float                          intensity;

    private boolean                          lastAuditoryTick      = false;

    /**
     * The speed of smell constant.
     */
    protected final float                    SPEED_OF_SMELL        = SimConfig.getInstance().speed_Of_Smell;

    /**
     * The maximum distance of smell.
     */
    protected float                          max_smell_distance    = SimConfig.getInstance().max_Smell_Distance;

    /**
     * The speed of sound constant.
     */
    protected final float                    SPEED_OF_SOUND        = SimConfig.getInstance().speed_Of_Sound;

    /**
     * The maximum distance of sound.
     */
    protected float                          max_sound_distance    = SimConfig.getInstance().max_Sound_Distance;

    /**
     * A HashMap that stores the event property type along with the {@link EventProperty} for all properties this
     * environment event has.
     */
    protected HashMap<String, EventProperty> eventProperties       = new HashMap<String, EventProperty>();
    private boolean                          notSmellableNextCycle = false;
    private boolean                          notAudibleNextCycle   = false;

    /**
     * Creates a new environment event object with the given id.
     * 
     * @param eventID
     *        The id of the environment event being created.
     */
    public EnvEvent(int eventID)
    {
        this.eventID = eventID;
        age = 0;
    }

    /**
     * Adds a new vision property to this event
     * 
     * @param type
     *        the type of the newly created property
     * @param value
     *        the value of the newly created property
     */
    protected void addVisionProperty(String type, int value)
    {
        addEventProperty(new EventProperty(type, value, Sense.Vision));
    }

    /**
     * Adds a new smell property to this event
     * 
     * @param type
     *        the type of the newly created property
     * @param value
     *        the value of the newly created property
     */
    protected void addSmellProperty(String type, int value)
    {
        addEventProperty(new EventProperty(type, value, Sense.Smell));
    }

    /**
     * Adds a new hearing property to this event
     * 
     * @param type
     *        the type of the newly created property
     * @param value
     *        the value of the newly created property
     */
    protected void addHearingProperty(String type, int value)
    {
        addEventProperty(new EventProperty(type, value, Sense.Hearing));
    }

    /**
     * Adds a new {@link EventProperty} to this environment event.
     * 
     * @param eventProperty
     *        The eventProperty to be added to this event.
     */
    public void addEventProperty(EventProperty eventProperty)
    {
        eventProperties.put(eventProperty.getType(), eventProperty);
    }

    /**
     * Gets the event property for the given property type.
     * 
     * @param type
     *        The type of event property to be returned.
     * @return The event property with the given type.
     */
    public EventProperty getEventProperty(String type)
    {
        return eventProperties.get(type);
    }

    /**
     * Gets a list of event properties that can be perceived by the given sense.
     * 
     * @param sense
     *        The sense that perceives the list of event properties to be retrieved.
     * @return
     *         The list of event properties perceived by the given sense.
     */
    public List<EventProperty> getEventPropertiesForSense(Sense sense)
    {
        ArrayList<EventProperty> values = new ArrayList<EventProperty>();
        Iterator<EventProperty> iter = eventProperties.values().iterator();

        while(iter.hasNext())
        {
            EventProperty tempEventProperty = iter.next();
            if(tempEventProperty.getSense().equals(sense))
            {
                values.add(tempEventProperty);
            }
        }

        return values;
    }

    /**
     * Gets the event id.
     * 
     * @return The event id.
     */
    public int getID()
    {
        return eventID;
    }

    /**
     * Changes the event id.
     * 
     * @param eventID
     *        The new event id.
     */
    public void setEventID(int eventID)
    {
        this.eventID = eventID;
    }

    /**
     * Gets the time that the event occurred.
     * 
     * @return the time the event occurred.
     */
    public long getEventOccurredTime()
    {
        return eventOccurredTime;
    }

    /**
     * Changes the time that the event occurred.
     * 
     * @param eventOccurredTime
     *        The new time that the event occurred.
     */
    public void setEventOccurredTime(long eventOccurredTime)
    {
        this.eventOccurredTime = eventOccurredTime;
    }

    /**
     * Returns whether the event is smellable or not smellable.
     * 
     * @return true, if the event is smellable and false otherwise.
     */
    public boolean isCurrentlySmellable()
    {
        return currentlySmellable;
    }

    /**
     * Changes the event to be smellable or not smellable.
     * 
     * @param currentlySmellable
     *        A boolean representing if the event is smellable or not smellable.
     */
    public void setCurrentlySmellable(boolean currentlySmellable)
    {
        this.currentlySmellable = currentlySmellable;
    }

    /**
     * Returns whether the event is audible or not audible.
     * 
     * @return true, if the event is audible and false otherwise.
     */
    public boolean isCurrentlyAudiable()
    {
        return currentlyAudiable;
    }

    /**
     * Changes the event to be audible or not audible.
     * 
     * @param currentlyAudiable
     *        A boolean representing if the event is audible or not audible.
     */
    public void setCurrentlyAudible(boolean currentlyAudiable)
    {
        this.currentlyAudiable = currentlyAudiable;
    }

    /**
     * Returns whether the event is visible or not visible.
     * 
     * @return true, if the event is visible and false otherwise.
     */
    public boolean isCurrentlyVisible()
    {
        return currentlyVisible;
    }

    /**
     * Changes the event to be visible or not visible.
     * 
     * @param currentlyVisible
     *        A boolean representing if the event is visible or not visible.
     */
    public void setCurrentlyVisible(boolean currentlyVisible)
    {
        this.currentlyVisible = currentlyVisible;
    }

    /**
     * Gets the visible position of this event
     * 
     * @return the event's visible position
     */
    public Vector3f getVisiblePosition()
    {
        return origin;
    }

    /**
     * Gets the visible scale of this event
     * 
     * @return the event's visible scale
     */
    public Vector3f getVisibleScale()
    {
        return new Vector3f(intensity, intensity, intensity);
    }

    /**
     * Gets the acoustic emission of this event
     * 
     * @return the event's acoustic emission
     */
    public float getAcousticEmission()
    {
        return intensity;
    }

    /**
     * Gets the audible position of this event
     * 
     * @return the event's audible position
     */
    public Vector3f getAudiblePosition()
    {
        return origin;
    }

    /**
     * Gets the smell position of this event
     * 
     * @return the event's smell position
     */
    public Vector3f getSmellPosition()
    {
        return origin;
    }

    /**
     * Gets the current smell radius of this event
     * 
     * @return the event's current smell radius
     */
    public float getCurrentSmellRadius()
    {
        return currentSmellRadius;
    }

    /**
     * Gets the current sound radius of this event
     * 
     * @return the event's current sound radius
     */
    public float getCurrentSoundRadius()
    {
        return currentSoundRadius;
    }

    /**
     * Gets the intensity of the fireworks event.
     * 
     * @return the intensity of the fireworks event.
     */
    public float getIntensity()
    {
        return intensity;
    }

    /**
     * Changes the intensity of the fireworks event.
     * 
     * @param intensity
     *        The new intensity of the fireworks event.
     */
    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    /**
     * Changes the current sound radius of the fireworks event.
     * 
     * @param currentSoundRadius
     *        The new sound radius of the fireworks event.
     */
    public void setCurrentSoundRadius(float currentSoundRadius)
    {
        this.currentSoundRadius = currentSoundRadius;
    }

    /**
     * Changes the current smell radius of the fireworks event.
     * 
     * @param currentSmellRadius
     *        The new smell radius of the fireworks event.
     */
    public void setCurrentSmellRadius(float currentSmellRadius)
    {
        this.currentSmellRadius = currentSmellRadius;
    }

    /**
     * Gets the origin of the event.
     * 
     * @return
     *         The event origin.
     */
    public Vector3f getOrigin()
    {
        return origin;
    }

    /**
     * Changes the origin of the event.
     * 
     * @param origin
     *        The new origin of the event.
     */
    public void setOrigin(Vector3f origin)
    {
        this.origin = origin;
    }

    /**
     * Changes the length of time the event has existed.
     * 
     * @param age
     *        The new length of time for the event.
     */
    public void setAge(int age)
    {
        this.age = age;
    }

    /**
     * Returns the lifetime (the duration) of the event.
     * 
     * @return the length of time the event has existed.
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Gets the maximum length of time the event should exist in the simulation.
     * 
     * @return the maximum length of time the event can exist.
     */
    public int getMaxAge()
    {
        return maxAge;
    }

    /**
     * Returns whether it is the last auditory tick of the event or not.
     * 
     * @return true, if it is the last auditory tick of the event and false otherwise.
     */
    public boolean isLastAuditoryTick()
    {
        return lastAuditoryTick;
    }

    /**
     * Changes the last auditory tick of the event to be true or false.
     * 
     * @param lastAuditoryTick
     *        A boolean representing if it is the last auditory tick of the event or not.
     */
    public void setLastAuditoryTick(boolean lastAuditoryTick)
    {
        this.lastAuditoryTick = lastAuditoryTick;
    }

    /**
     * Determines if this event has reached its max life time.
     * 
     * @return true if the event has reached its max lifetime. False otherwise.
     */
    public boolean hasExpired()
    {
        return age > maxAge;
    }

    /**
     * Process propagation of this event
     */
    public void propagate()
    {
        // processes visible properties of the event
        if(this instanceof Visible)
        {
            // speed of light is fast, only visible one tick
            if(getAge() > 3)
            {
                setCurrentlyVisible(false);
            }
        }

        // propagates smell
        if(this instanceof Smellable)
        {

            if(notSmellableNextCycle)
            {
                setCurrentlySmellable(false);
            }

            float newSmellRadius = currentSmellRadius + SPEED_OF_SMELL;
            // smells can't extend beyond their max radius
            if(newSmellRadius > max_smell_distance)
            {
                newSmellRadius = max_smell_distance;
                notSmellableNextCycle = true;
            }
            currentSmellRadius = newSmellRadius;
        }

        // propagates sound
        if(this instanceof Audible)
        {
            if(notAudibleNextCycle)
            {
                setCurrentlyAudible(false);
            }

            float newSoundRadius = currentSoundRadius + SPEED_OF_SOUND;
            // smells can't extend beyond their max radius
            if(newSoundRadius > max_sound_distance)
            {
                newSoundRadius = max_sound_distance;
                notAudibleNextCycle = true;
            }
            currentSoundRadius = newSoundRadius;
        }
    }

    public Rectangle2D getBounds()
    {
        Vector3f scale = Vector3f.UNIT_XYZ;

        if(this instanceof Audible && this instanceof Smellable)
        {
            float max = FastMath.max(max_sound_distance, max_smell_distance);
            scale = scale.multLocal(max);
        }
        else if(this instanceof Audible)
        {
            scale = scale.multLocal(max_sound_distance);
        }
        else if(this instanceof Smellable)
        {
            scale = scale.multLocal(max_smell_distance);
        }

        return PhysicsHelper.createBoundingArea(origin, scale);
    }
}
