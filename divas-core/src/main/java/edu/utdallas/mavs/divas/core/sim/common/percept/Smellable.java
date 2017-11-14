/*
 * File URL : $HeadURL: https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/state/Smellable.java $
 * Revision : $Rev: 345 $
 * Last modified at: $Date: 2010-05-07 13:26:19 -0500 (Fri, 07 May 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.core.sim.common.percept;

import com.jme3.math.Vector3f;

/**
 * This interface is implemented by external events in the simulation that can be perceived by smell (olfactory senses).
 */
public interface Smellable
{
    /**
     * Gets the origin of the event's smell.
     * 
     * @return the origin of the smell.
     */
    public Vector3f getSmellPosition();

    /**
     * Gets the smell intensity of the event.
     * 
     * @return the smell intensity.
     */
    public float getSmellIntensity();

    /**
     * Gets the radius of the event's smell.
     * 
     * @return the radius of the event's smell.
     */
    public float getCurrentSmellRadius();
}
