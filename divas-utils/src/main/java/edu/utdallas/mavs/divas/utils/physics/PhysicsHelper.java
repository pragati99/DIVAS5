package edu.utdallas.mavs.divas.utils.physics;

import java.awt.geom.Rectangle2D;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Cylinder;

/**
 * Helper class for physics of the simulation
 */
public class PhysicsHelper
{

    /**
     * Calculate the bounding volume of an object
     * 
     * @param boundingShape
     *        The bounding volume to be created.
     * @param position
     *        The position of the volume to created.
     * @param scale
     *        The scale of the volume.
     * @return the bounding volume corresponding to the object's {@link BoundingShape}
     */
    public static BoundingVolume createBoundingVolume(BoundingShape boundingShape, Vector3f position, Vector3f scale)
    {
        BoundingVolume bv = null;
        switch(boundingShape)
        {
        case BOX:
            bv = new BoundingBox(position, scale.x, scale.y, scale.z);
            break;
        case SPHERE:
            bv = new BoundingSphere(scale.x, position);
            break;
        case CAPSULE:
            Cylinder cylinder = new Cylinder(10, 10, scale.x, scale.y);
            bv = cylinder.getBound();
            bv.setCenter(position);
            break;
        }
        return bv;
    }

    /**
     * Calculate the bounding area of an object
     * 
     * @param position
     *        The position of the bounding area.
     * @param scale
     *        The scale of the bounding area.
     * @return the rectangular bounding area corresponding to the object's {@link BoundingShape}
     */
    public static Rectangle2D createBoundingArea(Vector3f position, Vector3f scale)
    {
        float horizontalUpperLeft = position.x - scale.x;
        float verticalUpperLeft = position.z - scale.z;
        return new Rectangle2D.Float(horizontalUpperLeft, verticalUpperLeft, 2 * scale.x, 2 * scale.z);
    }

}
