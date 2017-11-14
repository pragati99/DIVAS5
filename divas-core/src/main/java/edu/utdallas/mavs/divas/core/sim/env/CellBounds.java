package edu.utdallas.mavs.divas.core.sim.env;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.utils.physics.ActualObjectPolygon;

/**
 * This class stores information about the cell controller boundaries boundaries.
 * <p>
 * The <code>CellController</code> boundaries have a rectangular shape. Because of that, the <code>CellBounds</code> class extends the <code>Rectangle2D.Float</code> class to benefit from it's characteristics.
 */
public class CellBounds extends Rectangle2D.Float implements Serializable
{
    private static final long serialVersionUID = -3448715738751319264L;

    private float             min_Y;
    private float             max_Y;

    /**
     * Constructs the <code>CellBounds</code> class.
     */
    public CellBounds()
    {
        super();
    }

    /**
     * Constructs the <code>CellBounds</code> class by setting the given parameters.
     * 
     * @param minX
     *        Minimum X coordinate.
     * @param maxX
     *        Maximum X coordinate.
     * @param minY
     *        Minimum Y coordinate.
     * @param maxY
     *        Maximum Y coordinate.
     * @param minZ
     *        Minimum Z coordinate.
     * @param maxZ
     *        Maximum Z coordinate.
     */
    public CellBounds(float minX, float maxX, float minY, float maxY, float minZ, float maxZ)
    {
        super(minX, minZ, maxX - minX, maxZ - minZ);
        this.min_Y = minY;
        this.max_Y = maxY;
    }

    /**
     * @return Minimum Y coordinate.
     */
    public float getMin_Y()
    {
        return min_Y;
    }

    /**
     * Sets Minimum Y coordinate.
     * 
     * @param min_Y
     *        Minimum Y coordinate to be set.
     */
    public void setMin_Y(float min_Y)
    {
        this.min_Y = min_Y;
    }

    /**
     * Sets Maximum Y coordinate.
     * 
     * @param max_Y
     *        Maximum Y coordinate to be set.
     */
    public void setMax_Y(float max_Y)
    {
        this.max_Y = max_Y;
    }

    /**
     * @return Maximum Y coordinate.
     */
    public float getMax_Y()
    {
        return max_Y;
    }

    /**
     * Tests if the given position is contained within the cell bounds.
     * 
     * @param x
     *        X coordinate.
     * @param y
     *        Y coordinate.
     * @param z
     *        Z coordinate.
     * @return boolean flag that indicates of the given position in this cell bounds.
     */
    public boolean contains(float x, float y, float z)
    {
        return super.contains(x, z) && y >= min_Y && y < max_Y;
    }

    /**
     * Tests if the given position represented as <code>Vector3f</code> is contained within the cell bounds.
     * 
     * @param v
     *        <code>Vector3f</code> that represents the position to be tested.
     * @return boolean flag that indicates of the given position in this cell bounds.
     */
    public boolean contains(Vector3f v)
    {
        return super.contains(v.x, v.z) && v.y >= min_Y && v.y < max_Y;
    }

    @Override
    public String toString()
    {
        return "X: " + getMinX() + " - " + getMaxX() + "\n" + "Y: " + min_Y + " - " + max_Y + "\n" + "Z: " + getMinY() + " - " + getMaxY() + "\n";
    }

    /**
     * Split the cell bound into two equally divided cell bounds.
     * 
     * @return Array of <code>CellBounds</code> that is generated from the split operation.
     */
    public CellBounds[] split()
    {
        CellBounds parts[] = new CellBounds[2];

        float x_size = (float) getWidth();
        float y_size = (float) getHeight();

        float x_min = (float) getMinX();
        float y_min = (float) getMinY();

        // TODO: splitting the longest dimension
        if(y_size >= x_size)
        {
            y_size = y_size / 2;
            parts[0] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
            y_min += y_size;
            parts[1] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
        }
        else
        {
            x_size = x_size / 2;
            parts[0] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
            x_min += x_size;
            parts[1] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
        }

        return parts;
    }

    /**
     * Split the cell bound into two demographically partioned cell bounds.
     * 
     * @param agents this cell's agents
     * @return Array of <code>CellBounds</code> that is generated from the split operation.
     */
    public CellBounds[] splitDemographically(Collection<AgentState> agents)
    {
        CellBounds parts[] = new CellBounds[2];

        float x_size = (float) getWidth();
        float y_size = (float) getHeight();

        float x_min = (float) getMinX();
        float y_min = (float) getMinY();

        Vector2f agentPopulationSplitPosition = new Vector2f();

        for(AgentState a : agents)
        {
            agentPopulationSplitPosition.addLocal(a.getPosition2D());
        }

        agentPopulationSplitPosition.divideLocal(agents.size());

        if(y_size >= x_size)
        {
            y_size = agentPopulationSplitPosition.y - y_min;
            parts[0] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
            y_min += y_size;
            parts[1] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, (float) getMaxY());
        }
        else
        {
            x_size = agentPopulationSplitPosition.x - x_min;
            parts[0] = new CellBounds(x_min, x_min + x_size, getMin_Y(), getMax_Y(), y_min, y_min + y_size);
            x_min += x_size;
            parts[1] = new CellBounds(x_min, (float) getMaxX(), getMin_Y(), getMax_Y(), y_min, y_min + y_size);
        }

        return parts;
    }
    
    public boolean intersects(ActualObjectPolygon g)
    {
        Vector2f position = new Vector2f((float)getCenterX(), (float)getCenterY());
        Vector2f scale = new Vector2f((float)getWidth()/2, (float)getHeight()/2);
        Vector2f pospos = new Vector2f(scale.x, scale.y);
        Vector2f posneg = new Vector2f(scale.x, -scale.y);
        Vector2f negpos = new Vector2f(-scale.x, scale.y);
        Vector2f negneg = new Vector2f(-scale.x, -scale.y);
        pospos = new Vector2f(position.add(pospos));
        posneg = new Vector2f(position.add(posneg));
        negpos = new Vector2f(position.add(negpos));
        negneg = new Vector2f(position.add(negneg));
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(pospos.x, pospos.y));
        coordinates.add(new Coordinate(posneg.x, posneg.y));
        coordinates.add(new Coordinate(negneg.x, negneg.y));
        coordinates.add(new Coordinate(negpos.x, negpos.y));
        coordinates.add(new Coordinate(pospos.x, pospos.y));
        Coordinate[] coordinatearray = new Coordinate[5];
        coordinates.toArray(coordinatearray);
        GeometryFactory fact = new GeometryFactory();
        LinearRing linear = fact.createLinearRing(coordinatearray);
        Polygon polygon = fact.createPolygon(linear, null);
        return g.intersects(polygon);
    }
}
