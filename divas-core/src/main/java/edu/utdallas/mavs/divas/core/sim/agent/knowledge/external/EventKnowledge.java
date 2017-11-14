package edu.utdallas.mavs.divas.core.sim.agent.knowledge.external;

import java.io.Serializable;
import java.util.HashMap;


public class EventKnowledge implements Serializable
{
    private static final long               serialVersionUID        = 1L;

    String                                  name;

    HashMap<String, EventPropertyKnowledge> EventPropertyKnowledges = new HashMap<String, EventPropertyKnowledge>();

    public HashMap<String, EventPropertyKnowledge> getEventPropertyKnowledges()
    {
        return EventPropertyKnowledges;
    }

    public EventKnowledge(String name)
    {
        this.name = name;
    }

    public void addEventPropertyKnowledge(EventPropertyKnowledge epk)
    {
        EventPropertyKnowledges.put(epk.getType(), epk);
    }

    public EventPropertyKnowledge getEventProperty(String type)
    {
        return EventPropertyKnowledges.get(type);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
