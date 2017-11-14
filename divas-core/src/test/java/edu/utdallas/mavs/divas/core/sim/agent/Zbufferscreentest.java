//package edu.utdallas.mavs.divas.core.sim.agent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.jme3.app.SimpleApplication;
//import com.jme3.material.Material;
//import com.jme3.math.ColorRGBA;
//import com.jme3.math.FastMath;
//import com.jme3.math.Vector3f;
//import com.jme3.scene.Geometry;
//import com.jme3.scene.shape.Line;
//
//
//public class Zbufferscreentest extends SimpleApplication
//{
//
//    public static void main(String[] args)
//    {
//        Zbufferscreentest app = new Zbufferscreentest();
//        app.start(); // start the game
//    }
//
//    int resX  = 200;
//    int halfX = 100;
//    int resY  = 150;
//    int halfY = 75;
//
//    @Override
//    public void simpleInitApp()
//    {
//
//        getFlyByCamera().setEnabled(true);
//
//        getFlyByCamera().setDragToRotate(true);
//        getFlyByCamera().setMoveSpeed(50);
//        getFlyByCamera().setRotationSpeed(3);
//        List<RaySegment> rays;
//        rays = new ArrayList<RaySegment>();
//        for(int z = 0; z < resX * resY + 1; z++)
//        {
//            rays.add(new RaySegment());
//        }
//
//        // System.out.println(rays.size());
//        generateRayEndPoints(rays);
//        for(int i = 0; i < rays.size(); i++)
//        {
//            Line box1 = new Line(new Vector3f(0, 0, 0), rays.get(i).end);
//            Geometry blue = new Geometry("Box", box1);
//            Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            mat1.setColor("Color", ColorRGBA.Blue);
//            blue.setMaterial(mat1);
//            rootNode.attachChild(blue);
//        }
//    }
//
//    private void generateRayEndPoints(List<RaySegment> rays)
//    {
//        Vector3f agentLocation = new Vector3f(0, 0, 0).add(new Vector3f(0, 1.8f, 0)); // TODO use real agent dimensions
//        Vector3f agentHeading = new Vector3f(1, 0, 0);
//        Vector3f w = agentHeading.normalize();
//        Vector3f screenCenter = agentLocation.add(agentHeading.mult(150));
//
//        // u and v represent the axis of the circle (with w being the circles normal)
//        Vector3f u = new Vector3f();
//        Vector3f v = new Vector3f();
//        // calculate u & v (orthonormal to w)
//        if(FastMath.abs(w.x) >= FastMath.abs(w.y))
//        {
//            float factor = 1 / FastMath.sqrt(w.x * w.x + w.z * w.z);
//            u.x = -w.z * factor;
//            u.y = 0;
//            u.z = w.x * factor;
//        }
//        else
//        {
//            float factor = 1 / FastMath.sqrt(w.y * w.y + w.z * w.z);
//            u.x = 0;
//            u.y = w.z * factor;
//            u.z = -w.y * factor;
//        }
//        v = w.cross(u);
//
//        for(int xx = 0; xx < resX; xx++)
//        {
//            for(int yy = 0; yy < resY; yy++)
//            {
//                rays.get(resY * xx + yy).end = screenCenter.add(u.mult(xx - halfX)).add(v.mult(yy - halfY));
//            }
//
//        }
//
//    }
//
//}
