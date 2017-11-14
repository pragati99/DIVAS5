package edu.utdallas.mavs.divas.visualization.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.SimCommander;
import edu.utdallas.mavs.divas.core.client.SimFacade;
import edu.utdallas.mavs.divas.mts.CommunicationModule;

/**
 * This class implements this module's dependency injection container.
 */
public class VisualizerModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(SimFacade.class).to(SimCommander.class).in(Singleton.class);
        bind(SimAdapter.class).in(Singleton.class);
        bind(CommunicationModule.class).toProvider(CommunicationModuleProvider.class).in(Singleton.class);
    }
}
