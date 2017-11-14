package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

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
 * This class describes a non-deterministic vision algorithm for virtual agents.
 * 
 * @param <S> the agent state type
 */
public class NDDivasVision<S extends HumanAgentState> extends AbstractVision<S, HumanKnowledgeModule<S>, HumanPerceptionModule> implements Serializable
{
    private static final long            serialVersionUID         = 1L;

    private List<Ray>                    visibleRegion;

    private long                         visibleRegionCycleUpdate = 0;

    private boolean                      obstructionEnabled       = true;

    /**
     * The obstruction test list.
     */
    private Map<Integer, EnvObjectState> obstructionTestList;

    /**
     * Creates a new non-deterministic vision algorithm
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     * @param humanPerceptionModule
     *        the agent's PerceptionModule
     */
    public NDDivasVision(HumanKnowledgeModule<S> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
        obstructionTestList = new HashMap<Integer, EnvObjectState>();
    }

    @Override
    public void receiveAgents(List<AgentState> agents)
    {
        updateVisibleRegion();

        Collections.shuffle(agents);

        int i = 0;
        int size = agents.size();
        while(i < 10 && i < size)
        {
            receive(agents.get(i));
            i++;
        }
    }

    @Override
    public void receiveEvents(List<EnvEvent> events)
    {
        for(EnvEvent event : events)
        {
            if(event.isCurrentlyVisible())
            {
                receive(event);
            }
        }
    }

    @Override
    public void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        updateVisibleRegion();
        CollisionResults results = new CollisionResults();
        for(EnvObjectState obj : envObjs)
            receive(obj, results);
    }

    /**
     * Process the perception of a single agent
     * 
     * @param agentState
     *        the agent to be perceived
     */
    public void receive(AgentState agentState)
    {

        // CollisionResults results = new CollisionResults();
        // for(int i = 0; i < visibleRegion.size(); i++)
        // {
        // knowledgeModule.getSelf().getID(), visibleRegion.get(i).getOrigin(),
        // //
        // visibleRegion.get(i).getOrigin().add(visibleRegion.get(i).direction.normalize()).multLocal(knowledgeModule.getSelf().getVisibleDistance()),
        // agentState.getID()));
        // agentState.collideWith(visibleRegion.get(i), results);
        // if(results.size() > 0)
        // {
        // if(results.getClosestCollision().getDistance() <= knowledgeModule.getSelf().getVisibleDistance())
        // {
        // knowledgeModule.addAgent(agentState);
        // knowledgeModule.addElementToVisionList(agentState.getID(), "agent");
        // break;
        // }
        // }
        // }
        // results.clear();

        if(agentState.isVisible() && agentState.getID() != knowledgeModule.getId())
        {
            // CASE 1 and CASE 4 tested here (Center of object)
            boolean isSeen = testVisiblePoint(agentState.getPosition());
            if(isSeen)
            {
                knowledgeModule.addAgent(agentState);
            }
        }
    }

    /**
     * Process the perception of a single environment object
     * 
     * @param envObj
     *        the environment object to perceive
     * @param results
     *        the visible list
     */
    public void receive(EnvObjectState envObj, CollisionResults results)
    {
        if(!envObj.isVisible())
            return;

        boolean isVisible = false;
        // CASE 1 and CASE 2 tested here (Center of object)
        isVisible = testVisiblePoint(envObj.getVisiblePosition());

        if(!isVisible) // no need to do more tests if visible
        {
            // CASE 3 tested here (8 corners of AABB)
            List<Vector3f> points = envObj.getPoints();
            int count = 1;
            while(!isVisible && (count < 9)) // test the 8 points to see if visible (count = 1 ... 8)
            {
                isVisible = testVisiblePoint(points.get(count));
                count++;
            }

            // CASE 4 tested here (intersection tests against the object's bounding volume)
            if(!isVisible) // no need to do more tests if visible
            {
                // our rays are agent location -> myEndPoint[0-#case3Rays]
                // Do checks for each vision cone segment VS. each face - intersection tests are performed here
                results.clear();
                for(int i = 0; i < visibleRegion.size() && !isVisible; i++)
                {
                    Ray collisionRay = visibleRegion.get(i);
                    envObj.intersects(collisionRay, results);
                    if(results.size() > 0)
                    {
                        if(knowledgeModule.getSelf().getEyePosition().distance(results.getClosestCollision().getContactPoint()) <= knowledgeModule.getSelf().getVisibleDistance())
                        {
                            isVisible = true;
                            break;
                        }
                    }
                }
            }
        }

        // end all cases (if visible, add it)
        if(isVisible)
        {
            // add to the obstruction list for testing
            if(obstructionEnabled == true)
            {
                obstructionTestList.put(envObj.getID(), envObj);
            }

            // if obstruction testing is disabled, just directly add objects here.
            if(obstructionEnabled == false)
            {
                knowledgeModule.addEnvObj(envObj);
            }
        }
    }

    /**
     * Process the perception of a single event
     * 
     * @param event
     *        the event to perceive
     */
    public void receive(EnvEvent event)
    {
        AgentState self = knowledgeModule.getSelf();
        if(event instanceof Visible)
        {
            Visible visibleEvent = (Visible) event;

            // CASE 1 and CASE 4 tested here (Center of object)
            boolean isSeen = testVisiblePoint(visibleEvent.getVisiblePosition());

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
                    perceptionModule.addPerceivedData(tempsd);
                }
            }
        }
    }

    @Override
    public void resolveObstructions()
    {
        if(obstructionEnabled)
        {
            updateVisibleRegion();

            // find the id of all visible objects
            Set<Integer> visibleObjectIds = new HashSet<Integer>();
            CollisionResults results = new CollisionResults();
            for(Ray collisionRay : visibleRegion)
            {
                EnvObjectState closest = null;
                int eoId = -1;
                float currMinDistance = Float.MAX_VALUE;
                for(EnvObjectState eo : obstructionTestList.values())
                {
                    eo.intersects(collisionRay, results);
                    if(results.size() > 0 && results.getClosestCollision().getDistance() < currMinDistance)
                    {
                        eoId = eo.getID();
                        currMinDistance = results.getClosestCollision().getDistance();
                        closest = eo;
                    }
                    results.clear();
                }
                if(eoId != -1)
                {
                    visibleObjectIds.add(eoId);
                    knowledgeModule.addEnvObj(closest);
                }
            }
        }

        obstructionTestList.clear();
    }

    private boolean testVisiblePoint(Vector3f point)
    {
        AgentState self = knowledgeModule.getSelf();

        // get the vector from the agent to the event
        Vector3f v = point.subtract(self.getPosition());

        // only do angle calculations if the object is in visual range
        if(v.length() <= knowledgeModule.getSelf().getVisibleDistance())
        {
            // calculate the unit vector of the other point
            // Vector3f u_V = v.normalizeLocal();
            // dot product cos(a) = dp, so a = arccos(dp)
            // float dp = u_V.dot(self.getHeading());
            // double angle = org.apache.commons.math.util.FastMath.acos(dp);
            // Angle must be less than vision angle
            // if(angle < (knowledgeModule.getSelf().getFOV() / 2 * (FastMath.PI / 180)))
            if(org.apache.commons.math.util.FastMath.acos(v.normalizeLocal().dot(knowledgeModule.getSelf().getHeading())) < (knowledgeModule.getSelf().getFOV() / 2 * (FastMath.PI / 180)))
            {
                // if we get in here, the "thing" is in visual distance AND within our visual angle
                v = null;
                return true;
            }
        }
        v = null;
        return false;
    }

    private void updateVisibleRegion()
    {
        if(knowledgeModule.getTime() != visibleRegionCycleUpdate || visibleRegion == null)
        {
            visibleRegion = knowledgeModule.getSelf().getVisibleRegion();
            visibleRegionCycleUpdate = knowledgeModule.getTime();
        }
    }

}
