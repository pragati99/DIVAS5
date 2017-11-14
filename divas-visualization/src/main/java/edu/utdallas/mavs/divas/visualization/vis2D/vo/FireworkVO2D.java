package edu.utdallas.mavs.divas.visualization.vis2D.vo;

import java.awt.geom.GeneralPath;

import com.jme3.math.Vector3f;

/**
 * Contains information about shapes given to the fireworks in the 2D visualizer
 * 
 * 
 */
public class FireworkVO2D extends BaseVO2D
{

	/**
	 * Constructs a fireworks visual object
	 * 
	 * @param eventID
	 *        The id of the fireworks in the simulation
	 * @param origin
	 *        The position of the fireworks in the simulation
	 * @param age
	 *        The age of the fireworks in terms of cycles, initially it's zero
	 */
	public FireworkVO2D(int eventID, Vector3f origin, int age)
	{
		super(eventID, origin, age, false);
	}

	/**
	 * Returns an generalPath that forms a star shape to represent the fireworks
	 * 
	 * @return generalPath that forms a star shape
	 */
	public GeneralPath attachFirework()
	{
		int xPoints[] = { 22, 33, 54, 36, 41, 27, 13, 18, 0, 21 };
		int yPoints[] = { 0, 18, 18, 27, 48, 36, 48, 27, 18, 18 };

		// create a star from a series of points
		GeneralPath star = new GeneralPath();

		// set the initial coordinate of the General Path
		star.moveTo(xPoints[0], yPoints[0]);

		// create the star--this does not draw the star
		for(int count = 1; count < xPoints.length; count++)
			star.lineTo(xPoints[count], yPoints[count]);

		// close the shape
		star.closePath();

		return star;
	}

}
