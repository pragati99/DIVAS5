package edu.utdallas.mavs.evacuation.visualization.vis3D.vo;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import edu.utdallas.mavs.divas.core.config.VisConfig;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.evacuation.simulation.config.EvacuationVisConfig;
import edu.utdallas.mavs.evacuation.simulation.config.SkyboxEnum;

/**
 * This class describes a factory for visualized environment objects.
 */
public class EnvObjectFactory
{
    /**
     * This is a factory method for visualized environment objects.
     * 
     * @param state
     *        an environment object state to be factored into a visualized event
     * @return a spatial associated with the newly created visualized environment object
     */
    public static Spatial createEnvObjectVO(EnvObjectState state)
    {
        Spatial envObjModel;
        SkyboxEnum skybox = VisConfig.getInstance().getCustomProperty(EvacuationVisConfig.SKYBOX);
        if(skybox.equals(SkyboxEnum.Night_Stars) || VisConfig.getInstance().getCustomProperty(EvacuationVisConfig.SKYBOX).equals(SkyboxEnum.Night_Red))
        {
            envObjModel = Visualizer3DApplication.getInstance().getAssetManager().loadModel(state.getModelName().replace(".j3o", "_night.j3o"));
        }
        else
        {
            envObjModel = Visualizer3DApplication.getInstance().getAssetManager().loadModel(state.getModelName());
        }
        return envObjModel;
    }

    /**
     * @param modelName The model to get local scale for
     * @param x Scale X
     * @param y Scale Y
     * @param z Scale Z
     * @return The required local scale for the model
     */
    public static Vector3f getLocalScale(String modelName, float x, float y, float z)
    {
        if(modelName.equals("objects/Tree.mesh.j3o"))
        {
            return new Vector3f(x * 4, y * 4, z * 4);
        }

        return new Vector3f(x, y, z);
    }

}
