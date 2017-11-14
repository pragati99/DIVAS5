package edu.utdallas.mavs.divas.core.sim.common.event;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents the event of adding a drum (musical percussion instrument) to the simulation.
 * <p>
 * The drum event is an external environment event that is visible and audible to agents in the simulation. Thus, {@link DrumsEvent} implements {@link Visible} and {@link Audible}.
 */
public class DrumsEvent extends EnvEvent implements Visible, Audible, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new drums event in the simulation that is visible to vision senses and audible to hearing senses of
     * agents.
     */
    public DrumsEvent()
    {
        super(-1);
        addVisionProperty("drums", 1);
        addHearingProperty("boom", 80);
    }

    @Override
    public void propagate()
    {
        setCurrentSoundRadius(max_sound_distance);
    }
}
