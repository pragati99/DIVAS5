package edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal;

import java.io.Serializable;

public class Goal implements Serializable
{
    private static final long serialVersionUID = 1L;

    protected int             id;
    protected float           utilityValue;
    protected boolean         achieved         = false;
    protected long            removeTime;
    protected String          type;
    protected String          name;
    protected Object          value;

    public Goal(String type, String name, Object value)
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Goal(String type, String name, Object value, float utility)
    {
        this.type = type;
        this.name = name;
        this.value = value;
        this.utilityValue = utility;
    }

    public Goal(String type, String name, Object value, float utility, long removeTime)
    {
        this.type = type;
        this.name = name;
        this.value = value;
        this.utilityValue = utility;
        this.removeTime = removeTime;
    }

    public Goal(int id, String type)
    {
        this.type = type;
        this.id = id;
    }

    public Goal(String type, int id, float utilityValue)
    {
        this.type = type;
        this.id = id;
        this.utilityValue = utilityValue;
    }

    public Goal(float utilityValue)
    {
        this.utilityValue = utilityValue;
    }

    public long getRemoveTime()
    {
        return removeTime;
    }

    public void setRemoveTime(long removeTime)
    {
        this.removeTime = removeTime;
    }

    public boolean isAchieved()
    {
        return achieved;
    }

    public void setAchieved(boolean achieved)
    {
        this.achieved = achieved;
    }

    public float getUtilityValue()
    {
        return utilityValue;
    }

    public void setUtilityValue(float utilityValue)
    {
        this.utilityValue = utilityValue;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

}
