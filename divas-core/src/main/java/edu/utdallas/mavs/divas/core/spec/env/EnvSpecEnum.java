package edu.utdallas.mavs.divas.core.spec.env;

import edu.utdallas.mavs.divas.utils.ExtensibleEnum;

/**
 * Enumeration of environments available to be used in the simulation.
 */
public class EnvSpecEnum extends ExtensibleEnum<EnvSpecEnum>
{
    /**
     * Complex room containing 19 environment objects.
     */
    public final static EnvSpecEnum Default     = new EnvSpecEnum("Default", 0);

    /**
     * Larger room containing random environment objects.
     */
    public final static EnvSpecEnum RandomRoom  = new EnvSpecEnum("RandomRoom", 1);

    /**
     * Empty Room
     */
    public final static EnvSpecEnum EmptyRoom   = new EnvSpecEnum("EmptyRoom", 2);

    /**
     * Room with boxes
     */
    public final static EnvSpecEnum BoxRoom     = new EnvSpecEnum("BoxRoom", 3);

    /**
     * Complex room containing 19 environment objects.
     */
    public final static EnvSpecEnum ComplexRoom = new EnvSpecEnum("ComplexRoom", 4);

    protected EnvSpecEnum(String name)
    {        
        super(name);        
    }

    protected EnvSpecEnum(String name, int ordinal)
    {
        super(name, ordinal);        
    }

    @Override
    public EnvSpecEnum[] getEnumValues()
    {
        return values(EnvSpecEnum.class);
    }

    public static EnvSpecEnum create(String name)
    {
        return new EnvSpecEnum(name);
    }
}
