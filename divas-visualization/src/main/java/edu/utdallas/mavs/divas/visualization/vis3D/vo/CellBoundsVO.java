package edu.utdallas.mavs.divas.visualization.vis3D.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import edu.utdallas.mavs.divas.core.sim.env.CellBounds;
import edu.utdallas.mavs.divas.core.sim.env.CellID;
import edu.utdallas.mavs.divas.core.sim.env.CellMap;
import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.engine.VisualizerTask;
import edu.utdallas.mavs.divas.visualization.vis3D.utils.VisToolbox;

/**
 * This class represents a cell bounds VO.
 */
public class CellBoundsVO extends BaseVO
{
    /** add all cell bounds to this node which is added to visNode, this allows us to easily enable/disable showing cell bounds **/
    protected static Node            allCellBoundsNode = new Node("AllCellBoundariesNode");

    protected static boolean         isShown           = false;

    protected float                  opacity           = .15f;

    protected ColorRGBA              outlineColor      = ColorRGBA.White;

    protected Map<CellID, ColorRGBA> cellColors        = new HashMap<CellID, ColorRGBA>();

    /**
     * Constructs a cell VO
     * 
     * @param id the id of the cell
     * @param bounds the bounds of the cell
     * @param cycle the simulation cycle number associated with this cell state
     */
    public CellBoundsVO()
    {
        this.name = "CellBoundsVO";
        allCellBoundsNode.attachChild(this);
    }

    public void setBounds(final CellMap cellMap, long cycles)
    {
        if(cycles > this.cycle)
        {
            this.cycle = cycles;
            Callable<Object> task = new Callable<Object>()
            {
                @Override
                public Object call() throws Exception
                {
                    detachAllChildren();
                    for(CellID cellID : cellMap.getLeafBounds().keySet())
                    {
                        setBounds(cellMap.getLeafBounds().get(cellID), cellID);
                    }

                    return null;
                }
            };
            Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, 0));

        }
    }

    /**
     * Sets the bounds of the cell associated with this VO
     * 
     * @param bounds the new bounds of this cell
     * @param cellID
     */
    public void setBounds(CellBounds bounds, CellID cellID)
    {
        Geometry cell;
        Box box = new Box(new Vector3f((float) bounds.getMinX(), bounds.getMin_Y(), (float) bounds.getMinY()), new Vector3f((float) bounds.getMaxX(), .3f, (float) bounds.getMaxY()));
        cell = new Geometry("Cell", box);
        Material mat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        if(!cellColors.containsKey(cellID))
        {
            cellColors.put(cellID, VisToolbox.getRandomColor(0.3f));
        }
        ColorRGBA color = cellColors.get(cellID);
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);
        cell.setMaterial(mat);
        cell.setQueueBucket(Bucket.Translucent);
        attachChild(cell);

        // Geometry outline;
        // Box outBox = new Box(new Vector3f((float) bounds.getMinX(), bounds.getMin_Y(), (float) bounds.getMinY()), new Vector3f((float) bounds.getMaxX(), 20f, (float) bounds.getMaxY()));
        // outline = new Geometry("OutCell", outBox);
        // Material outMat = new Material(Visualizer3DApplication.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        // outMat.setColor("Color", new ColorRGBA(color.r, color.g, color.b, 0.3f));
        // outMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        // outMat.getAdditionalRenderState().setDepthWrite(false);
        // outline.setMaterial(outMat);
        // outline.setQueueBucket(Bucket.Translucent);
        // attachChild(outline);
    }

    public static void show()
    {
        Callable<Object> task = new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                Visualizer3DApplication.getInstance().getVisRootNode().attachChild(allCellBoundsNode);
                isShown = true;
                return null;
            }
        };
        Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, 0));
    }

    public static void hide()
    {
        Callable<Object> task = new Callable<Object>()
        {
            @Override
            public Object call() throws Exception
            {
                Visualizer3DApplication.getInstance().getVisRootNode().detachChild(allCellBoundsNode);
                isShown = false;
                return null;
            }
        };
        Visualizer3DApplication.getInstance().getApp().enqueue(new VisualizerTask(task, 0));
    }

    @Override
    protected void updateState()
    {
        // do not update states for this VO
    }

    public static boolean isShown()
    {
        return isShown;
    }
}
