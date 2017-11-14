/*package edu.utdallas.mavs.divas.gui.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.pennychecker.eventbus.EventBus;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.client.SimCommander;
import edu.utdallas.mavs.divas.core.client.SimFacade;
import edu.utdallas.mavs.divas.gui.mvp.presenter.SimControlPanelPresenter;
import edu.utdallas.mavs.divas.gui.mvp.presenter.SimulationContainerPresenter;
import edu.utdallas.mavs.divas.gui.mvp.view.SimControlPanelView;
import edu.utdallas.mavs.divas.gui.mvp.view.SimulationContainerView;
import edu.utdallas.mavs.divas.gui.repository.SimRepository;
import edu.utdallas.mavs.divas.gui.services.SimUpdateHandler;
import edu.utdallas.mavs.divas.mts.CommunicationModule;

public class GuiModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind(SimulationContainerPresenter.Display.class).to(SimulationContainerView.class).in(Singleton.class);
        bind(SimulationContainerPresenter.class).in(Singleton.class);

        bind(SimControlPanelPresenter.Display.class).to(SimControlPanelView.class).in(Singleton.class);
        bind(SimControlPanelPresenter.class).in(Singleton.class);

        // bind(FxSimControlPanelPresenter.Display.class).to(FxSimControlPanelView.class).in(Singleton.class);
        // bind(FxSimControlPanelPresenter.class).in(Singleton.class);

        bind(CommunicationModule.class).toProvider(CommunicationModuleProvider.class).in(Singleton.class);
        bind(SimFacade.class).to(SimCommander.class).in(Singleton.class);
        bind(SimAdapter.class).in(Singleton.class);
        bind(EventBus.class).toProvider(EventBusProvider.class).in(Singleton.class);

        bind(SimUpdateHandler.class).in(Singleton.class);

        bind(SimRepository.class).in(Singleton.class);
    }

}
*/