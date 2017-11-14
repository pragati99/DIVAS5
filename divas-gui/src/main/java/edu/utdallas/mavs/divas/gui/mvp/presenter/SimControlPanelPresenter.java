//package edu.utdallas.mavs.divas.gui.mvp.presenter;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import java.io.File;
//import java.util.Map;
//import java.util.Random;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.JMenuItem;
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
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.LoadEnvironmentXMLDialog;
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.LoadSimulationDialog;
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.SaveEnvironmentDialog;
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.dialog.SaveSimulationDialog;
//import edu.utdallas.mavs.divas.gui.repository.SimRepository;
//import edu.utdallas.mavs.divas.gui.services.SimUpdateHandler;
//
//public class SimControlPanelPresenter extends FxPresenter<SimControlPanelPresenter.Display>
//{
//    private static final int AUTO_REFRESH_INTERVAL = 1000;
//
//    private SimAdapter       simAdapter;
//
//    private SimRepository    simRepository;
//
//    private ActionListener   reorganizationActionListener;
//
//    private Timer            autoRefreshTimer;
//
//    @Inject
//    public SimControlPanelPresenter(Display display, EventBus eventBus, SimAdapter simAdapter, SimRepository simRepository, SimUpdateHandler simUpdateHandler)
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
//        display.getStartButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent e)
//            {
//                simAdapter.startSimulation();
//            }
//        });
//
//        display.getPauseButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent e)
//            {
//                simAdapter.pauseSimulation();
//            }
//        });
//
//        display.getAddAgentButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent e)
//            {
//                Random r = new Random();
//                for(int i = 0; i < 100; i++)
//                {
//                    simAdapter.createAgent(new AgentState(), new Vector3f(-75 + r.nextInt(150), 0, -75 + r.nextInt(150)));
//                }
//            }
//        });
//
//        display.getReorganizationStrategyComboBox().addActionListener(reorganizationActionListener = new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                ReorganizationStrategy str = display.getReorganizationStrategyComboBox().getItemAt(display.getReorganizationStrategyComboBox().getSelectedIndex());
//                simAdapter.setReorganizationStrategy(str);
//                display.getMainFrame().requestFocusInWindow();
//            }
//        });
//
//        display.getReorganizationStrategyComboBox().addFocusListener(new FocusListener()
//        {
//            @Override
//            public void focusGained(FocusEvent e)
//            {
//                if(display.getSearchAutoRefreshCheckBox().isSelected() && autoRefreshTimer != null)
//                {
//                    autoRefreshTimer.cancel();
//                }
//            }
//
//            @Override
//            public void focusLost(FocusEvent e)
//            {
//                if(display.getSearchAutoRefreshCheckBox().isSelected())
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
//        display.getSearchButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                String query = display.getSearchQuery();
//                Map<Integer, SimEntity> simEntities = simRepository.find(query);
//                display.updateSearchResults(simEntities);
//            }
//        });
//
//        display.getSearchAutoRefreshCheckBox().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
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
//        display.getSaveOptionsButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                display.saveSimConfig();
//                simAdapter.notifySimPropertiesChange();
//            }
//        });
//
//        display.getSaveVisConfigButton().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                display.saveVisConfigButton();
//                simAdapter.notifySimPropertiesChange();
//            }
//        });
//
//        display.getConnectMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                CommunicationModuleProvider.connect(display.getMainFrame());
//                display.updateControlPanelState(CommunicationModuleProvider.isConnected());
//            }
//        });
//
//        display.getDisconnectMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                CommunicationModuleProvider.disconnect();
//                SimRepository.clear();
//                display.updateControlPanelState(CommunicationModuleProvider.isConnected());
//            }
//        });
//
//        // display.getNewEnvironmentMenuItem().addActionListener(new ActionListener()
//        // {
//        // @Override
//        // public void actionPerformed(ActionEvent e)
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
//        // //simAdapter.startSimulation();
//        // }
//        // }
//        // }
//        // });
//
//        display.getLoadEnvironmentMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                if(LoadEnvironmentXMLDialog.isLoading(display.getMainFrame()))
//                {
//                    File file = LoadEnvironmentXMLDialog.getEnvFile(display.getMainFrame());
//                    if(file != null)
//                    {
//                        // simAdapter.pauseSimulation();
//                        simAdapter.loadEnvironmentXML(file);
//                        SimRepository.clear();
//                        display.updateControlPanelState(true);
//                        // simAdapter.startSimulation();
//                    }
//                }
//            }
//        });
//
//        display.getSaveEnvironmentMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                File file = SaveEnvironmentDialog.getFile(display.getMainFrame());
//                if(file != null)
//                {
//                    // simAdapter.pauseSimulation();
//                    simAdapter.saveEnvironment(file);
//                    // simAdapter.startSimulation();
//                }
//            }
//        });
//
//        display.getSaveSimulationMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                File file = SaveSimulationDialog.getFile(display.getMainFrame());
//                if(file != null)
//                {
//                    // simAdapter.pauseSimulation();
//                    simAdapter.saveSimulationSnapshot(file);
//                    // simAdapter.startSimulation();
//                }
//            }
//        });
//
//        display.getLoadSimulationMenuItem().addActionListener(new ActionListener()
//        {
//            @Override
//            public void actionPerformed(ActionEvent e)
//            {
//                if(LoadSimulationDialog.isLoading(display.getMainFrame()))
//                {
//                    simAdapter.pauseSimulation();
//
//                    File file = LoadSimulationDialog.getEnvFile(display.getMainFrame());
//
//                    if(file != null)
//                    {
//                        simAdapter.loadSimulationSnapshot(file);
//                        SimRepository.clear();
//                        display.updateControlPanelState(true);
//                        // simAdapter.startSimulation();
//                    }
//                }
//            }
//        });
//
//        registerHandler(eventBus.addHandler(SimSummaryUpdateEvent.TYPE, new SimSummaryUpdateEventHandler()
//        {
//            @Override
//            public void onSimSummaryUpdateEvent(SimSummaryUpdateEvent event)
//            {
//                display.getReorganizationStrategyComboBox().removeActionListener(reorganizationActionListener);
//                display.setSimulationSummary(event.getSimSummary(), simRepository.getAgentsCount(), simRepository.getEnvObjectsCount());
//                display.getReorganizationStrategyComboBox().addActionListener(reorganizationActionListener);
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
//        public JButton getAddAgentButton();
//
//        public JButton getStartButton();
//
//        public JButton getPauseButton();
//
//        public JButton getSearchButton();
//
//        public JCheckBox getSearchAutoRefreshCheckBox();
//
//        public JButton getSaveOptionsButton();
//
//        public JMenuItem getConnectMenuItem();
//
//        public JMenuItem getDisconnectMenuItem();
//
//        public JComboBox<ReorganizationStrategy> getReorganizationStrategyComboBox();
//
//        public void saveSimConfig();
//
//        public JButton getSaveVisConfigButton();
//
//        public void saveVisConfigButton();
//
//        // public JMenuItem getNewEnvironmentMenuItem();
//
//        public JMenuItem getLoadEnvironmentMenuItem();
//
//        public JMenuItem getSaveEnvironmentMenuItem();
//
//        public JMenuItem getSaveSimulationMenuItem();
//
//        public JMenuItem getLoadSimulationMenuItem();
//
//        public JFrame getMainFrame();
//    }
//}
