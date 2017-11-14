//package edu.utdallas.mavs.divas.gui.mvp.view;
//
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.layout.StackPane;
//import edu.utdallas.mavs.divas.gui.DivasGuiApplication;
//import edu.utdallas.mavs.divas.gui.mvp.presenter.SimulationContainerPresenter;
//
//public class SimulationContainerView extends Parent implements SimulationContainerPresenter.Display
//{
//    private StackPane stackPane;
//
//    public SimulationContainerView()
//    {
//        initialize();
//    }
//
//    @Override
//    public void showParent(Parent parent)
//    {
//        parent.toFront();
//
//        for(Node node : parent.getChildrenUnmodifiable())
//        {
//            node.requestFocus();
//        }
//
//        DivasGuiApplication.showStage(parent.isVisible());
//    }
//
//    @Override
//    public void addParent(Parent parent)
//    {
//        stackPane.getChildren().add(parent);
//    }
//
//    @Override
//    public void removeParent(Parent parent)
//    {
//        stackPane.getChildren().remove(parent);
//    }
//
//    @Override
//    public Parent asParent()
//    {
//        return this;
//    }
//
//    private void initialize()
//    {
//        stackPane = new StackPane();
//        this.getChildren().add(stackPane);
//    }
//}
