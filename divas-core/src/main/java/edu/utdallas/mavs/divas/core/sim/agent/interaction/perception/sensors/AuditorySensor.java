package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.SensedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;
import edu.utdallas.mavs.divas.core.sim.common.percept.Audible;
import edu.utdallas.mavs.divas.core.sim.common.percept.AudioPerceptor;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes a virtual agent's auditory sensor
 */
public class AuditorySensor extends PhysicalSensor<HumanKnowledgeModule<?>, HumanPerceptionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static float      PIx4             = (float) (Math.PI * 4);

    /**
     * Creates a new auditory sensor
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     * @param humanPerceptionModule
     *        the agent's perception module
     */
    public AuditorySensor(HumanKnowledgeModule<?> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
    }

    @Override
    public void setAlgorithm()
    {
        // No algorithms for hearing
    }

    /**
     * Process the perception of an event
     * 
     * @param event
     *        an event to perceive
     */
    public void receive(EnvEvent event)
    {
        if(event instanceof Audible)
        {
            AgentState self = knowledgeModule.getSelf();
            Audible audibleEvent = (Audible) event;

            // calculate the distance between the agent and the origin of the sound
            float distance = audibleEvent.getAudiblePosition().distance(self.getPosition());

            // calculate the intensity of the sound at the agent's position
            float localIntensity = (float) (10 * Math.log10((audibleEvent.getAcousticEmission() / (PIx4 * distance * distance)) / Math.pow(10, -12)));

            // if the sound can be heard from agent's current position
            if(self instanceof AudioPerceptor)
            {
                if(localIntensity >= ((AudioPerceptor) self).getMinAudibleThreshold())
                {
                    if((distance < audibleEvent.getCurrentSoundRadius()))
                    {
                        if(event.isCurrentlyAudiable())
                        {
                            List<EventProperty> epList = event.getEventPropertiesForSense(Sense.Hearing);
                            for(int i = 0; i < epList.size(); i++)
                            {
                                float certainty = 100;
                                EventProperty eventProperty = epList.get(i);
                                SensedData tempsd = new SensedData(knowledgeModule.getTime(), eventProperty.getType(), eventProperty.getSense(), eventProperty.getValue(), certainty, knowledgeModule.getAuditoryTrustConstant());
                                tempsd.setDirection(event.getOrigin().subtract(self.getPosition()));
                                tempsd.setOrigin(event.getOrigin());
                                tempsd.setIntensity(event.getIntensity());
                                perceptionModule.addPerceivedData(tempsd);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Process the perception of an environment object
     * 
     * @param envObj
     *        the environmnet object to perceive
     */
    public void receive(EnvObjectState envObj)
    {
        // cannot hear objects currently
    }

    /**
     * Process the perception of an agent
     * 
     * @param agent
     *        the agent to perceive
     */
    public void receive(AgentState agent)
    {
        if(knowledgeModule.getSelf().getPosition().distance(agent.getPosition()) < 8)
        {
            knowledgeModule.addAgent(agent);
        }
    }

    @Override
    protected void receiveAgents(List<AgentState> agents)
    {
        for(AgentState agent : agents)
            receive(agent);
    }

    @Override
    protected void receiveEvents(List<EnvEvent> events)
    {
        for(EnvEvent event : events)
        {
            if(event.isCurrentlyAudiable())
            {
                receive(event);
            }
        }
    }

    @Override
    protected void receiveEnvObjs(List<EnvObjectState> envObjs)
    {
        for(EnvObjectState obj : envObjs)
            receive(obj);

    }

    @Override
    public void resolveObstructions()
    {
        // no obstruction test to perform
    }
}
