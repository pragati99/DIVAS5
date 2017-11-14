package edu.utdallas.mavs.evacuation.simulation.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import edu.utdallas.mavs.evacuation.simulation.host.EvacuationHost;


public class SimulationModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(EvacuationHost.class).in(Singleton.class);
    }
}
