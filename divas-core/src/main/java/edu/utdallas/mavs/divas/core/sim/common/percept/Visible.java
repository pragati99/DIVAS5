
package edu.utdallas.mavs.divas.core.sim.common.percept;

import com.jme3.math.Vector3f;

/**
 * This interface is implemented by agents, environment objects and external events in the simulation that can be perceived by vision senses.
 */
public interface Visible
{
    /**
     * Gets the visible position of the event.
     * 
     * @return the visible position of the event.
     */
    public Vector3f getVisiblePosition();

    /**
     * Gets the visible scale of the event.
     * 
     * @return the visible scale of the event.
     */
    public Vector3f getVisibleScale();
}

