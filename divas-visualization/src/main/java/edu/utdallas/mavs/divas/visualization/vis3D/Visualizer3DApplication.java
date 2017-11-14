package edu.utdallas.mavs.divas.visualization.vis3D;

import com.google.inject.Inject;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import edu.utdallas.mavs.divas.core.client.SimAdapter;
import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.DivasAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.spectator.VisualSpectator;

/**
 * This class describes the 3D visualizer singleton class.
 * <p>
 * It contains the main components of the 3D visualizer.
 * 
 * @param <A> the application type
 */
public abstract class Visualizer3DApplication<A extends BaseApplication<?, ?>>
{
    /**
     * The JMe3 application, responsible for the 3D visualization of the simulation
     */
    protected final A                           app;

    /**
     * This component's singleton instance
     */
    protected static Visualizer3DApplication<?> instance;

    /**
     * The adapter for communicating with the simulation
     */
    protected final SimAdapter                  simClientAdapter;

    /**
     * handler for processing incoming update messages from the simulation
     */
    protected final VisualSpectator             spectator;

    /**
     * The visualizer configuration settings
     */
    private static VisConfig                    visConfig = VisConfig.getInstance();

    /**
     * The {@code Visualizer3DApplication} constructor.
     * 
     * @param visConfig user-defined settings for the visualizer
     * @param simClientAdapter adapter for communicating with the simulation
     * @param app the jme3 application
     * @param spectator handler for processing incoming update messages from the simulation
     */
    @Inject
    public Visualizer3DApplication(SimAdapter simClientAdapter, A app, VisualSpectator spectator)
    {
        this.app = app;
        this.simClientAdapter = simClientAdapter;
        this.spectator = spectator;
        instance = this;
    }

    /**
     * Gets the singleton instance of the 3D visualizer.
     * 
     * @return the 3D visualizer singleton instance
     */
    public static Visualizer3DApplication<?> getInstance()
    {
        return instance;
    }

    /**
     * Gets the JMe3 application asset manager.
     * 
     * @return the application asset manager, used for manipulating and loading graphical resources
     */
    public AssetManager getAssetManager()
    {
        return instance.app.getAssetManager();
    }

    /**
     * Gets the JMe3 application root node, to which the visualized objects are attached.
     * 
     * @return the visualizer root node
     */
    public Node getVisRootNode()
    {
        return app.getStateManager().getState(DivasAppState.class).getVisRootNode();
    }

    /**
     * Gets the singleton instance of the JMe3 application.
     * 
     * @return the instance of the JMe3 application
     */
    public A getApp()
    {
        return app;
    }

    /**
     * Gets the JMe3 application input manager.
     * 
     * @return the application's input manager, used to interact with the JMe3 application
     */
    public InputManager getInputManager()
    {
        return app.getInputManager();
    }

    /**
     * Gets the JMe3 application camera handler.
     * 
     * @return the application's camera handler, allowing for the selection of different camera modes.
     */
    public Camera getCamera()
    {
        return app.getCamera();
    }

    /**
     * Gets the JMe3 application view port.
     * 
     * @return the application's view port, which is used for the on screen statistics and FPS
     */
    public ViewPort getViewPort()
    {
        return app.getViewPort();
    }

    /**
     * Gets the application's simulation commander.
     * 
     * @return the simulation commander, which is used for communicating with the simulation
     */
    public SimAdapter getSimCommander()
    {
        return simClientAdapter;
    }

    /**
     * Applies user-defined configuration settings and starts the 3D visualizer.
     */
    public void start()
    {
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Divas 3D Visualizer");
        settings.setFullscreen(visConfig.fullscreen);
        settings.setWidth((visConfig.display_resolution != null) ? visConfig.display_resolution.width : 1280);
        settings.setHeight((visConfig.display_resolution != null) ? visConfig.display_resolution.height : 768);
        settings.setDepthBits(24);
        settings.setFrequency((visConfig.display_resolution != null) ? visConfig.display_resolution.frequency : 75);
        settings.setVSync(true);
        settings.setSamples(visConfig.antialiasing);
        app.setSettings(settings);

        app.start();

        while(!app.getContext().isRenderable() || !app.isInitialized())
        {
            // wait until the application is renderable and initialized
        }

        spectator.start();
    }

    /**
     * Stops the 3D visualizer spectator.
     */
    public void stop()
    {
        spectator.stop();
    }

    /**
     * Gets the visualizer configuration settings
     * 
     * @return visconfig
     */
    public static VisConfig getVisConfig()
    {
        return visConfig;
    }
}
