package edu.utdallas.mavs.divas.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.apache.log4j.PropertyConfigurator;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.utdallas.mavs.divas.gui.guice.GuiModule;
import edu.utdallas.mavs.divas.gui.mvp.presenter.SimulationContainerPresenter;
import edu.utdallas.mavs.divas.gui.mvp.view.swing.StyleHelper;
import edu.utdallas.mavs.divas.gui.services.LwjglApplication;
import edu.utdallas.mavs.divas.gui.services.SimUpdateHandler;

public abstract class DivasGuiApplication extends Application
{
    private static Stage               stage;

    private static DivasGuiApplication instance;

    public abstract String getVis2DMainClass();

    public abstract String getVis3DMainClass();

    @Override
    public void start(Stage primaryStage)
    {
        // configure logging properties
        PropertyConfigurator.configure("log4j.properties");

        DivasGuiApplication.stage = primaryStage;
        primaryStage.setTitle(StyleHelper.TITLE);

        // forces lwjgl natives to be unpacked
        LwjglApplication.setup();

        final Injector injector = Guice.createInjector(com.google.inject.Stage.PRODUCTION, new GuiModule());
        final SimulationContainerPresenter simulationContainerPresenter = injector.getInstance(SimulationContainerPresenter.class);
        final SimUpdateHandler simUpdateHandler = injector.getInstance(SimUpdateHandler.class);
        simUpdateHandler.start();

        StackPane root = new StackPane();
        Scene scene = new Scene(root, StyleHelper.WIDTH, StyleHelper.HEIGHT);
        root.getChildren().add(simulationContainerPresenter.getDisplay().asParent());

        scene.getStylesheets().add("/style/style.css");
        primaryStage.setScene(scene);
        instance = this;
    }

    public static DivasGuiApplication getInstance()
    {
        return instance;
    }

    public static void showStage(boolean visible)
    {
        if(visible)
            stage.show();
        else
            stage.hide();
    }
}
*/