/**
 * 
 */
package edu.utdallas.mavs.divas.visualization.vis3D.utils;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * This is a helper class for snapping and gluing objects to a grid.
 * <p>
 * The grid step is 1. The algorithm snaps to the closest point of an object, giving its center and scale. Regardless of
 * the objects' shape, the algorithm consider a box shape.
 */
public class SnapAndGlueHelper
{

    /**
     * Calculates the snapping translation of an object
     * 
     * @param center the center of the object to be snapped
     * @param scale the scale of the object to be snapper
     * @return the snapping translation
     */
    public static Vector3f calculateSnapTranslation(Vector3f center, Vector3f scale)
    {
        Vector3f trans = new Vector3f(0f, 0f, 0f);
        Vector2f snap = calculateSnapPoint(center, scale);
        Vector3f s = new Vector3f(snap.x, 0, snap.y);
        Vector3f diff = s.subtract(center);

        if(diff.x > 0f)
        {
            trans.x = -scale.x + s.x;
        }
        else
        {
            trans.x = scale.x + s.x;
        }

        if(diff.z > 0f)
        {
            trans.z = -scale.z + s.z;
        }
        else
        {
            trans.z = scale.z + s.z;
        }

        return trans;
    }

    private static Vector2f calculateSnapPoint(Vector3f center, Vector3f scale)
    {
        Vector2f closest = null;
        float closestDistance = 100f;

        Vector2f p1 = new Vector2f(center.x + scale.x, center.z + scale.z);
        Vector2f p2 = new Vector2f(center.x + scale.x, center.z - scale.z);
        Vector2f p3 = new Vector2f(center.x - scale.x, center.z + scale.z);
        Vector2f p4 = new Vector2f(center.x - scale.x, center.z - scale.z);

        List<NeighboringPoint> neighbors = new ArrayList<NeighboringPoint>();
        neighbors.add(getClosestPoint(p1));
        neighbors.add(getClosestPoint(p2));
        neighbors.add(getClosestPoint(p3));
        neighbors.add(getClosestPoint(p4));

        for(NeighboringPoint s : neighbors)
        {
            if(s.getDistance() < closestDistance)
            {
                closest = s.getPoint();
                closestDistance = s.getDistance();
            }
        }

        return closest;
    }

    private static NeighboringPoint getClosestPoint(Vector2f pos)
    {

        Vector2f closest = null;
        float closestDistance = 100f;

        List<Vector2f> neighbors = getNeighbors(pos);

        for(Vector2f s : neighbors)
        {
            float tmp = s.distance(pos);
            if(tmp < closestDistance)
            {
                closest = s;
                closestDistance = tmp;
            }
        }

        return new NeighboringPoint(closest, closestDistance);
    }

    private static List<Vector2f> getNeighbors(Vector2f p)
    {
        ArrayList<Vector2f> neighbors = new ArrayList<Vector2f>();

        neighbors.add(new Vector2f(FastMath.floor(p.x), FastMath.floor(p.y)));
        neighbors.add(new Vector2f(FastMath.floor(p.x), FastMath.ceil(p.y)));
        neighbors.add(new Vector2f(FastMath.ceil(p.x), FastMath.floor(p.y)));
        neighbors.add(new Vector2f(FastMath.ceil(p.x), FastMath.ceil(p.y)));

        return neighbors;
    }

}

/**
 * This class represents an auxiliary data structure to hold temporary values of a 2D point and a float representing a
 * distance.
 */
class NeighboringPoint
{
    Vector2f point;
    float    distance;

    public NeighboringPoint(Vector2f point, float distance)
    {
        super();
        this.point = point;
        this.distance = distance;
    }

    public Vector2f getPoint()
    {
        return point;
    }

    public float getDistance()
    {
        return distance;
    }
}
