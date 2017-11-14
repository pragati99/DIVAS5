//package edu.utdallas.mavs.divas.gui.mvp.presenter;
//
//import java.util.Map;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.MenuItem;
//import javafx.stage.Stage;
//
//import javax.swing.JFrame;
//
//import com.google.inject.Inject;
//import com.jme3.math.Vector3f;
//import com.pennychecker.eventbus.EventBus;
//import com.pennychecker.presenter.javafx.FxDisplay;
//import com.pennychecker.presenter.javafx.FxPresenter;
//
//import edu.utdallas.mavs.divas.core.client.SimAdapter;
//import edu.utdallas.mavs.divas.core.client.dto.SimDto;
//import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
//import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
//import edu.utdallas.mavs.divas.gui.event.SimStatusUpdateEvent;
//import edu.utdallas.mavs.divas.gui.event.SimStatusUpdateEventHandler;
//import edu.utdallas.mavs.divas.gui.event.SimSummaryUpdateEvent;
//import edu.utdallas.mavs.divas.gui.event.SimSummaryUpdateEventHandler;
//import edu.utdallas.mavs.divas.gui.guice.CommunicationModuleProvider;
//import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
//import edu.utdallas.mavs.divas.gui.repository.SimRepository;
//import edu.utdallas.mavs.divas.gui.services.SimUpdateHandler;
//
//public class FxSimControlPanelPresenter extends FxPresenter<FxSimControlPanelPresenter.Display>
//{
//    private static final int               AUTO_REFRESH_INTERVAL = 1000;
//
//    private SimAdapter                     simAdapter;
//
//    private SimRepository                  simRepository;
//
//    private ChangeListener<? super Number> reorganizationActionListener;
//
//    private Timer                          autoRefreshTimer;
//
//    @Inject
//    public FxSimControlPanelPresenter(Display display, EventBus eventBus, SimAdapter simAdapter, SimRepository simRepository, SimUpdateHandler simUpdateHandler)
//    {
//        super(display, eventBus);
//        this.simAdapter = simAdapter;
//        this.simRepository = simRepository;
//        bind();
//    }
//
//    @Override
//    protected void onBind()
//    {
//        display.updateControlPanelState(CommunicationModuleProvider.isConnected());
//
//        display.getStartButton().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                simAdapter.startSimulation();
//            }
//        });
//
//        display.getPauseButton().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                simAdapter.pauseSimulation();
//            }
//        });
//
//        display.getAddAgentButton().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                Random r = new Random();
//                for(int i = 0; i < 100; i++)
//                {
//                    simAdapter.createAgent(new AgentState(), new Vector3f(-200 + r.nextInt(150), 0, -200 + r.nextInt(150)));
//                }
//            }
//        });
//
//        display.getReorganizationStrategyComboBox().getSelectionModel().selectedIndexProperty().addListener(reorganizationActionListener = new ChangeListener<Number>()
//        {
//            @Override
//            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2)
//            {
//                ReorganizationStrategy str = display.getReorganizationStrategyComboBox().getSelectionModel().getSelectedItem();
//                simAdapter.setReorganizationStrategy(str);
//            }
//        });
//
//        display.getReorganizationStrategyComboBox().focusedProperty().addListener(new ChangeListener<Boolean>()
//        {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
//            {
//                if(display.getReorganizationStrategyComboBox().isFocused())
//                {
//                    autoRefreshTimer.cancel();
//                }
//                else if(display.getSearchAutoRefreshCheckBox().isSelected())
//                {
//                    if(autoRefreshTimer != null)
//                    {
//                        autoRefreshTimer.cancel();
//                    }
//                    setupAutoRefreshTimer();
//                }
//            }
//        });
//
//        display.getSearchButton().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                String query = display.getSearchQuery();
//                Map<Integer, SimEntity> simEntities = simRepository.find(query);
//                display.updateSearchResults(simEntities);
//            }
//        });
//
//        display.getSearchAutoRefreshCheckBox().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                if(display.getSearchAutoRefreshCheckBox().isSelected())
//                {
//                    setupAutoRefreshTimer();
//                }
//                else
//                {
//                    autoRefreshTimer.cancel();
//                }
//            }
//        });
//
//        display.getSaveOptionsButton().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                display.saveSimConfig();
//                simAdapter.notifySimPropertiesChange();
//            }
//        });
//
//        display.getConnectMenuItem().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                CommunicationModuleProvider.connect(new JFrame());
//                display.updateControlPanelState(CommunicationModuleProvider.isConnected());
//            }
//        });
//
//        display.getDisconnectMenuItem().setOnAction(new EventHandler<ActionEvent>()
//        {
//            @Override
//            public void handle(ActionEvent arg0)
//            {
//                CommunicationModuleProvider.disconnect();
//                SimRepository.clear();
//                display.updateControlPanelState(CommunicationModuleProvider.isConnected());
//            }
//        });
//
//        // display.getNewEnvironmentMenuItem().setOnAction(new EventHandler<ActionEvent>()
//        // {
//        // @Override
//        // public void handle(ActionEvent arg0)
//        // {
//        // if(LoadEnvironmentDialog.isLoading(display.getMainFrame()))
//        // {
//        // simAdapter.pauseSimulation();
//        //
//        // File file = LoadEnvironmentDialog.getEnvFile(display.getMainFrame());
//        //
//        // if(file != null)
//        // {
//        // simAdapter.loadEnvironment(file);
//        // SimRepository.clear();
//        // display.updateControlPanelState(true);
//        // simAdapter.startSimulation();
//        // }
//        // }
//        // }
//        // });
//
//        // display.getSaveSimulationMenuItem().addActionListener(new ActionListener()
//        // {
//        // @Override
//        // public void actionPerformed(ActionEvent e)
//        // {
//        // File file = SaveSimulationDialog.getFile(display.getMainFrame());
//        // if(file != null)
//        // {
//        // simAdapter.pauseSimulation();
//        // simAdapter.saveSimulationSnapshot(file);
//        // simAdapter.startSimulation();
//        // }
//        // }
//        // });
//
//        // display.getLoadSimulationMenuItem().addActionListener(new ActionListener()
//        // {
//        // @Override
//        // public void actionPerformed(ActionEvent e)
//        // {
//        // if(LoadSimulationDialog.isLoading(display.getMainFrame()))
//        // {
//        // simAdapter.pauseSimulation();
//        //
//        // File file = LoadSimulationDialog.getEnvFile(display.getMainFrame());
//        //
//        // if(file != null)
//        // {
//        // simAdapter.loadSimulationSnapshot(file);
//        // SimRepository.clear();
//        // display.updateControlPanelState(true);
//        // simAdapter.startSimulation();
//        // }
//        // }
//        // }
//        // });
//
//        registerHandler(eventBus.addHandler(SimSummaryUpdateEvent.TYPE, new SimSummaryUpdateEventHandler()
//        {
//            @Override
//            public void onSimSummaryUpdateEvent(SimSummaryUpdateEvent event)
//            {
//                display.getReorganizationStrategyComboBox().getSelectionModel().selectedIndexProperty().removeListener(reorganizationActionListener);
//                display.setSimulationSummary(event.getSimSummary(), simRepository.getAgentsCount(), simRepository.getEnvObjectsCount());
//                display.getReorganizationStrategyComboBox().getSelectionModel().selectedIndexProperty().addListener(reorganizationActionListener);
//            }
//        }));
//
//        registerHandler(eventBus.addHandler(SimStatusUpdateEvent.TYPE, new SimStatusUpdateEventHandler()
//        {
//            @Override
//            public void onSimStatusUpdateEvent(SimStatusUpdateEvent event)
//            {
//                display.setSimulationStatus(event.isRunning());
//            }
//        }));
//    }
//
//    private void setupAutoRefreshTimer()
//    {
//        autoRefreshTimer = new Timer("SimSummaryAutoRefresh", true);
//        autoRefreshTimer.schedule(new TimerTask()
//        {
//            @Override
//            public void run()
//            {
//                String query = display.getSearchQuery();
//                Map<Integer, SimEntity> simEntities = simRepository.find(query);
//                display.refreshSearchResults(simEntities);
//            }
//        }, AUTO_REFRESH_INTERVAL, AUTO_REFRESH_INTERVAL);
//    }
//
//    @Override
//    protected void onUnbind()
//    {
//
//    }
//
//    @Override
//    public void refreshDisplay()
//    {
//
//    }
//
//    public interface Display extends FxDisplay
//    {
//        public void setSimulationSummary(SimDto simulationSummary, int agentCount, int envObjCount);
//
//        public void updateControlPanelState(boolean connected);
//
//        public void setSimulationStatus(boolean running);
//
//        public void updateSearchResults(Map<Integer, SimEntity> simEntities);
//
//        public void refreshSearchResults(Map<Integer, SimEntity> simEntities);
//
//        public String getSearchQuery();
//
//        public Button getAddAgentButton();
//
//        public Button getStartButton();
//
//        public Button getPauseButton();
//
//        public Button getSearchButton();
//
//        public CheckBox getSearchAutoRefreshCheckBox();
//
//        public Button getSaveOptionsButton();
//
//        public MenuItem getConnectMenuItem();
//
//        public MenuItem getDisconnectMenuItem();
//
//        public ComboBox<ReorganizationStrategy> getReorganizationStrategyComboBox();
//
//        public void saveSimConfig();
//
//        public MenuItem getNewEnvironmentMenuItem();
//
//        public MenuItem getSaveSimulationMenuItem();
//
//        public MenuItem getLoadSimulationMenuItem();
//
//        public Stage getMainFrame();
//    }
//}
