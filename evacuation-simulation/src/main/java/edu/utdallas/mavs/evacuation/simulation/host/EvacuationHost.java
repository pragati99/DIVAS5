package edu.utdallas.mavs.evacuation.simulation.host;

import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.evacuation.simulation.sim.EvacuationSimulation;

/**
 * This class describes a Host for simulations of evacation scenarios.
 */
public class EvacuationHost extends Host
{
    @Override
    protected void createSimulation(MTSClient client)
    {
        simulation = new EvacuationSimulation(client);
    }
}
