package edu.utdallas.mavs.divas.core.spec.env;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;

/**
 * This class represents a cell state specification.
 */
public class CellStateSpec implements Serializable
{
    private static final long    serialVersionUID = 1L;
    private List<EnvObjectState> envObjects;
    private CellID               cellID;
    private CellBounds           bounds;

    /**
     * @param rootID
     * @param cellBounds
     */
    public CellStateSpec(CellID rootID, CellBounds cellBounds)
    {
        this.cellID = rootID;
        this.bounds = cellBounds;
        this.envObjects = new ArrayList<EnvObjectState>();
    }

    /**
     * Gets the cell state ID
     * 
     * @return the cellID
     */
    public CellID getCellID()
    {
        return cellID;
    }

    /**
     * Gets the {@link CellBounds} of this cell state.
     * 
     * @return the cell state boundaries.
     */
    public CellBounds getBounds()
    {
        return bounds;
    }

    /**
     * @param state
     */
    public void addEnvObject(EnvObjectState state)
    {
        envObjects.add(state);
    }

    /**
     * Gets this cell environment objects
     * 
     * @return this cell environment objects
     */
    public List<EnvObjectState> getEnvObjects()
    {
        return envObjects;
    }
}
