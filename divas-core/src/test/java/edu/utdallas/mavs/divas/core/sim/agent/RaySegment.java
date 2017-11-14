//package edu.utdallas.mavs.divas.core.sim.agent;
//
//import com.jme3.math.Vector3f;
//
//import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
//
//public class RaySegment
//{
//
//    public Vector3f        start, end;
//
//    private EnvObjectState firstHitEnvObj;
//
//    private float          firstHitDistance;
//
//    public RaySegment()
//    {
//        start = new Vector3f();
//        end = new Vector3f();
//        firstHitEnvObj = null;
//        firstHitDistance = Float.MAX_VALUE;
//    }
//
//    public void SetupPoints(Vector3f start, Vector3f end)
//    {
//        this.start = start;
//        this.end = end;
//    }
//
//    public EnvObjectState getFirstHitEnvObj()
//    {
//        return firstHitEnvObj;
//    }
//
//    public void setFirstHitEnvObj(EnvObjectState firstHitEnvObj)
//    {
//        this.firstHitEnvObj = firstHitEnvObj;
//    }
//
//    public float getFirstHitDistance()
//    {
//        return firstHitDistance;
//    }
//
//    public void setFirstHitDistance(float firstHitDistance)
//    {
//        this.firstHitDistance = firstHitDistance;
//    }
//
//}
