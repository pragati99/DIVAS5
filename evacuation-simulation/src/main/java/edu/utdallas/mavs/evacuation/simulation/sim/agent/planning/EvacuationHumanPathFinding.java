package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.Collision;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.Collision.CollisionType;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.ontology.Thing;
import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.utils.collections.LightWeightBoundedMap;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule.MentalStateOfAgent;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.AgentKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.EnvObjectKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.VirtualKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.VisionModel;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology.Door;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.FaceTask;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.IdleTask;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.MoveTask;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;

public class EvacuationHumanPathFinding extends AbstractPathFinding<EvacuationHumanKnowledgeModule> implements Serializable
{
    private static final int                                MAX_SUBGOAL_CYC   = 20;

    private static final int                                MAX_NODE          = 40;

    private static final long                               serialVersionUID  = 1L;

    private final static Logger                             logger            = LoggerFactory.getLogger(EvacuationHumanPathFinding.class);

    private static final int                                STARTGOALMAXDIST  = 95;
    private static final int                                MAXUPDATEPERCYCLE = 50;

    private static final String                             WALL              = "wall";
    private static final String                             ENV_OBJECT        = "envObject";
    private static final String                             FLOOR             = "floor";
    private static final String                             DOOR              = "door";
    private static final String                             HEADING           = "Heading";
    private static final String                             POSITION          = "Position";
    private static final String                             AGENT             = "agent";
    private static final String                             IDLE              = "Idle";

    private HumanTaskModule                                 taskModule;

    private static final float                              space             = 1.5f;
    private static final float                              maxAngleTurn      = FastMath.PI / 4;

    private List<PathFindingNode>                           agentPath         = null;
    private boolean                                         replan            = false;

    private LightWeightBoundedMap<Integer, PathFindingNode> nodeStore         = new LightWeightBoundedMap<Integer, PathFindingNode>(MAX_NODE);

    private ArrayDeque<PathFindingNode>                     nodesToProcess;

    private PathFindingNode                                 newNodeSave;

    private Vector3f                                        lastMove;

    private boolean                                         unableToFindPath;

    RVOPathFinder                                           RVO;

    Vector3f                                                savedpos          = new Vector3f();

    private Vector3f                                        tempSubGoal       = null;

    int                                                     turnTend          = 1;

    private long                                            subGoalStartCycle = 0;

    private HeadingType                                     heading           = HeadingType.GOING;

    public enum HeadingType
    {
        GOING, TURNING, PUSHED
    }

    public EvacuationHumanPathFinding(EvacuationHumanKnowledgeModule knowledgeModule, HumanTaskModule taskModule)
    {
        super(knowledgeModule);
        this.taskModule = taskModule;
        nodesToProcess = new ArrayDeque<PathFindingNode>();
        RVO = new RVOPathFinder(knowledgeModule);
    }

    @Override
    public void pathplan(Vector3f vecGoal, Plan generatedPlan)
    {
        if(knowledgeModule.isWaiting())
        {
            knowledgeModule.setWaitingTime(knowledgeModule.getWaitingTime() + 1);
            if(knowledgeModule.getWaitingTime() > 3)
            {
                knowledgeModule.setWaiting(false);
            }
        }

        List<Collision> collisions = getState().getCollisions();

        // System.out.println("Agent id: " + getState().getID());
        // System.out.println("I am at: " + getState().getPosition());
        // if(agentPath != null)
        // {
        //
        // printPath(agentPath, 1);
        // }
        // else
        // {
        // System.out.println("path null");
        // }
        // System.out.println("My final goal is: " + vecGoal);

        savedpos = getState().getPosition();
        Vector3f currentPos = getState().getPosition();
        Vector3f currentHeading = getState().getHeading();
        Vector3f nextPos = null;

        for(Collision collision : collisions)
        {
            if(collision.getCollidableType().equals(CollisionType.ENVOBJ))
            {
                // addCollidedObjectToKnowledge(collision);
                replan = true;
                // System.out.println("Colliding with Object");
            }
            else if(collision.getCollidableType().equals(CollisionType.AGENT))
            {
                // System.out.println("Colliding with agent");
            }
        }

        if(tempSubGoal == null || (knowledgeModule.getTime() - subGoalStartCycle) > MAX_SUBGOAL_CYC)
        {
            tempSubGoal = makeTemporarySubGoal(vecGoal);
        }

        if(!testifTempSubGoalStillNeeded(vecGoal))
        {
            tempSubGoal = null;
        }

        if(tempSubGoal == null)
        {
            nextPos = RVOPathFinding(vecGoal, currentPos, nextPos);
        }
        else
        {
            nextPos = RVOPathFinding(tempSubGoal, currentPos, nextPos);
        }

        // System.out.println("NextPos: " + nextPos);
        lastMove = nextPos;

        applyPositionalChange(generatedPlan, currentPos, currentHeading, nextPos, vecGoal);

        if(unableToFindPath)
        {
            int sign = 1;
            Vector3f nextHeading = new Vector3f(currentHeading.x * FastMath.cos(maxAngleTurn * sign) + currentHeading.z * FastMath.sin(maxAngleTurn * sign), 0, -currentHeading.x * FastMath.sin(maxAngleTurn * sign) + currentHeading.z
                    * FastMath.cos(maxAngleTurn * sign));

            addFaceTask(generatedPlan, nextHeading);
        }

        knowledgeModule.cleanNearbyAgents();
    }

    private boolean testifTempSubGoalStillNeeded(Vector3f vecGoal)
    {
        List<RVOObstacle> perceivedRVOObstacles = knowledgeModule.getPerceivedRVOObstacles();
        if(perceivedRVOObstacles.size() > 0)
        {
            RVOObstacle rvoObstacle = perceivedRVOObstacles.get(0);
            EnvObjectKnowledgeStorageObject obsj = knowledgeModule.findEnvObj(rvoObstacle.getID());
            if(obsj.agentPathIntersectsObj2D(getState().getPosition(), vecGoal, getState().getRadius()))
            {
                return true;
            }
        }
        return false;
    }

    private Vector3f makeTemporarySubGoal(Vector3f vecGoal)
    {
        // Vector3f newVec = null;
        // Vector3f retVec = null;
        // Vector3f vecTowardGoal = vecGoal.subtract(getState().getPosition());
        // int testAngle = 3;
        // if(vecTowardGoal.angleBetween(Vector3f.UNIT_X) < testAngle *
        // FastMath.DEG_TO_RAD ||
        // vecTowardGoal.angleBetween(Vector3f.UNIT_X.mult(-1)) < testAngle *
        // FastMath.DEG_TO_RAD)
        // {
        // Vector3f vecToObj = angleTestObj(vecGoal);
        // if(vecToObj != null)
        // {
        // // newVec = vecToObj;
        // newVec = new Vector3f(0, 0, vecToObj.z);
        // newVec.normalizeLocal();
        // }
        // }
        //
        // if(vecTowardGoal.angleBetween(Vector3f.UNIT_Z) < testAngle *
        // FastMath.DEG_TO_RAD ||
        // vecTowardGoal.angleBetween(Vector3f.UNIT_Z.mult(-1)) < testAngle *
        // FastMath.DEG_TO_RAD)
        // {
        // Vector3f vecToObj = angleTestObj(vecGoal);
        // if(vecToObj != null)
        // {
        // // newVec = vecToObj;
        // newVec = new Vector3f(vecToObj.x, 0, 0);
        // newVec.normalizeLocal();
        // }
        // }
        // if(newVec != null)
        // {
        // vecTowardGoal.normalizeLocal();
        // vecTowardGoal.addLocal(newVec);
        // vecTowardGoal.normalizeLocal();
        // retVec =
        // getState().getPosition().add(vecTowardGoal).mult(knowledgeModule.desiredSpeed);
        // }
        Vector3f retVec = null;

        List<RVOObstacle> perceivedRVOObstacles = knowledgeModule.getPerceivedRVOObstacles();
        if(perceivedRVOObstacles.size() > 0)
        {
            RVOObstacle rvoObstacle = perceivedRVOObstacles.get(0);
            EnvObjectKnowledgeStorageObject obsj = knowledgeModule.findEnvObj(rvoObstacle.getID());
            if(obsj.agentPathIntersectsObj2D(getState().getPosition(), vecGoal, getState().getRadius()))
            {
                List<Vector3f> points = new ArrayList<Vector3f>();
                List<Vector3f> safePoints = new ArrayList<Vector3f>();
                points.add(obsj.getPosition().add(obsj.getScale().getX() + space, 0, obsj.getScale().getZ() + space));
                points.add(obsj.getPosition().add(-1 * (obsj.getScale().getX() + space), 0, obsj.getScale().getZ() + space));
                points.add(obsj.getPosition().add(obsj.getScale().getX() + space, 0, -1 * (obsj.getScale().getZ() + space)));
                points.add(obsj.getPosition().add(-1 * (obsj.getScale().getX() + space), 0, -1 * (obsj.getScale().getZ() + space)));

                for(Vector3f point : points)
                {
                    if(!obsj.agentPathIntersectsObj2D(getState().getPosition(), point, getState().getRadius()))
                    {
                        safePoints.add(point);
                    }
                }

                float closestDist = Float.MAX_VALUE;
                Vector3f closestVec = null;
                for(Vector3f point : safePoints)
                {
                    float distance = point.distance(vecGoal);
                    if(distance < closestDist)
                    {
                        closestDist = distance;
                        closestVec = point;
                    }
                }

                if(closestVec != null)
                {
                    subGoalStartCycle = knowledgeModule.getTime();
                    retVec = closestVec;
                }
            }
        }

        return retVec;
    }

    protected boolean testObs(Vector3f vecGoal)
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.isCollidable() && !obj.getType().equals(DOOR))
            {
                if(obj.agentPathIntersectsObj2D(getState().getPosition(), vecGoal, getState().getRadius()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean testHeading(Vector3f vecGoal)
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.isCollidable() && !obj.getType().equals(WALL) && !obj.getType().equals(DOOR))
            {
                if(obj.agentPathIntersectsObj2D(getState().getPosition(), vecGoal, getState().getRadius()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean testGoal(Vector3f vecGoal)
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.isCollidable())
            {
                if(obj.intersects2D(vecGoal, getState().getScale()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    protected void applyPositionalChange(Plan generatedPlan, Vector3f currentPos, Vector3f currentHeading, Vector3f nextPos, Vector3f vecGoal)
    {

        if((nextPos != null) && nextPos.distance(currentPos) > .01)
        {

            if(heading != HeadingType.PUSHED)
            {
                // logger.debug("I WANT TO MOVE! "+ getState().getID());
                knowledgeModule.setSavedPos(knowledgeModule.getSelf().getPosition());

                // set heading to match new path.
                heading = HeadingType.GOING;
                heading = setNewHeading(generatedPlan, currentPos, currentHeading, nextPos);

                if(heading == HeadingType.GOING)
                {
                    MoveTask moveTask = (MoveTask) taskModule.createTask(POSITION, knowledgeModule.getTime());
                    moveTask.setPosition(new Vector3f(nextPos));
                    generatedPlan.addTask(moveTask);
                    knowledgeModule.setWantToMove(true);
                }
            }
            else if(heading == HeadingType.PUSHED)
            {
                MoveTask moveTask = (MoveTask) taskModule.createTask(POSITION, knowledgeModule.getTime());
                moveTask.setPosition(new Vector3f(nextPos));
                generatedPlan.addTask(moveTask);
                knowledgeModule.setWantToMove(true);
                knowledgeModule.getSelf().setPosture(Posture.Walk_back);

                setNewHeadingPushed(generatedPlan, currentPos, currentHeading, nextPos, vecGoal);
            }
        }
        else
        {
            knowledgeModule.setWantToMove(false);
            // knowledgeModule.getSelf().setPosture(Posture.STANDING);
            knowledgeModule.getSelf().setPosture(Posture.Idle1);

            IdleTask idleTask = (IdleTask) taskModule.createTask(IDLE, knowledgeModule.getTime());
            generatedPlan.addTask(idleTask);
        }

        if((nextPos != null) && nextPos.distance(currentPos) < .0f)
        {
            Random r = new Random();
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_DOOR))
            {
                float dist = getState().getPosition().distance(knowledgeModule.findEnvObj(knowledgeModule.getGoalDoor().getId()).getPosition());
                if(dist < 8 && dist > 2)
                {
                    if(!knowledgeModule.isWaiting())
                    {
                        knowledgeModule.setWaiting(true);
                        knowledgeModule.setWaitingTime(0);
                    }
                }
            }
        }
        else if(nextPos != null)
        {
            knowledgeModule.setWaiting(false);
        }
//
//         MoveTask moveTask = (MoveTask) taskModule.createTask(POSITION, knowledgeModule.getTime());
//         moveTask.setPosition(new Vector3f(currentPos.add(1, 1, 1)));
//         generatedPlan.addTask(moveTask);
        
    }

    protected Vector3f RVOPathFinding(Vector3f vecGoal, Vector3f currentPos, Vector3f nextPos)
    {
        if(heading == HeadingType.PUSHED)
        {
            heading = HeadingType.GOING;
        }

        Vector3f expectedHeading = vecGoal.subtract(currentPos).normalize();

        Vector3f RVOHeading = null;
        if(heading != HeadingType.TURNING)
        {
            float distanceToDestination = vecGoal.subtract(currentPos).length();
            if(getState().getDesiredSpeed() >= distanceToDestination)
            {
                // move to my destination
                if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_DOOR) && tempSubGoal == null && knowledgeModule.isSirenHeard() && knowledgeModule.getGoalDoor().isExit())
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.ARRIVED_AT_EXIT);
                }
                else if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_DOOR) && tempSubGoal == null)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.ARRIVED_IN_ROOM);
                }
                else if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.EXITING_BUILDING) && tempSubGoal == null)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.EXITED_BUILDING);
                }
                else if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_TO_SAFETY) && tempSubGoal == null)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.SAFE);
                }
                // else if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING) && tempSubGoal == null)
                // {
                // knowledgeModule.setCurrentMentalState(knowledgeModule.getPreviousMentalState());
                // }
                else if(tempSubGoal == null)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.ARRIVED);
                }
                else
                {
                    // arrived at subgoal
                }
                RVOHeading = RVO.getGoodHeading(new Vector2f(vecGoal.x, vecGoal.z));
                nextPos = getState().getPosition().add(RVOHeading);
                tempSubGoal = null;
            }
            else
            {

                // if(knowledgeModule.isWaiting())
                // {
                // vecGoal = getState().getPosition();
                // }

                // System.out.println("**********************************************START RVO PATH FINDING HERE**********************************************");
                RVOHeading = RVO.getGoodHeading(new Vector2f(vecGoal.x, vecGoal.z));

                // System.out.println("**********************************************END   RVO PATH FINDING HERE**********************************************");

                float angleBetween = expectedHeading.angleBetween(RVOHeading);
                if(angleBetween > FastMath.PI / 2)
                {

                    heading = HeadingType.PUSHED;
                    // nextPos = getState().getPosition().add(RVOHeading.mult(.3f / RVOHeading.length()));
                    // nextPos = getState().getPosition().add(RVOHeading.mult(.3f));
                    nextPos = getState().getPosition().add(RVOHeading);
                    // nextPos = getState().getPosition();
                    // System.out.println("####@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@AGEMT{ISJJEDE{ISJPUSHED");
                    // knowledgeModule.setWaiting(true);
                    // knowledgeModule.setWaitingTime(0);
                }
                else
                {
                    nextPos = getState().getPosition().add(RVOHeading);
                }

            }
        }
        else
        {
            nextPos = lastMove;
        }

        return nextPos;
    }

    protected Vector3f oldPathFinding(Vector3f vecGoal, Vector3f currentPos, Vector3f nextPos, Vector3f goalPoint, Vector3f finalPos)
    {
        List<PathFindingNode> newNodes = calculateNewTravelNodes(knowledgeModule.getNewEnvObjs());

        removeBadNodes(newNodes);
        for(PathFindingNode pathFindingNode : newNodes)
        {
            nodeStore.put(pathFindingNode.getNodeID(), pathFindingNode);
        }
        updateNeighbors(newNodes);

        // System.out.println("Time to update neighbors: " +
        // (System.currentTimeMillis() - before));

        if(agentPath != null)
        {
            if(agentPath.size() > 0)
            {
                goalPoint = agentPath.get(0).getPoint();
            }
        }

        if(agentPath == null || (agentPath.size() == 0) || replan == true || testObstacled(goalPoint))
        {
            unableToFindPath = false;
            deepPathFinder(vecGoal);
            replan = false;
        }

        if(agentPath != null)
        {
            if(agentPath.size() > 0)
            {
                goalPoint = agentPath.get(0).getPoint();
            }
        }

        knowledgeModule.getSelf().setPlanningDetails(knowledgeModule.getSelf().getPlanningDetails().concat(", GoalPoint: " + goalPoint));
        knowledgeModule.getSelf().setPlanningDetails(knowledgeModule.getSelf().getPlanningDetails().concat(", EnvObjID: " + agentPath.get(0).getParentEnvObjID()));
        if(goalPoint != null)
        {

            Vector3f directionVector = goalPoint.subtract(currentPos);

            // calculate the distance to the destination
            float distanceToDestination = directionVector.length();

            // calculate the unit vector of the edge
            // currently setting agent head position to where it's going

            if(distanceToDestination == 0)
            {
                // I have arrived at a destination
                agentPath.remove(0);
                if(agentPath.size() == 0)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
                }
                else
                {
                    goalPoint = agentPath.get(0).getPoint();
                    directionVector = goalPoint.subtract(currentPos);
                    distanceToDestination = directionVector.length();
                }
            }
            else if((getState().getDesiredSpeed() >= distanceToDestination))
            {
                // move to my destination
                nextPos = goalPoint;
            }
            else
            {
                Vector3f unitVector = directionVector.clone();
                unitVector.normalizeLocal();
                // unitVector.addLocal(avoidAgents());
                // unitVector.normalizeLocal();
                nextPos = getState().getPosition().add(unitVector.mult(getState().getDesiredSpeed()));
                if(checkMoveAgainstWalls(nextPos))
                {
                    // System.out.println("Agent " + getState().getID() +
                    // "  Getting in here");
                    nextPos = getState().getPosition().add(directionVector.normalize().mult(getState().getDesiredSpeed()));
                }
            }

        }

        if(finalPos != null)
        {
            nextPos = finalPos;
        }

        // don't move thru walls!!! -_-
        // nextPos = checkMoveAgainstWalls(nextPos, vecGoal);

        if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING) && nextPos.subtract(currentPos).dot(knowledgeModule.getThreatDirection()) > 0)
        {
            Vector3f directionVector = vecGoal.subtract(currentPos);
            directionVector.normalizeLocal();
            Vector3f u_Vnew = directionVector.clone();
            for(VisionModel vis : knowledgeModule.getVisibleList())
            {
                if(vis.getType().equals("envObject"))
                {
                    EnvObjectKnowledgeStorageObject envObj = knowledgeModule.findEnvObj(vis.getId());
                    if(envObj.getType().equals("box"))
                    {

                        Vector3f d_V = envObj.getPosition().subtract(getState().getPosition());

                        float dist = FastMath.sqrt(FastMath.pow(envObj.getScale().getX(), 2) + FastMath.pow(envObj.getScale().getZ(), 2)) + getState().getDesiredSpeed() + 5;
                        if(d_V.length() <= dist)
                        {

                            Vector3f temp = d_V.cross(directionVector);
                            u_Vnew.addLocal((temp.cross(d_V)));

                            if((u_Vnew.x == 0) && (u_Vnew.y == 0) && (u_Vnew.z == 0))
                            {
                                u_Vnew = directionVector.cross(new Vector3f(0, 1, 0));
                            }
                        }
                    }
                }
            }
            u_Vnew.setY(0);
            u_Vnew.normalizeLocal();
            // u_Vnew = directionVector;
            nextPos = getState().getPosition().add(u_Vnew.mult(getState().getDesiredSpeed()));
        }
        return nextPos;
    }

    protected void addFaceTask(Plan generatedPlan, Vector3f nextHeading)
    {
        FaceTask faceTask = (FaceTask) taskModule.createTask(HEADING, knowledgeModule.getTime());
        faceTask.setHeading(new Vector3f(nextHeading));

        generatedPlan.addTask(faceTask);
    }

    protected HeadingType setNewHeading(Plan generatedPlan, Vector3f currentPos, Vector3f currentHeading, Vector3f nextPos)
    {

        HeadingType result = HeadingType.GOING;
        Vector3f nextHeading = (nextPos.subtract(currentPos)).normalizeLocal();

        if((nextHeading.getX() == 0) && (nextHeading.getY() == 0) && (nextHeading.getZ() == 0))
        {
            nextHeading = currentHeading; // must look at SOMEthing.
        }

        float angleBetween = nextHeading.angleBetween(currentHeading);
        Vector3f cp = currentHeading.cross(nextHeading);
        if((angleBetween > maxAngleTurn))
        {
            result = HeadingType.TURNING;
            float sign = FastMath.sign(cp.y);
            if(sign == 0)
            {
                sign = 1;
            }
            nextHeading.set(currentHeading.x * FastMath.cos(maxAngleTurn * sign) + currentHeading.z * FastMath.sin(maxAngleTurn * sign), 0, -currentHeading.x * FastMath.sin(maxAngleTurn * sign) + currentHeading.z * FastMath.cos(maxAngleTurn * sign));
        }
        addFaceTask(generatedPlan, nextHeading);

        return result;
    }

    protected void setNewHeadingPushed(Plan generatedPlan, Vector3f currentPos, Vector3f currentHeading, Vector3f nextPos, Vector3f vecGoal)
    {
        Vector3f nextHeading;
        if(tempSubGoal == null)
        {
            nextHeading = (vecGoal.subtract(currentPos)).normalizeLocal();
        }
        else
        {
            nextHeading = (tempSubGoal.subtract(currentPos)).normalizeLocal();
        }
        if((nextHeading.getX() == 0) && (nextHeading.getY() == 0) && (nextHeading.getZ() == 0))
        {
            nextHeading = currentHeading; // must look at SOMEthing.
        }

        float angleBetween = nextHeading.angleBetween(currentHeading);
        Vector3f cp = currentHeading.cross(nextHeading);
        float pushMaxTurn = maxAngleTurn / 8;
        if((angleBetween > pushMaxTurn))
        {
            float sign = FastMath.sign(cp.y);
            if(sign == 0)
            {
                sign = 1;
            }
            nextHeading.set(currentHeading.x * FastMath.cos(pushMaxTurn * sign) + currentHeading.z * FastMath.sin(pushMaxTurn * sign), 0, -currentHeading.x * FastMath.sin(pushMaxTurn * sign) + currentHeading.z * FastMath.cos(pushMaxTurn * sign));
        }
        addFaceTask(generatedPlan, nextHeading);

    }

    private void addCollidedObjectToKnowledge(Collision collision)
    {
        EnvObjectState obj = new EnvObjectState();
        obj.setType("box");
        obj.setDescription("box");
        obj.setScale(new Vector3f(1, 1, 1));
        obj.setPosition(lastMove);
        obj.setID(10000 + collision.getCollidableId());
        knowledgeModule.addEnvObj(obj);
    }

    private Vector3f avoidAgents()
    {
        Vector3f totalForce = new Vector3f(0, 0, 0);
        knowledgeModule.getSelf().setPlanningDetails(knowledgeModule.getSelf().getPlanningDetails().concat(", Avoiding: "));
        for(AgentKnowledgeStorageObject agent : knowledgeModule.getNearbyAgents().values())
        {
            knowledgeModule.getSelf().setPlanningDetails(knowledgeModule.getSelf().getPlanningDetails().concat(agent.getID() + ", "));
            Vector3f d = agent.getPosition().subtract(getState().getPosition());

            Vector3f tForce = d.cross(getState().getVelocity()).cross(d);
            if(d.dot(getState().getVelocity()) < .001)
            {
                tForce.set(-getState().getHeading().z, 0, getState().getHeading().x);
            }
            tForce.normalizeLocal();

            float distanceWeight = (d.length() - knowledgeModule.getNearDistance()) * (d.length() - knowledgeModule.getNearDistance());
            float opposingWeight;
            if(getState().getVelocity().dot(agent.getVelocity()) > 0)
            {
                opposingWeight = 1.2f;
            }
            else
            {
                opposingWeight = 2.4f;
            }
            tForce.multLocal(distanceWeight);
            tForce.multLocal(opposingWeight);

            totalForce.addLocal(tForce);
        }

        // totalForce.normalizeLocal();
        float forceMultiplier = .01f;
        return totalForce.mult(forceMultiplier);
    }

    /**
     * Make sure the agent is not moving from one side of a wall to another.
     * 
     * @param nextPos
     * @return
     */
    private boolean checkMoveAgainstWalls(Vector3f nextPos)
    {
        List<VisionModel> visibleList = knowledgeModule.getVisibleList();
        for(int val = 0; val < visibleList.size(); val++)
        {
            if(visibleList.get(val).getType().compareTo(ENV_OBJECT) == 0)
            {
                EnvObjectKnowledgeStorageObject seenObj = knowledgeModule.findEnvObj(visibleList.get(val).getId());
                if(seenObj != null)
                {
                    if(seenObj.isCollidable())
                    {
                        if(seenObj.intersects2D(nextPos, getState().getScale()))
                        {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    /**
     * The new DEEP path finder obstacle avoidance algorithm.
     * 
     * @param currentPositionalGoal
     *        The Current goal location.
     */
    private void deepPathFinder(Vector3f goal)
    {

        List<PathFindingNode> path = findGoodSearchPath(goal, knowledgeModule.getSelf().getPosition());

        if(path != null)
        {
            // grr
        }
        else
        {
            logger.debug("PATH IS NULL");
            logger.debug("My goal is: " + goal);
        }

        agentPath = path;

    }

    public void printPath(List<PathFindingNode> path)
    {
        int count = 0;
        System.out.println("My PATH is: ");
        for(PathFindingNode node : path)
        {
            System.out.println(count + ": " + node.getPoint());
            count++;
        }
    }

    public void printDoorPath(List<Thing> list)
    {
        int count = 0;
        if(list != null)
        {
            System.out.println("My PATH is: ");
            for(Thing node : list)
            {
                if(node instanceof Door)
                {
                    System.out.println(count + ": " + ((Door) node).getId());
                    count++;

                }
            }
        }
    }

    private List<PathFindingNode> findGoodSearchPath(Vector3f goal, Vector3f start)
    {
        cleanUpStoredNodes();

        ArrayList<Integer> closedNodeIDS = new ArrayList<>();
        PathFindingNode startNode = new PathFindingNode(getState().getAgentSize(), start, -2);
        PathFindingNode goalNode = new PathFindingNode(getState().getAgentSize(), goal, -3);

        List<Integer> openNodeIDs = prepareNodes(goal, start, startNode, goalNode);

        NodeSorter sorter = new NodeSorter(nodeStore, startNode, goalNode);
        boolean needsort = false;

        while(openNodeIDs.size() > 0)
        {
            PathFindingNode currentNode;

            if(needsort)
            {
                sorter.sort(openNodeIDs);
                needsort = false;
            }

            int currentNodeID = openNodeIDs.get(0);
            if(currentNodeID == startNode.getNodeID())
            {
                currentNode = startNode;
            }
            else if(currentNodeID == goalNode.getNodeID())
            {
                currentNode = goalNode;
                return makepath(currentNode);
            }
            else
            {
                currentNode = nodeStore.get(currentNodeID);
            }
            openNodeIDs.remove(0);
            currentNode.setClosed(true);
            closedNodeIDS.add(currentNode.getNodeID());
            for(int neighborID : currentNode.getNeighbors())
            {
                PathFindingNode neighbor = nodeStore.get(neighborID);
                if(neighbor != null)
                {
                    needsort = processNode(goal, neighbor, openNodeIDs, needsort, currentNode);
                }
            }
            if(currentNode.isConnectedToGoal())
            {
                needsort = processNode(goal, goalNode, openNodeIDs, needsort, currentNode);
            }

            // FIXME: temporary bug fix. The number of loops is exploding!
            // if(loops > 10)
            // {
            // break;
            // }
        }
        // if(loops > 20)
        // System.out.println("fail: "+loops);
        Random r = new Random();
        // return makepath(maxdepthnode); // if can't find any path to goal,
        // return a long path.
        int randomint = r.nextInt(closedNodeIDS.size());

        if(closedNodeIDS.get(randomint) == startNode.getNodeID())
        {
            // System.out.println("AgentID: " + getState().getID());
            // System.out.println("returning path to self? closed nodes size: "
            // + closedNodeIDS.size());
            // System.out.println("random was: " + randomint);
            // System.out.println("start node # of neighbors was: " +
            // startNode.getNeighbors().size());
            unableToFindPath = true;
            return makepath(startNode);
        }
        else if(nodeStore.get(closedNodeIDS.get(randomint)) != null)
        {
            return makepath(nodeStore.get(closedNodeIDS.get(randomint)));
        }
        else
            return null;
    }

    private boolean processNode(Vector3f goal, PathFindingNode node, List<Integer> openNodeIDs, boolean needsort, PathFindingNode currentNode)
    {
        if(!node.isClosed())
        {
            float tempGScore = currentNode.getGscore() + currentNode.getPoint().distance(node.getPoint());
            if(node.isNewNode() || (tempGScore < node.getGscore()))
            {
                if(node.isNewNode())
                {
                    openNodeIDs.add(node.getNodeID());
                    needsort = true;
                    node.setNewNode(false);
                }
                node.setCameFrom(currentNode);
                node.setDepth(currentNode.getDepth() + 1);
                node.setGscore(tempGScore);
                node.setFscore(node.getGscore() + estimateCost(node.getPoint(), goal));
            }
        }
        return needsort;
    }

    private List<Integer> prepareNodes(Vector3f goal, Vector3f start, PathFindingNode startNode, PathFindingNode goalNode)
    {
        startNode.calculateStartNeighbors(nodeStore.values(), knowledgeModule.getEnvObjects(), STARTGOALMAXDIST, goalNode);
        // startNode.calculateNeighborsGoal(goalNode,
        // knowledgeModule.getEnvObjects());

        testIfNodesCanSeeGoal(goal, start, goalNode);

        List<Integer> openNodeIDs = new ArrayList<Integer>();
        openNodeIDs.add(startNode.getNodeID());
        return openNodeIDs;
    }

    protected void testIfNodesCanSeeGoal(Vector3f goal, Vector3f start, PathFindingNode goalNode)
    {
        for(PathFindingNode node : nodeStore.values())
        {
            node.setFscore(estimateCost(start, goal));
            if(node.getPoint().distance(goal) < STARTGOALMAXDIST)
            {
                node.calculateNeighborsGoal(goalNode, knowledgeModule.getEnvObjects());
            }
        }
    }

    protected void cleanUpStoredNodes()
    {
        for(PathFindingNode pathFindingNode : nodeStore.values())
        {
            pathFindingNode.setDepth(0);
            pathFindingNode.setFscore(0);
            pathFindingNode.setGscore(0);
            pathFindingNode.setClosed(false);
            pathFindingNode.setNewNode(true);
            pathFindingNode.setConnectedToGoal(false);
        }
    }

    protected void cleanUpStoredDoors()
    {
        for(Door d : knowledgeModule.getDoors())
        {
            d.setDepth(0);
            d.setFscore(0);
            d.setGscore(0);
            d.setClosed(false);
            d.setNewNode(true);
        }
    }

    private List<PathFindingNode> makepath(PathFindingNode currentNode)
    {
        List<PathFindingNode> path = new ArrayList<PathFindingNode>();

        while(currentNode.getCameFrom() != null)
        {
            path.add(currentNode);
            currentNode = currentNode.getCameFrom();
        }
        if(path.size() == 0)
        {
            path.add(currentNode);
        }

        List<PathFindingNode> reversepath = new ArrayList<PathFindingNode>();

        for(int i = path.size() - 1; i >= 0; i--)
        {
            reversepath.add(path.get(i));
        }

        return reversepath;
    }

    private List<Thing> makepathDoor(Door currentNode)
    {
        List<Thing> path = new ArrayList<Thing>();

        while(currentNode.getCameFrom() != null)
        {
            path.add(currentNode);
            currentNode = currentNode.getCameFrom();
        }
        if(path.size() == 0)
        {
            path.add(currentNode);
        }

        List<Thing> reversepath = new ArrayList<Thing>();

        for(int i = path.size() - 1; i >= 0; i--)
        {
            reversepath.add(path.get(i));
        }

        return reversepath;
    }

    public List<Thing> findPathToDoor(Door goal, Vector3f start)
    {
        cleanUpStoredDoors();

        EnvObjectKnowledgeStorageObject goalObj;
        if(goal.isExit())
        {
            goalObj = knowledgeModule.getExitDoor(goal.getId());
        }
        else
        {
            goalObj = knowledgeModule.findEnvObj(goal.getId());
        }

        if(goalObj == null)
        {
            goalObj = knowledgeModule.getImportantObject(goal.getId());
        }

        // for(Door d : knowledgeModule.getDoors())
        // {
        // d.setFscore(estimateCost(start, goalObj.getPosition()));
        // }
        ArrayList<Integer> closedNodeIDS = new ArrayList<>();

        List<Integer> openNodeIDs = new ArrayList<Integer>();
        Door startNode = createStartNode();
        addStartNeighbors(startNode);
        startNode.setFscore(estimateCost(start, goalObj.getPosition()));
        openNodeIDs.add(startNode.getId());

        DoorSorter sorter = new DoorSorter(knowledgeModule.getDoorsMap(), goal);
        boolean needsort = false;

        while(openNodeIDs.size() > 0)
        {
            Door currentNode;

            if(needsort)
            {
                sorter.sort(openNodeIDs);
                needsort = false;
            }

            // System.out.println("Fscores:");
            // for(int i : openNodeIDs)
            // {
            // Door d = knowledgeModule.getDoor(i);
            // if(d != null)
            // System.out.println("NodeId: " + d.getId() + ", Fscore: " + d.getFscore());
            // else
            // System.out.println("door is null");
            //
            // }

            int currentNodeID = openNodeIDs.get(0);
            if(currentNodeID == goal.getId())
            {
                currentNode = goal;
                return makepathDoor(currentNode);
            }
            else if(currentNodeID == startNode.getId())
            {
                currentNode = startNode;
            }
            else
            {
                currentNode = knowledgeModule.getDoor(currentNodeID);
            }
            openNodeIDs.remove(0);
            currentNode.setClosed(true);
            closedNodeIDS.add(currentNode.getId());
            for(int neighborID : currentNode.getNeighbors())
            {
                Door neighbor = knowledgeModule.getDoor(neighborID);
                if(neighbor != null)
                {
                    needsort = processDoor(goal, neighbor, openNodeIDs, needsort, currentNode);
                }
            }
        }
        return null;
    }

    protected void addStartNeighbors(Door startNode)
    {
        for(Door d : knowledgeModule.getDoors())
        {
            EnvObjectKnowledgeStorageObject dObj = knowledgeModule.findEnvObj(d.getId());
            if(dObj != null)
            {
                if(getState().getPosition().distance(dObj.getPosition()) < 180)
                {
                    boolean intersects = false;
                    for(EnvObjectKnowledgeStorageObject testObj : knowledgeModule.getEnvObjects())
                    {
                        if(testObj.getID() != dObj.getID())
                        {
                            if(testObj.getType().equals(WALL) || testObj.getType().equals(DOOR))
                            {
                                if(testObj.miniBAIntersects2DLine(getState().getPosition(), dObj.getPosition()))
                                {
                                    intersects = true;
                                    break;
                                }
                            }
                        }
                    }
                    if(!intersects)
                    {
                        startNode.addNeighbor(d.getId());
                    }

                }
            }
        }

    }

    private boolean processDoor(Door goal, Door node, List<Integer> openNodeIDs, boolean needsort, Door currentNode)
    {
        if(!node.isClosed())
        {
            VirtualKnowledgeStorageObject currentNodeObj;
            if(currentNode.getId() == -1)
            {
                currentNodeObj = new VirtualKnowledgeStorageObject(-1, getState().getPosition());
            }
            else
            {
                currentNodeObj = knowledgeModule.findEnvObj(currentNode.getId());
            }

            EnvObjectKnowledgeStorageObject nodeObj;
            if(node.isExit())
            {
                nodeObj = knowledgeModule.getExitDoor(node.getId());
            }
            else
            {
                nodeObj = knowledgeModule.findEnvObj(node.getId());
            }

            float tempGScore = currentNode.getGscore() + estimateCost(currentNodeObj, nodeObj);
            tempGScore = addCongestionFactor(node, currentNode, tempGScore);

            if(node.getDoorCongestion() > 8 && node.getDoorflow() < 0 && !node.isExit())
            {
                return false;
            }

            if(node.isNewNode() || (tempGScore < node.getGscore()))
            {
                if(node.isNewNode())
                {
                    openNodeIDs.add(node.getId());
                    node.setNewNode(false);
                }
                needsort = true;
                node.setCameFrom(currentNode);
                node.setDepth(currentNode.getDepth() + 1);
                node.setGscore(tempGScore);
                EnvObjectKnowledgeStorageObject goalObj;
                if(goal.isExit())
                {
                    goalObj = knowledgeModule.getExitDoor(goal.getId());
                }
                else
                {
                    goalObj = knowledgeModule.findEnvObj(goal.getId());
                }

                node.setFscore(node.getGscore() + estimateCost(nodeObj, goalObj));
                // addCongestionFactorF(goal, node);

            }
        }
        return needsort;
    }

    protected void addCongestionFactorF(Door goal, Door node)
    {
        if(node.getDoorCongestion() > 4)
        {
            node.setFscore(node.getFscore() + 25 * node.getDoorCongestion());
        }
        if(goal.getDoorCongestion() > 4)
        {
            node.setFscore(node.getFscore() + 25 * goal.getDoorCongestion());
        }

        if(node.getDoorCongestion() > 10)
        {
            node.setFscore(node.getFscore() + 50 * node.getDoorCongestion());
        }
        if(goal.getDoorCongestion() > 10)
        {
            node.setFscore(node.getFscore() + 50 * goal.getDoorCongestion());
        }
    }

    protected float addCongestionFactor(Door node, Door currentNode, float tempGScore)
    {

        if(node.getDoorCongestion() > 4)
        {
            tempGScore = tempGScore + 25 * node.getDoorCongestion();
        }

        if(node.getDoorCongestion() > 10)
        {
            tempGScore = tempGScore + 50 * node.getDoorCongestion();
        }
        return tempGScore;
    }

    protected Door createStartNode()
    {
        Door startNode = new Door(-1);
        startNode.setDepth(0);
        startNode.setFscore(0);
        startNode.setGscore(0);
        startNode.setClosed(false);
        startNode.setNewNode(true);
        return startNode;
    }

    private float estimateCost(Vector3f start, Vector3f goal)
    {
        return start.distance(goal);
    }

    private float estimateCost(VirtualKnowledgeStorageObject start, VirtualKnowledgeStorageObject goal)
    {
        if(start != null && goal != null)
        {
            return start.getPosition().distance(goal.getPosition());
        }
        return 2000;
    }

    // @SuppressWarnings("unused")
    // private List<EnvObjectKnowledgeStorageObject> calculateAvoidList(Vector3f
    // goal)
    // {
    // List<EnvObjectKnowledgeStorageObject> avoidList = new
    // ArrayList<EnvObjectKnowledgeStorageObject>();
    //
    // for(EnvObjectKnowledgeStorageObject obj :
    // knowledgeModule.getEnvObjects())
    // {
    // if(obj.isCollidable())
    // {
    // boolean inWay = obj.intersects(knowledgeModule.getSelf().getPosition(),
    // goal);
    //
    // if(!inWay)
    // {
    // inWay = obj.isInBetween(knowledgeModule.getSelf().getPosition(), goal,
    // space);
    // }
    //
    // if(inWay)
    // {
    // avoidList.add(obj);
    // }
    // }
    // }
    // return avoidList;
    // }

    private List<PathFindingNode> calculateNewTravelNodes(List<EnvObjectKnowledgeStorageObject> newObjsList)
    {
        List<PathFindingNode> nodes = new ArrayList<PathFindingNode>();

        for(EnvObjectKnowledgeStorageObject obj : newObjsList)
        {
            if(!obj.getType().equals(WALL))
            {
                if(obj.getType().equals(DOOR))
                {
                    nodes.addAll(obj.calcNodesForDoor(space, getState().getAgentSize()));
                }
                else if(obj.getType().equals(FLOOR))
                {
                    nodes.addAll(obj.calcNodesNoCenter(-3, getState().getAgentSize()));
                }
                else
                {
                    nodes.addAll(obj.calcNodesNoCenter(space, getState().getAgentSize()));
                }
            }
        }

        return nodes;

    }

    @Override
    public void notifyReact()
    {
        replan = true;
    }

    private void updateNeighbors(List<PathFindingNode> newNodes)
    {
        long endTime = System.currentTimeMillis() + MAXUPDATEPERCYCLE;

        nodesToProcess.addAll(newNodes);

        // process new nodes
        updateNewNodes(newNodes);
        // update for new envobjs
        updateNewEnvObjs();

        // System.out.println("Nodes to proess SIZEEEEE " +
        // nodesToProcess.size());
        if(newNodeSave != null)
        {
            newNodeSave.calculateNeighbors(nodeStore.values(), knowledgeModule.getEnvObjects(), endTime, STARTGOALMAXDIST);
        }

        PathFindingNode newNode = null;
        while(System.currentTimeMillis() <= endTime && (newNode = nodesToProcess.poll()) != null)
        {
            newNode.calculateNeighbors(nodeStore.values(), knowledgeModule.getEnvObjects(), endTime, STARTGOALMAXDIST);
        }
        if(System.currentTimeMillis() >= endTime)
        {
            newNodeSave = newNode;
        }
        else
        {
            newNodeSave = null;
        }

    }

    protected void updateNewNodes(List<PathFindingNode> newNodes)
    {
        for(PathFindingNode newNode : newNodes)
        {
            for(PathFindingNode storedNode : nodeStore.values())
            {
                if(newNode.getPoint().distance(storedNode.getPoint()) < STARTGOALMAXDIST)
                {
                    if(!nodesToProcess.contains(storedNode))
                    {
                        nodesToProcess.add(storedNode);
                        storedNode.clearNeighbors();
                    }
                }
            }
        }
    }

    protected void updateNewEnvObjs()
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getNewEnvObjs())
        {
            for(PathFindingNode storedNode : nodeStore.values())
            {
                if(obj.getPosition().distance(storedNode.getPoint()) < STARTGOALMAXDIST)
                {
                    if(!nodesToProcess.contains(storedNode))
                    {
                        nodesToProcess.add(storedNode);
                        storedNode.clearNeighbors();
                    }
                }
            }
        }
    }

    private void removeBadNodes(List<PathFindingNode> newNodes)
    {
        List<PathFindingNode> toRemove = new ArrayList<PathFindingNode>();
        for(PathFindingNode pathFindingNode : newNodes)
        {
            for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
            {
                if(obj.isCollidable() && obj.intersects2D(pathFindingNode.getPoint(), getState().getScale()))
                {

                    toRemove.add(pathFindingNode);
                    break;
                }
            }
        }

        for(PathFindingNode pathFindingNode : toRemove)
        {
            newNodes.remove(pathFindingNode);
        }

    }

    private boolean testObstacled(Vector3f goalPoint)
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.isCollidable() && obj.agentPathIntersectsObj2D(getState().getPosition(), goalPoint, knowledgeModule.getSelf().getAgentSize()))
            {
                return true;
            }
        }
        return false;
    }

    private EHumanAgentState getState()
    {
        return knowledgeModule.getSelf();
    }

    public PathFindingNode getNodeByID(int id)
    {
        return nodeStore.get(id);
    }

    public void removeTempSubGoal()
    {
        tempSubGoal = null;
    }

    @Override
    public void notifyUserGoalChange()
    {
        tempSubGoal = null;
    }
}
