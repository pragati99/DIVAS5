package edu.utdallas.mavs.evacuation.simulation.sim;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.msg.TickMsg;
import edu.utdallas.mavs.divas.core.sim.Simulation;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.evacuation.simulation.sim.env.EvacuationEnvironment;

/**
 * This class describes a simulation for evacuation scenarios.
 */
public class EvacuationSimulation extends Simulation<EvacuationEnvironment> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /* Remove comments for performance profiling */

    // protected transient Slf4JStopWatch simWatch = new Slf4JStopWatch("Simulation");
    //
    // protected transient Slf4JStopWatch phaseWatch = new Slf4JStopWatch("Phase");

    /**
     * Creates a new instance of the simulation for evacuation scenarios.
     * 
     * @param client
     *        the MTS client
     */
    public EvacuationSimulation(MTSClient client)
    {
        super(client);
    }

    @Override
    protected void createEnvironment(MTSClient client)
    {
        environment = new EvacuationEnvironment(client);
    }

    @Override
    public void tick(TickMsg tick)
    {
        /* Remove comments for performance profiling */

        // String phaseTag = String.format("%s-%s", environment.getStrategy(), tick.getPhase());
        // String simTag = String.format("%s", environment.getStrategy());

        // if(tick.getPhase().equals(Phase.ENVIRONMENT))
        // {
        // simWatch.stop(simTag);
        // simWatch.start(simTag);
        // }

        // phaseWatch.start(phaseTag);
        super.tick(tick);
        // phaseWatch.stop(phaseTag);
    }
}
