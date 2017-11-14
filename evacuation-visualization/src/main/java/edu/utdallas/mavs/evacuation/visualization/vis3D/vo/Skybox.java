package edu.utdallas.mavs.evacuation.visualization.vis3D.vo;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.visualization.vis3D.BaseApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.evacuation.simulation.config.EvacuationVisConfig;
import edu.utdallas.mavs.evacuation.simulation.config.SkyboxEnum;

/**
 * This class describes the skybox of the 3D visualization.
 */
public class Skybox
{
    private final static Logger logger = LoggerFactory.getLogger(Skybox.class);

    private static Spatial      sky;

    private Skybox()
    {}

    /**
     * Unloads the skybox in the visualization
     */
    public static void unloadSky()
    {
        try
        {
            Visualizer3DApplication.getInstance().getApp().enqueue(new Callable<Object>()
            {

                @Override
                public Object call() throws Exception
                {
                    dettachSky();
                    return null;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void dettachSky()
    {
        Visualizer3DApplication.getInstance().getApp().getRootNode().detachChild(sky);
    }

    /**
     * Loads the skybox in the visualization
     */
    public static void loadSky()
    {

        try
        {
            Visualizer3DApplication.getInstance().getApp().enqueue(new Callable<Object>()
            {

                @Override
                public Object call() throws Exception
                {
                    updateSky();
                    return null;
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void updateSky()
    {
        Application app = Visualizer3DApplication.getInstance().getApp();
        AssetManager assetManager = app.getAssetManager();

        SkyboxEnum skyboxName = VisConfig.getInstance().getCustomProperty(EvacuationVisConfig.SKYBOX);
        String skyboxPath = String.format("skybox/%s/", skyboxName.toString());

        try
        {
            if(skyboxName.equals(SkyboxEnum.Sunny) || skyboxName.equals(SkyboxEnum.Sunny_Nature) || skyboxName.equals(SkyboxEnum.Cloudy) || skyboxName.equals(SkyboxEnum.Dusk) || skyboxName.equals(SkyboxEnum.Night_Red)
                    || skyboxName.equals(SkyboxEnum.Night_Stars) || skyboxName.equals(SkyboxEnum.Rise) || skyboxName.equals(SkyboxEnum.Sunny_Sea) || skyboxName.equals(SkyboxEnum.Sunset))
            {
                Texture west = assetManager.loadTexture(String.format("%sW.jpg", skyboxPath));
                Texture east = assetManager.loadTexture(String.format("%sE.jpg", skyboxPath));
                Texture north = assetManager.loadTexture(String.format("%sN.jpg", skyboxPath));
                Texture south = assetManager.loadTexture(String.format("%sS.jpg", skyboxPath));
                Texture up = assetManager.loadTexture(String.format("%sU.jpg", skyboxPath));
                Texture down = assetManager.loadTexture(String.format("%sD.jpg", skyboxPath));

                sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down, Vector3f.UNIT_XYZ);
            }
            else
            {
                Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
                Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
                Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
                Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
                Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
                Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

                sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down, Vector3f.UNIT_XYZ);
            }

            if(sky != null)
            {
                sky.setQueueBucket(Bucket.Sky);

                Node node = ((BaseApplication<?,?>) app).getRootNode();
                node.attachChild(sky);
            }

        }
        catch(Exception e)
        {
            logger.error("Error occured when loading the skybox.");
        }
    }
}
