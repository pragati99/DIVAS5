package edu.utdallas.mavs.divas.core.sim.common.event;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents the event of adding fireworks to the simulation.
 * <p>
 * The {@link FireworkEvent} is an external environment event that can be visible, audible and smellable to agents in the simulation. Thus, {@link FireworkEvent} implements {@link Visible},
 * {@link Audible} and {@link Smellable}.
 */
public class FireworkEvent extends EnvEvent implements Visible, Audible, Smellable, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new fireworks event in the simulation that is visible, audible and smellable to vision, hearing and
     * smell senses of agents.
     */
    public FireworkEvent()
    {
        super(-1);
        addVisionProperty("flash", 40);
        addVisionProperty("colors", 50);
        addHearingProperty("boom", 50);
        addSmellProperty("smoke", 20);
    }

    @Override
    public float getSmellIntensity()
    {
        return 20;
    }

    @Override
    public boolean hasExpired()
    {
        return super.hasExpired() && currentSoundRadius >= max_sound_distance && currentSmellRadius >= max_smell_distance;
    }
}
