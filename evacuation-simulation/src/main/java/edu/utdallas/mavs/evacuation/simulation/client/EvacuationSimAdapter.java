package edu.utdallas.mavs.evacuation.simulation.client;

import com.google.inject.Inject;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.SimFacade;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.ActivatedObjectState;

public class EvacuationSimAdapter extends SimAdapter
{

    @Inject
    public EvacuationSimAdapter(SimFacade simFacade)
    {
        super(simFacade);
    }

    /**
     * Creates an environment object in the simulation.
     * 
     * @param envObject
     */
    @Override
    public void createEnvObject(EnvObjectState envObject)
    {        
        if(envObject instanceof ActivatedObjectState)
        {
            envObject.getPosition().setY(10);
        }
        else
        {
            envObject.getPosition().setY(0);
        }
        simFacade.createEnvObject(envObject);
    }

    /**
     * Updates the state of an agent or environment object. The Y component of the location will be set to 0.
     * 
     * @param state
     *        the state to be updated.
     */
    @Override
    public void sendStateUpdate(VirtualState state)
    {        
        if(state instanceof ActivatedObjectState)
        {
            state.getPosition().setY(10);
        }
        else
        {
            state.getPosition().setY(0);
        }
        simFacade.sendStateUpdate(state);
    }
}
