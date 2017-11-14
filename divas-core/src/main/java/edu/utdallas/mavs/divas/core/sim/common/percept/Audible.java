/*
 * File URL : $HeadURL: https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/state/Audible.java $
 * Revision : $Rev: 502 $
 * Last modified at: $Date: 2010-09-02 18:32:40 -0500 (Thu, 02 Sep 2010) $
 * Last modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.core.sim.common.percept;

import com.jme3.math.Vector3f;

/**
 * This interface is implemented by external events in the simulation that make sound and can be perceived by its sound.
 */
public interface Audible
{
    /**
     * Gets the position of the origin of the event's sound.
     * 
     * @return the origin of the sound.
     */
    public Vector3f getAudiblePosition();

    /**
     * Gets the acoustic emission of the event's sound.
     * 
     * @return the acoustic emission.
     */
    public float getAcousticEmission();

    /**
     * Gets the radius of the event's sound.
     * 
     * @return the radius of the event's sound.
     */
    public float getCurrentSoundRadius();
}
