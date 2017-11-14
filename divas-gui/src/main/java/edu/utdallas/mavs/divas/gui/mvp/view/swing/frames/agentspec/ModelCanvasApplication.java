package edu.utdallas.mavs.divas.gui.mvp.view.swing.frames.agentspec;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

@SuppressWarnings("javadoc")
public class ModelCanvasApplication extends SimpleApplication
{

    private final int    GRID_LINES   = 100;
    private final float  GRID_SPACING = 2.5f;
    private Node         grid;
    private Spatial      model;
    private AnimControl  control;
    private AmbientLight al;
    private boolean      rotate       = false;

    public static void main(String[] args)
    {
        ModelCanvasApplication app = new ModelCanvasApplication();
        app.start();
    }

    @Override
    public void simpleInitApp()
    {
        // creating and showing grid
        createGrid();
        rootNode.attachChild(grid);

        // Adding ambient light
        al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        // Adding Mock model and getting the animation control
        Random generator = new Random();
        int agentCat = generator.nextInt(3); // main agent category
        String agentPath = "";
        int agentSubCat = generator.nextInt(20) + 1; // Sub agent category, Specify the agent material
        if(agentCat == 0)
        {
            agentPath = "agents/male_fat" + agentSubCat + ".j3o";
        }
        else if(agentCat == 1)
        {
            agentPath = "agents/male_tall" + agentSubCat + ".j3o";
        }
        else if(agentCat == 2)
        {
            agentPath = "agents/male_tough" + agentSubCat + ".j3o";
        }

        model = assetManager.loadModel(agentPath);
        model.setLocalTranslation(0f, 0f, 0f);
        model.scale(10f);
        control = model.getControl(AnimControl.class);
        rootNode.attachChild(model);

        // Deactivating flying camera and setting up chase camera
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);
        ChaseCamera chaseCam = new ChaseCamera(cam, model, inputManager);
        chaseCam.setDefaultDistance(50f);
        chaseCam.setMaxDistance(200f);
        chaseCam.setMinVerticalRotation(-FastMath.PI / 2);
        chaseCam.setLookAtOffset(new Vector3f(0f, 10f, 0f));
    }

    @Override
    public void simpleUpdate(float tpf)
    {
        // Rotate model and grid
        if(rotate)
        {
            model.rotate(0f, 0.5f * tpf, 0f);
            grid.rotate(0f, 0.5f * tpf, 0f);
        }
    }

    /**
     * Creates the grid.
     */
    private void createGrid()
    {
        grid = new Node("Grid");

        // Calculating vertices
        float edge = GRID_LINES / 2 * GRID_SPACING;
        for(int i = 0; i <= GRID_LINES; i++)
        {
            float coord = (i - GRID_LINES / 2) * GRID_SPACING;

            Line line = new Line(new Vector3f(-edge, 0f, coord), new Vector3f(edge, 0f, coord));

            // Creating a geometry, and apply a single color material to it
            Geometry geo = new Geometry("line", line);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Gray);
            geo.setMaterial(mat);

            grid.attachChild(geo);

            line = new Line(new Vector3f(coord, 0f, -edge), new Vector3f(coord, 0f, +edge));

            // Creating a geometry, and apply a single color material to it
            geo = new Geometry("line", line);
            geo.setMaterial(mat);

            grid.attachChild(geo);
        }
    }

    /**
     * Show/hide the model in the canvas.
     * 
     * @param visible
     */
    public void isNodeVisible(final boolean visible)
    {
        enqueue(new Callable<Object>()
        {

            @Override
            public Object call() throws Exception
            {
                if(visible)
                {
                    rootNode.attachChild(model);
                }
                else
                {
                    rootNode.detachChild(model);
                }
                return null;
            }
        });
    }

    /**
     * SHow/hide the grid in the canvas.
     * 
     * @param visible
     */
    public void isGridVisible(final boolean visible)
    {
        enqueue(new Callable<Object>()
        {

            @Override
            public Object call() throws Exception
            {
                if(visible)
                {
                    rootNode.attachChild(grid);
                }
                else
                {
                    rootNode.detachChild(grid);
                }
                return null;
            }
        });
    }

    /**
     * Select the active animation. NULL for no animation.
     * 
     * @param name
     *        of the animation
     */
    public void setAnimation(final String animation)
    {
        enqueue(new Callable<Object>()
        {

            @Override
            public Object call() throws Exception
            {
                if(animation != null)
                {
                    control.createChannel().setAnim(animation);
                }
                else
                {
                    control.clearChannels();
                }
                return null;
            }
        });
    }

    /**
     * Start/stop model rotation.
     * 
     * @param rotate
     */
    public void isRotating(final boolean rotate)
    {
        enqueue(new Callable<Object>()
        {

            @Override
            public Object call() throws Exception
            {
                ModelCanvasApplication.this.rotate = rotate;
                return null;
            }
        });
    }

    /**
     * Reset the default state of the canvas.
     */
    public void resetView()
    {
        enqueue(new Callable<Object>()
        {

            @Override
            public Object call() throws Exception
            {
                ModelCanvasApplication.this.rotate = false;
                control.clearChannels();
                rootNode.attachChild(model);
                rootNode.attachChild(grid);
                return null;
            }
        });
    }

    /**
     * Returns the collection of animations' names in de model.
     * 
     * @return Collection<String> the collection
     */
    public Collection<String> getAnimationList()
    {
        return control.getAnimationNames();
    }

    public void terminate()
    {
        destroy();

        // allows OpenAl stuff to tear down
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e1)
        {
            e1.printStackTrace();
        }
    }
}
