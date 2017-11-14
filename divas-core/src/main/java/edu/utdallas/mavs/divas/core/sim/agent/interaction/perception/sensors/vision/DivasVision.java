package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.SensedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;
import edu.utdallas.mavs.divas.core.sim.common.percept.Visible;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;

/**
 * This class describes the original Divas algorithm for vision.
 */
@SuppressWarnings("javadoc")
public class DivasVision<S extends HumanAgentState> extends AbstractVision<S, HumanKnowledgeModule<S>, HumanPerceptionModule> implements Serializable
{
    private static final long     serialVersionUID         = 1L;

    /**
     * Whether or not obstruction is enabled.
     */
    boolean                       obstructionEnabled       = true;

    /**
     * Number of rays per level in the vision cone.
     */
    private static final int      case3Rays                = SimConfig.getInstance().vision_Rays_Count;

    /**
     * Number of circles in the vision cone.
     */
    private static final int      NumOfCircles             = SimConfig.getInstance().vision_Circles_Count;

    /**
     * The obstruction test list.
     */
    private List<ObstructionData> obstructionTestList;

    /**
     * Agents visible region
     */
    private List<Ray>             visibleRegion;
    /**
     * when the visible region was updated
     */
    private long                  visibleRegionCycleUpdate = 0;

    /**
     * Create the vision algorithm.
     * 
     * @param knowledgeModule
     *        The agent knowledge module.
     */
    public DivasVision(HumanKnowledgeModule<S> knowledgeModule, HumanPerceptionModule perceptionModule)
    {
        super(knowledgeModule, perceptionModule);
        obstructionTestList = new ArrayList<>();
    }

    /**
     * Vision Perceive Events
     * 
     * @param event
     *        The event to be tested.
     */
    public void receive(EnvEvent event, Vector3f workVector)
    {
        AgentState self = knowledgeModule.getSelf();
        if(event instanceof Visible)
        {
            Visible visibleEvent = (Visible) event;

            // CASE 1 and CASE 4 tested here (Center of object)
            boolean isSeen = testVisiblePoint(visibleEvent.getVisiblePosition(), workVector);

            if((isSeen) && event.isCurrentlyVisible())
            {
                // ///////NEXT ADD THIS TO AUDITORY & SMELL... bomb is visible for x ticks
                List<EventProperty> epList = event.getEventPropertiesForSense(Sense.Vision);
                for(int i = 0; i < epList.size(); i++)
                {
                    float certainty = 100;
                    EventProperty eventProperty = epList.get(i);
                    SensedData tempsd = new SensedData(knowledgeModule.getTime(), eventProperty.getType(), eventProperty.getSense(), eventProperty.getValue(), certainty, knowledgeModule.getVisionTrustConstant());
                    tempsd.setOrigin(event.getOrigin());
                    tempsd.setDirection(event.getOrigin().subtract(self.getPosition()));

                    tempsd.setIntensity(event.getIntensity());

                    perceptionModule.addPerceivedData(tempsd);
                }
            }
        }
    }

    /**
     * Vision Perceive Agents
     * 
     * @param agentState
     *        The agent to be tested.
     */
    public void receive(AgentState agentState, Vector3f workVector)
    {
        if(agentState.getID() != knowledgeModule.getId())
        {
            // CASE 1 and CASE 4 tested here (Center of object)
            boolean isSeen = testVisiblePoint(agentState.getPosition(), workVector);
            if(isSeen)
            {
                knowledgeModule.addAgent(agentState);
            }
        }
    }

    /**
     * Vision Perceive Environment Objects
     * 
     * @param envObj
     *        The environment object to be tested.
     */
    public void receive(EnvObjectState envObj, CollisionResults results, Vector3f workVector)
    {
        // CASE 0 - Distance

        if(envObj.getPosition().distance(knowledgeModule.getSelf().getPosition()) - FastMath.sqrt(envObj.getScale().getX() * envObj.getScale().getX() + envObj.getScale().getZ() * envObj.getScale().getZ()) < knowledgeModule.getSelf().getVisibleDistance())
        {
            // CASE 1 and CASE 2 tested here (Center of object)
            boolean isVisible = testVisiblePoint(envObj.getVisiblePosition(), workVector);
            // end CASE 1 & 2

            if(!isVisible) // no need to do more tests if visible
            {
                // CASE 3 tested here (8 corners of AABB)
                int count = 1;
                while(!isVisible && (count < 9)) // test the 8 points to see if visible (count = 1 ... 8)
                {
                    isVisible = testVisiblePoint(envObj.getPoint(count), workVector);
                    count++;
                }
                // end CASE 3

                // CASE 4 tested here (Fire rays through the 6 AABB faces)
                if(!isVisible) // no need to do more tests if visible
                {
                    int i = 0;
                    while((i < (case3Rays * NumOfCircles)) && !isVisible)// for each line segment
                    {
                        // the heading ray of collision
                        // Ray collisionRay = new Ray(agentLocation, myEndPoints[i].subtract(agentLocation));
                        envObj.collideWith(visibleRegion.get(i), results);

                        if(results.size() > 0)
                        {
                            if(knowledgeModule.getSelf().getEyePosition().distance(results.getClosestCollision().getContactPoint()) <= knowledgeModule.getSelf().getVisibleDistance())
                            {
                                isVisible = true;
                            }
                        }

                        i++;
                    }
                }
                // end CASE 4
            }

            // end all cases (if visible, add it)
            if(isVisible)
            {
                // add to the obstruction list for testing
                if(obstructionEnabled == true)
                {
                    Vector3f eye = knowledgeModule.getSelf().getEyePosition();
                    if(!envObj.isCollidable() && envObj.getBoundingArea().intersects(new GeometryFactory().createPoint(new Coordinate(eye.x, eye.z))))
                    {
                        knowledgeModule.addEnvObj(envObj);
                    }
                    else
                    {
                        obstructionTestList.add(new ObstructionData(envObj.getID(), envObj.getBoundingVolume()));
                    }
                }

                // if obstruction testing is disabled, just directly add objects here.
                if(obstructionEnabled == false)
                {
                    knowledgeModule.addEnvObj(envObj);
                }
            }
        }
    }

    /**
     * Test whether a single point is visible to the agent or not.
     * 
     * @param point
     *        The 3D point (vector value) to be tested.
     * @param workVector
     * @return whether visible (true) or not.
     */
    private boolean testVisiblePoint(Vector3f point, Vector3f workVector)
    {
        // get the vector from the agent to the event
        workVector.set(point.x, point.y, point.z);
        workVector.subtractLocal(knowledgeModule.getSelf().getPosition());

        // only do angle calculations if the object is in visual range
        if(workVector.length() <= knowledgeModule.getSelf().getVisibleDistance())
        {
            // calculate the unit vector of the other point UV
            // dot product cos(a) = dp, so a = arccos(dp)
            // Angle must be less than vision angle
            // if(angle < (knowledgeModule.getSelf().getHorizontalFOV() / 2 * (FastMath.PI / 180)))
            if(org.apache.commons.math.util.FastMath.acos(workVector.normalizeLocal().dot(knowledgeModule.getSelf().getHeading())) < (knowledgeModule.getSelf().getFOV() / 2 * (FastMath.PI / 180)))
            {
                // if we get in here, the "thing" is in visual distance AND
                // within our visual angle
                return true;
            }
        }
        return false;

    }

    /**
     * The obstruction test using JME3 collision detection techniques.
     */
    @Override
    public void resolveObstructions()
    {
        if(obstructionEnabled)
        {
            updateVisibleRegion();

            CollisionResults results = new CollisionResults();

            for(int i = 0; i < case3Rays * NumOfCircles; i++)
            {
                ObstructionData closest = null;
                float currMinDistance = Float.MAX_VALUE;
                for(ObstructionData obsData : obstructionTestList)
                {
                    // Ray collisionRay = new Ray(agentLocation, myEndPoints[i].subtract(agentLocation));
                    obsData.collideWith(visibleRegion.get(i), results);
                    if(results.size() > 0 && results.getClosestCollision().getDistance() < currMinDistance)
                    {
                        currMinDistance = results.getClosestCollision().getDistance();
                        closest = obsData;
                    }
                    results.clear();
                }
                if(closest != null)
                {
                    for(int j = 0; j < envObjsList.size(); j++)
                    {
                        if(envObjsList.get(j).getID() == closest.getId())
                        {
                            knowledgeModule.addEnvObj(envObjsList.get(j));
                        }
                    }

                }
            }
            results = null;
        }
        obstructionTestList.clear();
        envObjsList = null;
    }

    @Override
    public void receiveAgents(List<AgentState> agents)
    {
        updateVisibleRegion();
        Vector3f workVector = new Vector3f();
        for(AgentState agent : agents)
            receive(agent, workVector);
    }

    @Override
    public void receiveEvents(List<EnvEvent> events)
    {
        updateVisibleRegion();
        Vector3f workVector = new Vector3f();
        for(EnvEvent event : events)
        {
            if(event.isCurrentlyVisible())
            {
                receive(event, workVector);
            }
        }
    }

    @Override
    public void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        Vector3f workVector = new Vector3f();
        CollisionResults results = new CollisionResults();
        for(EnvObjectState obj : envObjs)
        {
            receive(obj, results, workVector);
        }
        envObjsList = envObjs;
    }

    List<EnvObjectState> envObjsList;

    private void updateVisibleRegion()
    {
        if(knowledgeModule.getTime() != visibleRegionCycleUpdate || visibleRegion == null)
        {
            visibleRegion = knowledgeModule.getSelf().getVisibleRegion();
            visibleRegionCycleUpdate = knowledgeModule.getTime();
        }
    }

}
