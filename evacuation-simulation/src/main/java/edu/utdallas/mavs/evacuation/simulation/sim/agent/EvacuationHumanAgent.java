package edu.utdallas.mavs.evacuation.simulation.sim.agent;

import java.io.Serializable;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.sim.agent.AbstractAgent;
import edu.utdallas.mavs.divas.core.sim.agent.driver.keyboard.KeyboardAgentDriver;
import edu.utdallas.mavs.divas.core.sim.agent.driver.wiimote.WiimoteAgentDriver;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.communication.SimpleAgentCommunicationModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentControlType;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.interaction.HumanInteractionModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.external.EnvObjectKnowledgeStorageObject;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.EvacuationHumanPathFinding;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.EvacuationHumanPlanExecutor;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.EvacuationHumanPlanGenerator;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.EvacuationHumanPlanningModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.planning.EvacuationHumanReactionModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangeHeadingStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.MoveStimulus;

/**
 * Human Agent
 */
public class EvacuationHumanAgent extends AbstractAgent<EHumanAgentState, EvacuationHumanKnowledgeModule, HumanInteractionModule, EvacuationHumanPlanningModule, HumanTaskModule> implements Serializable
{
    private static final long serialVersionUID = 4439413037652115193L;

    public EvacuationHumanAgent(EHumanAgentState state)
    {
        super(state);
    }

    @Override
    protected Stimuli generateStimuli()
    {
        Stimuli stimuli = null;

        // if the agent needs to be simulated
        if(getState().getControlType().equals(AgentControlType.Autonomous))
        {
            if(agentDriver != null)
            {
                agentDriver.close();
                agentDriver = null;
            }
            stimuli = plan(); // generate stimuli
        }
        // if the agent is being controlled by the keyboard driver
        else if(getState().getControlType().equals(AgentControlType.Keyboard))
        {
            if(agentDriver != null)
            {
                if(!agentDriver.getClass().getName().equals(KeyboardAgentDriver.class.getName()))
                {
                    agentDriver.close();
                    agentDriver = new KeyboardAgentDriver(getId());
                }
            }
            else
                agentDriver = new KeyboardAgentDriver(getId());

            // request stimuli from the driver
            stimuli = requestDriverStimuli();
        }
        else if(getState().getControlType().equals(AgentControlType.Wiimote))
        {
            if(agentDriver != null)
            {
                if(!agentDriver.getClass().getName().equals(WiimoteAgentDriver.class.getName()))
                {
                    agentDriver.close();
                    agentDriver = new WiimoteAgentDriver(getId());
                }
            }
            else
                agentDriver = new WiimoteAgentDriver(getId());

            // request stimuli from the driver
            stimuli = requestDriverStimuli();
        }
        
        
        return stimuli;
    }

    private Stimuli plan()
    {
        return planningModule.plan();
    }

    private Stimuli requestDriverStimuli()
    {
        Stimuli stimuli = new Stimuli();

        Vector3f currentPos = getState().getPosition();
        Vector3f currentHeading = getState().getHeading();

        Vector3f nextPos = currentPos.clone();
        Vector3f nextHeading = currentHeading.clone();

        // calculate position based on previous heading and sum of forward and
        // lateral motion
        float forwardMotion = agentDriver.grabForwardDelta();
        float lateralMotion = agentDriver.grabLateralDelta();

        Vector3f normHeading = currentHeading.normalize();

        // calculate positive lateral vector (rotate heading 90 degrees east)
        Vector3f normLateral = new Vector3f(-normHeading.getZ(), normHeading.getY(), normHeading.getX());

        nextPos.setX(currentPos.getX() + (normHeading.getX() * forwardMotion) + (normLateral.getX() * lateralMotion));
        nextPos.setY(currentPos.getY() + (normHeading.getY() * forwardMotion) + (normLateral.getY() * lateralMotion));
        nextPos.setZ(currentPos.getZ() + (normHeading.getZ() * forwardMotion) + (normLateral.getZ() * lateralMotion));

        if(!nextPos.equals(currentPos))
            stimuli.add(new MoveStimulus(getState().getID(), nextPos.x, nextPos.y, nextPos.z));

        // calculate new heading (rotate about y axis)
        float rotation = -agentDriver.grabRotationDelta() * FastMath.PI / 180.0f;

        float x = currentHeading.getX();
        float z = currentHeading.getZ();

        nextHeading.setX(x * FastMath.cos(rotation) + z * FastMath.sin(rotation));
        nextHeading.setY(0);
        nextHeading.setZ(z * FastMath.cos(rotation) - x * FastMath.sin(rotation));
        nextHeading.normalizeLocal();

        if(!nextHeading.equals(currentHeading))
            stimuli.add(new ChangeHeadingStimulus(getState().getID(), nextHeading.x, nextHeading.y, nextHeading.z));

        return stimuli;
    }

    public EnvObjectKnowledgeStorageObject getFloorAgentIsOn()
    {
        Vector3f position = getState().getPosition();
        for(EnvObjectKnowledgeStorageObject envObjModel : knowledgeModule.getEnvObjects())
        {
            if(envObjModel.getType().compareTo("floor") == 0)
            {

                float xlowlim = envObjModel.getPosition().getX() - envObjModel.getScale().getX();
                float xhighlim = envObjModel.getPosition().getX() + envObjModel.getScale().getX();
                float zlowlim = envObjModel.getPosition().getZ() - envObjModel.getScale().getZ();
                float zhighlim = envObjModel.getPosition().getZ() + envObjModel.getScale().getZ();
                if((position.getX() >= xlowlim) && (position.getX() <= xhighlim) && (position.getZ() >= zlowlim) && (position.getZ() <= zhighlim))
                {
                    return envObjModel;
                }
            }
        }
        return null;
    }

    @Override
    protected EvacuationHumanKnowledgeModule createKnowledgeModule(EHumanAgentState state)
    {
        return new EvacuationHumanKnowledgeModule(state);
    }

    @Override
    protected HumanInteractionModule createInteractionModule(EvacuationHumanKnowledgeModule knowledgeModule)
    {
        return new HumanInteractionModule(new HumanPerceptionModule(knowledgeModule), new SimpleAgentCommunicationModule(getId()));
    }

    @Override
    protected EvacuationHumanPlanningModule createPlanningModule(EvacuationHumanKnowledgeModule knowledgeModule, HumanTaskModule taskModule, HumanInteractionModule humanInteractionModule)
    {
        EvacuationHumanPathFinding pathFinding = new EvacuationHumanPathFinding(knowledgeModule, taskModule);
        EvacuationHumanPlanGenerator planGenerator = new EvacuationHumanPlanGenerator(knowledgeModule, taskModule, humanInteractionModule, pathFinding);
        EvacuationHumanPlanExecutor planExecutor = new EvacuationHumanPlanExecutor(knowledgeModule);
        EvacuationHumanReactionModule reactionModule = new EvacuationHumanReactionModule(knowledgeModule, taskModule, humanInteractionModule, pathFinding);
        return new EvacuationHumanPlanningModule(planGenerator, planExecutor, knowledgeModule, reactionModule);
    }

    @Override
    protected HumanTaskModule createTaskModule(EvacuationHumanKnowledgeModule knowledgeModule)
    {
        return new HumanTaskModule(knowledgeModule);
    }
}
