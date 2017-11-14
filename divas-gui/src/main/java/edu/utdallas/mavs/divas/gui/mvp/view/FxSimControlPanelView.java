//package edu.utdallas.mavs.divas.gui.mvp.view;
//
//import java.util.Map;
//
//import javafx.beans.InvalidationListener;
//import javafx.beans.Observable;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.scene.Parent;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.CheckMenuItem;
//import javafx.scene.control.CheckMenuItemBuilder;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.Menu;
//import javafx.scene.control.MenuBar;
//import javafx.scene.control.MenuBuilder;
//import javafx.scene.control.MenuItem;
//import javafx.scene.control.MenuItemBuilder;
//import javafx.scene.input.KeyCombination;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import edu.utdallas.mavs.divas.core.client.dto.SimDto;
//import edu.utdallas.mavs.divas.core.sim.env.ReorganizationStrategy;
//import edu.utdallas.mavs.divas.gui.mvp.model.SimEntity;
//import edu.utdallas.mavs.divas.gui.mvp.presenter.FxSimControlPanelPresenter;
//
//public class FxSimControlPanelView extends Parent implements FxSimControlPanelPresenter.Display
//{
//    final Label   outputLabel = new Label();
//    final MenuBar menuBar     = new MenuBar();
//
//    public FxSimControlPanelView()
//    {
//        super();
//        VBox vbox = new VBox(20);
//
//        // Sub menus for Options->Submenu 1
//
//        MenuItem menu111 = MenuItemBuilder.create().text("blah").build();
//
//        final MenuItem menu112 = MenuItemBuilder.create().text("foo").build();
//
//        final CheckMenuItem menu113 = CheckMenuItemBuilder.create().text("Show \"foo\" item").selected(true).build();
//
//        menu113.selectedProperty().addListener(new InvalidationListener()
//        {
//
//            @Override
//            public void invalidated(Observable valueModel)
//            {
//
//                menu112.setVisible(menu113.isSelected());
//
//                System.err.println("MenuItem \"foo\" is now " + (menu112.isVisible() ? "" : "not") + " visible.");
//
//            }
//
//        });
//
//        // Options->Submenu 1 submenu
//
//        Menu menu11 = MenuBuilder.create()
//
//        .text("Submenu 1")
//
//        // .graphic(new ImageView(new Image(MenuSample.class.getResourceAsStream("menuInfo.png"))))
//
//        .items(menu111, menu112, menu113)
//
//        .build();
//
//        // Options->Submenu 2 submenu
//
//        MenuItem menu121 = MenuItemBuilder.create().text("Item 1").build();
//
//        MenuItem menu122 = MenuItemBuilder.create().text("Item 2").build();
//
//        Menu menu12 = MenuBuilder.create().text("Submenu 2").items(menu121, menu122).build();
//
//        // Options->Change Text
//
//        final String change[] = { "Change Text", "Change Back" };
//
//        final MenuItem menu13 = MenuItemBuilder.create().text(change[0]).accelerator(KeyCombination.keyCombination("Shortcut+C")).build();
//
//        menu13.setOnAction(new EventHandler<ActionEvent>()
//        {
//
//            @Override
//            public void handle(ActionEvent t)
//            {
//
//                menu13.setText((menu13.getText().equals(change[0])) ? change[1] : change[0]);
//
//                outputLabel.setText(((MenuItem) t.getTarget()).getText() + " - action called");
//
//            }
//
//        });
//
//        // Options menu
//
//        Menu menu1 = MenuBuilder.create().text("Options").items(menu11, menu12, menu13).build();
//
//        menuBar.getMenus().addAll(menu1);
//
//        this.getChildren().add(vbox);
//
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
//    {}
//
//    @Override
//    public void updateControlPanelState(boolean connected)
//    {}
//
//    @Override
//    public void setSimulationStatus(boolean running)
//    {}
//
//    @Override
//    public void updateSearchResults(Map<Integer, SimEntity> simEntities)
//    {}
//
//    @Override
//    public void refreshSearchResults(Map<Integer, SimEntity> simEntities)
//    {}
//
//    @Override
//    public String getSearchQuery()
//    {
//        return null;
//    }
//
//    @Override
//    public Button getAddAgentButton()
//    {
//        return null;
//    }
//
//    @Override
//    public Button getStartButton()
//    {
//        return null;
//    }
//
//    @Override
//    public Button getPauseButton()
//    {
//        return null;
//    }
//
//    @Override
//    public Button getSearchButton()
//    {
//        return null;
//    }
//
//    @Override
//    public CheckBox getSearchAutoRefreshCheckBox()
//    {
//        return null;
//    }
//
//    @Override
//    public Button getSaveOptionsButton()
//    {
//        return null;
//    }
//
//    @Override
//    public MenuItem getConnectMenuItem()
//    {
//        return null;
//    }
//
//    @Override
//    public MenuItem getDisconnectMenuItem()
//    {
//        return null;
//    }
//
//    @Override
//    public ComboBox<ReorganizationStrategy> getReorganizationStrategyComboBox()
//    {
//        return null;
//    }
//
//    @Override
//    public void saveSimConfig()
//    {}
//
//    @Override
//    public MenuItem getNewEnvironmentMenuItem()
//    {
//        return null;
//    }
//
//    @Override
//    public MenuItem getSaveSimulationMenuItem()
//    {
//        return null;
//    }
//
//    @Override
//    public MenuItem getLoadSimulationMenuItem()
//    {
//        return null;
//    }
//
//    @Override
//    public Stage getMainFrame()
//    {
//        return null;
//    }
//}
