package edu.utdallas.mavs.divas.visualization.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.utils.EventLoader.Event;

/**
 * This class describes a repository for visualizer resources.
 */
public class VisResourcesRepository
{
    private List<VisResource<AgentState>>     agents;

    private List<VisResource<EnvObjectState>> objects;

    private List<VisResource<Event>>          events;

    private static VisResourcesRepository     instance;

    private VisResourcesRepository()
    {
        agents = AgentLoader.getVisResources();
        objects = EnvObjectLoader.getVisResources();
        events = EventLoader.getVisResources();

        Collections.sort(agents, new VisResourcesComparator<AgentState>());
        Collections.sort(objects, new VisResourcesComparator<EnvObjectState>());
        Collections.sort(events, new VisResourcesComparator<Event>());
    }

    /**
     * Gets the singleton instance of this repository
     * 
     * @return the singleton
     */
    private static VisResourcesRepository getInstance()
    {
        if(instance == null)
            instance = new VisResourcesRepository();
        return instance;
    }

    /**
     * Gets all agent resources
     * 
     * @return a list of agent resource definitions
     */
    public static List<VisResource<AgentState>> findAllAgents()
    {
        return getInstance().agents;
    }

    /**
     * Gets agent resources filtered by a given query
     * 
     * @param query
     *        a search query
     * @return a list of agent resource definitions
     */
    public static List<VisResource<AgentState>> findAgents(String query)
    {
        return search(getInstance().agents, query);
    }

    /**
     * Gets all object resources
     * 
     * @return a list of agent resource definitions
     */
    public static List<VisResource<EnvObjectState>> findAllEnvObjects()
    {
        return getInstance().objects;
    }

    /**
     * Gets object resources filtered by a given query
     * 
     * @param query
     *        a search query
     * @return a list of agent resource definitions
     */
    public static List<VisResource<EnvObjectState>> findEnvObjects(String query)
    {
        return search(getInstance().objects, query);
    }

    /**
     * Gets all event resources
     * 
     * @return a list of agent resource definitions
     */
    public static List<VisResource<Event>> findAllEvents()
    {
        return getInstance().events;
    }

    /**
     * Gets event resources filtered by a given query
     * 
     * @param query
     *        a search query
     * @return a list of agent resource definitions
     */
    public static List<VisResource<Event>> findEvents(String query)
    {
        return search(getInstance().events, query);
    }

    private static <T> List<VisResource<T>> search(List<VisResource<T>> resources, String query)
    {
        List<VisResource<T>> results = new ArrayList<>();

        String lcQuery = query.toLowerCase();

        for(VisResource<T> r : resources)
        {
            if(r.getType().toLowerCase().contains(lcQuery) || r.getName().toLowerCase().contains(lcQuery) || r.getDescription().toLowerCase().contains(lcQuery))
                results.add(r);
        }

        return results;
    }

    class VisResourcesComparator<T> implements Comparator<VisResource<T>>
    {
        @Override
        public int compare(VisResource<T> o1, VisResource<T> o2)
        {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
