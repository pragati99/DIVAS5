package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.AuditorySensor;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.OlfactorySensor;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors.VisualSensor;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;

/**
 * This class describes the perception module of a human agent.
 */
public class HumanPerceptionModule extends AbstractPerceptionModule<HumanKnowledgeModule<?>> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The agent's vision sensor
     */
    protected VisualSensor    visualSensor;

    /**
     * The agent's auditory sensor
     */
    protected AuditorySensor  auditorySensor;

    /**
     * The agent's olfactory sensor
     */
    protected OlfactorySensor olfactorySensor;

    /**
     * Creates a new human perception module
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     */
    public HumanPerceptionModule(HumanKnowledgeModule<?> knowledgeModule)
    {
        super(knowledgeModule);
        visualSensor = new VisualSensor(knowledgeModule, this);
        auditorySensor = new AuditorySensor(knowledgeModule, this);
        olfactorySensor = new OlfactorySensor(knowledgeModule, this);
        addSense(visualSensor);
        addSense(auditorySensor);
        addSense(olfactorySensor);
    }

    @Override
    protected void updateSensors()
    {
        // TODO add enable/disable to visual sensor. Also, check if it can be enabled/disabled from the visualizer
        auditorySensor.setEnabled(knowledgeModule.getSelf().isAuditoryEnabled());
        olfactorySensor.setEnabled(knowledgeModule.getSelf().isOlfactoryEnabled());
    }
}
