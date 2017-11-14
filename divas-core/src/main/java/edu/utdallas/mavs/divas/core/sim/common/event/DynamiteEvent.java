package edu.utdallas.mavs.divas.core.sim.common.event;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Destructive;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents an dynamite event.
 * <p>
 * The {@link DynamiteEvent} is an external environment event that can be visible, audible and smellable to agents in the simulation. Thus, BombEvent implements {@link Visible}, {@link Audible} and {@link Smellable}.
 */
public class DynamiteEvent extends EnvEvent implements Visible, Audible, Smellable, Destructive
{
    private static final long serialVersionUID = 1L;
    private boolean           smoke;

    /**
     * Creates an dynamite event that is visible, audible and smellable to vision, hearing and smell senses.
     */
    public DynamiteEvent()
    {
        super(-1);
        addVisionProperty("flash", 170);
        addVisionProperty("fire", 70);
        addHearingProperty("boom", 150);
        addSmellProperty("smoke", 90);
    }

    @Override
    public float getSmellIntensity()
    {
        return 60;
    }

    /**
     * Returns a flag indicating if this dynamite explosion produces smoke or not.
     * 
     * @return a boolean indicating if this explosion produces smoke or not.
     */
    public boolean isSmoke()
    {
        return smoke;
    }

    /**
     * Changes the dynamite to produce smoke or not.
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
