package edu.utdallas.mavs.evacuation.simulation.spec.agent;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.jme3.math.Vector3f;

import edu.utdallas.mavs.divas.core.config.SimConfig;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.HumanProperties.Gender;
import edu.utdallas.mavs.divas.core.spec.agent.AgentLoader;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.FaceTask;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.MoveTask;
import edu.utdallas.mavs.evacuation.simulation.sim.common.state.EHumanAgentState;

public class EvacuationAgentLoader extends AgentLoader implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public AgentState createAgent(String modelName)
    {
        EHumanAgentState agentState = createAgentState(modelName);
        
        agentState.setAgentType("EvacuationHuman");
        agentState.setMaxSpeed(5f);
        agentState.setDesiredSpeed(1f);
        agentState.setVisibleDistance(30f);
        agentState.setFOV(30f);
        agentState.setMinAudibleThreshold(.2f);
        agentState.setAcousticEmission(0f);
        agentState.setSmellSensitivity(0.00000000001f);
        agentState.setReachDistance(3f);
        agentState.setScale(new Vector3f(1.001f, 5f, 1.001f));
        agentState.setVisionAlgorithm(SimConfig.getInstance().default_Vision_Algorithm);
        
        Set<String> taskNames = new HashSet<String>();
        taskNames.add(FaceTask.NAME);
        taskNames.add(MoveTask.NAME);        
        agentState.setTaskNames(taskNames);

        return agentState;
    }

    /**
     * This is a factory method for generating random agents models.
     * @param model 
     * 
     * @param state
     *        The agent state to be created
     * @return a spatial associated with the newly created visualized event
     */
    public EHumanAgentState createAgentState(String model)
    {
        Random generator = new Random();
        String modelNames[] = { "male_tough", "male_fat", "male_tall", "woman_fat", "woman_slim", "woman_bonus", "girl_kid", "boy_kid" };
        // String modelNames[] = { "male_tough", "male_fat", "male_tall" };
        String modelName = null;
        if(model == null || model.isEmpty())
        {
            modelName = modelNames[generator.nextInt(3)];
        }
        else
        {
            modelName = model;
        }

        int clothingSeed = 0;
        if(modelName.equals("male_tough") || modelName.equals("male_fat") || modelName.equals("male_tall"))
        {
            clothingSeed = 20;
        }
        else if(modelName.equals("woman_fat") || modelName.equals("woman_slim"))
        {
            clothingSeed = 11;
        }
        else if(modelName.equals("woman_bonus"))
        {
            clothingSeed = 3;
        }
        else if(modelName.equals("girl_kid"))
        {
            clothingSeed = 12;
        }
        else if(modelName.equals("boy_kid"))
        {
            clothingSeed = 6;
        }

        EHumanAgentState agentState = new EHumanAgentState();

        int agentClothing = generator.nextInt(clothingSeed) + 1;
        agentState.setClothing(agentClothing);

        if(modelName.equals("male_tough") || modelName.equals("male_fat") || modelName.equals("male_tall") || modelName.equals("boy_kid"))
        {
            agentState.setGender(Gender.MALE);
        }
        else
        {
            agentState.setGender(Gender.FEMALE);
        }

        agentState.setModelName(modelName + agentClothing);

        return agentState;
    }

    /**
     * This is a factory method for getting the appropriate agent spec.
     * 
     * @param modelName
     *        The model name for the agent
     * @return The posture related to the agent
     */
    public String getAgentSpecName(String modelName)
    {
        return AgentSpecEnum.Default.toString().toLowerCase();
    }
}
