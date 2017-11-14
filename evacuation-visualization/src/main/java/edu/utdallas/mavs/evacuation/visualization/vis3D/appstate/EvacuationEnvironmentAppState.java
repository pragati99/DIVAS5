package edu.utdallas.mavs.evacuation.visualization.vis3D.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.visualization.vis3D.appstate.EnvironmentAppState;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Terrain;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.effect.Water;
import edu.utdallas.mavs.evacuation.simulation.config.EvacuationVisConfig;
import edu.utdallas.mavs.evacuation.visualization.vis3D.vo.Skybox;

/**
 * This class describes the evacuation environment application state.
 * <p>
 * Everything that is not simulated, for example, terrain, sky, water, light, etc., is attached to this application state. This application state allows the lights of the 3D visualization to be turned on and off, having the effect of showing or
 * hiding the visualized objects attached to this application state.
 * <p>
 * There are two lights in the simulation, one direction light, representing the Sun, and another ambient light, representing the natural ambient light.
 */
public class EvacuationEnvironmentAppState extends EnvironmentAppState
{
    private DirectionalLight sun;
    private AmbientLight     al;

    boolean                  waterLoaded   = false;
    boolean                  terrainLoaded = false;

    @Override
    public void update(float tpf)
    {}

    @Override
    public void stateDetached(AppStateManager stateManager)
    {
        super.stateDetached(stateManager);
        Skybox.unloadSky();
        if(waterLoaded)
        {
            waterLoaded = false;
            Water.unloadWater(app);
        }
        unloadLights();
        if(terrainLoaded)
        {
            terrainLoaded = false;
            Terrain.unloadTerrain();
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        app.getCamera().setFrustumFar(20000);
        Skybox.loadSky();
        if(VisConfig.getInstance().getCustomProperty(EvacuationVisConfig.WATER))
        {
            waterLoaded = true;
            Water.loadWater(app);
        }
        loadLights();
        if(VisConfig.getInstance().getCustomProperty(EvacuationVisConfig.TERRAIN))
        {
            terrainLoaded = true;
            Terrain.loadTerrain();
        }
        app.getCamera().setLocation(new Vector3f(30, 30, 30));
        app.getCamera().lookAt(new Vector3f(-150, 15, -150), Vector3f.UNIT_Y);
    }

    @Override
    public void unloadLights()
    {
        app.getRootNode().removeLight(sun);
        app.getRootNode().removeLight(al);
    }

    @Override
    public void loadLights()
    {
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.577f, -0.577f, -0.577f));
        app.getRootNode().addLight(sun);

        al = new AmbientLight();
        // al.setColor(ColorRGBA.White.mult(2.5f));
        al.setColor(new ColorRGBA(1, 1, 1, 0));
        app.getRootNode().addLight(al);
    }
}
