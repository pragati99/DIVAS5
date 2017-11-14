package edu.utdallas.mavs.evacuation.visualization.guice;

import com.google.inject.Singleton;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.SimCommander;
import edu.utdallas.mavs.divas.core.client.SimFacade;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.visualization.guice.CommunicationModuleProvider;
import edu.utdallas.mavs.divas.visualization.guice.VisualizerModule;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.PlayGround;
import edu.utdallas.mavs.evacuation.simulation.client.EvacuationSimAdapter;
import edu.utdallas.mavs.evacuation.visualization.vis3D.EvacuationApplication;
import edu.utdallas.mavs.evacuation.visualization.vis3D.EvacuationVisualizer3DApplication;
import edu.utdallas.mavs.evacuation.visualization.vis3D.spectator.EvacuationPlayground;

/**
 * This class implements this module's dependency injection container.
 */
public class EvacuationVisualizerModule extends VisualizerModule
{
    @Override
    protected void configure()
    {
        // super.configure();
        bind(SimFacade.class).to(SimCommander.class).in(Singleton.class);
        bind(SimAdapter.class).to(EvacuationSimAdapter.class).in(Singleton.class);
        bind(CommunicationModule.class).toProvider(CommunicationModuleProvider.class).in(Singleton.class);

        bind(EvacuationVisualizer3DApplication.class).in(Singleton.class);
        bind(EvacuationApplication.class).in(Singleton.class);
        bind(PlayGround.class).to(EvacuationPlayground.class).in(Singleton.class);
    }
}
