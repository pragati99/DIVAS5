package edu.utdallas.mavs.divas.core.sim.common.event;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents the event of adding a spotlight to the simulation.
 * <p>
 * The spotlight event is an external environment event that is visible to agents in the simulation. Thus, {@link SpotlightEvent} implements {@link Visible}.
 */
public class SpotlightEvent extends EnvEvent implements Visible, Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new spotlight event in the simulation that is visible to vision senses of agents.
     */
    public SpotlightEvent()
    {
        super(-1);
        addVisionProperty("flash", 95);
    }

    @Override
    public void propagate()
    {
        // do not propagate spotlight
    }
}
