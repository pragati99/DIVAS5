package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.ontology.Thing;

/**
 * Class to store knowledge about rooms
 */
public class Room extends Thing
{

    /**
     * Serializer ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Each room can have any number of doors
     */
    private List<Integer>     doorsIDs;

    /**
     * Each room has an ID
     */
    int                       id;

    /**
     * Whether or not the "room" is inside our outside
     */
    boolean                   outside          = false;

    /**
     * Whether or not the "room" is inside our outside
     * 
     * @return Whether outside
     */
    public boolean isOutside()
    {
        return outside;
    }

    /**
     * Set Whether or not the "room" is inside our outside
     * 
     * @param outside
     *        whether outside
     */
    public void setOutside(boolean outside)
    {
        this.outside = outside;
    }

    /**
     * Create a new room
     * 
     * @param id
     *        the room's ID
     */
    public Room(int id)
    {
        doorsIDs = new ArrayList<Integer>();
        this.id = id;
    }

    /**
     * Get all the doors in the room.
     * 
     * @return The doors
     */
    public List<Integer> getDoorsIDs()
    {
        return doorsIDs;
    }

    /**
     * Check whether room has door with specified ID or not.
     * 
     * @param id
     *        ID to check
     * @return true if it has the door
     */
    public boolean hasDoor(int id)
    {
        // prevent concurrent modification of doors
        synchronized(doorsIDs)
        {
            for(Integer i : doorsIDs)
            {
                if(i.equals(id))
                {
                    return true;
                }
            }
        }
        // System.out.println("rut FAWSEE on "+id);
        return false;
    }

    /**
     * Add a new door to the room.
     * 
     * @param id
     *        The door's ID
     */
    public void addDoor(int id)
    {
        doorsIDs.add(id);
    }

    /**
     * Get the room's ID
     * 
     * @return ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the room's ID
     * 
     * @param id
     *        ID
     */
    public void setId(int id)
    {
        this.id = id;
    }
}
