package edu.utdallas.mavs.divas.visualization.vis2D.vo;

import com.jme3.math.Vector3f;

/**
 * This is the abstract base class of the two dimensional visualization shapes for events
 * <p>
 * Stores the general information that is shared between the event shapes
 * This information includes:
 * <ul>
 * <li>The event ID
 * <li>The origin for the event
 * <li>The age of the event 
 * <li>A flag that indicates if the event is attached to the visualizer
 * </ul>
 * <p>
 */
public abstract class BaseVO2D
{
	/**
	 * Event ID
	 */
	protected int		eventID;

	/**
	 * The position of the event
	 */
	protected Vector3f	origin;

	/**
	 * Event age in simulation cycles
	 */
	protected int		age;

	/**
	 * Event visualization flag
	 */
	protected boolean	isAttached;

	/**
	 * Constructs the event visualization object
	 * 
	 * @param eventID
	 *        Event ID
	 * @param origin
	 *        The position of the event
	 * @param age
	 *        Event age in simulation cycles
	 * @param isAttached
	 *        Event visualization flag
	 */
	public BaseVO2D(int eventID, Vector3f origin, int age, boolean isAttached)
	{
		this.eventID = eventID;
		this.origin = origin;
		this.age = age;
		this.isAttached = isAttached;
	}

	/**
	 * Get's the age of the event in terms of simulation cycles
	 * 
	 * @return event age in simulation cycles
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * Sets the age of the event in terms of simulation cycles
	 * 
	 * @param age
	 *        The event age
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 * Get's the event Id
	 * 
	 * @return Event Id
	 */
	public int getEventID()
	{
		return eventID;
	}

	/**
	 * Get's the position of the event
	 * 
	 * @return Event position
	 */
	public Vector3f getOrigin()
	{
		return origin;
	}

	/**
	 * Indicates if the event is visualized in the 2D panel. This method is used start
	 * calculate the event age after visualization.
	 * 
	 * @return Boolean flag that indicates if the explosion is visualized
	 */
	public boolean isAttached()
	{
		return isAttached;
	}

	/**
	 * Sets the event visualization flag
	 * 
	 * @param isAttached
	 *        The visualization flag
	 */
	public void setAttached(boolean isAttached)
	{
		this.isAttached = isAttached;
	}

}
