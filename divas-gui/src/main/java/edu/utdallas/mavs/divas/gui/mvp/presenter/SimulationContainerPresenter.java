//package edu.utdallas.mavs.divas.gui.mvp.presenter;
//
//import com.google.inject.Inject;
//
//import com.pennychecker.eventbus.EventBus;
//import com.pennychecker.presenter.javafx.FxContainerDisplay;
//import com.pennychecker.presenter.javafx.FxContainerPresenter;
//
//public class SimulationContainerPresenter extends FxContainerPresenter<SimulationContainerPresenter.Display>
//{
//
//    @Inject
//    public SimulationContainerPresenter(Display display, EventBus eventBus, SimControlPanelPresenter simControlPanelPresenter)
//    {
//        super(display, eventBus, simControlPanelPresenter);
//
//        assert null != simControlPanelPresenter;
//        // assert null != fxSimControlPanelPresenter;
//
//        display.addParent(simControlPanelPresenter.getDisplay().asParent());
//        // display.addParent(fxSimControlPanelPresenter.getDisplay().asParent());
//
//        showPresenter(simControlPanelPresenter);
//        // showPresenter(fxSimControlPanelPresenter);
//        bind();
//    }
//
//    @Override
//    public void refreshDisplay()
//    {}
//
//    public interface Display extends FxContainerDisplay
//    {
//
//    }
//}
