package edu.utdallas.mavs.divas.utils.physics;

import java.io.Serializable;
import java.util.ArrayList;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * If object is rotated, this corresponds to the actual rotated object. (A polygon)
 */
public class ActualObjectPolygon implements Serializable
{
    private static final long serialVersionUID = 1L;
    Polygon                   polygon;

    public ActualObjectPolygon()
    {}

    public ActualObjectPolygon(Quaternion rotation, Vector3f scale, Vector2f position)
    {

        Vector2f pospos = new Vector2f(scale.x, scale.z);
        Vector2f posneg = new Vector2f(scale.x, -scale.z);
        Vector2f negpos = new Vector2f(-scale.x, scale.z);
        Vector2f negneg = new Vector2f(-scale.x, -scale.z);
        Vector3f posposRot = rotation.mult(new Vector3f(pospos.x, 0, pospos.y));
        Vector3f posnegRot = rotation.mult(new Vector3f(posneg.x, 0, posneg.y));
        Vector3f negposRot = rotation.mult(new Vector3f(negpos.x, 0, negpos.y));
        Vector3f negnegRot = rotation.mult(new Vector3f(negneg.x, 0, negneg.y));
        pospos = new Vector2f(posposRot.x, posposRot.z);
        posneg = new Vector2f(posnegRot.x, posnegRot.z);
        negpos = new Vector2f(negposRot.x, negposRot.z);
        negneg = new Vector2f(negnegRot.x, negnegRot.z);
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
        polygon = fact.createPolygon(linear, null);
    }

    public ActualObjectPolygon(Vector2f position, Vector3f scale)
    {
        Vector2f pospos = new Vector2f(scale.x, scale.z);
        Vector2f posneg = new Vector2f(scale.x, -scale.z);
        Vector2f negpos = new Vector2f(-scale.x, scale.z);
        Vector2f negneg = new Vector2f(-scale.x, -scale.z);
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
        polygon = fact.createPolygon(linear, null);
    }

    public void UpdatePolygon(Quaternion rotation, Vector3f scale, Vector2f position)
    {
        Vector2f pospos = new Vector2f(scale.x, scale.z);
        Vector2f posneg = new Vector2f(scale.x, -scale.z);
        Vector2f negpos = new Vector2f(-scale.x, scale.z);
        Vector2f negneg = new Vector2f(-scale.x, -scale.z);
        float angles[] = new float[3];
        rotation.toAngles(angles);
        float angle = angles[1] * -1;
        pospos = new Vector2f(pospos.x * FastMath.cos(angle) - pospos.y * FastMath.sin(angle), pospos.x * FastMath.sin(angle) + pospos.y * FastMath.cos(angle));
        posneg = new Vector2f(posneg.x * FastMath.cos(angle) - posneg.y * FastMath.sin(angle), posneg.x * FastMath.sin(angle) + posneg.y * FastMath.cos(angle));
        negpos = new Vector2f(negpos.x * FastMath.cos(angle) - negpos.y * FastMath.sin(angle), negpos.x * FastMath.sin(angle) + negpos.y * FastMath.cos(angle));
        negneg = new Vector2f(negneg.x * FastMath.cos(angle) - negneg.y * FastMath.sin(angle), negneg.x * FastMath.sin(angle) + negneg.y * FastMath.cos(angle));
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
        polygon = fact.createPolygon(linear, null);
    }

    public boolean intersects(ActualObjectPolygon g)
    {
        return polygon.intersects(g.getPolygon());
    }

    public boolean intersects(Geometry g)
    {
        return polygon.intersects(g);
    }

    public boolean contains(Geometry g)
    {
        return polygon.contains(g);
    }

    public Polygon getPolygon()
    {
        return polygon;
    }

    public void setPolygon(Polygon polygon)
    {
        this.polygon = polygon;
    }

    public void print()
    {
        System.out.println("Object:");
        for(Coordinate c : polygon.getCoordinates())
        {
            System.out.println(c);
        }
    }

}
