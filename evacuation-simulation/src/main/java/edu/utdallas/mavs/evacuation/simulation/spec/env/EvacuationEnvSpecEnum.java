package edu.utdallas.mavs.evacuation.simulation.spec.env;

import edu.utdallas.mavs.divas.core.spec.env.EnvSpecEnum;

/**
 * Enumeration of environments available to be used in the simulation.
 */
public class EvacuationEnvSpecEnum
{
    /**
     * Complex room containing 19 environment objects.
     */
    public final static EnvSpecEnum DemoRoom       = EnvSpecEnum.create("DemoRoom");

    /**
     * Larger room containing random environment objects.
     */
    public final static EnvSpecEnum RandomEvacRoom = EnvSpecEnum.create("RandomEvacRoom");

    /**
     * Larger room containing random environment objects.
     */
    public final static EnvSpecEnum SimpleRoom     = EnvSpecEnum.create("SimpleRoom");
}
