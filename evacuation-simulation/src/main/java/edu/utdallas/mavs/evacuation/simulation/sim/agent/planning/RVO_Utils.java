package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class RVO_Utils implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final float EPSILON          = 0.0001f;

    public static boolean doLinesegmentsIntersect(float[] line1, float[] line2)
    {
        final float EPS = 0.00001f;

        float x1 = line1[0];
        float x2 = line1[2];
        float x3 = line2[0];
        float x4 = line2[2];

        float y1 = line1[1];
        float y2 = line1[3];
        float y3 = line2[1];
        float y4 = line2[3];

        float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        float numera = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        float numerb = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);

        // are the line coincident?
        if(Math.abs(numera) < EPS && Math.abs(numerb) < EPS && Math.abs(denom) < EPS)
        {
            return true;
        }

        // are the lines parallel?
        if(Math.abs(denom) < EPS)
        {
            return false;
        }

        // is the intersection along the segment?
        float mua = numera / denom;
        float mub = numerb / denom;

        if(mua < 0 || mua > 1 || mub < 0 || mub > 1)
        {
            return false;
        }

        return true;
    }

    // returns the cos(theta) of the two vectors
    public static float sameDirection(Vector2f v1, Vector2f v2)
    {
        return v1.dot(v2) / (v1.length() * v2.length()); // 0 - pi
    }

    /**
     * returns angle from v1 to v2
     * 
     * @param v1
     * @param v2
     * @return angle between v1 and v2 in 0-pi
     */
    public static float angleBetween(Vector2f v1, Vector2f v2)
    {
        return FastMath.acos(sameDirection(v1, v2)); // 0-pi
    }

    /**
     * @param v1
     * @param v2
     * @return an angle from v1 to v2 in -pi to pi
     */
    public static float angleBetweenWSign(Vector2f v1, Vector2f v2)
    {
        return FastMath.atan2(v2.y, v2.x) - FastMath.atan2(v1.y, v1.x); // -pi to pi
    }

    public static float det(Vector2f a, Vector2f b)
    {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    public static boolean lineSegmentIntersectionTest(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f p4)
    {
        float x1 = p1.getX();
        float x2 = p2.getX();
        float x3 = p3.getX();
        float x4 = p4.getX();
        float y1 = p1.getY();
        float y2 = p2.getY();
        float y3 = p3.getY();
        float y4 = p4.getY();

        float denom = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if(denom == 0)
        {
            return false;
        }
        else
        {
            float num1 = ((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3));
            float num2 = ((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3));
            float ua = num1 / denom;
            float ub = num2 / denom;
            if(Double.compare(ua, -EPSILON) > 0 && Double.compare(ua - 1, EPSILON) < 0 && Double.compare(ub, -EPSILON) > 0 && Double.compare(ub - 1, EPSILON) < 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

    }

    public static float calcDistanceToLineSegment(Vector2f p1, Vector2f p2, Vector2f p3)
    {

        final float xDelta = p2.getX() - p1.getX();
        final float yDelta = p2.getY() - p1.getY();

        if((xDelta == 0) && (yDelta == 0))
        {
            throw new IllegalArgumentException("p1 and p2 cannot be the same point");
        }

        final float u = ((p3.getX() - p1.getX()) * xDelta + (p3.getY() - p1.getY()) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Vector2f closestPoint;
        if(u < 0)
        {
            closestPoint = p1;
        }
        else if(u > 1)
        {
            closestPoint = p2;
        }
        else
        {
            closestPoint = new Vector2f(p1.getX() + u * xDelta, p1.getY() + u * yDelta);
        }

        return closestPoint.distance(p3);
    }

    public static float absSq(Vector2f bMinusA)
    {
        return bMinusA.dot(bMinusA);
    }

    public static boolean leftOf(Vector2f a, Vector2f b, Vector2f c)
    {
        Vector2f aMinusC = new Vector2f(a);
        aMinusC.subtractLocal(c);

        Vector2f bMinusA = new Vector2f(b);
        bMinusA.subtractLocal(a);

        if(Double.compare(RVO_Utils.det(aMinusC, bMinusA), 0.0f) > 0)
        {

            return true;
        }
        else
        {

            return false;
        }
    }
}
