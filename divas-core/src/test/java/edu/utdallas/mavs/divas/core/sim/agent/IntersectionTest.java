//package edu.utdallas.mavs.divas.core.sim.agent;
//
//import org.junit.Ignore;
//import org.junit.Test;
//
//import com.jme3.bounding.BoundingSphere;
//import com.jme3.collision.CollisionResult;
//import com.jme3.collision.CollisionResults;
//import com.jme3.math.Ray;
//import com.jme3.math.Vector3f;
//import com.jme3.scene.Geometry;
//import com.jme3.scene.Node;
//import com.jme3.scene.shape.Box;
//import com.jme3.scene.shape.Sphere;
//
//public class IntersectionTest
//{
//    @Ignore
//    @Test
//    public void testJme3BoxIntersection()
//    {
//        CollisionResults results = new CollisionResults();
//
//        Vector3f agentLocation = new Vector3f(0, 0, 0);
//        Vector3f agentHeading = new Vector3f(1, 0, 1);
//        Ray ray = new Ray(agentLocation, agentHeading);
//
//        Node baseNode = new Node("baseNode");
//
//        // box
//        Box b = new Box(new Vector3f(0, 0, 0), 1, 1, 1);
//        Geometry box = new Geometry("Box", b);
//        box.setLocalTranslation(1, 0, 1);
//        baseNode.attachChild(box);
//        System.out.println("bound: " + b.getBound());
//
//        baseNode.collideWith(ray, results);
//
//        if(results.size() > 0)
//        {
//            CollisionResult closest = results.getClosestCollision();
//            System.out.println("found: " + closest.getGeometry().getName());
//        }
//    }
//
//    @Ignore
//    @Test
//    public void testJme3SphereIntersection()
//    {
//        CollisionResults results = new CollisionResults();
//
//        Vector3f agentLocation = new Vector3f(0, 0, 0);
//        Vector3f agentHeading = new Vector3f(1, 0, 1);
//        Ray ray = new Ray(agentLocation, agentHeading);
//
//        Node baseNode = new Node("baseNode");
//
//        // box
//        Box b = new Box(1, 1, 1);
//        Geometry box = new Geometry("Box", b);
//        box.setLocalTranslation(new Vector3f(10, 0, 10));
//        // box.setLocalScale(new Vector3f(1, 1, 1));
//        baseNode.attachChild(box);
//        System.out.println("bound: " + b.getBound());
//
//        // sphere
//        Sphere s = new Sphere(10, 10, 1);
//        s.setBound(new BoundingSphere());
//        s.updateBound();
//        Geometry sphere = new Geometry("Sphere", s);
//        sphere.setLocalTranslation(-10, 0, -10);
//        baseNode.attachChild(sphere);
//        System.out.println("bound: " + s.getBound());
//
//        baseNode.collideWith(ray, results);
//
//        if(results.size() > 0)
//        {
//            for(CollisionResult result : results)
//            {
//                System.out.println("name: " + result.getGeometry().getName());
//                System.out.println("distance: " + result.getDistance());
//            }
//
//            CollisionResult closest = results.getClosestCollision();
//            System.out.println("found: " + closest.getGeometry().getName());
//        }
//    }
//
//}
