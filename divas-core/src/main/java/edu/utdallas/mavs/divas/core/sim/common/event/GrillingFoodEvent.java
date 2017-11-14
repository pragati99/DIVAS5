package edu.utdallas.mavs.divas.core.sim.common.event;

import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;

/**
 * This class represents the event of adding a grill to the simulation.
 * <p>
 * The grilling food event is an external environment event that is visible, audible and smellable to agents in the simulation. Thus, {@link GrillingFoodEvent} implements {@link Visible}, {@link Audible} and {@link Smellable}.
 */
public class GrillingFoodEvent extends EnvEvent implements Visible, Audible, Smellable
{
    private static final long serialVersionUID = 1L;
    private boolean           smoke;

    /**
     * Creates a new grilling food event in the simulation that is visible, audible and smellable to vision, hearing and
     * smell senses of agents.
     */
    public GrillingFoodEvent()
    {
        super(-1);
        addVisionProperty("grill", 1);
        addHearingProperty("sizzle", 12);
        addSmellProperty("smoke", 8);
    }

    @Override
    public float getSmellIntensity()
    {
        return 8;
    }

    /**
     * Returns a flag indicating if this grill event produces smoke or not.
     * 
     * @return a boolean indicating if this grill event produces smoke or not.
     */
    public boolean isSmoke()
    {
        return smoke;
    }

    /**
     * Changes the grilling food event to produce smoke or not.
     * 
     * @param smoke
     *        a boolean indicating if this grill event produces smoke or not.
     */
    public void setSmoke(boolean smoke)
    {
        this.smoke = smoke;
    }

    @Override
    public void propagate()
    {
        setCurrentSmellRadius(max_smell_distance);
        setCurrentSoundRadius(max_sound_distance);
    }
}
