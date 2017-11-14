package edu.utdallas.mavs.divas.visualization.vis3D.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import edu.utdallas.mavs.divas.visualization.vis3D.vo.BaseVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.InterpolatedVO;

/**
 * This class describes the base application state of the 3D visualizer.
 * <p>
 * It contains the visualization root node, to which visualized objects (VOs) are attached to be visualized by the 3D visualizer, and the editing root node, to which editable VOs are attached to be modified by the user interacting with the
 * visualizer.
 * <p>
 * This application state is also responsible for the interpolation of VOs, allowing for the VOs to move smoothly.
 */
public class DivasAppState extends AbstractAppState
{
    private static SimpleApplication app;
    private Node                     visRootNode;
    private Node                     editRootNode;

    @Override
    public void update(float tpf)
    {}

    @Override
    public void stateDetached(AppStateManager stateManager)
    {
        super.stateDetached(stateManager);
        app.getRootNode().detachChild(visRootNode);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        DivasAppState.app = (SimpleApplication) app;
        if(visRootNode == null)
            visRootNode = new Node();
        if(editRootNode == null)
            editRootNode = new Node();
        ((SimpleApplication) app).getRootNode().attachChild(visRootNode);
        ((SimpleApplication) app).getRootNode().attachChild(editRootNode);
    }

    /**
     * Gets DIVAs application state of the 3D visualizer.
     * 
     * @return DIVAs application state
     */
    public DivasAppState getAppState()
    {
        return this;
    }

    /**
     * Gets the visualizer root node, to which the VOs are attached.
     * 
     * @return the root node of the 3D visualizer
     */
    public Node getVisRootNode()
    {
        return visRootNode;
    }

    /**
     * Gets the editing root node, to which editables VOs are attached.
     * 
     * @return the editing root node of the 3D visualizer
     */
    public Node getEditRootNode()
    {
        return editRootNode;
    }

    /**
     * Interpolates each VO based on the current time of the rendering loop of the 3D visualizer.
     * 
     * @param now the current time of the visualizer's update loop
     */
    public void interpolate(long now)
    {
        if(visRootNode.getChildren() == null)
            return;

        for(Spatial child : visRootNode.getChildren())
        {
            if(child != null)
            {
                if(child instanceof InterpolatedVO)
                {
                    ((InterpolatedVO) child).interpolate(now);
                }
            }
        }

        if(editRootNode.getChildren() == null)
            return;

        for(Spatial child : editRootNode.getChildren())
        {
            if(child != null)
            {
                if(child instanceof InterpolatedVO)
                {
                    ((InterpolatedVO) child).interpolate(now);
                }
            }
        }
    }

    /**
     * Updates each VO's HUD based on the current selection status of the VO.
     */
    public void updateHUD()
    {
        if(visRootNode.getChildren() == null)
            return;

        for(Spatial child : visRootNode.getChildren())
        {
            if(child != null)
            {
                if(child instanceof BaseVO)
                {
                    BaseVO vo = (BaseVO) child;

                    if(vo.hasHUDInformation())
                    {
                        vo.updateHUD();
                    }
                }
            }
        }
    }

}
