package edu.utdallas.mavs.divas.core.host;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.Simulation;
import edu.utdallas.mavs.divas.core.sim.env.Environment;

public class SimSnapshot<E extends Environment<?>> implements Serializable
{
    private static final long serialVersionUID = 1534309712830394419L;

    private Heartbeat         heartbeat;

    private IdManager         idManager;

    private Simulation<?>     simulation;

    public SimSnapshot(Heartbeat heartbeat, IdManager idManager)
    {
        this.heartbeat = heartbeat;
        this.idManager = idManager;
    }

    public SimSnapshot(Heartbeat heartbeat, IdManager idManager, Simulation<?> simulation)
    {
        this(heartbeat, idManager);
        this.simulation = simulation;
    }

    public Heartbeat getHeartbeat()
    {
        return heartbeat;
    }

    public IdManager getIdManager()
    {
        return idManager;
    }

    public Simulation<?> getSimulation()
    {
        return simulation;
    }
}
