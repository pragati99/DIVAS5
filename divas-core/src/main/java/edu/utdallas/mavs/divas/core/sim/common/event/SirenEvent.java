package edu.utdallas.mavs.divas.core.sim.common.event;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;

/**
 * This class represents an Siren event.
 * <p>
 * The {@link SirenEvent} is an external environment event audible.
 */
public class SirenEvent extends EnvEvent implements Audible
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates Siren event that is audible
     */
    public SirenEvent()
    {
        super(-1);
        addHearingProperty("siren", 100);
        max_sound_distance = 84;
        this.currentlyVisible = true;
        maxAge = 20;
    }

    public float getVisualizedIntensity()
    {
        return max_sound_distance / 5.5f;
    }

}
