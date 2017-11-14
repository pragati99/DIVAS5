package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import edu.utdallas.mavs.divas.utils.physics.ActualObjectPolygon;

/**
 * Store critial information about virtual objects. (All virtual objects will have this information)
 */
public class VirtualKnowledgeStorageObject extends AbstractKnowledgeStorageObject
{
    private static final long     serialVersionUID = 1L;
    /**
     * Virtual Object's Position
     */
    protected Vector3f            position         = new Vector3f(0, 0, 0);
    /**
     * Virtual Object's Scale
     */
    protected Vector3f            scale            = new Vector3f(0, 0, 0);

    /**
     * Flag indicating if this object is collidable
     */
    protected boolean             isCollidable     = true;

    /**
     * The bounding area of the virtual entity in this state.
     */
    protected ActualObjectPolygon boundingArea;

    /**
     * Returns the rectangles bounding area.
     * 
     * @return the rectangles bounding area
     */
    public ActualObjectPolygon getBoundingArea()
    {
        return boundingArea;
    }

    /**
     * Construct a Virtual Object's KSO
     * 
     * @param id
     *        the ID
     * @param scale
     *        the Scale
     * @param position
     *        the Position
     * @param isCollidable
     *        if the object is collidable or not
     * @param boundingArea
     *        the object's bounding area
     * @param boundingVolume
     *        the object's bounding volume
     */
    public VirtualKnowledgeStorageObject(int id, Vector3f scale, Vector3f position, boolean isCollidable, ActualObjectPolygon boundingArea)
    {
        super(id);
        this.scale = scale;
        this.position = position;
        this.isCollidable = isCollidable;
        this.boundingArea = boundingArea;
    }

    /**
     * Construct a Virtual Object's KSO
     * 
     * @param id
     *        the ID
     * @param position
     *        the Position
     */
    public VirtualKnowledgeStorageObject(int id, Vector3f position)
    {
        super(id);
        this.position = position;
    }

    /**
     * Get Virtual Object's Position
     * 
     * @return Position
     */
    public Vector3f getPosition()
    {
        return position;
    }

    /**
     * Get Virtual Object's Position
     * 
     * @return Position
     */
    public Vector2f getPosition2D()
    {
        return new Vector2f(position.x, position.z);
    }

    /**
     * Set Virtual Object's Position
     * 
     * @param position
     *        Position
     */
    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    /**
     * Get Virtual Object's scale
     * 
     * @return Scale
     */
    public Vector3f getScale()
    {
        return scale;
    }

    /**
     * Set Virtual Object's Scale
     * 
     * @param scale
     *        Scale
     */
    public void setScale(Vector3f scale)
    {
        this.scale = scale;
    }

    /**
     * Checks if the object is collidable
     * 
     * @return true if colidable. False otherwise.
     */
    public boolean isCollidable()
    {
        return isCollidable;
    }

    /**
     * Checks if its bounding area intersects the line determined by the given start and end points
     * 
     * @param endPoint
     *        end point of the line
     * @param startPoint
     *        start point of the line
     * @return true if it intersects. Otherwise, false.
     */
    public boolean intersects2DLine(Vector3f endPoint, Vector3f startPoint)
    {
        return boundingArea.intersects(new GeometryFactory().createLineString(new Coordinate[] {new Coordinate(endPoint.x, endPoint.z), new Coordinate(startPoint.x, startPoint.z)}));
    }

    /**
     * Checks if its bounding area contains the given point
     * 
     * @param point
     *        a point
     * @return true if it contains. Otherwise, false.
     */
    public boolean contains2D(Vector3f point)
    {
//        System.out.println("start-----------");
//        System.out.println("Scale; "+scale);
//        boundingArea.print();
//        System.out.println("Points: " + point.x+","+ point.z);
//    System.out.println("end-------------");
        return boundingArea.contains(new GeometryFactory().createPoint(new Coordinate(point.x, point.z)));
    }

    /**
     * Checks if its bounding area intersects an agent
     * 
     * @param testPoint
     *        point to test
     * @param testScale
     *        scale of agent to be tested
     * @return true if they intersect. Otherwise, false.
     */
    public boolean intersects2D(Vector3f testPoint, Vector3f testScale)
    {
        ActualObjectPolygon test = new ActualObjectPolygon(new Vector2f(testPoint.x, testPoint.y), testScale);
        return boundingArea.intersects(test);
    }

}
