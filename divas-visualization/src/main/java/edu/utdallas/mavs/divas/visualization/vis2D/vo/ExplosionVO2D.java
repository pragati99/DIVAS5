package edu.utdallas.mavs.divas.visualization.vis2D.vo;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.visualization.vis2D.utils.Interpolation;

/**
 * Contains information about shapes given to the explosions in the 2D visualizer
 */
public class ExplosionVO2D extends BaseVO2D
{
	/**
	 * The intensity of the explosion
	 */
	private float	intensity;
	/**
	 * Indicates if the explosion has a smoke
	 */
	private boolean	smoke;

	/**
	 * Constructs an explosion visual object
	 * 
	 * @param eventID
	 *        The id of the explosion in the simulation
	 * @param origin
	 *        The position of the explosion in the simulation
	 * @param age
	 *        The age of the explosion in terms of cycles, initially it's zero
	 * @param intensity
	 *        The explosion intensity
	 * @param smoke
	 *        If the explosion has a smoke
	 */
	public ExplosionVO2D(int eventID, Vector3f origin, int age, float intensity, boolean smoke)
	{
		super(eventID, origin, age, false);
		this.intensity = intensity;
		this.smoke = smoke;
	}

	/**
	 * Gets the intensity of the explosion
	 * 
	 * @return intensity of the explosion
	 */
	public float getIntensity()
	{
		return intensity;
	}

	/**
	 * Indicates if the explosion has smoke
	 * 
	 * @return The explosion smoke flag
	 */
	public boolean isSmoke()
	{
		return smoke;
	}

	/**
	 * Returns an ellipse with random location close to the explosion original position
	 * 
	 * @param envBounds
	 *        The environment bounds
	 * @param panelBounds
	 *        The 2D visualizer panel bounds
	 * @return An ellipse with location inside a 15 diameter circle of the explosion position
	 */
	public Ellipse2D.Double attachExplosion(Rectangle2D envBounds, Rectangle2D panelBounds)
	{
		Point2D eLoc = Interpolation.interpolate(envBounds, panelBounds, new Point2D.Float(origin.getX(), origin.getZ()));
		return new Ellipse2D.Double((eLoc.getX() + Math.random() * 15 - Math.random() * 15 - intensity), (eLoc.getY() + Math.random() * 15 - Math.random() * 15 - intensity), (2 * intensity + Math.random() * 5), (2 * intensity + Math.random() * 5));
	}

	/**
	 * This method draws an ellipse to represent explosion smoke, the ellipse size is proportional
	 * to the explosion age
	 * 
	 * @param envBounds
	 *        The environment bounds
	 * @param panelBounds
	 *        The 2D visualizer panel bounds
	 * @return Ellipse that represents the explosion smoke
	 */
	public Ellipse2D.Double attachExplosionSmoke(Rectangle2D envBounds, Rectangle2D panelBounds)
	{
		Point2D eLoc = Interpolation.interpolate(envBounds, panelBounds, new Point2D.Float(origin.getX(), origin.getZ()));
		return new Ellipse2D.Double(eLoc.getX() - (5 * age / 2), eLoc.getY() - (5 * age / 2), (2 * intensity * age / 2), (2 * intensity * age / 2));
	}
}
