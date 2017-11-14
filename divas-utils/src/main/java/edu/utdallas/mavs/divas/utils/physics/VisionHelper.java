package edu.utdallas.mavs.divas.utils.physics;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

/**
 * Helper class for agent vision
 */
public class VisionHelper
{
    private static int rotatingConeCounter = 0;

    /**
     * Creates a vision cone as a list of rays.
     * 
     * @param position the vertex of the cone
     * @param headingDirection the direction of the cone axis
     * @param fov the field of view (angle) of the cone
     * @param visibleDistance the visible distance
     * @param circlesCount number of circles in the vision cone
     * @param raysPerCircleCount number of rays per circle to be added in the vision cone
     * @return a list of rays
     */
    public static List<Ray> createVisionCone(Vector3f position, Vector3f headingDirection, float fov, float visibleDistance, int circlesCount, int raysPerCircleCount)
    {
        List<Ray> visibleRegion = new ArrayList<Ray>();
        Vector3f w = headingDirection.normalize();
        Vector3f myEndPoints[] = new Vector3f[raysPerCircleCount * circlesCount + 1];
        Vector3f myOrigEndPoints[] = new Vector3f[raysPerCircleCount * circlesCount + 1];

        Vector3f circleCenter = position.add(headingDirection.mult(visibleDistance));

        // u and v represent the axis of the circle (with w being the circles normal)
        Vector3f u = new Vector3f();
        Vector3f v = null;

        if(FastMath.abs(w.x) >= FastMath.abs(w.y))
        {
            float factor = 1 / FastMath.sqrt(w.x * w.x + w.z * w.z);
            u.x = -w.z * factor;
            u.y = 0;
            u.z = w.x * factor;
        }
        else
        {
            float factor = 1 / FastMath.sqrt(w.y * w.y + w.z * w.z);
            u.x = 0;
            u.y = w.z * factor;
            u.z = -w.y * factor;
        }
        v = w.crossLocal(u);

        float angle1;
        float angle2;

        int i = 0;
        for(int circleCount = 0; circleCount <= (circlesCount - 1); circleCount++)
        {
            i = 0;

            angle1 = fov / 2 * ((circlesCount - 1f - circleCount + 1f) / circlesCount); // make smaller circles (part of cone)
            angle2 = 90 - angle1; // we know 1 angle of triangle is 90 degrees
            angle1 = angle1 * FastMath.DEG_TO_RAD; // So others must be angle 1 and 90 minus angle 1
            angle2 = angle2 * FastMath.DEG_TO_RAD;

            float radius = visibleDistance * FastMath.sin(angle1) / FastMath.sin(angle2);

            float rotatingConeCountRadians = rotatingConeCounter * FastMath.DEG_TO_RAD;

            if(rotatingConeCounter > 3600)
                rotatingConeCounter = 0;
            else
                rotatingConeCounter++;

            for(float t = 0; t < (2 * FastMath.PI); t = t + (2 * FastMath.PI) / raysPerCircleCount) // case 3 rays config variable.
            {
                myOrigEndPoints[i + circleCount * raysPerCircleCount] = circleCenter.add(u.mult((radius * FastMath.cos(t + rotatingConeCountRadians)))).add(v.mult((radius * FastMath.sin(t + rotatingConeCountRadians))));
                i++;
            }

            i = 0;

            // scale to VD (else agents see too far.)
            while(i < raysPerCircleCount)
            {
                Vector3f scaled = myOrigEndPoints[i + circleCount * raysPerCircleCount].subtract(position);
                scaled.normalizeLocal();
                scaled.multLocal(visibleDistance);
                myEndPoints[i + circleCount * raysPerCircleCount] = position.add(scaled);
                i++;
            }
        }

        for(int j = 0; j < raysPerCircleCount * circlesCount; j++)
        {
            visibleRegion.add(new Ray(position, myEndPoints[j].subtractLocal(position).normalizeLocal()));
        }

        visibleRegion.add(new Ray(position, headingDirection.normalize()));
        return visibleRegion;
    }
}
