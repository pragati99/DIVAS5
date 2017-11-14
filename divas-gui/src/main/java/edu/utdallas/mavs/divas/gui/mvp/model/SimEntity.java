package edu.utdallas.mavs.divas.gui.mvp.model;

public class SimEntity
{
    private int           id;
    private SimEntityType type;
    private String        name;
    private String        cellID;
    private Object        state;

    public enum SimEntityType
    {
        Agent, EnvObject, Event
    }

    public SimEntity(int id, SimEntityType type, String name, String cellID, Object state)
    {
        super();
        this.id = id;
        this.type = type;
        this.name = name;
        this.cellID = cellID;
        this.state = state;
    }

    public int getId()
    {
        return id;
    }

    public SimEntityType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getCellID()
    {
        return cellID;
    }

    public Object getState()
    {
        return state;
    }

}
