package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.bounding.BoundingSphere;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.shader.VarType;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.utils.physics.VisionHelper;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;

/**
 * This class describes a visualized agent
 * 
 * @param <S>
 *        the agent state type
 */
public abstract class AgentVO<S extends AgentState> extends InterpolatedVO
{
    /**
     * Agents models directory path
     */
    protected static final String AGENTS_MODELS_DIR   = "agents/";

    /**
     * The animation channel of this VO
     */
    protected AnimChannel         channel;

    /**
     * The animation control of this VO
     */
    protected AnimControl         control;

    /**
     * Flag indicating if this VO has been selected by the user
     */
    protected Boolean             selected            = false;

    /**
     * Flag indicating if this VO is in camera mode (agent camera mode)
     */
    protected Boolean             camMode             = false;

    /**
     * The agent state associated with this VO
     */
    protected S                   state;

    /**
     * The previous FOV of this VO
     */
    protected float               prevFov             = Float.NEGATIVE_INFINITY;

    /**
     * The previous visible distance of this VO
     */
    protected float               prevVisibleDistance = Float.NEGATIVE_INFINITY;

    /**
     * The selection light of this VO, used to highlight the VO selected by the user
     */
    protected AmbientLight        trackingLight;

    /**
     * The vision cone of this VO
     */
    protected List<Geometry>      visionCone          = new ArrayList<Geometry>();

    /**
     * Flag indicating if the vision cone of this VO is being shown
     */
    protected boolean             visionConeEnabled;

    /**
     * This VO spatial
     */
    protected Spatial             agentModel;

    /**
     * The current rotation of this VO
     */
    protected Quaternion          rotation;

    /**
     * For debugging vision. If true, shows vision rays.
     */
    private boolean               debugVisionEnabled  = false;

    /**
     * Context selection status of this VO
     */
    protected boolean             contextSelected;

    /**
     * This VO's selection ID
     */
    protected BitmapText          selectionID;

    /**
     * Creates a new agent VO
     * 
     * @param state
     *        the agent state to be associated with this VO
     * @param cycle
     *        the simulation cycle number associated with the agent state
     */
    public AgentVO(S state, long cycle)
    {
        super("AgentVO", cycle);
        this.state = state;
    }

    /**
     * Updates this VO's visible properties (e.g., posture, vision cone)
     */
    protected abstract void updateVisibleProperties();

    /**
     * Gets the simulation model VO size ratio
     * 
     * @return the ratio
     */
    protected abstract float getModelRatio();

    /**
     * Gets the agent state associated with this VO
     * 
     * @return the agent state
     */
    public AgentState getState()
    {
        return state;
    }

    /**
     * Gets the selection light of this VO
     * 
     * @return the selection light, used when this VO is selected
     */
    public AmbientLight getSelectionLight()
    {
        return trackingLight;
    }

    /**
     * Changes the selection light of this VO
     * 
     * @param trackingLight
     *        a new selection light
     */
    public void setTrackingLight(AmbientLight trackingLight)
    {
        this.trackingLight = trackingLight;
    }

    @Override
    protected void updateState()
    {
        // setLocalTranslation(state.getPosition());
        rotation = new Quaternion();
        rotation.lookAt(state.getHeading().negate(), Vector3f.UNIT_Y);

        // setLocalScale(new Vector3f(1, 1, 1));
        updateState(state.getPosition(), rotation, new Vector3f(1, 1, 1));

        // updates VO's visible properties
        updateVisibleProperties();

    }

    /**
     * Updates the agent state associated with this VO
     * 
     * @param state
     *        the new agent state
     * @param cycle
     *        the simulation cycle number associated with this VO
     */
    public void setState(S state, long cycle)
    {
        this.state = state;
        this.cycle = cycle;        
        
        // Enqueue state update to the main JME application thread
        enqueueStateUpdate(cycle);
    }

    @Override
    protected void attachSpatial()
    {
        super.attachSpatial();

        // Visualizer3DApplication.getInstance().getVisRootNode().attachChild(myRootChild);

        setLocalTranslation(state.getPosition());

        // Set agent model path
        setupAgentModel();

        // Sets agent HUD info
        setupHUDInfo();

        agentModel.setLocalScale(state.getScale().getY() * getModelRatio());

        // Setup agent animation
        setupAnimation();

        for(int i = 0; i < ((Node) agentModel).getChildren().size(); i++)
        {
            Geometry g = (Geometry) ((Node) agentModel).getChildren().get(i);
            Material mat2 = g.getMaterial();
            mat2.setParam("Ambient", VarType.Vector4, new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f));
            mat2.setColor("Diffuse", ColorRGBA.White);
            mat2.setFloat("Shininess", 5f); // [0,128]
        }

        if(Visualizer3DApplication.getInstance().getVisRootNode() != null)
        {
            attachChild(agentModel);
            // Show model skeleton
            // showModelSkeleton();

            // Show a box instead of agent
            // showBox();
        }

        setSelected(selected);
        setSelected(contextSelected);
    }

    /**
     * Sets this VO HUD display information
     */
    protected void setupHUDInfo()
    {
        Vector3f selectionMarkPosition = getSelectionIDScreenPosition();
        selectionID = VisToolbox.createHUDText("Agent" + state.getID(), Integer.toString(state.getID()), ColorRGBA.Orange, selectionMarkPosition);
        selectionID.setCullHint(CullHint.Always);
        Visualizer3DApplication.getInstance().getApp().getGuiNode().attachChild(selectionID);
    }

    /**
     * Changes the selection status of this VO
     * 
     * @param selected
     *        a boolean flag indicating if this VO was selected by the user
     */
    public void setSelected(Boolean selected)
    {
        if(selected)
        {
            if(!this.selected && !this.contextSelected)
            {
                addLight(trackingLight);
                displayHUDInfo();
            }
        }
        else if(!this.isContextSelected())
        {
            if(trackingLight != null)
            {
                removeLight(trackingLight);
            }
            hideHUDInfo();
        }

        this.selected = selected;
    }

    /**
     * Checks if this VO is selected
     * 
     * @return a boolean flag indicating selection status
     */
    public Boolean isSelected()
    {
        return selected;
    }

    /**
     * Changes the context selection status of this VO
     * 
     * @param contextSelected
     */
    public void setContextSelected(boolean contextSelected)
    {
        if(contextSelected)
        {
            if(!this.selected && !this.contextSelected)
            {
                addLight(trackingLight);
                displayHUDInfo();
                setVisionCone(visionConeEnabled);
            }
        }
        else if(!this.isSelected())
        {
            if(trackingLight != null)
            {
                removeLight(trackingLight);
            }
            hideHUDInfo();
        }

        this.contextSelected = contextSelected;
    }

    /**
     * Gets the screen position of the selection mark
     * 
     * @return the screen position
     */
    protected Vector3f getSelectionIDScreenPosition()
    {
        Vector3f selectionMarkPosition = getLocalTranslation().add(new Vector3f(0, getLocalScale().y + 5, 0));
        Vector3f screenCoordinates = Visualizer3DApplication.getInstance().getApp().getCamera().getScreenCoordinates(selectionMarkPosition);
        return screenCoordinates;
    }

    /**
     * Gets the context selection status of this VO
     * 
     * @return true if selected, false otherwise
     */
    public boolean isContextSelected()
    {
        return contextSelected;
    }

    /**
     * Changes the camera mode of this VO
     * 
     * @param camMode
     *        a flag indicating if this VO should be in agent camera mode
     */
    public void setCamMode(Boolean camMode)
    {
        this.camMode = camMode;
    }

    /**
     * Checks if this VO is in agent camera mode
     * 
     * @return a flag indicating if this VO is in camera mode
     */
    public Boolean isCamModeOn()
    {
        return camMode;
    }

    /**
     * Checks the vision cone is enabled for this agent VO
     * 
     * @return a flag indicating if the vision cone is enabled for this VO
     */
    public Boolean isVisionConeEnabled()
    {
        return visionConeEnabled;
    }

    /**
     * Sets the vision cone for this agent VO to enable or disabled.
     * 
     * @param enabled
     *        true if the vision cone is enabled and false otherwise.
     */
    public void setVisionCone(boolean enabled)
    {
        if(visionConeEnabled && !enabled)
        {
            detachVisionCone();
        }
        else if(!visionConeEnabled && enabled)
        {
            attachVisionCone();
        }
    }

    /**
     * Gets the list of animations for this VO
     * 
     * @return the animations of this VO
     */
    public Collection<String> getAnimationList()
    {
        return control.getAnimationNames();
    }

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

    protected void setupAnimation()
    {

    }

    /**
     * Show a box
     */
    @SuppressWarnings("unused")
    private void showBox()
    {
        Box solidBox = new Box(new Vector3f(0, 0, 0), 0.4f, 3, 0.4f);
        Geometry solidGeometry = new Geometry("Box", solidBox);
        Material solidMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        solidMaterial.setColor("Color", ColorRGBA.randomColor());
        solidGeometry.setMaterial(solidMaterial);
        attachChild(solidGeometry);
    }

    /**
     * Show skeleton for the 3D Env Obj
     */
    @SuppressWarnings("unused")
    private void showModelSkeleton()
    {
        Box box = new Box(new Vector3f(0, state.getScale().getY(), 0), state.getScale().getX(), state.getScale().getY(), state.getScale().getZ());
        Geometry geometry = new Geometry("Box", box);
        Material material = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.randomColor());
        material.getAdditionalRenderState().setWireframe(true);
        geometry.setMaterial(material);
        geometry.setQueueBucket(Bucket.Transparent);
        attachChild(geometry);
    }

    /**
     * Attaches the vision cone to this VO
     */
    protected void attachVisionCone()
    {
        if(state.canSee())
        {
            VisionPerceptor v = (VisionPerceptor) state;
            List<Geometry> cones = VisToolbox.createVisionCone(agentModel.getLocalTranslation().add(new Vector3f(0, v.getVisionHeight(), -0.25f)), v.getVisibleDistance(), v.getFOV(), SimConfig.getInstance().vision_Circles_Count, 32);
            for(Geometry c : cones)
            {
                visionCone.add(c);
                attachChild(c);
            }
            if(debugVisionEnabled)
            {
                attachVisionConeRays();
            }
            visionConeEnabled = true;
        }
    }

    /**
     * Detaches the vision cone from this VO
     */
    protected void detachVisionCone()
    {
        if(visionCone != null)
        {
            for(Geometry g : visionCone)
            {
                detachChild(g);
            }
        }

        visionConeEnabled = false;
    }

    /**
     * Attach the vision rays used by the vision algorithm to perceive the environment
     */
    private void attachVisionConeRays()
    {
        if(state.canSee())
        {
            VisionPerceptor v = (VisionPerceptor) state;
            List<Ray> rays = VisionHelper.createVisionCone(agentModel.getLocalTranslation().add(new Vector3f(0f, v.getVisionHeight(), 0f)), (agentModel.getLocalRotation().mult(new Vector3f(0, 0, 1f))).negate(), v.getFOV(), v.getVisibleDistance(),
                    SimConfig.getInstance().vision_Circles_Count, SimConfig.getInstance().vision_Rays_Count);
            for(int i = 0; i < rays.size(); i++)
            {
                Vector3f origin = rays.get(i).getOrigin();
                Vector3f dir = rays.get(i).getDirection();
                Vector3f normalize = dir.normalize();
                Vector3f mul = normalize.mult(v.getVisibleDistance());
                Vector3f endPoint = origin.add(mul);

                Line l = new Line(origin, endPoint);
                Geometry g = new Geometry("Line", l);
                Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", ColorRGBA.Orange);
                g.setMaterial(mat);
                visionCone.add(g);
                attachChild(g);
            }
        }
    }

    @Override
    public void updateHUD()
    {
        if(selectionID == null)
            return;

        FrustumIntersect contains = Visualizer3DApplication.getInstance().getApp().getCamera().contains(new BoundingSphere(1f, getLocalTranslation()));
        if(contains == FrustumIntersect.Inside)
        {
            selectionID.setLocalTranslation(getSelectionIDScreenPosition());
            displayHUDInfo();
        }
        else
        {
            hideHUDInfo();
        }
    }

    /**
     * Hides the agent ID from screen
     */
    protected void hideHUDInfo()
    {
        if(selectionID != null)
        {
            selectionID.setCullHint(CullHint.Always);
        }

    }

    @Override
    public void removeHUDInfo()
    {
        Visualizer3DApplication.getInstance().getApp().getGuiNode().detachChild(selectionID);
    }

    /**
     * Displays HUD Information on screen
     */
    protected void displayHUDInfo()
    {
        selectionID.setCullHint(CullHint.Never);
    }

    @Override
    public boolean hasHUDInformation()
    {
        return selected || contextSelected;
    }

    @Override
    public void destroy()
    {
        super.destroy();
    }

    @Override
    protected void detachSpatial()
    {
        super.detachSpatial();
    }
}
