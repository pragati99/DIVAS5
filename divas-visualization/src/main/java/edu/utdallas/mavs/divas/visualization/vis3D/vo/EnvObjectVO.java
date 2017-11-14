package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import com.jme3.bounding.BoundingSphere;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera.FrustumIntersect;
import com.jme3.scene.Spatial;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;

/**
 * This class describes a visualized environment object
 */
public abstract class EnvObjectVO extends InterpolatedVO
{
    /**
     * Textures directory
     */
    protected static final String TEXTURES_DIR       = "Textures/";

    /**
     * The corresponding environment object's state of this VO
     */
    protected EnvObjectState      state;

    /**
     * This VO's spatial
     */
    protected Spatial             envObjModel;

    /**
     * Flag indicating if this VO has been selected by the user
     */
    protected Boolean             selected           = false;

    /**
     * Context selection status of this VO
     */
    protected boolean             contextSelected    = false;

    /**
     * Indicates if this object is locked in the screen
     */
    protected boolean             isLocked           = false;
    /**
     * Flag indicating if the environment object skeleton is attached and visualized
     */
    protected boolean             isSkeletonAttached = false;

    /**
     * The selection light of this VO, used to highlight the VO selected by the user
     */
    protected AmbientLight        trackingLight;

    /**
     * This VO's selection ID
     */
    protected BitmapText          selectionID;

    /**
     * @param state
     *        the VO's environment object state
     * @param cycle
     *        the cycle of the simulation associated with the environment object state
     */
    public EnvObjectVO(EnvObjectState state, long cycle)
    {
        super("EnvObjectVO " + state.getID(), cycle);
        this.state = state;
        interpolated = false;
    }

    /**
     * Constructs an empty interpolated VO
     * To be called only for cloning purposes. Not to be called if the VO is to be actually created in the visualization.
     * 
     * @param state
     *        the VO's environment object state
     */
    public EnvObjectVO(EnvObjectState state)
    {
        super();
        this.state = state;
        interpolated = false;
    }

    /**
     * Attaches this model geometric form
     */
    protected abstract void attachModel();

    /**
     * Creates a VO given its state
     * 
     * @param clone the clone state of the vo
     * @return the new VO
     */
    protected abstract EnvObjectVO createClone(EnvObjectState clone);

    /**
     * Gets the environment object's state of this VO
     * 
     * @return the environment object's state
     */
    public EnvObjectState getState()
    {
        return state;
    }

    @Override
    protected void attachSpatial()
    {
        super.attachSpatial();
        setupHUDInfo();
        attachModel();
    }

    /**
     * Sets this VO HUD display information
     */
    protected void setupHUDInfo()
    {
        Vector3f selectionMarkPosition = getSelectionIDScreenPosition();
        selectionID = VisToolbox.createHUDText("EnvObj" + state.getID(), Integer.toString(state.getID()), ColorRGBA.Orange, selectionMarkPosition);
        selectionID.setCullHint(CullHint.Always);
        Visualizer3DApplication.getInstance().getApp().getGuiNode().attachChild(selectionID);
    }

    /**
     * Updates a new environment object's state to this VO
     * 
     * @param state
     *        the new environment object's state
     * @param cycle
     *        the simulation cycle number associated with the environment object state
     */
    public void setState(EnvObjectState state, long cycle)
    {
        if(!isModifying)
        {
            enqueueStateUpdate(cycle);
            this.state = state;
        }
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
     * Marks this object as selected
     * 
     * @param selected
     *        true if selected, false otherwise
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
     * Changes the context selection status of this VO
     * 
     * @param contextSelected
     */
    public void setContextSelected(boolean contextSelected)
    {
        // this.contextSelected = contextSelected;
        if(contextSelected)
        {
            if(!this.selected && !this.contextSelected)
            {
                addLight(trackingLight);
                displayHUDInfo();
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
     * Changes the selection light of this VO
     * 
     * @param trackingLight
     *        a new selection light
     */
    public void setTrackingLight(AmbientLight trackingLight)
    {
        this.trackingLight = trackingLight;
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
     * Indicates if VO is locked in the screen. Prevents dragging.
     * 
     * @return true if locked, false otherwise.
     */
    public boolean isLocked()
    {
        return isLocked;
    }

    /**
     * Copies this VO to the editing root node
     * 
     * @return new VO
     */
    public EnvObjectVO copy()
    {
        EnvObjectState clone = state.copy();
        EnvObjectVO vo = createClone(clone);
        vo.setLocalRotation(this.getLocalRotation());
        vo.setLocalScale(this.getLocalScale());
        vo.setLocalTranslation(this.getLocalTranslation());
        vo.setLocalTransform(this.getLocalTransform());

        vo.attachModel();

        return vo;
    }

    /**
     * Gets the screen position of the selection mark
     * 
     * @return the screen position
     */
    protected Vector3f getSelectionIDScreenPosition()
    {
        Vector3f selectionMarkPosition = getLocalTranslation().add(new Vector3f(0, getLocalScale().y / 2, 0));
        Vector3f screenCoordinates = Visualizer3DApplication.getInstance().getApp().getCamera().getScreenCoordinates(selectionMarkPosition);
        return screenCoordinates;
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

    @Override
    public void removeHUDInfo()
    {
        Visualizer3DApplication.getInstance().getApp().getGuiNode().detachChild(selectionID);
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
}
