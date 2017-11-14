package edu.utdallas.mavs.divas.visualization.vis3D.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;

/**
 * This class describes the environment application state.
 * <p>
 * Everything that is not simulated, for example, terrain, sky, water, light, etc., is attached to this application state. This application state allows the lights of the 3D visualization to be turned on and off, having the effect of showing or
 * hiding the visualized objects attached to this application state.
 * <p>
 * There are two lights in the simulation, one direction light, representing the Sun, and another ambient light, representing the natural ambient light.
 */
public abstract class EnvironmentAppState extends AbstractAppState
{
    protected SimpleApplication app;

    @Override
    public void update(float tpf)
    {}

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        this.app = (BaseApplication<?, ?>) app;
    }

    /**
     * Removes the lights of the 3D visualization. This has the effect of hiding the VOs.
     */
    public abstract void unloadLights();

    /**
     * Adds the lights of the 3D visualization. This has the effect of showing the VOs.
     */
    public abstract void loadLights();

    /**
     * Gets the environment application state of the 3D visualizer.
     * 
     * @return the environment application state
     */
    public EnvironmentAppState getAppState()
    {
        return this;
    }

}
