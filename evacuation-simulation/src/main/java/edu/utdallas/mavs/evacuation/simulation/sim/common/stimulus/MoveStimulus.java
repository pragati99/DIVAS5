package edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;

/**
 * An agent move stimulus. The agent tells the environment where it wishes to move to from where it currently is.
 */
public class MoveStimulus extends AgentStimulus implements Comparable<MoveStimulus>
{
    private static final long serialVersionUID = 1L;
    private float             x;
    private float             y;
    private float             z;

    /**
     * Create a new move stimulus using agent ID and position vector.
     * 
     * @param id
     *        the agent's ID
     * @param position
     *        the agent's position
     */
    public MoveStimulus(int id, Vector3f position)
    {
        this(id, position.x, position.y, position.z);
    }

    /**
     * Create a new move stimulus using agent ID and position in floats.
     * 
     * @param id
     *        The agent's ID
     * @param x
     *        The agent's X position
     * @param y
     *        The agent's Y position
     * @param z
     *        The agent's Z position
     */
    public MoveStimulus(int id, float x, float y, float z)
    {
        super(id);

        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Get the position where the agent wishes to move.
     * 
     * @return the position where the agent wishes to move
     */
    public Vector3f getPosition()
    {
        return new Vector3f(x, y, z);
    }

    @Override
    public int compareTo(MoveStimulus o)
    {
        if(this.id > o.id)
            return 1;
        else if(this.id < o.id)
            return -1;
        else
            return 0;
    }
}
