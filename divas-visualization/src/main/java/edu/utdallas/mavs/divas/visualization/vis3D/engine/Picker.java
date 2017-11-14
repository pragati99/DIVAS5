package edu.utdallas.mavs.divas.visualization.vis3D.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import edu.utdallas.mavs.divas.visualization.vis3D.Visualizer3DApplication;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.AgentVO;
import edu.utdallas.mavs.divas.visualization.vis3D.vo.EnvObjectVO;

/**
 * This class handles the mouse picking operation on the 3D visualization.
 */
public class Picker
{
    private final static Logger     logger              = LoggerFactory.getLogger(Picker.class);

    /**
     * The 3D visualizer's input manager
     */
    protected InputManager          inputManager        = Visualizer3DApplication.getInstance().getInputManager();

    /**
     * The 3D visualizer's camera device
     */
    protected Camera                cam                 = Visualizer3DApplication.getInstance().getCamera();

    /**
     * The id of the current selected visualized object
     */
    protected int                   currentlySelectedID = -1;

    /**
     * The current selected visualized object
     */
    protected CollisionResult       currentlySelected;

    /**
     * The type of the current selected visualized object
     */
    protected CurrentlySelectedType currentlySelectedType;

    /**
     * Types of objects to be picked
     */
    public enum CurrentlySelectedType
    {
        AGENT, ENVIRONMENT_OBJECT
    }

    /**
     * Picks the visualized object intersected by the mouse cursor. If more than one object is intersected, picks the
     * closest one.
     */
    public void pick()
    {
        // 1. Reset results list, and the previous closet object.
        CollisionResults results = new CollisionResults();
        currentlySelected = null;
        currentlySelectedID = -1;

        // 2. Aim the ray from cam loc to cam direction.
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // 3. Collect intersections between Ray and Shootables in results list.
        Ray ray = new Ray(click3d, dir);        

        Visualizer3DApplication.getInstance().getVisRootNode().collideWith(ray, results);

        // 4. log the results
        printCollisionResults(results);

        // 5. Use the results (we mark the hit object)
        if(results.size() > 0)
        {
            // The closest collision point is what was truly hit:
            CollisionResult closest = results.getClosestCollision();
            if(Visualizer3DApplication.getInstance().getApp().isDebugMode())
            {
                Visualizer3DApplication.getInstance().getApp().displayMessage("Picked Coordinate: " + closest.getContactPoint());
            }

            if(closest.getGeometry().getParent() instanceof EnvObjectVO)
            {
                EnvObjectVO envObj = (EnvObjectVO) closest.getGeometry().getParent();

                logger.debug("Picked: " + envObj.getState().getType() + "[" + envObj.getState().getID() + "]" + ", Location: " + closest.getContactPoint());

                currentlySelectedID = envObj.getState().getID();
                currentlySelected = closest;
                currentlySelectedType = CurrentlySelectedType.ENVIRONMENT_OBJECT;

                updateEnvObjectSelection(envObj);

            }
            else if(closest.getGeometry().getParent().getParent() instanceof EnvObjectVO)
            {
                EnvObjectVO envObj = (EnvObjectVO) closest.getGeometry().getParent().getParent();

                logger.debug("Picked: " + envObj.getState().getType() + "[" + envObj.getState().getID() + "]" + ", Location: " + closest.getContactPoint());

                currentlySelectedID = envObj.getState().getID();
                currentlySelected = closest;
                currentlySelectedType = CurrentlySelectedType.ENVIRONMENT_OBJECT;

                updateEnvObjectSelection(envObj);

            }
            else if(closest.getGeometry().getParent() instanceof AgentVO)
            {
                AgentVO agent = (AgentVO) closest.getGeometry().getParent();

                logger.debug("Agent " + agent.getState().getID());

                currentlySelectedID = agent.getState().getID();
                currentlySelected = closest;
                currentlySelectedType = CurrentlySelectedType.AGENT;

                updateAgentSelection(agent);

            }
            else if(closest.getGeometry().getParent().getParent() instanceof AgentVO)
            {
                AgentVO agent = (AgentVO) closest.getGeometry().getParent().getParent();

                logger.debug("Agent " + agent.getState().getID());

                currentlySelectedID = agent.getState().getID();
                currentlySelected = closest;
                currentlySelectedType = CurrentlySelectedType.AGENT;

                updateAgentSelection(agent);
            }

        }
        // else if(VisConfig.getInstance().terrain)
        // {
        // CollisionResults results2 = new CollisionResults();
        // Terrain.getTerrain().collideWith(ray, results2);
        // if(results2.size() > 0)
        // {
        // // The closest collision point is what was truly hit:
        // CollisionResult closest = results2.getClosestCollision();
        // currentlySelected = closest;
        // currentlySelectedType = CurrentlySelectedType.TERRAIN;
        // }
        // }
    }

    private void printCollisionResults(CollisionResults results)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("----- Collisions? " + results.size() + "-----");
            for(int i = 0; i < results.size(); i++)
            {
                // For each hit, we know distance, impact point, name of geometry.
                float dist = results.getCollision(i).getDistance();
                Vector3f pt = results.getCollision(i).getContactPoint();
                String hit = results.getCollision(i).getGeometry().getName();

                logger.debug("* Collision #" + i);
                logger.debug("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                logger.debug("Parent name is: " + results.getCollision(i).getGeometry().getParent().getName());
            }
        }
    }

    /**
     * Updates the agent selection, highlighting the selected agents.
     * 
     * @param agent
     *        the agent VO
     */
    protected void updateAgentSelection(AgentVO agent)
    {
        // left blank
    }

    /**
     * Updates the environment object selection.
     * 
     * @param envObj
     *        the environment object VO
     */
    protected void updateEnvObjectSelection(EnvObjectVO envObj)
    {
        // left blank
    }

    /**
     * Gets the id of the currently selected object.
     * 
     * @return the id of the currently selected object
     */
    public int getCurrentlySelectedID()
    {
        return currentlySelectedID;
    }

    /**
     * Gets a boolean indicating if there is any object selected
     * 
     * @return <code>true</code> if there is an object currently selected. <code>false</code> otherwise.
     */
    public boolean hasSelection()
    {
        // return currentlySelected != null && currentlySelectedType != CurrentlySelectedType.TERRAIN ? true : false;
        return currentlySelected != null;
    }

    /**
     * Gets the currently selected object.
     * 
     * @return the currently selected VO
     */
    public CollisionResult getCurrentlySelected()
    {
        return currentlySelected;
    }

    /**
     * Gets the currently selected object type
     * 
     * @return the currently selected object type
     */
    public CurrentlySelectedType getCurrentlySelectedType()
    {
        return currentlySelectedType;
    }

    /**
     * Checks if currently selected object is an environment object
     * 
     * @return <code>true</code> if the selected object is an environment object.
     */
    public boolean isSelectedEnvObject()
    {
        return currentlySelectedType == CurrentlySelectedType.ENVIRONMENT_OBJECT;
    }

    /**
     * Checks if currently selected object is an agent
     * 
     * @return <code>true</code> if the selected object is an agent.
     */
    public boolean isSelectedAgent()
    {
        return currentlySelectedType == CurrentlySelectedType.AGENT;
    }

}
