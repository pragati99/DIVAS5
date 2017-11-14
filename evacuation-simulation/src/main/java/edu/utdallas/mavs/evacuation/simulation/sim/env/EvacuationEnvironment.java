package edu.utdallas.mavs.evacuation.simulation.sim.env;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.env.SelfOrganizingEnvironment;
import edu.utdallas.mavs.divas.core.spec.agent.AgentLoader;
import edu.utdallas.mavs.divas.core.spec.env.EnvLoader;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.MTSClient;
import edu.utdallas.mavs.evacuation.simulation.spec.agent.EvacuationAgentLoader;

/**
 * This class describes a
 */
public class EvacuationEnvironment extends SelfOrganizingEnvironment<EvacuationCellController> implements Serializable
{
    private static final long   serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private final static Logger logger           = LoggerFactory.getLogger(EvacuationEnvironment.class);

    /**
     * Creates a new Evacuation Environmnent.
     * 
     * @param client
     *        the MTS client
     */
    public EvacuationEnvironment(MTSClient client)
    {
        super(client);
    }

    @Override
    protected EvacuationCellController createCellController(CellState cellState, CommunicationModule commModule)
    {
        return new EvacuationCellController(cellState, commModule, this);
    }

    @Override
    protected AgentLoader createAgentLoader()
    {
        return new EvacuationAgentLoader();
    }

    @Override
    protected EnvLoader createEnvLoader()
    {
        return super.createEnvLoader();
    }
}
