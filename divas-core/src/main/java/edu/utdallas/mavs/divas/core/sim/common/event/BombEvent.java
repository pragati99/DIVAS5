package edu.utdallas.mavs.divas.core.sim.common.event;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Destructive;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents an explosion event.
 * <p>
 * The {@link BombEvent} is an external environment event that can be visible, audible and smellable to agents in the simulation. Thus, BombEvent implements {@link Visible}, {@link Audible} and {@link Smellable}.
 */
public class BombEvent extends EnvEvent implements Visible, Audible, Smellable, Destructive, Serializable
{
    private static final long serialVersionUID = 1L;
    private boolean           smoke;

    /**
     * Creates an explosion event that is visible, audible and smellable to vision, hearing and smell senses.
     */
    public BombEvent()
    {
        super(-1);
        addVisionProperty("flash", 90);
        addVisionProperty("fire", 30);
        addHearingProperty("boom", 95);
        addSmellProperty("smoke", 50);
    }

    @Override
    public float getSmellIntensity()
    {
        return 60;
    }

    /**
     * Returns a flag indicating if this explosion produces smoke or not.
     * 
     * @return a boolean indicating if this explosion produces smoke or not.
     */
    public boolean isSmoke()
    {
        return smoke;
    }

    /**
     * Changes the explosion to produce smoke or not.
     * 
     * @param smoke
     *        a boolean indicating if this explosion produces smoke or not.
     */
    public void setSmoke(boolean smoke)
    {
        this.smoke = smoke;
    }

    @Override
    public boolean hasExpired()
    {
        return super.hasExpired() && currentSoundRadius >= max_sound_distance && currentSmellRadius >= max_smell_distance;
    }

    public int getVisualizedIntensity()
    {
        return 3;
    }

}
