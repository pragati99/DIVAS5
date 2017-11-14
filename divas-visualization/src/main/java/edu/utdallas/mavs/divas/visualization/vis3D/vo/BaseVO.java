package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.VisualizerTask;

/**
 * This class describes an abstract visualized object (VO)
 */
public abstract class BaseVO extends Node
{
    private final static Logger logger      = LoggerFactory.getLogger(BaseVO.class);

    /**
     * The current cycle number of the simulation from where the state of this VO
     */
    protected long              cycle;

    /**
     * Flag that indicates if this VO is attached to the root node of the application
     */
    protected boolean           isAttached  = false;

    /**
     * Flag that indicates if this VO is being modified by the user
     */
    protected boolean           isModifying = false;

    /**
     * Constructs a new VO
     * 
     * @param nodeName
     *        the name of the VO
     * @param cycle
     *        the current cycle of the simulation
     */
    public BaseVO(String nodeName, long cycle)
    {
        super(nodeName);
        this.cycle = cycle;
        enqueueAttachSpatial();
    }

    /**
     * Constructs a new VO
     */
    public BaseVO()
    {}

    /**
     * Delegate method that attach the spatial of the VO.
     */
    protected void attachSpatial()
    {
        setShadowMode(ShadowMode.CastAndReceive);
        if(Visualizer3DApplication.getInstance().getVisRootNode() != null)
        {
            if(this == null)
                logger.error("this is null");
            Visualizer3DApplication.getInstance().getVisRootNode().attachChild(this);
            isAttached = true;
        }
    }

    /**
     * Delegate method that detach the spatial of the VO.
     */
    protected void detachSpatial()
    {
        removeHUDInfo();
        Visualizer3DApplication.getInstance().getVisRootNode().detachChild(this);
        isAttached = false;
    }

    /**
     * Delegate method that update the state of the VO.
     */
    protected abstract void updateState();

    /**
     * Enqueue updates to VO node in the main visualizer thread. Required in
     * JME3.
     * 
     * @param cycle
     *        the cycle number of the update
     */
    public void enqueueStateUpdate(long cycle)
    {
        Callable<Object> task = new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                if(!isAttached && !isModifying)
                {
                    attachSpatial();
                }
                updateState();
                return null;
            }
        };

        Visualizer3DApplication.getInstance().getApp().enqueueTask(new VisualizerTask(task, cycle));
    }

    /**
     * Enqueue new object attachment to VO node in the main visualizer thread.
     * Required in JME3.
     */
    public void enqueueAttachSpatial()
    {
        Callable<Object> task = new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                if(!isAttached)
                    attachSpatial();
                return null;
            }
        };
        Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, 0));
    }

    /**
     * Enqueue this object deattachment to VO node in the main visualizer thread.
     * Required in JME3.
     */
    public void enqueueDetachSpatial()
    {
        Callable<Object> task = new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                detachSpatial();
                return null;
            }
        };
        Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, 0));
    }

    /**
     * Enqueue this object deattachment to VO node in the main visualizer thread.
     * Required in JME3.
     */
    public void destroy()
    {
        enqueueDetachSpatial();
    }

    /**
     * Returns the visualizer asset manager
     * 
     * @return AssetManager
     */
    protected AssetManager getAssetManager()
    {
        return Visualizer3DApplication.getInstance().getAssetManager();
    }

    /**
     * Changes the modifying status of this object.
     * 
     * @param isModifying
     *        a boolean indicating if this object is being modified by the user
     */
    public void setModifying(boolean isModifying)
    {
        this.isModifying = isModifying;
    }

    /**
     * Gets the application GUI node
     * 
     * @return the application GUI node
     */
    protected Node getGuiNode()
    {
        return Visualizer3DApplication.getInstance().getApp().getGuiNode();
    }

    /**
     * Updates HUD information for this VO
     */
    public void updateHUD()
    {}

    /**
     * Destroys HUD information for this VO
     */
    public void removeHUDInfo()
    {}

    /**
     * Indicates if this VO has HUD information to display
     * 
     * @return true if it has HUD information, false otherwise
     */
    public boolean hasHUDInformation()
    {
        return false;
    }
}
