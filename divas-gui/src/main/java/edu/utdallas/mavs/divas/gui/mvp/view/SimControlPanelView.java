//package edu.utdallas.mavs.divas.gui.mvp.view;
//
//import java.util.Map;
//
//import javafx.scene.Parent;
//
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.JMenuItem;
//
//import edu.utdallas.mavs.divas.core.client.dto.SimDto;
//import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
//import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
//import edu.utdallas.mavs.divas.gui.mvp.presenter.SimControlPanelPresenter;
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.SwingParent;
//import edu.utdallas.mavs.divas.gui.mvp.view.swing.panels.SimPanel;
//
//public class SimControlPanelView extends SwingParent<SimPanel> implements SimControlPanelPresenter.Display
//{
//    public SimControlPanelView()
//    {
//        super(new SimPanel());
//    }
//
//    @Override
//    public Parent asParent()
//    {
//        return this;
//    }
//
//    @Override
//    public void setSimulationSummary(SimDto simulationSummary, int agentCount, int envObjCount)
//    {
//        contentPanel.setSimulationSummary(simulationSummary, agentCount, envObjCount);
//    }
//
//    @Override
//    public void updateControlPanelState(boolean connected)
//    {
//        contentPanel.updateControls(connected);
//        connectionMenu.updateMenu(connected);
//        visualizerMenu.updateMenu(connected);
//    }
//
//    @Override
//    public JButton getStartButton()
//    {
//        return contentPanel.getStartButton();
//    }
//
//    @Override
//    public JButton getPauseButton()
//    {
//        return contentPanel.getPauseButton();
//    }
//
//    @Override
//    public JButton getAddAgentButton()
//    {
//        return contentPanel.getAddAgentButton();
//    }
//
//    @Override
//    public JButton getSearchButton()
//    {
//        return contentPanel.getSearchButton();
//    }
//
//    @Override
//    public JCheckBox getSearchAutoRefreshCheckBox()
//    {
//        return contentPanel.getSearchAutoRefreshCheckBox();
//    }
//
//    @Override
//    public JButton getSaveOptionsButton()
//    {
//        return contentPanel.getSaveOptionsButton();
//    }
//
//    @Override
//    public JMenuItem getConnectMenuItem()
//    {
//        return connectionMenu.getConnectMenuItem();
//    }
//
//    @Override
//    public JMenuItem getDisconnectMenuItem()
//    {
//        return connectionMenu.getDisconnectMenuItem();
//    }
//
//    @Override
//    public JComboBox<ReorganizationStrategy> getReorganizationStrategyComboBox()
//    {
//        return contentPanel.getReorganizationStrategyComboBox();
//    }
//
//    @Override
//    public void setSimulationStatus(boolean running)
//    {
//        if(running)
//            contentPanel.setRunning();
//        else
//            contentPanel.setPaused();
//    }
//
//    @Override
//    public void updateSearchResults(Map<Integer, SimEntity> simEntities)
//    {
//        contentPanel.updateSearchResults(simEntities);
//    }
//
//    @Override
//    public void refreshSearchResults(Map<Integer, SimEntity> simEntities)
//    {
//        contentPanel.refreshSearchResults(simEntities);
//    }
//
//    @Override
//    public String getSearchQuery()
//    {
//        return contentPanel.getSearchQuery();
//    }
//
//    @Override
//    public void saveSimConfig()
//    {
//        contentPanel.saveSimConfig();
//    }
//
//    @Override
//    public JButton getSaveVisConfigButton()
//    {
//        return getVisualizerMenu().getSaveVisConfigButton();
//    }
//
//    @Override
//    public void saveVisConfigButton()
//    {
//        getVisualizerMenu().saveVisConfig();
//    }
//
////    @Override
////    public JMenuItem getNewEnvironmentMenuItem()
////    {
////        return fileMenu.getNewEnvironmentMenuItem();
////    }
//    
//    @Override
//    public JMenuItem getLoadEnvironmentMenuItem()
//    {
//        return fileMenu.getLoadEnvironmentMenuItem();
//    }
//    
//    @Override
//    public JMenuItem getSaveEnvironmentMenuItem()
//    {
//        return fileMenu.getSaveEnvironmentMenuItem();
//    }
//
//    @Override
//    public JMenuItem getSaveSimulationMenuItem()
//    {
//        return fileMenu.getSaveSimulationMenuItem();
//    }
//
//    @Override
//    public JMenuItem getLoadSimulationMenuItem()
//    {
//        return fileMenu.getLoadSimulationMenuItem();
//    }
//
//    @Override
//    public JFrame getMainFrame()
//    {
//        return getFrame();
//    }
//}
