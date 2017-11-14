package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.AgentMessage;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.ontology.Thing;
import edu.utdallas.mavs.divas.core.sim.agent.planning.AbstractPlanGenerator;
import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.interaction.HumanInteractionModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule.MentalStateOfAgent;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.AgentKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.EnvObjectKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.VisionModel;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.internal.LocationGoal;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology.Door;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.FaceTask;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.IdleTask;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.OpenDoorTask;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;

public class EvacuationHumanPlanGenerator extends AbstractPlanGenerator<EvacuationHumanKnowledgeModule, HumanTaskModule>
{
    private static final long                  serialVersionUID = 1L;

    private final static Logger                logger           = LoggerFactory.getLogger(EvacuationHumanPlanGenerator.class);

    private static final String                WALL             = "wall";
    private static final String                FLOOR            = "floor";
    private static final String                DOOR             = "door";
    private static final String                GRILLING_FOOD    = "GrillingFood";
    private static final String                DRUMS            = "Drums";
    private static final String                FIREWORK         = "Firework";
    private static final String                ENV_OBJECT       = "envObject";
    private static final String                VECTOR           = "Vector";
    private static final String                HEADING          = "Heading";
    private static final String                POSITION         = "Position";
    private static final String                OPENDOOR         = "OpenDoor";
    private static final String                EXITDOOR         = "outsidedoor";
    private static final String                IDLE             = "Idle";

    private PlanEvaluator<EvacuationPlan>      planEvaluator;
    private PathFinding<EvacuationHumanKnowledgeModule> pathFinding;
    private HumanInteractionModule             interactionModule;

    private Vector3f                           currentVecGoal;

    /**
     * Create a evacuation human plan generator.
     * 
     * @param knowledgeModule
     * @param taskModule
     * @param humanInteractionModule
     * @param planStorageModule
     * @param pathFinding
     */
    public EvacuationHumanPlanGenerator(EvacuationHumanKnowledgeModule knowledgeModule, HumanTaskModule taskModule, HumanInteractionModule humanInteractionModule,

    EvacuationHumanPathFinding pathFinding)
    {
        super(knowledgeModule, taskModule);
        this.interactionModule = humanInteractionModule;
        this.pathFinding = pathFinding;
        this.planEvaluator = new PlanEvaluator<EvacuationPlan>(knowledgeModule);
    }

    @Override
    public Plan plan()
    {
        // System.out.println("EXIT DOR SIIIIIIIIIIIIIIIIIIIZ" + knowledgeModule.getExitDoors().size());
        // printState();

        // System.out.println(knowledgeModule.getDoors().size());
        // System.out.println(getState().getReachDistance());

        // System.out.println(knowledgeModule.getEnvObjects().size());

        EvacuationPlan generatedPlan = new EvacuationPlan();

        updateAgentKnowledge();

        respondToEvents();

        processActionGoals(generatedPlan);

        pathPlanning(generatedPlan);

        planMovement(generatedPlan);

        planEvaluator.evaluatePlan(generatedPlan);
        // planStorageModule.addPlan(generatedPlan);
        generatedPlan.setAgentPath(knowledgeModule.getAgentPath());
        knowledgeModule.getSelf().setAgentPath(makeVectorPath());
        return generatedPlan;
    }

    protected void pathPlanning(Plan generatedPlan)
    {
        if(!knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.SAFE))
        {
            if(!knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_TO_SAFETY))
            {
                reevaluateCurrentPlan();
            }
            if(!knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.PERSUING_USER_GOAL))
            {
                processNavigationGoals(); // don't process navigation if goal specified by user.
            }
        }
        else
        {

            knowledgeModule.setWaiting(true);
            knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
            knowledgeModule.getSelf().setPosture(Posture.Idle1);

            IdleTask idleTask = (IdleTask) taskModule.createTask(IDLE, knowledgeModule.getTime());
            generatedPlan.addTask(idleTask);

        }

    }

    private void reevaluateCurrentPlan()
    {
        boolean stuck = checkIfStuck();

        // if(knowledgeModule.getAgentPath().getDistanceFactor() > 3)
        // {
        // createPath();
        // }

        if(!stuck)
        {
            if(havePath() && knowledgeModule.getTime() % 25 == 0)
            {
                Vector3f goalPos = null;
                Door finalStep = (Door) knowledgeModule.getAgentPath().getFinalStep();
                if(finalStep.getDoorCongestion() < 8 && knowledgeModule.getGoalDoor().getDoorCongestion() > 5)
                {
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
                    knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                    goalPos = calculatePath(goalPos, finalStep);
                    testPossibleGoal(goalPos);
                }
            }

            // if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_DOOR) && havePath())
            // {
            // EnvObjectKnowledgeStorageObject goalDoor = knowledgeModule.findEnvObj(knowledgeModule.getGoalDoor().getId());
            // if(goalDoor != null)
            // {
            // checkCongestionDoor(knowledgeModule.getGoalDoor());
            // if(knowledgeModule.getGoalDoor().getDoorCongestion() > 4 && getState().getPosition().distance(goalDoor.getPosition()) > 2)
            // {
            // knowledgeModule.setStuckPos(getState().getPosition());
            // knowledgeModule.resetStuckCount();
            // knowledgeModule.resetTimeOnCurrentLegOfPath();
            // knowledgeModule.setCurrentMentalState(MentalStateOfAgent.NEED_NEW_PATH);
            // // knowledgeModule.setWaiting(true);
            // // knowledgeModule.setWaitingTime(0);
            // }
            //
            // }
            // }
        }

    }

    private void checkCongestionDoor(Door goalDoor)
    {
        int agentsNearDoor = 0;
        int doorNearnessThreshold = 8;
        List<Integer> ANDlist = new ArrayList<>();
        for(AgentKnowledgeStorageObject a : knowledgeModule.getAgents())
        {
            EnvObjectKnowledgeStorageObject goalDoorObj = knowledgeModule.findEnvObj(goalDoor.getId());
            if(goalDoorObj != null)
            {
                if(a.getPosition().distance(goalDoorObj.getPosition()) < doorNearnessThreshold)
                {
                    agentsNearDoor++;
                    ANDlist.add(a.getID());
                }
            }
        }

        if(agentsNearDoor > 0)
        {
            float sum = 0;
            Vector3f myPosVec = knowledgeModule.findEnvObj(goalDoor.getId()).getPosition().subtract(getState().getPosition()).normalize();
            for(Integer agentID : ANDlist)
            {
                // sum = sum + myPosVec.dot(knowledgeModule.findAgent(agentID).getVelocity().normalize());
                sum = sum + myPosVec.dot(knowledgeModule.findEnvObj(goalDoor.getId()).getPosition().subtract(knowledgeModule.findAgent(agentID).getPosition()).normalize());
            }
            goalDoor.setDoorflow(sum);
        }
        else
        {
            goalDoor.setDoorflow(0);
        }

        goalDoor.setDoorCongestion(agentsNearDoor);
        goalDoor.setCycleSet(knowledgeModule.getTime());
        // System.out.println("####################################################CONGS FOR" + goalDoor.getId() + " ==== " + agentsNearDoor);
    }

    private List<Vector3f> makeVectorPath()
    {
        List<Vector3f> ret = new ArrayList<>();
        if(knowledgeModule.getReactGoal() != null)
        {
            ret.add((Vector3f) knowledgeModule.getReactGoal().getValue());
        }
        if(knowledgeModule.getExplorePos() != null)
        {
            ret.add(knowledgeModule.getExplorePos());
        }
        if(knowledgeModule.getAgentPath().getPath() != null)
        {
            for(Thing a : knowledgeModule.getAgentPath().getPath())
            {
                if(a instanceof Door)
                {
                    EnvObjectKnowledgeStorageObject obj = knowledgeModule.findEnvObj(((Door) a).getId());

                    if(obj == null && ((Door) a).isExit())
                    {
                        obj = knowledgeModule.getExitDoor(((Door) a).getId());
                    }

                    if(obj != null)
                    {
                        ret.add(obj.getPosition());
                    }
                    else
                        ret.add(null);
                }
            }
        }
        else
        {
            ret.add(currentVecGoal);
        }

        if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.SAFE))
        {
            ret.clear();
            ret.add(getState().getPosition());
        }

        return ret;

    }

    protected boolean checkIfStuck()
    {
        boolean ret = false;
        int stuckTime = 20;
        knowledgeModule.incrementTimeOnCurrentLegOfPath();
        if(getState().getPosition().distance(knowledgeModule.getStuckPos()) < stuckTime / 5)
        {
            knowledgeModule.incrementStuckCount();
            if(knowledgeModule.getStuckCount() > stuckTime)
            {
                // knowledgeModule.setCurrentState(StateOfAgent.STUCK);
                // System.out.println("tempsub:" + tempSubGoal);
                //
                // System.out.println("STUCKKKKK");
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.NEED_NEW_PATH);
                ret = true;

                ((EvacuationHumanPathFinding) pathFinding).removeTempSubGoal();
                // knowledgeModule.setLocalizedSelf(false);
                // knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
                // knowledgeModule.setLocalizationTime(50);
                // knowledgeModule.clearNearbyDoor();
                knowledgeModule.setStuckPos(getState().getPosition());
                knowledgeModule.resetStuckCount();
                knowledgeModule.resetTimeOnCurrentLegOfPath();
            }
        }
        else
        {
            knowledgeModule.resetStuckCount();
            knowledgeModule.setStuckPos(getState().getPosition());
        }
        if(knowledgeModule.getTimeOnCurrentLegOfPath() > knowledgeModule.getPathLegThreshold())
        {
            knowledgeModule.setStuckPos(getState().getPosition());
            knowledgeModule.resetStuckCount();
            knowledgeModule.resetTimeOnCurrentLegOfPath();
            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.NEED_NEW_PATH);
            ret = true;
        }
        return ret;
    }

    protected void updateExploringData()
    {
        if(!knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.EXPLORING_AREA))
        {
            knowledgeModule.setExplorePos(null);
        }
    }

    protected void processActionGoals(Plan generatedPlan)
    {
        openDoor(generatedPlan);
    }

    protected void openDoor(Plan generatedPlan)
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.getType().equals(DOOR) && !obj.isOpen() && getState().getPosition().distance(obj.getPosition()) < getState().getReachDistance() * 2.5f)
            {
                OpenDoorTask openDoorTask = (OpenDoorTask) taskModule.createTask(OPENDOOR, knowledgeModule.getTime());
                openDoorTask.setId(obj.getID());
                generatedPlan.addTask(openDoorTask);
            }
        }
    }

    protected void processNavigationGoals()
    {
        if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.NEED_NEW_PATH))
        {
            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
            knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
            if(havePath())
            {
                Vector3f goalPos = null;
                Door finalStep = (Door) knowledgeModule.getAgentPath().getFinalStep();
                if(finalStep.getDoorCongestion() < 8)
                {
                    goalPos = calculatePath(goalPos, finalStep);
                }
                else
                {
                    createPath();
                }

                testPossibleGoal(goalPos);
            }
            else
            {
                createPath();
            }
        }
        else
        {

            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.ARRIVED))
            {
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
            }
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.ARRIVED_IN_ROOM))
            {
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                enterNewRoom();
            }
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.ARRIVED_AT_EXIT))
            {
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                findSafeSpotOutside();
            }
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.EXITED_BUILDING))
            {
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                testPossibleGoal(knowledgeModule.getFinalGoalPos());
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.GOING_TO_SAFETY);
            }

            localizeSelf();

            if(amIdle())
            {
                if(!havePath())
                {
                    createPath();
                }
                else
                {
                    knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());

                    knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                    // System.out.println("continuing on path");
                    Door currentDoor;
                    if(knowledgeModule.isGetSafe())
                    {
                        currentDoor = (Door) knowledgeModule.getAgentPath().getNextStepNoRemove();
                        knowledgeModule.setGetSafe(false);
                    }
                    else
                    {
                        currentDoor = (Door) knowledgeModule.getAgentPath().getNextStep();
                    }

                    if(currentDoor != null)
                    {
                        knowledgeModule.resetTimeOnCurrentLegOfPath();
                        // ((EvacuationHumanPathFinding) pathFinding).printDoorPath(knowledgeModule.getAgentPath().getPath());
                        Vector3f goalPos = null;
                        goalPos = setGoalDoor(goalPos, currentDoor);
                        testPossibleGoal(goalPos);
                    }

                }
            }
        }
    }

    private void findSafeSpotOutside()
    {
        Vector3f goalPos = null;

        EnvObjectKnowledgeStorageObject closestWall = null;
        for(int i = 0; i < knowledgeModule.getPerceivedRVOObstacles().size(); i++)
        {
            EnvObjectKnowledgeStorageObject foundObj = knowledgeModule.findEnvObj(knowledgeModule.getPerceivedRVOObstacles().get(i).getID());
            if(foundObj.getType().equals(WALL))
            {
                closestWall = foundObj;
                break;
            }
        }
        Random r = new Random();
        int forwardDist = 15 + r.nextInt(50);
        int horizDist = r.nextInt(400) - 200;
        Vector3f dir = null;
        if(closestWall != null)
        {
            float xDist = FastMath.abs(getState().getPosition().x - closestWall.getPosition().x);
            float zDist = FastMath.abs(getState().getPosition().z - closestWall.getPosition().z);

            if(xDist > zDist)
            {
                dir = new Vector3f(horizDist, 0, FastMath.sign(getState().getHeading().z) * forwardDist);
            }
            else
            {
                dir = new Vector3f(FastMath.sign(getState().getHeading().x) * forwardDist, 0, horizDist);
            }
        }
        if(dir != null)
        {
            goalPos = getState().getPosition().add(dir);
        }

        EnvObjectKnowledgeStorageObject floor = knowledgeModule.findEnvObj(FLOOR);
        goalPos = reverseGoal(goalPos, dir, floor);

        // int count = 0;
        // while(testBadPoint(goalPos))
        // {
        // count++;
        // goalPos = getState().getPosition().add(getState().getHeading().mult(10)).add(r.nextInt(11) - 5, 0, r.nextInt(11) - 5);
        // if(count > 5)
        // {
        // goalPos = null;
        // break;
        // }
        // }

        Vector3f firstGoalPos = exitBuilding();
        if(goalPos != null)
        {
            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.EXITING_BUILDING);
        }
        knowledgeModule.setFinalGoalPos(goalPos);

        testPossibleGoal(firstGoalPos);
    }

    protected boolean amIdle()
    {
        return knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.IDLE);
    }

    protected boolean havePath()
    {
        return knowledgeModule.getAgentPath().getPath() != null;
    }

    protected void updateAgentKnowledge()
    {
        processMessages();

        updateEmotionalState();

        updateKnowledgeGraph();

        updateExploringData();

        processNewKnowledge();

        // printKnowledgeGraph();
    }

    private void processNewKnowledge()
    {
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getNewEnvObjs())
        {
            if(obj.getType().equals(DOOR))
            {
                knowledgeModule.addDoorObj(obj);
            }
            if(obj.getType().equals(WALL))
            {
                knowledgeModule.addWallObj(obj);
            }
        }
    }

    private void printKnowledgeGraph()
    {
        System.out.println("################################KNOWLEDGE GRAPH START################################");
        if(knowledgeModule.getDoors().size() > 0)
        {
            List<Integer> visitedList = new ArrayList<>();
            List<Integer> openList = new ArrayList<>();
            Door firstDoor = knowledgeModule.getDoorsMap().values().iterator().next();

            openList.add(firstDoor.getId());
            visitedList.add(firstDoor.getId());
            while(openList.size() > 0)
            {
                int currentNodeID = openList.get(0);
                openList.remove(0);
                Door currentDoor = knowledgeModule.getDoor(currentNodeID);
                System.out.println(currentNodeID + " connects to: ");
                for(int i : currentDoor.getNeighbors())
                {
                    System.out.print(knowledgeModule.getDoor(i).getId() + ", ");
                }
                System.out.println();
                for(int i : currentDoor.getNeighbors())
                {
                    if(!visitedList.contains(i))
                    {
                        openList.add(i);
                        visitedList.add(i);
                    }
                }
            }

        }
        System.out.println("################################KNOWLEDGE GRAPH END##################################");
    }

    protected void printState()
    {
        System.out.println("CurState: " + knowledgeModule.getCurrentMentalState());
        // System.out.println("DoorID: " + knowledgeModule.getGoalDoor().getId());
        // System.out.println("Nearbydoors size: " + knowledgeModule.getNearbyDoors().size());
    }

    private void createPath()
    {
        if(knowledgeModule.isLocalizedSelf() && !knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_DOOR))
        {
            Random r = new Random();
            Vector3f goalPos = null;

            Door doorToVisit = null;

            if(knowledgeModule.getDoors().size() > 0)
            {
                if(knowledgeModule.isSirenHeard() && knowledgeModule.getExitDoors().size() > 0)
                {
                    Door closestExit = null;
                    float closest = Float.MAX_VALUE;
                    List<Thing> bestPath = null;
                    for(EnvObjectKnowledgeStorageObject d : knowledgeModule.getExitDoors().values())
                    {
                        Door tempDoor = knowledgeModule.findDoor(d.getID());
                        List<Thing> tempPath = ((EvacuationHumanPathFinding) pathFinding).findPathToDoor(tempDoor, getState().getPosition());

                        if(tempDoor.getFscore() < closest)
                        {
                            closestExit = tempDoor;
                            closest = tempDoor.getFscore();
                            bestPath = tempPath;
                        }

                    }

                    if(closestExit != null)
                    {
                        doorToVisit = closestExit;
                        if(bestPath != null && bestPath.size() > 0)
                        {
                            float f = doorToVisit.getFscore() / getState().getPosition().distance(knowledgeModule.findEnvObj(doorToVisit.getId()).getPosition());
                            // if(f > 3)
                            // System.out.println("distance factor Value: " + f);
                            knowledgeModule.resetTimeOnCurrentLegOfPath();
                            knowledgeModule.getAgentPath().setPath(bestPath);
                            knowledgeModule.getAgentPath().setValue(doorToVisit.getFscore());
                            knowledgeModule.getAgentPath().setDistanceFactor(f);
                            Door nearbyDoor = (Door) knowledgeModule.getAgentPath().getFirstStep();
                            goalPos = setGoalDoor(goalPos, nearbyDoor);
                            EnvObjectKnowledgeStorageObject objModel = knowledgeModule.findEnvObj(doorToVisit.getId());
                            if(objModel != null)
                            {
                                knowledgeModule.clearImportantObjects();
                                knowledgeModule.addImportantObject(objModel);
                            }
                        }
                    }
                }
                else
                {

                    if(knowledgeModule.getDoors().size() > 0) // find unvisited door anywhere
                    {
                        List<Door> visitWorthyFrontDoors = new ArrayList<>();
                        List<Door> visitWorthyBackDoors = new ArrayList<>();
                        Iterator<Door> doorIter = knowledgeModule.getDoors().iterator();
                        while(doorIter.hasNext())
                        {
                            Door d = doorIter.next();
                            if(!d.isVisited() && !d.isExit())
                            {
                                if(verifyGoalExists(d))
                                {
                                    EnvObjectKnowledgeStorageObject dObj = knowledgeModule.findEnvObj(d.getId());
                                    if(dObj != null)
                                    {
                                        if(getState().getHeading().dot(dObj.getPosition().subtract(getState().getPosition())) > 0)
                                        {
                                            visitWorthyFrontDoors.add(d);
                                        }
                                        else
                                        {
                                            visitWorthyBackDoors.add(d);
                                        }
                                    }

                                }
                            }
                        }

                        if(visitWorthyFrontDoors.size() > 0)
                        {
                            doorToVisit = visitWorthyFrontDoors.get(r.nextInt(visitWorthyFrontDoors.size()));
                            // System.out.println("Going Unvisited (front) DOOR: " + doorToVisit.getId());
                        }
                        else if(visitWorthyBackDoors.size() > 0)
                        {
                            doorToVisit = visitWorthyBackDoors.get(r.nextInt(visitWorthyBackDoors.size()));
                            // System.out.println("Going Unvisited (rear) DOOR: " + doorToVisit.getId());
                        }
                    }

                    if(doorToVisit != null && !verifyGoalExists(doorToVisit))
                    {
                        // knowledgeModule.getDoorsMap().remove(doorToVisit.getId());
                        doorToVisit = null;
                    }
                    if(doorToVisit != null)
                    {
                        goalPos = calculatePath(goalPos, doorToVisit);
                    }
                }
            }
            testPossibleGoal(goalPos);
        }
    }

    protected Vector3f calculatePath(Vector3f goalPos, Door doorToVisit)
    {
        List<Thing> path = ((EvacuationHumanPathFinding) pathFinding).findPathToDoor(doorToVisit, getState().getPosition());
        // System.out.println("Start Point: " + getState().getPosition());
        // ((EvacuationHumanPathFinding) pathFinding).printDoorPath(path);
        if(path != null && path.size() > 0)
        {
            float f = doorToVisit.getFscore() / getState().getPosition().distance(knowledgeModule.findEnvObj(doorToVisit.getId()).getPosition());
            // if(f > 3)
            // System.out.println("distance factor Value: " + f);
            knowledgeModule.resetTimeOnCurrentLegOfPath();
            knowledgeModule.getAgentPath().setPath(path);
            knowledgeModule.getAgentPath().setValue(doorToVisit.getFscore());
            knowledgeModule.getAgentPath().setDistanceFactor(f);
            Door nearbyDoor = (Door) knowledgeModule.getAgentPath().getFirstStep();
            goalPos = setGoalDoor(goalPos, nearbyDoor);
            EnvObjectKnowledgeStorageObject objModel = knowledgeModule.findEnvObj(doorToVisit.getId());
            if(objModel != null)
            {
                knowledgeModule.clearImportantObjects();
                knowledgeModule.addImportantObject(objModel);
            }
        }
        return goalPos;
    }

    protected boolean verifyGoalExists(Door doorToVisit)
    {
        if(knowledgeModule.findEnvObj(doorToVisit.getId()) == null)
        {
            if(!doorToVisit.isExit())
            {
                return false;
            }
        }
        return true;
    }

    protected Vector3f setGoalDoor(Vector3f goalPos, Door nearbyDoor)
    {
        EnvObjectKnowledgeStorageObject obj = knowledgeModule.findEnvObj(nearbyDoor.getId());
        if(obj != null)
        {
            goalPos = getDoorTravelPos(obj);
            knowledgeModule.setGoalDoor(nearbyDoor);
            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.GOING_DOOR);
        }
        else
        {
            knowledgeModule.setLocalizedSelf(false);
        }
        return goalPos;
    }

    private Vector3f getDoorTravelPos(EnvObjectKnowledgeStorageObject obj)
    {
        Vector3f agentVec = obj.getPosition().subtract(getState().getPosition());

        Vector3f returnVal;
        if(obj.getScale().x < obj.getScale().z)
        {

            if(agentVec.dot(Vector3f.UNIT_X) > 0)
            {
                returnVal = obj.getPosition().add(Vector3f.UNIT_Z.mult(1.5f));
            }
            else
            {
                returnVal = obj.getPosition().add(Vector3f.UNIT_Z.mult(-1.5f));
            }

        }
        else
        {

            if(agentVec.dot(Vector3f.UNIT_Z) < 0)
            {
                returnVal = obj.getPosition().add(Vector3f.UNIT_X.mult(1.5f));
            }
            else
            {
                returnVal = obj.getPosition().add(Vector3f.UNIT_X.mult(-1.5f));
            }

        }

        return returnVal;
    }

    protected void testPossibleGoal(Vector3f goalPos)
    {
        // System.out.println("hi4");
        if(goalPos != null)
        {
            setNewGoal(goalPos);
        }
        else
        {
            knowledgeModule.setLocalizedSelf(false);
            knowledgeModule.setGetSafe(true);
        }
    }

    private void enterNewRoom()
    {
        Vector3f goalPos = null;

        EnvObjectKnowledgeStorageObject closestWall = null;
        for(int i = 0; i < knowledgeModule.getPerceivedRVOObstacles().size(); i++)
        {
            EnvObjectKnowledgeStorageObject foundObj = knowledgeModule.findEnvObj(knowledgeModule.getPerceivedRVOObstacles().get(i).getID());
            if(foundObj.getType().equals(WALL))
            {
                closestWall = foundObj;
                break;
            }
        }

        Vector3f dir = null;
        if(closestWall != null)
        {
            float xDist = FastMath.abs(getState().getPosition().x - closestWall.getPosition().x);
            float zDist = FastMath.abs(getState().getPosition().z - closestWall.getPosition().z);

            if(xDist > zDist)
            {
                dir = new Vector3f(0, 0, FastMath.sign(getState().getHeading().z) * 2);
            }
            else
            {
                dir = new Vector3f(FastMath.sign(getState().getHeading().x) * 2, 0, 0);
            }
        }
        if(dir != null)
        {
            goalPos = getState().getPosition().add(dir);
        }

        // int count = 0;
        // while(testBadPoint(goalPos))
        // {
        // count++;
        // goalPos = getState().getPosition().add(getState().getHeading().mult(10)).add(r.nextInt(11) - 5, 0, r.nextInt(11) - 5);
        // if(count > 5)
        // {
        // goalPos = null;
        // break;
        // }
        // }
        if(goalPos != null)
        {
            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.ENTERING_NEW_ROOM);
        }

        testPossibleGoal(goalPos);
    }

    private Vector3f exitBuilding()
    {
        Vector3f goalPos = null;

        EnvObjectKnowledgeStorageObject closestWall = null;
        for(int i = 0; i < knowledgeModule.getPerceivedRVOObstacles().size(); i++)
        {
            EnvObjectKnowledgeStorageObject foundObj = knowledgeModule.findEnvObj(knowledgeModule.getPerceivedRVOObstacles().get(i).getID());
            if(foundObj.getType().equals(WALL))
            {
                closestWall = foundObj;
                break;
            }
        }

        Vector3f dir = null;
        if(closestWall != null)
        {
            float xDist = FastMath.abs(getState().getPosition().x - closestWall.getPosition().x);
            float zDist = FastMath.abs(getState().getPosition().z - closestWall.getPosition().z);

            if(xDist > zDist)
            {
                dir = new Vector3f(0, 0, FastMath.sign(getState().getHeading().z) * 5);
            }
            else
            {
                dir = new Vector3f(FastMath.sign(getState().getHeading().x) * 5, 0, 0);
            }
        }
        if(dir != null)
        {
            goalPos = getState().getPosition().add(dir);
        }

        EnvObjectKnowledgeStorageObject floor = knowledgeModule.findEnvObj(FLOOR);
        goalPos = reverseGoal(goalPos, dir, floor);

        // int count = 0;
        // while(testBadPoint(goalPos))
        // {
        // count++;
        // goalPos = getState().getPosition().add(getState().getHeading().mult(10)).add(r.nextInt(11) - 5, 0, r.nextInt(11) - 5);
        // if(count > 5)
        // {
        // goalPos = null;
        // break;
        // }
        // }
        return goalPos;
    }

    protected Vector3f reverseGoal(Vector3f goalPos, Vector3f dir, EnvObjectKnowledgeStorageObject floor)
    {
        if(floor != null)
        {
            if(floor.contains2D(goalPos))
            {
                goalPos = getState().getPosition().subtract(dir);
            }
        }
        return goalPos;
    }

    protected void setNewGoal(Vector3f goalPos)
    {
        knowledgeModule.getGoals().add(new Goal(VECTOR, POSITION, goalPos, 10));
    }

    protected void localizeSelf()
    {

        if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.EXPLORING_AREA))
        {
            knowledgeModule.incrementExploringTime();
            if(knowledgeModule.getExploringTime() > knowledgeModule.getLocalizationTime())
            {
                knowledgeModule.setLocalizedSelf(true);
                knowledgeModule.resetExploringTime();
            }
        }

        if(!knowledgeModule.isLocalizedSelf() && amIdle())
        {
            exploreArea();
        }

    }

    protected void processMessages()
    {
        readInbox();
        AgentMessage am;

        for(int i = 0; i < 2; i++)
        {
            am = new AgentMessage(getState().getID(), getState().getID(), knowledgeModule.getTime(), "HI!!!");
            interactionModule.sendMessage(am);
        }
    }

    protected void updateEmotionalState()
    {
        if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING))
        {
            knowledgeModule.setPanicTime(knowledgeModule.getPanicTime() - 1);
            if(knowledgeModule.getPanicTime() <= 0)
            {
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.IDLE);
                knowledgeModule.setReactGoal(null);
                pathFinding.notifyReact();
            }
        }
        if(!knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING) && knowledgeModule.getReactGoal() != null)
        {
            knowledgeModule.getGoals().remove(knowledgeModule.getReactGoal());
            knowledgeModule.setReactGoal(null);
        }

    }

    private void readInbox()
    {
        AgentMessage am;
        while((am = interactionModule.getMessageFromInbox()) != null)
        {
            // logger.debug("Source: " + am.getSourceID() + ", Destin: " + am.getDestinationID() + ", Message: " + am.getMessage());
        }
    }

    private void exploreArea()
    {
        Vector3f goalPos = null;

        goalPos = generateRandomNearbyPos();

        if(goalPos != null)
        {
            knowledgeModule.setExplorePos(goalPos);
            setNewGoal(goalPos);
        }

    }

    private Vector3f generateRandomNearbyPos()
    {
        Vector3f goalPos = new Vector3f();
        Random r = new Random();

        goalPos = getState().getPosition().add(r.nextInt(31) - 15, 0, r.nextInt(31) - 15);

        // goalPos = new Vector3f(r.nextInt(50) - 25, 0, r.nextInt(50) - 25);
        int count = 0;
        while(testBadPointAndPath(goalPos))
        {
            count++;
            goalPos = getState().getPosition().add(r.nextInt(31) - 15, 0, r.nextInt(31) - 15);
            // goalPos = new Vector3f(r.nextInt(50) - 25, 0, r.nextInt(50) - 25);
            if(count > 5)
                return null;
        }
        knowledgeModule.setCurrentMentalState(MentalStateOfAgent.EXPLORING_AREA);
        return goalPos;
    }

    private boolean testBadPointAndPath(Vector3f vec)
    {
        // System.out.println("here1: ");
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.isCollidable())
            {
                if(obj.intersects2D(vec, getState().getScale()))
                {
                    return true;
                }
                if(obj.agentPathIntersectsObj2D(getState().getPosition(), vec, getState().getRadius()))
                {
                    return true;
                }
            }
        }
        // System.out.println("here2: ");
        EnvObjectKnowledgeStorageObject floor = knowledgeModule.findEnvObj(FLOOR);
        if(floor != null)
        {
            if(!floor.contains2D(vec))
            {
                // System.out.println("here4: ");
                return true;
            }
        }
        // System.out.println("here3: ");
        return false;
    }

    /**
     * Agent updates its knowledge graph of rooms and doors.
     */

    private void updateKnowledgeGraph()
    {
        updatePostition();
        updateOldObjects();
        addSeenDoors();

        // if(!knowledgeModule.isInsideDoor())
        // {
        // addSeenDoors();
        // }
    }

    private void updateOldObjects()
    {
        for(EnvObjectKnowledgeStorageObject newObj : knowledgeModule.getNewEnvObjs())
        {
            if(newObj.getType().equals(WALL) || newObj.getType().equals(DOOR))
            {
                for(EnvObjectKnowledgeStorageObject doorObj : knowledgeModule.getEnvObjects())
                {
                    if(doorObj.getType().equals(DOOR) && newObj.getPosition().distance(doorObj.getPosition()) < 180 && doorObj.getID() != newObj.getID())
                    {
                        Door d = knowledgeModule.getDoor(doorObj.getID());
                        if(d != null)
                        {
                            Iterator<Integer> iter = d.getNeighbors().iterator();
                            while(iter.hasNext())
                            {
                                int nID = iter.next();
                                EnvObjectKnowledgeStorageObject nObj = knowledgeModule.findEnvObj(nID);
                                if(nObj != null && newObj.getID() != nObj.getID())
                                {
                                    if(newObj.miniBAIntersects2DLine(doorObj.getPosition(), nObj.getPosition()))
                                    {
                                        // System.out.println("Object " + newObj.getType() + " with ID " + newObj.getID() + "intersects between : (" + d.getId() + "," + nID + ")");
                                        iter.remove();
                                        Door n = knowledgeModule.getDoor(nID);
                                        if(n != null)
                                        {
                                            n.removeNeighbor(d.getId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updatePostition()
    {
        boolean inDoor = false;
        for(EnvObjectKnowledgeStorageObject obj : knowledgeModule.getEnvObjects())
        {
            if(obj.getType().equals(DOOR))
            {
                if(obj.intersects2D(getState().getPosition(), getState().getScale()))
                {
                    inDoor = true;
                    Door door = knowledgeModule.getDoor(obj.getID());
                    if(door != null)
                    {
                        door.setVisited(true);
                        if(door.isExit() && !knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.GOING_TO_SAFETY))
                        {
                            knowledgeModule.setCurrentMentalState(MentalStateOfAgent.ARRIVED_AT_EXIT);
                        }
                    }
                }
            }
        }
        if(inDoor)
        {
            knowledgeModule.setInsideDoor(true);
        }
        else
        {
            knowledgeModule.setInsideDoor(false);
        }
    }

    /**
     * Agent updates its knowledge by adding newly seen doors to its door knowledgebase.
     */
    private void addSeenDoors()
    {

        List<VisionModel> visList = knowledgeModule.getVisibleList();

        for(int i = 0; i < visList.size(); i++)
        {
            if(visList.get(i).getType().equals(ENV_OBJECT))
            {
                EnvObjectKnowledgeStorageObject obj = knowledgeModule.findEnvObj(visList.get(i).getId());
                if(obj != null)
                {
                    if(obj.getType().equals(DOOR))
                    {
                        Door newDoor = knowledgeModule.addDoor(obj.getID());
                        if(newDoor != null)
                        {
                            if(obj.getDescription().equals(EXITDOOR))
                            {
                                newDoor.setExit(true);
                                knowledgeModule.addExitDoor(obj);
                            }
                            checkCongestionDoor(newDoor);
                            findNearbyDoors(obj, newDoor);
                        }
                        else
                        {
                            Door d = knowledgeModule.getDoor(obj.getID());
                            checkCongestionDoor(d);
                        }
                    }
                }
            }
        }

        for(Door d : knowledgeModule.getDoors())
        {
            if(d.getCycleSet() > 0 && knowledgeModule.getTime() - d.getCycleSet() > 100)
            {
                // System.out.println("RESETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTRESETTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
                d.setDoorCongestion(0);
                d.setCycleSet(-1);
            }
        }

    }

    protected void findNearbyDoors(EnvObjectKnowledgeStorageObject obj, Door newDoor)
    {
        for(Door d : knowledgeModule.getDoors())
        {
            EnvObjectKnowledgeStorageObject dObj = knowledgeModule.findEnvObj(d.getId());
            if(dObj != null)
            {
                if(obj.getPosition().distance(dObj.getPosition()) < 180)
                {
                    if(d.getId() != newDoor.getId() && !d.getNeighbors().contains(newDoor.getId()))
                    {
                        boolean intersects = false;
                        for(EnvObjectKnowledgeStorageObject testObj : knowledgeModule.getEnvObjects())
                        {
                            if(testObj.getID() != obj.getID() && testObj.getID() != dObj.getID())
                            {
                                if(testObj.getType().equals(WALL) || testObj.getType().equals(DOOR))
                                {
                                    if(testObj.miniBAIntersects2DLine(obj.getPosition(), dObj.getPosition()))
                                    {
                                        intersects = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(!intersects)
                        {
                            newDoor.addNeighbor(d.getId());
                            d.addNeighbor(newDoor.getId());
                        }
                    }
                }
            }
        }
    }

    /**
     * Agent Path Finding.
     * 
     * @return TODO
     */
    private Plan planMovement(Plan generatedPlan)
    {

        if(knowledgeModule.getReactGoal() != null)
        {
            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING))
            {
                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 2.5f;
                knowledgeModule.getSelf().setPosture(Posture.Run);
            }
            else
            {
                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 5;
                knowledgeModule.getSelf().setPosture(Posture.Walk);
            }
            pathFinding.pathplan((Vector3f) knowledgeModule.getReactGoal().getValue(), generatedPlan);
        }
        else if(taskModule.containsTask(POSITION) && (taskModule.containsTask(HEADING)))
        {
            Vector3f vec = null;
            List<Goal> goal = knowledgeModule.getGoals();
            float maxUtil = 0;

            for(int i = 0; i < goal.size(); i++)
            {
                if(goal.get(i).getName().equals(POSITION))
                {
                    Object value = goal.get(i).getValue();

                    if(value instanceof Vector3f)
                    {
                        if(goal.get(i).getUtilityValue() > maxUtil)
                        {
                            vec = (Vector3f) value;

                            knowledgeModule.setCurrentGoal(goal.get(i));
                            maxUtil = goal.get(i).getUtilityValue();
                            if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.FLEEING))
                            {
                                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 2.5f;
                                knowledgeModule.getSelf().setPosture(Posture.Run);
                            }
                            else
                            {
                                knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 5;
                                knowledgeModule.getSelf().setPosture(Posture.Walk);
                            }
                        }
                    }

                }
                else if(goal.get(i).getName().equals(HEADING))
                {
                    FaceTask faceTask = (FaceTask) taskModule.createTask(HEADING, knowledgeModule.getTime());
                    faceTask.setHeading(new Vector3f((Vector3f) goal.get(i).getValue()));
                    generatedPlan.addTask(faceTask);
                }

            }
            if(vec != null)
            {
                if(knowledgeModule.isSirenHeard())
                {
                    knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 3.5f;
                    knowledgeModule.getSelf().setPosture(Posture.Run);
                }
                if(knowledgeModule.getCurrentMentalState().equals(MentalStateOfAgent.SAFE))
                {
                    knowledgeModule.desiredSpeed = knowledgeModule.getSelf().getMaxSpeed() / 3.5f;
                    knowledgeModule.getSelf().setPosture(Posture.Idle1);
                }

                pathFinding.pathplan(vec, generatedPlan);
                currentVecGoal = vec;

            }
        }
        return generatedPlan;
    }

    /**
     * Agent reaction. (React to immediate events that do not require planning.)
     */

    private void respondToEvents()
    {

        List<CombinedReasonedData> events = knowledgeModule.getEventsThisTick();

        boolean sirenOn = false;
        for(int i = 0; i < events.size(); i++)
        {
            CombinedReasonedData thisEvent = events.get(i);
            Vector3f pos = knowledgeModule.getSelf().getPosition();

            // System.out.println(thisEvent.getPredicatedName() + " " + thisEvent.getCertaintyPercent());

            if(thisEvent.getPredicatedName().equals(FIREWORK) && (thisEvent.getCertaintyPercent() > 100))
            {
                if(thisEvent.hasOrigin())
                {
                    Vector3f runDir = new Vector3f();
                    long time = knowledgeModule.getTime() + 20;
                    runDir = pos.subtract(thisEvent.getOrigin()).mult(-1);
                    float distance = pos.distance(thisEvent.getOrigin());
                    runDir.normalizeLocal();
                    runDir.multLocal(distance * .3f);
                    Vector3f newpos = pos.add(runDir);
                    newpos.setY(0);

                    knowledgeModule.getGoals().add(new Goal(VECTOR, POSITION, newpos, 20, time));
                    pathFinding.notifyReact();
                }
            }
            else if(thisEvent.getPredicatedName().equals(DRUMS) && (thisEvent.getCertaintyPercent() > 100))
            {

                long time = knowledgeModule.getTime() + 20;
                Vector3f runDir = new Vector3f();
                if(thisEvent.hasDirection())
                {
                    runDir = thisEvent.getDirection().mult(-1);
                }
                else if(thisEvent.hasOrigin())
                {
                    runDir = pos.subtract(thisEvent.getOrigin());
                }
                runDir.multLocal(10);

                knowledgeModule.getGoals().add(new Goal(VECTOR, POSITION, pos.add(runDir), 10, time));
                pathFinding.notifyReact();
            }
            else if(thisEvent.getPredicatedName().equals(GRILLING_FOOD) && (thisEvent.getCertaintyPercent() > 100))
            {

                long time = knowledgeModule.getTime() + 1;
                Vector3f runDir = new Vector3f();
                boolean change = false;
                if(thisEvent.hasDirection())
                {
                    runDir = thisEvent.getDirection();
                }
                else if(thisEvent.hasOrigin())
                {
                    runDir = pos.subtract(thisEvent.getOrigin()).mult(-1);
                    float distance = pos.distance(thisEvent.getOrigin());
                    runDir.normalizeLocal();
                    runDir.multLocal(distance);
                    if(distance < 4)
                    {
                        change = true;
                    }
                }

                Vector3f newpos = pos.add(runDir);

                if(change)
                {
                    newpos = pos;
                }

                knowledgeModule.getGoals().add(new Goal(VECTOR, POSITION, newpos, 12, time));
                pathFinding.notifyReact();
            }
            else if(thisEvent.getPredicatedName().equals("Siren") && (thisEvent.getCertaintyPercent() > 50))
            {
                sirenOn = true;
                // System.out.println("AGENT" + getState().getID() + " HEARD SIREN: " + knowledgeModule.getTime());
                if(!knowledgeModule.isSirenHeard())
                {
                    knowledgeModule.setStuckPos(getState().getPosition());
                    knowledgeModule.resetStuckCount();
                    knowledgeModule.resetTimeOnCurrentLegOfPath();
                    knowledgeModule.setCurrentMentalState(MentalStateOfAgent.NEED_NEW_PATH);
                    knowledgeModule.setSirenHeard(true);
                    knowledgeModule.getAgentPath().setPath(null);
                }
            }
        }

        if(sirenOn)
        {
            knowledgeModule.setSirenOn(true);
        }

    }

    private void removeOldEventReactions()
    {
        ArrayList<Goal> toBeRemoved = new ArrayList<Goal>();
        for(int i = 0; i < knowledgeModule.getGoals().size(); i++)
        {
            if(knowledgeModule.getGoals().get(i).getRemoveTime() == knowledgeModule.getTime())
            {
                toBeRemoved.add(knowledgeModule.getGoals().get(i));
            }
        }

        for(int i = 0; i < toBeRemoved.size(); i++)
        {
            knowledgeModule.getGoals().remove(toBeRemoved.get(i));
        }
    }

    /**
     * Get the agent self state.
     * 
     * @return
     */
    public EHumanAgentState getState()
    {
        return knowledgeModule.getSelf();
    }

    /**
     * Get the knowledge module.
     * 
     * @return KM
     */
    public EvacuationHumanKnowledgeModule getKnowledgeModule()
    {
        return knowledgeModule;
    }

    @Override
    public void addGoal(Goal newGoal)
    {
        if(newGoal instanceof LocationGoal)
        {

            Vector3f goalPos = ((LocationGoal) newGoal).getLocation();
            if(goalPos != null)
            {
                knowledgeModule.getGoals().remove(knowledgeModule.getCurrentGoal());
                knowledgeModule.getGoals().clear();
                knowledgeModule.getGoals().add(new Goal(VECTOR, POSITION, goalPos, 50));
                knowledgeModule.setCurrentMentalState(MentalStateOfAgent.PERSUING_USER_GOAL);
                knowledgeModule.getAgentPath().setPath(null);
                pathFinding.notifyUserGoalChange();
                knowledgeModule.clearImportantObjects();
            }
        }
    }

}
