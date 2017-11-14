package edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventKnowledge;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.external.EventPropertyKnowledge;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.utils.collections.BoundedPrioritySet;
import edu.utdallas.mavs.divas.utils.collections.LightWeightBoundedMap;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.AgentKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.EnvObjectKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.NeighborKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.VisionModel;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.ontology.Door;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.AgentPath;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.RVOObstacle;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.RVO_Utils;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;

/**
 * The Agents Knowledge Module. Stores all agent knowledge.
 */
public class EvacuationHumanKnowledgeModule extends HumanKnowledgeModule<EHumanAgentState>
{
    protected static final int                                 MAX_AGENT              = 100;
    protected static final int                                 MAX_ENV_OBJ            = 600;

    private static final int                                   MAX_NEIGHBOR_AGE       = 5;
    private static final long                                  serialVersionUID       = 1L;
    private static final float                                 NEAR_DISTANCE          = 20;
    private static final int                                   NEIGHBORS_COUNT        = 30;
    private static final String                                DOOR                   = "door";

    private Map<Integer, Door>                                 doors;
    private Map<Integer, EnvObjectKnowledgeStorageObject>      exitDoors              = new LightWeightBoundedMap<Integer, EnvObjectKnowledgeStorageObject>(MAX_ENV_OBJ);
    private Map<Integer, EnvObjectKnowledgeStorageObject>      importantObjects       = new LightWeightBoundedMap<Integer, EnvObjectKnowledgeStorageObject>(MAX_ENV_OBJ);
    private BoundedPrioritySet<NeighborKnowledgeStorageObject> nearbyAgents           = new BoundedPrioritySet<>(NEIGHBORS_COUNT, new DistanceComparator());

    private List<String>                                       dangerousEvents;
    private List<EnvObjectKnowledgeStorageObject>              wallsInCurrentRoom     = new ArrayList<EnvObjectKnowledgeStorageObject>();
    // private List<Door> nearbyDoors;
    private Map<Integer, EnvObjectKnowledgeStorageObject>      doorObjs               = new LightWeightBoundedMap<Integer, EnvObjectKnowledgeStorageObject>(MAX_ENV_OBJ * 2);
    private Map<Integer, EnvObjectKnowledgeStorageObject>      wallObjs               = new LightWeightBoundedMap<Integer, EnvObjectKnowledgeStorageObject>(MAX_ENV_OBJ);

    private List<Goal>                                         goals;
    private Goal                                               searchGoal             = null;
    private Goal                                               unStuckGoal            = null;
    private Goal                                               currentGoal            = null;
    private Goal                                               reactGoal              = null;
    private Door                                               goalDoor               = new Door(-1);

    private Vector3f                                           savedPos               = new Vector3f();
    private Vector3f                                           goingDoorPos           = new Vector3f();
    private Vector3f                                           treatDirection         = null;
    private Vector3f                                           stuckPos               = new Vector3f();
    private Vector3f                                           explorePos             = null;
    private Vector3f                                           finalGoalPos           = null;

    private boolean                                            wantToMove             = false;
    private boolean                                            agentIsStuck           = false;
    private boolean                                            inroom                 = false;
    private boolean                                            noDoors                = false;
    private boolean                                            searchingDoor          = false;
    private boolean                                            inNewRoom              = true;
    private boolean                                            finishedExploring      = false;
    private boolean                                            getSafe                = false;
    private boolean                                            insideDoor             = false;
    private boolean                                            localizedSelf          = false;
    private boolean                                            sirenHeard             = false;
    private boolean                                            waiting                = false;
    private boolean                                            sirenOn                = false;

    private int                                                waitingTime            = 0;
    private int                                                exploringTime          = 0;
    private int                                                localizationTime       = 30;
    private int                                                stuckCount             = 0;
    private int                                                panicTime;
    private int                                                checkingRoomNumber     = 0;
    private int                                                timeOnCurrentLegOfPath = 0;
    private int                                                pathLegThreshold       = 250;
    long                                                       obsCalculatedCycle     = -1;
    public float                                               desiredSpeed           = 0;
    private float                                              patience;
    private AgentPath                                          agentPath;
    private MentalStateOfAgent                                 currentMentalState     = MentalStateOfAgent.IDLE;

    List<RVOObstacle>                                          obs;

    protected Map<Integer, EnvObjectKnowledgeStorageObject>    envObjs;
    protected Map<Integer, AgentKnowledgeStorageObject>        agents;
    protected ArrayList<EnvObjectKnowledgeStorageObject>       newEnvObjs;

    /**
     * Event Knowledge Storage
     */
    protected Map<String, EventKnowledge>                      evk;
    protected ArrayList<CombinedReasonedData>                  eventsPerceivedThisTick;
    protected Map<Integer, VisionModel>                        visibleList;

    public enum MentalStateOfAgent
    {
        EXITED_BUILDING, EXPLORING_AREA, GOING_DOOR, FLEEING, IDLE, SAFE, PERSUING_USER_GOAL, ARRIVED_IN_ROOM, STUCK, ENTERING_NEW_ROOM, ARRIVED, NEED_NEW_PATH, ARRIVED_AT_EXIT, GOING_TO_SAFETY, EXITING_BUILDING
    }

    public List<Goal> getGoals()
    {
        return goals;
    }

    public EvacuationHumanKnowledgeModule(EHumanAgentState state)
    {
        super(state);
        this.envObjs = new LightWeightBoundedMap<Integer, EnvObjectKnowledgeStorageObject>(MAX_ENV_OBJ);
        this.newEnvObjs = new ArrayList<EnvObjectKnowledgeStorageObject>();
        this.agents = new LightWeightBoundedMap<Integer, AgentKnowledgeStorageObject>(MAX_AGENT);
        this.eventsPerceivedThisTick = new ArrayList<CombinedReasonedData>();
        this.visibleList = new HashMap<Integer, VisionModel>();        

        doors = new LightWeightBoundedMap<Integer, Door>(MAX_ENV_OBJ * 2);
        dangerousEvents = new ArrayList<>();
        // nearbyDoors = new ArrayList<>();
        cycle = 0;
        goals = new ArrayList<>();

        populateEventKnowledge();
        obs = new ArrayList<>();

        agentPath = new AgentPath();
        Random r = new Random();
        patience = r.nextFloat();
        if(patience < .2f)
        {
            patience = .2f;
        }
        if(patience > .9f)
        {
            patience = .9f;
        }
    }

    public Door findDoor(int doorID)
    {
        return doors.get(doorID);
    }

    public Door addDoor(int doorID)
    {
        if(doors.get(doorID) == null)
        {
            Door d = new Door(doorID);
            doors.put(doorID, d);
            return d;
        }
        return null;
    }

    public Door getDoor(int id)
    {
        return doors.get(id);
    }

    public Collection<Door> getDoors()
    {
        return doors.values();
    }

    public Map<Integer, Door> getDoorsMap()
    {
        return doors;
    }

    private void populateEventKnowledge()
    {
        evk = new HashMap<String, EventKnowledge>();

        EventKnowledge tempek = new EventKnowledge("Bomb");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("flash", Sense.Vision, 40, 200));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("fire", Sense.Vision, 20, 40));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("boom", Sense.Hearing, 50, 160));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("smoke", Sense.Smell, 20, 100));

        evk.put(tempek.getName(), tempek);

        tempek = new EventKnowledge("Firework");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("flash", Sense.Vision, 20, 50));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("colors", Sense.Vision, 40, 100));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("boom", Sense.Hearing, 40, 120));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("smoke", Sense.Smell, 5, 40));

        evk.put(tempek.getName(), tempek);

        tempek = new EventKnowledge("GrillingFood");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("grill", Sense.Vision, 1, 1));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("sizzle", Sense.Hearing, 5, 20));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("smoke", Sense.Smell, 3, 45));

        evk.put(tempek.getName(), tempek);

        tempek = new EventKnowledge("Drums");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("drums", Sense.Vision, 1, 1));
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("boom", Sense.Hearing, 10, 70));

        evk.put(tempek.getName(), tempek);

        tempek = new EventKnowledge("Spotlight");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("flash", Sense.Vision, 20, 80));

        evk.put(tempek.getName(), tempek);

        tempek = new EventKnowledge("Siren");
        tempek.addEventPropertyKnowledge(new EventPropertyKnowledge("siren", Sense.Hearing, 70, 140));

        evk.put(tempek.getName(), tempek);

        dangerousEvents.add("Bomb");
    }

    public boolean isSearchingDoor()
    {
        return searchingDoor;
    }

    public void setSearchingDoor(boolean searchingDoor)
    {
        this.searchingDoor = searchingDoor;
    }

    public boolean isLocalizedSelf()
    {
        return localizedSelf;
    }

    public void setLocalizedSelf(boolean localizedSelf)
    {
        this.localizedSelf = localizedSelf;
    }

    public List<EnvObjectKnowledgeStorageObject> getWallsInCurrentRoom()
    {
        return wallsInCurrentRoom;
    }

    public boolean isInNewRoom()
    {
        return inNewRoom;
    }

    public boolean isInsideDoor()
    {
        return insideDoor;
    }

    public void setInsideDoor(boolean insideDoor)
    {
        this.insideDoor = insideDoor;
    }

    public void setInNewRoom(boolean inNewRoom)
    {
        this.inNewRoom = inNewRoom;
    }

    public void setWallsInCurrentRoom(List<EnvObjectKnowledgeStorageObject> wallsInCurrentRoom)
    {
        this.wallsInCurrentRoom = wallsInCurrentRoom;
    }

    public boolean isWantToMove()
    {
        return wantToMove;
    }

    public void setWantToMove(boolean wantToMove)
    {
        this.wantToMove = wantToMove;
    }

    public boolean isAgentIsStuck()
    {
        return agentIsStuck;
    }

    public void setAgentIsStuck(boolean agentIsStuck)
    {
        this.agentIsStuck = agentIsStuck;
    }

    public boolean isInroom()
    {
        return inroom;
    }

    public void setInroom(boolean inroom)
    {
        this.inroom = inroom;
    }

    public Goal getSearchGoal()
    {
        return searchGoal;
    }

    public void setSearchGoal(Goal searchGoal)
    {
        this.searchGoal = searchGoal;
    }

    public boolean isNoDoors()
    {
        return noDoors;
    }

    public void setNoDoors(boolean noDoors)
    {
        this.noDoors = noDoors;
    }

    public int getCheckingRoomNumber()
    {
        return checkingRoomNumber;
    }

    public void setCheckingRoomNumber(int checkingRoomNumber)
    {
        this.checkingRoomNumber = checkingRoomNumber;
    }

    public Vector3f getSavedPos()
    {
        return savedPos;
    }

    public void setSavedPos(Vector3f savedPos)
    {
        this.savedPos = savedPos;
    }

    public Vector3f getGoingDoorPos()
    {
        return goingDoorPos;
    }

    public void setGoingDoorPos(Vector3f goingDoorPos)
    {
        this.goingDoorPos = goingDoorPos;
    }

    public Goal getUnStuckGoal()
    {
        return unStuckGoal;
    }

    public void setUnStuckGoal(Goal unStuckGoal)
    {
        this.unStuckGoal = unStuckGoal;
    }

    public Goal getCurrentGoal()
    {
        return currentGoal;
    }

    public void setCurrentGoal(Goal currentGoal)
    {
        this.currentGoal = currentGoal;
    }

    public int getExploringTime()
    {
        return exploringTime;
    }

    public void resetExploringTime()
    {
        exploringTime = 0;
    }

    public void incrementExploringTime()
    {
        exploringTime++;
    }

    public boolean isFinishedExploring()
    {
        return finishedExploring;
    }

    public void setFinishedExploring(boolean finishedExploring)
    {
        this.finishedExploring = finishedExploring;
    }

    public int getPanicTime()
    {
        return panicTime;
    }

    public void setPanicTime(int panicTime)
    {
        this.panicTime = panicTime;
    }

    public Vector3f getThreatDirection()
    {
        return treatDirection;
    }

    public void setThreatDirection(Vector3f treatDirection)
    {
        this.treatDirection = treatDirection;
    }

    protected void updateNearbyAgents(AgentState agent)
    {
        float distance = self.getPosition().distance(agent.getPosition());
        if(distance < NEAR_DISTANCE && agent.getID() != self.getID())
        {
            nearbyAgents.add(new NeighborKnowledgeStorageObject(agent, distance, getTime()));
        }
        /*
         * if(nearbyAgents.size() > NEIGHBORS_COUNT)
         * {
         * System.out.println(nearbyAgents.size() + "  ####################################################################################");
         * }
         */
    }

    public void cleanNearbyAgents()
    {
        Iterator<NeighborKnowledgeStorageObject> it = nearbyAgents.values().iterator();
        while(it.hasNext())
        {
            NeighborKnowledgeStorageObject n = it.next();
            if((getTime() - n.getCycle()) > MAX_NEIGHBOR_AGE)
            {
                it.remove();
            }
            else if(n.getCycle() != getTime())
            {
                n.setPosition(n.getPosition().add(n.getVelocity()));
                n.setDistance(self.getPosition().distance(n.getPosition()));
                if(n.getDistance() > NEAR_DISTANCE)
                {
                    it.remove();
                }
            }
        }
    }

    public BoundedPrioritySet<NeighborKnowledgeStorageObject> getNearbyAgents()
    {
        // for(NeighborKnowledgeStorageObject n : nearbyAgents.values())
        // {
        // System.out.println("AgentID: " + getId() + " NEIGHBORID: " + n.getID() + " DISTANCE: " + n.getDistance());
        // }
        return nearbyAgents;
    }

    public float getNearDistance()
    {
        return NEAR_DISTANCE;
    }

    public boolean isEventDangerous(String event)
    {
        if(dangerousEvents.contains(event))
        {
            return true;
        }
        return false;
    }

    public List<RVOObstacle> getPerceivedRVOObstacles()
    {
        if(obsCalculatedCycle < getTime())
        {
            obs.clear();
            obsCalculatedCycle = getTime();
            for(EnvObjectKnowledgeStorageObject obj : envObjs.values())
            {
                if(obj.isCollidable())
                {
                    if(obj.getPosition().distance(self.getPosition()) - FastMath.sqrt(obj.getScale().getX() * obj.getScale().getX() + obj.getScale().getZ() * obj.getScale().getZ()) < 30)
                    {
                        if(!obj.getType().equals(DOOR))
                        {
                            Vector2f pospos = new Vector2f(obj.getScale().x, obj.getScale().z);
                            Vector2f posneg = new Vector2f(obj.getScale().x, -obj.getScale().z);
                            Vector2f negpos = new Vector2f(-obj.getScale().x, obj.getScale().z);
                            Vector2f negneg = new Vector2f(-obj.getScale().x, -obj.getScale().z);

                            // outside
                            RVOObstacle obs1 = new RVOObstacle(obj.getPosition2D().add(pospos));
                            RVOObstacle obs2 = new RVOObstacle(obj.getPosition2D().add(negpos));
                            RVOObstacle obs3 = new RVOObstacle(obj.getPosition2D().add(negneg));
                            RVOObstacle obs4 = new RVOObstacle(obj.getPosition2D().add(posneg));

                            // inside
                            // RVOObstacle obs1 = new RVOObstacle(obj.getPosition2D().add(pospos));
                            // RVOObstacle obs2 = new RVOObstacle(obj.getPosition2D().add(posneg));
                            // RVOObstacle obs3 = new RVOObstacle(obj.getPosition2D().add(negneg));
                            // RVOObstacle obs4 = new RVOObstacle(obj.getPosition2D().add(negpos));

                            obs1.setID(obj.getID());
                            obs2.setID(obj.getID());
                            obs3.setID(obj.getID());
                            obs4.setID(obj.getID());

                            obs1.setNextObstacle(obs2);
                            obs2.setNextObstacle(obs3);
                            obs3.setNextObstacle(obs4);
                            obs4.setNextObstacle(obs1);

                            obs1.setPrevObstacle(obs4);
                            obs2.setPrevObstacle(obs1);
                            obs3.setPrevObstacle(obs2);
                            obs4.setPrevObstacle(obs3);

                            obs1.updateUnitVector();
                            obs2.updateUnitVector();
                            obs3.updateUnitVector();
                            obs4.updateUnitVector();

                            obs.add(obs1);
                            obs.add(obs2);
                            obs.add(obs3);
                            obs.add(obs4);

                            // RVOObstacle obs5 = new RVOObstacle(obj.getPosition2D().add(pospos));
                            // RVOObstacle obs6 = new RVOObstacle(obj.getPosition2D().add(negpos));
                            // obs5.setNextObstacle(obs6);
                            // obs6.setNextObstacle(obs5);
                            // obs5.setPrevObstacle(obs6);
                            // obs6.setPrevObstacle(obs5);
                            // obs5.updateUnitVector();
                            // obs6.updateUnitVector();
                            // obs.add(obs5);
                            // obs.add(obs6);

                            for(RVOObstacle rvoObstacle : obs)
                            {
                                // rvoObstacle.setDistance(rvoObstacle.getPoint().distance(self.getPosition2D()));

                                rvoObstacle.setDistance(RVO_Utils.calcDistanceToLineSegment(rvoObstacle.getPoint(), rvoObstacle.getNextObstacle().getPoint(), self.getPosition2D()));

                            }

                            Collections.sort(obs);

                            // for(RVOObstacle rvoObstacle : obs)
                            // {
                            // System.out.println("dist: " + rvoObstacle.getDistance());
                            // }
                            // for(RVOObstacle rvoObstacle : obs)
                            // {
                            // System.out.println(RVO_Utils.leftOf(rvoObstacle.getPrevObstacle().getPoint(), rvoObstacle.getPoint(), rvoObstacle.getNextObstacle().getPoint()));
                            // }
                            // obs.add(new RVOObstacle(obj.getPosition2D().add(pospos), obj.getPosition2D().add(negpos)));
                            // obs.add(new RVOObstacle(obj.getPosition2D().add(negpos), obj.getPosition2D().add(negneg)));
                            // obs.add(new RVOObstacle(obj.getPosition2D().add(negneg), obj.getPosition2D().add(posneg)));
                            // obs.add(new RVOObstacle(obj.getPosition2D().add(posneg), obj.getPosition2D().add(pospos)));
                        }
                    }
                }
            }
        }

        return obs;
    }

    class DistanceComparator implements Comparator<NeighborKnowledgeStorageObject>, Serializable
    {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(NeighborKnowledgeStorageObject arg0, NeighborKnowledgeStorageObject arg1)
        {
            Float f0 = arg0.getDistance();
            Float f1 = arg1.getDistance();
            return -f0.compareTo(f1);
        }
    }

    public Goal getReactGoal()
    {
        return reactGoal;
    }

    public void setReactGoal(Goal reactGoal)
    {
        this.reactGoal = reactGoal;
    }

    // public void addNearbyDoor(Door d)
    // {
    // nearbyDoors.add(d);
    // }
    //
    // public void clearNearbyDoor()
    // {
    // nearbyDoors.clear();
    // }
    //
    // public List<Door> getNearbyDoors()
    // {
    // return nearbyDoors;
    // }

    public Door getGoalDoor()
    {
        return goalDoor;
    }

    public void setGoalDoor(Door goalDoor)
    {
        this.goalDoor = goalDoor;
    }

    public MentalStateOfAgent getCurrentMentalState()
    {
        return currentMentalState;
    }

    public void setCurrentMentalState(MentalStateOfAgent newState)
    {
        // System.out.println("Changing state from: " + currentMentalState + " to: " + newState);
        currentMentalState = newState;
    }

    public int getLocalizationTime()
    {
        return localizationTime;
    }

    public void setLocalizationTime(int localizationTime)
    {
        this.localizationTime = localizationTime;
    }

    public Map<Integer, EnvObjectKnowledgeStorageObject> getExitDoors()
    {
        return exitDoors;
    }

    public void setExitDoors(Map<Integer, EnvObjectKnowledgeStorageObject> exitDoors)
    {
        this.exitDoors = exitDoors;
    }

    public void addExitDoor(EnvObjectKnowledgeStorageObject exitDoor)
    {
        exitDoors.put(exitDoor.getID(), exitDoor);
    }

    public EnvObjectKnowledgeStorageObject getExitDoor(int id)
    {
        return exitDoors.get(id);
    }

    // <<<<<<< .working
    public boolean isGetSafe()
    {
        return getSafe;
    }

    public void setGetSafe(boolean getSafe)
    {
        this.getSafe = getSafe;
    }

    public EnvObjectKnowledgeStorageObject getImportantObject(int id)
    {
        return importantObjects.get(id);
    }

    public void addImportantObject(EnvObjectKnowledgeStorageObject importantObject)
    {
        importantObjects.put(importantObject.getID(), importantObject);
    }

    public void removeImportantObject(EnvObjectKnowledgeStorageObject importantObject)
    {
        importantObjects.remove(importantObject.getID());
    }

    public void clearImportantObjects()
    {
        importantObjects.clear();
    }

    public Vector3f getStuckPos()
    {
        return stuckPos;
    }

    public void setStuckPos(Vector3f stuckPos)
    {
        this.stuckPos = stuckPos;
    }

    public int getStuckCount()
    {
        return stuckCount;
    }

    public void incrementStuckCount()
    {
        stuckCount++;
    }

    public void resetStuckCount()
    {
        stuckCount = 0;
    }

    public int getTimeOnCurrentLegOfPath()
    {
        return timeOnCurrentLegOfPath;
    }

    public void incrementTimeOnCurrentLegOfPath()
    {
        timeOnCurrentLegOfPath++;
    }

    public void resetTimeOnCurrentLegOfPath()
    {
        timeOnCurrentLegOfPath = 0;
    }

    public int getPathLegThreshold()
    {
        return pathLegThreshold;
    }

    public Vector3f getExplorePos()
    {
        return explorePos;
    }

    public void setExplorePos(Vector3f explorePos)
    {
        this.explorePos = explorePos;
    }

    public AgentPath getAgentPath()
    {
        return agentPath;
    }

    public void setAgentPath(AgentPath agentPath)
    {
        this.agentPath = agentPath;
    }

    public EnvObjectKnowledgeStorageObject getDoorObj(int i)
    {
        return doorObjs.get(i);
    }

    public EnvObjectKnowledgeStorageObject getWallObj(int i)
    {
        return wallObjs.get(i);
    }

    public void addDoorObj(EnvObjectKnowledgeStorageObject obj)
    {
        doorObjs.put(obj.getID(), obj);
    }

    public void addWallObj(EnvObjectKnowledgeStorageObject obj)
    {
        wallObjs.put(obj.getID(), obj);
    }

    public EnvObjectKnowledgeStorageObject findEnvObj(int envObjID)
    {
        EnvObjectKnowledgeStorageObject returnObj = envObjs.get(envObjID);
        if(returnObj == null)
        {
            returnObj = doorObjs.get(envObjID);
        }
        if(returnObj == null)
        {
            returnObj = wallObjs.get(envObjID);
        }
        return returnObj;
    }

    public boolean isSirenHeard()
    {
        return sirenHeard;
    }

    public void setSirenHeard(boolean sirenHeard)
    {
        this.sirenHeard = sirenHeard;
    }

    public boolean isWaiting()
    {
        return waiting;
    }

    public void setWaiting(boolean waiting)
    {
        this.waiting = waiting;
    }

    public int getWaitingTime()
    {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime)
    {
        this.waitingTime = waitingTime;
    }

    public float getPatience()
    {
        return patience;
    }

    public void setPatience(float patience)
    {
        this.patience = patience;
    }

    public boolean isSirenOn()
    {
        return sirenOn;
    }

    public void setSirenOn(boolean sirenOn)
    {
        this.sirenOn = sirenOn;
    }

    public Vector3f getFinalGoalPos()
    {
        return finalGoalPos;
    }

    public void setFinalGoalPos(Vector3f finalGoalPos)
    {
        this.finalGoalPos = finalGoalPos;
    }

    // =======
    /**
     * @param obj
     */
    @Override
    public void addEnvObj(EnvObjectState obj)
    {
        EnvObjectKnowledgeStorageObject envobjKSO = envObjs.get(obj.getID());

        if(envobjKSO != null)
        {
            if(envobjKSO.updateValues(obj))
            {
                newEnvObjs.add(envobjKSO);
            }
        }
        else
        {
            envobjKSO = new EnvObjectKnowledgeStorageObject(obj);
            envObjs.put(obj.getID(), envobjKSO);
            newEnvObjs.add(envobjKSO);
        }

        addElementToVisionList(obj.getID(), "envObject");
    }

    /**
     * @param agent
     */
    @Override
    public void addAgent(AgentState agent)
    {
        AgentKnowledgeStorageObject agentKSO = agents.get(agent.getID());
        if(agentKSO != null)
        {
            agentKSO.updateValues(agent);
        }
        else
        {
            agents.put(agent.getID(), new AgentKnowledgeStorageObject(agent));
        }
        updateNearbyAgents(agent);
        addElementToVisionList(agent.getID(), "agent");
    }

    // public EnvObjectKnowledgeStorageObject findEnvObj(int envObjID)
    // {
    // return envObjs.get(envObjID);
    // }

    public EnvObjectKnowledgeStorageObject findEnvObj(String description)
    {
        // prevent concurrent modification of knownNodes
        for(EnvObjectKnowledgeStorageObject envObjModel : envObjs.values())
            if(envObjModel.getDescription().equals(description))
                return envObjModel;

        return null;
    }

    public AgentKnowledgeStorageObject findAgent(int agentID)
    {
        return agents.get(agentID);
    }

    public List<EnvObjectKnowledgeStorageObject> getEnvObjects()
    {
        return new ArrayList<EnvObjectKnowledgeStorageObject>(envObjs.values());
    }

    public Map<Integer, EnvObjectKnowledgeStorageObject> getEnvObjectsMap()
    {
        return envObjs;
    }

    public List<EnvObjectKnowledgeStorageObject> getNewEnvObjs()
    {
        return newEnvObjs;
    }

    @Override
    public List<EventPropertyKnowledge> getEventKnowledgeFromType(String type)
    {
        ArrayList<EventPropertyKnowledge> returnData = new ArrayList<EventPropertyKnowledge>();

        Iterator<EventKnowledge> evkIter = evk.values().iterator();

        while(evkIter.hasNext())
        {
            EventKnowledge tempevk = evkIter.next();
            EventPropertyKnowledge tempepk = tempevk.getEventProperty(type);
            if(tempepk != null)
            {
                tempepk.setEventName(tempevk.getName());
                returnData.add(tempepk);
            }
        }

        return returnData;
    }

    @Override
    public EventKnowledge getEventKnowledgeByName(String name)
    {
        return evk.get(name);
    }

    @Override
    public List<CombinedReasonedData> getEventsThisTick()
    {
        return eventsPerceivedThisTick;
    }

    @Override
    public void addEventsThisTick(CombinedReasonedData crd)
    {
        eventsPerceivedThisTick.add(crd);
    }

    public List<VisionModel> getVisibleList()
    {
        return new ArrayList<VisionModel>(visibleList.values());
    }

    public void addElementToVisionList(int id, String type)
    {
        visibleList.put(id, new VisionModel(id, type));
    }

    @Override
    public void clearPerceptionKnowledge()
    {
        eventsPerceivedThisTick.clear();
        visibleList.clear();
        newEnvObjs.clear();
    }

    public Collection<AgentKnowledgeStorageObject> getAgents()
    {
        return agents.values();
    }

}
