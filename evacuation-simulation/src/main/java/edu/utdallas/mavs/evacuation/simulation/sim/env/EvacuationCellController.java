package edu.utdallas.mavs.evacuation.simulation.sim.env;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.host.Host;
import edu.utdallas.mavs.divas.core.msg.AssignEventIDMsg;
import edu.utdallas.mavs.divas.core.msg.RuntimeAgentCommandMsg;
import edu.utdallas.mavs.divas.core.sim.agent.Agent;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.SirenEvent;
import edu.utdallas.mavs.divas.core.sim.common.percept.AudioPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.SmellPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.percept.VisionPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentControlType;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.CellState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanAgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.VirtualState;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.AgentStimulus;
import edu.utdallas.mavs.divas.core.sim.env.AgentStateModel;
import edu.utdallas.mavs.divas.core.sim.env.SelfOrganizingCellController;
import edu.utdallas.mavs.divas.mts.CommunicationModule;
import edu.utdallas.mavs.divas.mts.DivasTopic;
import edu.utdallas.mavs.divas.mts.MTSPayload;
import edu.utdallas.mavs.divas.utils.physics.ActualObjectPolygon;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.HumanAgent;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.internal.LocationGoal;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.ActivatedObjectState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.ActivatedObjectState.ActiveObjectType;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.DoorObjectState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.Posture;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangeDesiredSpeedStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangeHeadingStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangePostureStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.CloseDoorStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.IdleStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.MoveStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.OpenDoorStimulus;

/**
 * This class describes an Evacuation Cell Controller.
 * <p>
 * It contains business specific rules for the Evacuation Scenario.
 */
public class EvacuationCellController extends SelfOrganizingCellController<EvacuationEnvironment> implements Serializable
{
    private static final long   serialVersionUID = -6841072718994136063L;

    private final static Logger logger           = LoggerFactory.getLogger(EvacuationCellController.class);

    /**
     * Constructs a new Evacuation Cell Controller
     * 
     * @param rootCellState
     *        the root cell state
     * @param comModule
     *        the communication module
     * @param environment
     *        the evacuation environment
     */
    public EvacuationCellController(CellState rootCellState, CommunicationModule comModule, EvacuationEnvironment environment)
    {
        super(rootCellState, comModule, environment);
    }

    @Override
    protected void processEvents()
    {
        super.processEvents();
        processActiveObjects();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected EvacuationCellController createChild(CellState state)
    {
        return new EvacuationCellController(state, comModule, environment);
    }

    @Override
    protected List<AgentStateModel> combineAgentStimuli()
    {
        Map<Integer, AgentStateModel> agentStates = new HashMap<Integer, AgentStateModel>();
        for(AgentStimulus action : agentStimuli)
        {
            EHumanAgentState state = (EHumanAgentState) cellState.getAgentState(action.getId());

            if(state != null)
            {
                // System.out.println(action);
                if(action instanceof MoveStimulus)
                {
                    AgentState myState = cellState.getAgentState(action.getId());
                    AgentStateModel agentStateModel = new AgentStateModel(myState);
                    agentStateModel.move(((MoveStimulus) action).getPosition());
                    agentStates.put(myState.getID(), agentStateModel);
                }
                else if(action instanceof ChangeHeadingStimulus)
                {
                    ChangeHeadingStimulus a = (ChangeHeadingStimulus) action;
                    state.setHeading(new Vector3f(a.getX(), a.getY(), a.getZ()));
                }
                else if(action instanceof ChangePostureStimulus)
                {
                    state.setPosture(((ChangePostureStimulus) action).getPosture());
                }
                else if(action instanceof ChangeDesiredSpeedStimulus)
                {
                    state.setDesiredSpeed(((ChangeDesiredSpeedStimulus) action).getdesiredSpeed());
                }
                else if(action instanceof OpenDoorStimulus)
                {
                    EnvObjectState envObject = cellState.getEnvObjectState(((OpenDoorStimulus) action).getEnvObjID());
                    if(state.getPosition().distance(envObject.getPosition()) < state.getReachDistance() * 2.5f)
                    {
                        if(envObject instanceof DoorObjectState)
                        {
                            ((DoorObjectState) envObject).setOpen(true);
                            envObject.setCollidable(false);
                        }
                    }
                }
                else if(action instanceof CloseDoorStimulus)
                {
                    EnvObjectState envObject = cellState.getEnvObjectState(((OpenDoorStimulus) action).getEnvObjID());
                    if(state.getPosition().distance(envObject.getPosition()) < state.getReachDistance() * 2.5f)
                    {
                        if(envObject instanceof DoorObjectState)
                        {
                            ((DoorObjectState) envObject).setOpen(false);
                            envObject.setCollidable(true);
                        }
                    }
                }
                else if(action instanceof IdleStimulus)
                {
                    state.setVelocity(new Vector3f(0, 0, 0));
                    state.setAcceleration(new Vector3f(0, 0, 0));
                }
            }
        }
        // clear the list of agent stimuli
        agentStimuli.clear();

        // resolveConflicts(agentStates);

        return new ArrayList<AgentStateModel>(agentStates.values());
    }

    @Override
    protected boolean collides(VirtualState s1, VirtualState s2)
    {
        // Allow only walls to collide with each other!
        if((s1 instanceof EnvObjectState) && (s2 instanceof EnvObjectState))
        {
            if(((EnvObjectState) s1).getType().equals("wall") && ((EnvObjectState) s2).getType().equals("wall"))
            {
                return false;
            }
        }
        return super.collides(s1, s2);
    }

    @Override
    protected void applyUserCommand(RuntimeAgentCommandMsg cmd)
    {
        EHumanAgentState agent = (EHumanAgentState) cellState.getAgentState(cmd.getAgentID());
        if(agent != null)
        {
            if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.ENABLE_AUDITORY_SENSOR)
                agent.setAuditoryEnabled(true);
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.DISABLE_AUDITORY_SENSOR)
                agent.setAuditoryEnabled(false);
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_VISION_ALG)
                agent.setVisionAlgorithm(VisionAlgorithm.valueOf(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.ENABLE_OLFACTORY_SENSOR)
                agent.setOlfactoryEnabled(true);
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.DISABLE_OLFACTORY_SENSOR)
                agent.setOlfactoryEnabled(false);
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_MIN_AUDIBLE_THRESHOLD)
                agent.setMinAudibleThreshold(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_MAX_VELOCITY)
                agent.setMaxSpeed(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_DESIRED_VELOCITY)
                agent.setDesiredSpeed(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_VISIBLE_DISTANCE)
                agent.setVisibleDistance(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_SMELL_SENSITIVITY)
                agent.setSmellSensitivity(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_FOV)
                agent.setFOV(Float.parseFloat(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_POSTURE)
                agent.setPosture(Posture.valueOf(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.SET_CONTROL_TYPE)
                agent.setControlType(AgentControlType.valueOf(cmd.getDataAsString()));
            else if(cmd.getCommand() == RuntimeAgentCommandMsg.RuntimeAgentCommand.UPDATE_AGENT_GOAL)
            {
                for(Agent a : agents.values())
                {
                    Vector3f v = (Vector3f) cmd.getData();
                    LocationGoal g = new LocationGoal(50, v);
                    a.addGoal(g);
                }
            }
        }
    }

    @Override
    protected Agent createAgent(AgentState initialState)
    {
        Agent agent = null;
        if(initialState instanceof EHumanAgentState)
        {
            agent = new HumanAgent((EHumanAgentState) initialState);
            logger.debug("Created " + initialState.getAgentType());
        }
        else
        {
            logger.warn("Agent of type " + initialState.getAgentType() + " does not exist");
        }
        return agent;
    }

    @SuppressWarnings("unused")
    private void resolveConflicts(Map<Integer, AgentStateModel> agentStates)
    {
        for(AgentStateModel agent : agentStates.values())
        {
            ActualObjectPolygon nextBoundingArea = new ActualObjectPolygon(new Vector2f(agent.getNextPosition().x, agent.getNextPosition().y), agent.getScale());

            // avoid collisions with environment objects
            for(EnvObjectState eo : cellState.getEnvObjects())
            {
                if(eo.isCollidable() && nextBoundingArea.intersects(eo.getBoundingArea()))
                {
                    // agent.undoMove();
                    break;
                }
            }

            // avoid collisions with other agents
            for(Agent other : agents.values())
            {
                if(agent.getId() != other.getId())
                {
                    if(agentStates.containsKey(other.getId()))
                    {
                        ActualObjectPolygon otherNextBoundingArea = agentStates.get(other.getId()).getNextBoundingArea();
                        if(nextBoundingArea.intersects(otherNextBoundingArea))
                        {
                            agent.undoMove();
                            break;
                        }
                    }

                    ActualObjectPolygon otherCurrBoundingArea = other.getState().getBoundingArea();
                    if(nextBoundingArea.intersects(otherCurrBoundingArea))
                    {
                        agent.undoMove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void processDestructiveEvent(EnvEvent event)
    {
        if(event.getAge() <= 1)
        {
            for(AgentState a : cellState.getAgentStates())
            {
                if(a instanceof HumanAgentState)
                {
                    EHumanAgentState agent = (EHumanAgentState) a;
                    if(agent.getPosition().distance(event.getOrigin()) <= 10)
                    {
                        logger.debug("Current agent posture: " + agent.getPosture());

                        if(agent.getPosture() != Posture.Death_backward)
                        {
                            agent.setPosture(getAgentPosture(agent.getModelName(), Posture.Death_backward));
                            agent.setAlive(false);
                            agent.setVelocity(Vector3f.ZERO);
                            agent.setAcceleration(Vector3f.ZERO);
                            agent.setAgentPath(new ArrayList<Vector3f>());
                        }
                    }
                }
            }

            for(EnvObjectState envObj : cellState.getEnvObjects())
            {
                if(envObj.getPosition().distance(event.getOrigin()) <= 20)
                {
                    envObj.setOnFire(true);
                }
            }
        }
    }

    protected void processActiveObjects()
    {
        for(EnvObjectState obj : cellState.getEnvObjects())
        {
            if(obj instanceof ActivatedObjectState)
            {
                if(((ActivatedObjectState) obj).getActiveObjectType().equals(ActiveObjectType.SIREN))
                {
                    if(((ActivatedObjectState) obj).isOn())
                    {
                        if(Host.getHost().getCycles() % 2 == 0)
                        {
                            SirenEvent sirenEvent = new SirenEvent();
                            sirenEvent.setEventOccurredTime(Host.getHost().getCycles());
                            sirenEvent.setOrigin(obj.getPosition());
                            sirenEvent.setIntensity(1);
                            sirenEvent.setCurrentlyAudible(true);
                            sendMessage(new MTSPayload(-1, new AssignEventIDMsg(sirenEvent)), DivasTopic.assignIDTopic);
                        }
                    }
                }
            }
        }
    }

    /**
     * This is a factory method for visualized agents postures.
     * 
     * @param modelName
     *        The model name for the agent
     * @param requiredPosture
     *        The required posture for the agent
     * @return The posture related to the agent
     */
    public Posture getAgentPosture(String modelName, Posture requiredPosture)
    {
        if(modelName.contains("woman") && requiredPosture.equals(Posture.Death_backward))
        {
            return Posture.sit_g_start;
        }
        return requiredPosture;

    }

    @Override
    protected void updateRuntimeProperties(AgentState agent)
    {
        HumanAgentState newState = (HumanAgentState) agent;
        Agent vAgent = agents.get(agent.getID());
        if(vAgent != null)
        {
            AgentState state = vAgent.getState();

            if(state.canSee())
            {
                VisionPerceptor v = (VisionPerceptor) state;
                v.setVisionAlgorithm(newState.getVisionAlgorithm());
                v.setVisibleDistance(newState.getVisibleDistance());
                v.setFOV(newState.getFOV());
            }

            if(state.canHear())
            {
                AudioPerceptor a = (AudioPerceptor) state;
                a.setAuditoryEnabled(newState.isAuditoryEnabled());
            }

            if(state.canSmell())
            {
                SmellPerceptor s = (SmellPerceptor) state;
                s.setOlfactoryEnabled(newState.isOlfactoryEnabled());
            }
        }
    }

}
