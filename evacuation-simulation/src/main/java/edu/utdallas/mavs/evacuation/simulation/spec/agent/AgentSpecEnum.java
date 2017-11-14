package edu.utdallas.mavs.evacuation.simulation.spec.agent;

import edu.utdallas.mavs.divas.utils.ExtensibleEnum;

/**
 * Enumeration with agent specification available to be used in the simulation.
 */
public class AgentSpecEnum extends ExtensibleEnum<AgentSpecEnum>
{
    /**
     * Default agent spec
     */
    public static final AgentSpecEnum Default = new AgentSpecEnum("Default", 0);

    protected AgentSpecEnum(String name)
    {
        super(name);
    }

    protected AgentSpecEnum(String name, int ordinal)
    {
        super(name, ordinal);
    }

    @Override
    public AgentSpecEnum[] getEnumValues()
    {
        return values(AgentSpecEnum.class);
    }

    public static AgentSpecEnum create(String name)
    {
        return new AgentSpecEnum(name);
    }
}
