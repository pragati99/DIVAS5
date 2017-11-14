package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.AbstractVision;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.BlindVision;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.DivasVision;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.GlobalVision;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.NDDivasVision;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.vision.VisionAlgorithm;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes a virtual agent's visual sensor.
 */
public class VisualSensor extends PhysicalSensor<HumanKnowledgeModule<?>, HumanPerceptionModule> implements Serializable
{
    private static final long                               serialVersionUID = 1L;
    private final static Logger                             logger           = LoggerFactory.getLogger(VisualSensor.class);

    /**
     * This sensor's vision algorithm
     */
    protected AbstractVision<?, ?, ?>                       visionAlg;

    /**
     * The available vision algorithms
     */
    protected Map<VisionAlgorithm, AbstractVision<?, ?, ?>> visionAlgs;

    /**
     * Creates a new visual sensor
     * 
     * @param knowledgeModule the agent's KnowledgeModule
     * @param humanPerceptionModule the agent's perception module
     */
    public VisualSensor(HumanKnowledgeModule<?> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
        visionAlgs = new HashMap<VisionAlgorithm, AbstractVision<?, ?, ?>>();
        loadVisionAlgorithms(knowledgeModule);
    }

    /**
     * Loads the vision algorithms for this sensor
     * 
     * @param knowledgeModule the agent's knowledge module
     */
    
    protected void loadVisionAlgorithms(HumanKnowledgeModule<?> knowledgeModule)
    {
        visionAlgs.put(VisionAlgorithm.BlindVision, new BlindVision<>(knowledgeModule, perceptionModule));
        visionAlgs.put(VisionAlgorithm.GlobalVision, new GlobalVision<>(knowledgeModule, perceptionModule));
        visionAlgs.put(VisionAlgorithm.DivasVision, new DivasVision<>(knowledgeModule, perceptionModule));
        visionAlgs.put(VisionAlgorithm.NDDivasVision, new NDDivasVision<>(knowledgeModule, perceptionModule));
    }

    @Override
    public void setAlgorithm()
    {
        VisionAlgorithm algName = knowledgeModule.getSelf().getVisionAlgorithm();

        try
        {
            visionAlg = visionAlgs.get(algName);
        }
        catch(Exception e)
        {
            logger.error("Couldn't set agent vision algorithm: " + algName);
            e.printStackTrace();
        }
    }

    @Override
    public void receiveAgents(List<AgentState> agents)
    {
        visionAlg.receiveAgents(agents);
    }

    @Override
    public void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        visionAlg.receiveEnvObjs(envObjs);
    }

    @Override
    public void receiveEvents(List<EnvEvent> events)
    {
        visionAlg.receiveEvents(events);
    }

    @Override
    public void resolveObstructions()
    {
        visionAlg.resolveObstructions();
    }
}
