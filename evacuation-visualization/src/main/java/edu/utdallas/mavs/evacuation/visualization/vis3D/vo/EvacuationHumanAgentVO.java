package edu.utdallas.mavs.evacuation.visualization.vis3D.vo;

import java.util.ArrayList;
import java.util.List;

import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;

/**
 * This class describes a visualized agent
 */
public class EvacuationHumanAgentVO extends AgentVO<EHumanAgentState>
{
    /**
     * A ratio constant to correct the mismatch between the 3D models and the simulation entity
     */
    private static final float MODEL_RATIO = 0.55f;

    /**
     * The previous posture of this VO
     */
    protected Posture          prevPosture = null;

    /**
     * The agent path of this VO
     */
    protected List<Geometry>   myPath      = new ArrayList<Geometry>();

    private boolean            agentPath   = false;

    private List<Vector3f>     lastPath    = null;

    ColorRGBA                  agentMarkColor;

    private Geometry           idSphere    = null;

    protected Node             myRootChild;

    /**
     * Creates a new agent VO
     * 
     * @param state
     *        the agent state to be associated with this VO
     * @param cycle
     *        the simulation cycle number associated with the agent state
     */
    public EvacuationHumanAgentVO(EHumanAgentState state, long cycle)
    {
        super(state, cycle);
        createIdSphere();
        myRootChild = new Node();
        AmbientLight agentLight = VisToolbox.createAmbientLight(ColorRGBA.White);
        addLight(agentLight);
    }

    @Override
    protected float getModelRatio()
    {
        return MODEL_RATIO;
    }

    protected void createIdSphere()
    {
        agentMarkColor = ColorRGBA.randomColor();
        agentMarkColor.a = .5f;
        Sphere sphere = new Sphere(30, 30, 1);
        idSphere = new Geometry("AgentPathF", sphere);
        Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.setColor("Color", agentMarkColor);
        idSphere.setMaterial(mat);

        idSphere.setQueueBucket(Bucket.Translucent);

        idSphere.setLocalTranslation(Vector3f.UNIT_Y.mult(7));
    }

    @Override
    protected void setupAgentModel()
    {
        String agentPath = new String();

        boolean lqModels = Visualizer3DApplication.getVisConfig().lowQualityModels;
        boolean animated = Visualizer3DApplication.getVisConfig().animatedModels;

        if(animated)
        {
            agentPath = AGENTS_MODELS_DIR + state.getModelName() + ((lqModels) ? "_lq" : "") + ".j3o";
        }
        else
        {
            agentPath = AGENTS_MODELS_DIR + "simple_model" + ".j3o";
        }

        agentModel = getAssetManager().loadModel(agentPath);
    }

    /**
     * Updates this VO's visible properties (e.g., posture, vision cone)
     */
    @Override
    protected void updateVisibleProperties()
    {

        updateAgentPath();

        if(Visualizer3DApplication.getVisConfig().animatedModels)
        {
            if(prevPosture != state.getPosture())
            {
                for(Posture posture : Posture.values())
                {
                    if(state.getPosture() == posture)
                    {
                        channel.setAnim(state.getPosture().toString().toLowerCase());
                        channel.setLoopMode(isLooping(posture));
                    }
                }
            }
        }

        prevPosture = state.getPosture();

        if(prevFov != state.getFOV() || prevVisibleDistance != state.getVisibleDistance())
        {
            if(visionConeEnabled)
            {
                detachVisionCone();
                attachVisionCone();
            }
        }

        prevFov = state.getFOV();
        prevVisibleDistance = state.getVisibleDistance();

    }

    private LoopMode isLooping(Posture posture)
    {
        if(posture == Posture.Death_backward || posture == Posture.Death_forward || posture == Posture.sit_g_start)
        {
            return LoopMode.DontLoop;
        }
        return LoopMode.Loop;
    }

    private void updateAgentPath()
    {
        if(pathHasChanged(lastPath, state.getAgentPath()))
        {

            if(pathIsContinue(lastPath, state.getAgentPath()))
            {
                if(myPath != null && myPath.get(0) != null)
                {
                    myRootChild.detachChild(myPath.remove(0));
                }
            }
            else
            {
                detachAgentPath();
                myPath = VisToolbox.createAgentPath(state.getPosition(), state.getAgentPath(), agentMarkColor);
                attachAgentPath();
            }
        }

        lastPath = state.getAgentPath();
    }

    private boolean pathIsContinue(List<Vector3f> oldPath, List<Vector3f> newPath)
    {
        if(oldPath == null)
        {
            return false;
        }

        if(oldPath.size() == (newPath.size() + 1))
        {
            for(int i = 0; i < newPath.size(); i++)
            {
                if(!newPath.get(i).equals(oldPath.get(i + 1)))
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    /**
     * Attaches the path
     */
    private void checkAgentPath()
    {
        // if(agentPath)
        // {
        // myRootChild.updateGeometricState();
        // }
        if(Visualizer3DApplication.getInstance().getApp().isDebugMode() && !agentPath)
        {
            // attachAgentPath();
            Visualizer3DApplication.getInstance().getVisRootNode().attachChild(myRootChild);
            agentPath = true;
            attachChild(idSphere);
        }
        else if(!Visualizer3DApplication.getInstance().getApp().isDebugMode() && agentPath)
        {
            // detachAgentPath();
            Visualizer3DApplication.getInstance().getVisRootNode().detachChild(myRootChild);
            agentPath = false;
            detachChild(idSphere);
        }
    }

    protected void attachAgentPath()
    {
        if(myPath != null)
        {
            // agentPath = true;
            for(Geometry c : myPath)
            {
                myRootChild.attachChild(c);
            }
        }
        // if(idSphere != null)
        // {
        // attachChild(idSphere);
        // }

    }

    protected void detachAgentPath()
    {
        if(myPath != null)
        {
            // agentPath = false;
            for(Geometry g : myPath)
            {
                myRootChild.detachChild(g);
            }
        }
        // if(idSphere != null)
        // {
        // detachChild(idSphere);
        // }

    }

    @Override
    protected void setupAnimation()
    {
        if(Visualizer3DApplication.getVisConfig().animatedModels)
        {
            control = agentModel.getControl(AnimControl.class);
            channel = control.createChannel();
            channel.setAnim((Posture.Walk).toString().toLowerCase());
        }
    }

    private boolean pathHasChanged(List<Vector3f> lastPath2, List<Vector3f> agentPath2)
    {
        if(lastPath2 == null)
        {
            return true;
        }
        if(lastPath2.size() != agentPath2.size())
        {
            return true;
        }
        for(int i = 0; i < lastPath2.size(); i++)
        {

            Vector3f last = lastPath2.get(i);
            Vector3f current = agentPath2.get(i);
            if(last != null && current != null)
            {
                if(!last.equals(current))
                {
                    return true;
                }
            }
            else if(last == null && current != null)
            {
                return true;
            }
            else if(last != null && current == null)
            {
                return true;
            }
        }
        return false;

    }

    @Override
    protected void detachSpatial()
    {
        detachAgentPath();
        Visualizer3DApplication.getInstance().getVisRootNode().detachChild(myRootChild);
        super.detachSpatial();
    }

    @Override
    protected void updateSelfObjects()
    {
        checkAgentPath();
    }
}
