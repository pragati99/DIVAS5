package edu.utdallas.mavs.divas.visualization.vis2D.utils;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class is used to map the relative locations between the simulation environment
 * and the 2D visualization panel
 */
public class Interpolation
{
	/**
	 * Creates a rectangle with relative height, width and position in the
	 * 2D visualizing panel, that is mapped to a given rectangle with height, width and position
	 * in the environment
	 * 
	 * @param from
	 *        The environment bounds
	 * @param to
	 *        The 2D visualizing panel bounds
	 * @param bounds
	 *        The rectangle bounds in the environment
	 * @return Rectangle in the relative height, width and position in the 2D visualizing panel
	 */
	public static Rectangle2D interpolate(Rectangle2D from, Rectangle2D to, Rectangle2D bounds)
	{
		// interpolate top left
		Point2D topLeft = interpolate(from, to, new Point2D.Double(bounds.getX(), bounds.getY()));

		// scale the rest
		double width = bounds.getWidth() * (to.getWidth() / from.getWidth());
		double height = bounds.getHeight() * (to.getHeight() / from.getHeight());

		return new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), width, height);
	}

	/**
	 * Maps a point position 2D visualizing panel to a point position in the simulation environment
	 * 
	 * @param from
	 *        The environment bounds
	 * @param to
	 *        The 2D visualizing panel bounds
	 * @param point
	 *        The point location in the 2D visualization panel
	 * @return The mapped position in the environment for the given point in the 2D visualizing panel.
	 */
	public static Point2D revereseInterpolate(Rectangle2D from, Rectangle2D to, Point2D point)
	{
		double x = (((point.getX() - to.getX()) * from.getWidth()) / to.getWidth()) + from.getX();
		double y = (((point.getY() - to.getY()) * from.getHeight()) / to.getHeight()) + from.getY();

		return new Point2D.Double(x, y);
	}

	/**
	 * Maps a point position in the simulation environment to a point position 2D visualizing panel.
	 * 
	 * 
	 * @param from
	 *        The environment bounds
	 * @param to
	 *        The 2D visualizing panel bounds
	 * @param point
	 *        The point that we need to calculate for it the relative position
	 *        in the 2D visualizing panel
	 * @return The mapped position in the 2D visualization panel for the given point in the environment
	 */
	public static Point2D interpolate(Rectangle2D from, Rectangle2D to, Point2D point)
	{
		double x = (((point.getX() - from.getX()) * to.getWidth()) / from.getWidth()) + to.getX();
		double y = (((point.getY() - from.getY()) * to.getHeight()) / from.getHeight()) + to.getY();

		return new Point2D.Double(x, y);
	}

	/**
	 * Compute a rectangular shape for the environment object after applying the
	 * the correct rotation and scaling factor.
	 * 
	 * @param eo
	 *        The environment object state to be visualized
	 * @param eoLoc
	 *        The relative (interpolated) location of the environment object in the visualizing panel
	 * @param scaleFactor
	 *        The scale factor used in the visualizer
	 * @return Shape : Rectangular shape that represents the environment object
	 */
	public static Shape getEnvObjectShape(EnvObjectState eo, Point2D eoLoc, Double scaleFactor)
	{
		Rectangle2D.Double rectangle = new Rectangle2D.Double( (eoLoc.getX() - ((eo.getScale().getX() * scaleFactor))),
				(eoLoc.getY() - ((eo.getScale().getZ() * scaleFactor))),  
				Math.ceil(eo.getScale().getX() * scaleFactor * 2),  
				Math.ceil(eo.getScale().getZ() * scaleFactor * 2));
		// This is to rotate the shape around it's center
		AffineTransform transform = new AffineTransform();
		transform.rotate(-1 * eo.getRotation().getY() * Math.PI / 2, rectangle.getX() + rectangle.width / 2, rectangle.getY() + rectangle.height / 2);
		Shape transformed = transform.createTransformedShape(rectangle);
		return transformed;
	}
}
