package edu.utdallas.mavs.divas.visualization.vis2D.engine;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.visualization.vis2D.spectator.VisualSpectator2D;

/**
 * This class handles the mouse selection operation on the 2D visualization panel.
 * <p>
 * This class is stores information about the last selected object in the 2D visualization panel.
 * Given the coordinates of the position clicked by the mouse, information are retrieved and stored
 * statically and globally in this class to be used later for editing objects.
 */
public class Picker2D
{
	/**
	 * The currently selected object ID.
	 */
	protected static int					currentlySelectedID	= -1;
	/**
	 * The currently selected object type which could be an agent or an environment object.
	 */
	protected static CurrentlySelectedType	currentlySelectedType;

	/**
	 * The possible types of the currently selected object in the 2D visualization panel.
	 * 
	 *
	 */
	public enum CurrentlySelectedType
	{
		/**
		 * The currently selected object in the 2D visualization panel is of Agent type.
		 */
		AGENT,
		/**
		 * The currently selected object in the 2D visualization panel is of Environment object type.
		 */
		ENVIRONMENT_OBJECT
	}

	/**
	 * Updates the global variables in this class currentlySelectedID, currentlySelectedType
	 * with the object which boundaries collides with the given X and Z coordinates.
	 * 
	 * @param x The X coordinate in the of the selected object position. 
	 * @param z The Z coordinate in the of the selected object position.
	 */
	public static void pick(double x, double z)
	{
		EnvObjectState eo = new EnvObjectState();
		eo.setPosition(new Vector3f((float) x, 0f, (float) z));
		eo.setScale(new Vector3f(1f, 1f, 1f));
		VirtualState pickResult = VisualSpectator2D.getPlayGround().getSelectedObject(eo);
		if(pickResult != null)
		{
			currentlySelectedID = pickResult.getID();
			if(pickResult instanceof EnvObjectState)
			{
				currentlySelectedType = CurrentlySelectedType.ENVIRONMENT_OBJECT;
			}
			else if(pickResult instanceof AgentState)
			{
				currentlySelectedType = CurrentlySelectedType.AGENT;
			}
		}
		else
		{
			currentlySelectedID = -1;
			currentlySelectedType = null;
		}

	}

	/**
	 * Gets the ID of the last selected object in the visualizer. The Id could be
	 * for an agent or an environment object. When the mouse is clicked on an object
	 * the ID of that object will be stored in this class.
	 * 
	 * @return Id of the selected object
	 */
	public static int getCurrentlySelectedID()
	{
		return currentlySelectedID;
	}

	/**
	 * Gets the type of the last selected object in the visualizer.
	 * The type could be an agent or an environment object.
	 * 
	 * @return The type of the last selected object
	 */
	public static CurrentlySelectedType getCurrentlySelectedType()
	{
		return currentlySelectedType;
	}

}
