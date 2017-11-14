package edu.utdallas.mavs.evacuation.visualization.vis3D.vo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.bounding.BoundingBox;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.DoorObjectState;

/**
 * This class describes a visualized environment object
 */
public class EvacuationEnvObjectVO extends EnvObjectVO
{
    private final static Logger   logger                  = LoggerFactory.getLogger(EvacuationEnvObjectVO.class);

    /**
     * A list conttains the list of environemnet object models names
     */
    protected static List<String> modifiedBoundingBoxList = new ArrayList<>();

    // protected Geometry skeleton;
    //
    // protected boolean skeletonMade = false;

    /**
     * @param state
     *        the VO's environment object state
     * @param cycle
     *        the cycle of the simulation associated with the environment object state
     */
    public EvacuationEnvObjectVO(EnvObjectState state, long cycle)
    {
        super(state, cycle);
    }

    /**
     * Constructs an empty interpolated VO
     * To be called only for cloning purposes. Not to be called if the VO is to be actually created in the visualization.
     * 
     * @param state
     *        the VO's environment object state
     */
    public EvacuationEnvObjectVO(EnvObjectState state)
    {
        super(state);
    }

    @Override
    public void updateState()
    {
        /*
         * update position and rotation (handled by InterpolatedVO.updateState(vector, quaternion) to enable smooth
         * motion)
         */
        if(state.getType().equals("door") && state instanceof DoorObjectState && ((DoorObjectState) state).isOpen())
        {
            updateState(state.getPosition().add(new Vector3f(0, state.getScale().y - .5f, 0)), state.getRotation(), new Vector3f(state.getScale().x, .5f, state.getScale().z));
        }
        else if(state.getType().equals("door") && state instanceof DoorObjectState && !((DoorObjectState) state).isOpen())
        {
            updateState(state.getPosition().add(new Vector3f(0, 0, 0)), state.getRotation(), new Vector3f(state.getScale()));
        }
        else if(state.getType().equals("door")) // assume open
        {
            updateState(state.getPosition().add(new Vector3f(0, 7.5f, 0)), state.getRotation(), new Vector3f(state.getScale().x, .5f, state.getScale().z));
        }
        else
        {
            updateState(state.getPosition(), state.getRotation(), state.getScale());
        }

        // if(Visualizer3DApplication.getInstance().getApp().isDebugMode() && !isSkeletonAttached)
        // {
        // isSkeletonAttached = true;
        // // Show skeleton for the 3D Env Obj
        // showModelSkeleton();
        // }
        // else if(!Visualizer3DApplication.getInstance().getApp().isDebugMode() && isSkeletonAttached)
        // {
        // isSkeletonAttached = false;
        // hideModelSkeleton();
        // }

    }

    // /**
    // * Debugging only. Shows the skeleton of the 3D model.
    // */
    // protected void showModelSkeleton()
    // {
    // if(!skeletonMade)
    // {
    // makeModelSkeleton();
    // }
    // attachChild(skeleton);
    // }

    // /**
    // * Creates the skeleton for the environment object
    // */
    // protected void makeModelSkeleton()
    // {
    // float modelX = ((BoundingBox) envObjModel.getWorldBound()).getXExtent();
    // float modelY = ((BoundingBox) envObjModel.getWorldBound()).getYExtent();
    // float modelZ = ((BoundingBox) envObjModel.getWorldBound()).getZExtent();
    // // System.out.println("envObjModel.getLocalScale()" + envObjModel.getLocalScale());
    // // System.out.println("new Vector3f(modelX, modelY, modelZ)" + new Vector3f(modelX, modelY, modelZ));
    // Box box = new Box(new Vector3f(0, modelY, 0), modelX, modelY, modelZ);
    // skeleton = new Geometry("Box", box);
    // Material material = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    // material.setColor("Color", ColorRGBA.randomColor());
    // material.getAdditionalRenderState().setWireframe(true);
    // skeleton.setMaterial(material);
    // skeleton.setQueueBucket(Bucket.Transparent);
    // skeletonMade = true;
    // }

    // protected void hideModelSkeleton()
    // {
    // detachChild(skeleton);
    // }

    /**
     * Attaches this model geometric form
     */
    @Override
    protected void attachModel()
    {
        setLocalTranslation(state.getPosition());

        if(state.getType().equals("floor"))
        {
            isLocked = true;
        }

        // Attach 3D Models
        if(state.getType().equals("3DModel"))
        {
            attachEnvObjectModel();
        }
        else
        {
            attachGeometry();
        }
    }

    /**
     * Attaches a 3D model representing this VO
     */
    protected void attachEnvObjectModel()
    {
        envObjModel = EnvObjectFactory.createEnvObjectVO(state);

        float stateX = 1;
        float stateY = 1;
        float stateZ = 1;

        float modelX = ((BoundingBox) envObjModel.getWorldBound()).getXExtent();
        float modelY = ((BoundingBox) envObjModel.getWorldBound()).getYExtent();
        float modelZ = ((BoundingBox) envObjModel.getWorldBound()).getZExtent();

        float x = stateX / modelX;
        float y = stateY / modelY;
        float z = stateZ / modelZ;

        Vector3f localScale = EnvObjectFactory.getLocalScale(state.getModelName(), x, y, z);
        // If the model is attached before there is no need to update the bounding box because the update is cached
        if(!modifiedBoundingBoxList.contains(state.getModelName()))
        {
            BoundingBox bb = (BoundingBox) envObjModel.getWorldBound();
            modifiedBoundingBoxList.add(state.getModelName());
            bb.setYExtent(bb.getYExtent() * 2);

            envObjModel.setModelBound(bb);
            envObjModel.setLocalScale(localScale);
        }
        // If the model is cached then you should multiply the Y scale by 2, otherwise the Y access will be the half
        else
        {
            envObjModel.setLocalScale(localScale.getX(), localScale.getY() * 2, localScale.getZ());
        }

        if(Visualizer3DApplication.getInstance().getVisRootNode() != null)
        {
            attachChild(envObjModel);
        }

    }

    /**
     * Attaches a textured geometry representing this VO
     */
    protected void attachGeometry()
    {
        Box box = new Box(new Vector3f(0, 0, 0), 1, 1, 1);
        Geometry geometry = new Geometry("Box", box);
        Material material = new Material(getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        try
        {
            Texture texture = getAssetManager().loadTexture(TEXTURES_DIR + state.getMaterial() + ".jpg");

            if(state.getType().equals("wall"))
            {
                texture.setWrap(WrapMode.Repeat);

                if(state.getScale().getZ() > 5)
                {
                    box.scaleTextureCoordinates(new Vector2f(state.getScale().getZ() / 2, 2.5f));
                }
                else
                {
                    box.scaleTextureCoordinates(new Vector2f(state.getScale().getX() / 2, 2.5f));
                }
            }

            material.setTexture("DiffuseMap", texture);
        }
        catch(Exception e)
        {
            if(state.getType().equals("door"))
            {
                Texture tex1 = getAssetManager().loadTexture(TEXTURES_DIR + "plasterwall.jpg");
                material.setTexture("DiffuseMap", tex1);
                AmbientLight doorLight = VisToolbox.createAmbientLight(ColorRGBA.White.mult(2f));
                addLight(doorLight);
            }
            else
            {
                material = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                material.setColor("Color", ColorRGBA.randomColor());
            }
        }
        geometry.setMaterial(material);
        attachChild(geometry);
    }

    @Override
    protected EnvObjectVO createClone(EnvObjectState clone)
    {
        return new EvacuationEnvObjectVO(clone);
    }
}
