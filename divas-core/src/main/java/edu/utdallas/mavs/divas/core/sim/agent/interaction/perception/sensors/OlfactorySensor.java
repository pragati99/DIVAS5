/*
 * File URL : $HeadURL:
 * https://mavs.utdallas.edu/svn/divas/branches/travis_diss/src/divas/sim/agent/interaction/perception
 * /OlfactorySensor.java $ Revision : $Rev: 471 $ Last modified at: $Date:
 * 2010-07-16 10:18:34 -0500 (Fri, 16 Jul 2010) $ Last
 * modified by: $Author: CAMPUS\tls022100 $
 */

package edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.sensors;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.HumanPerceptionModule;
import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.SensedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.HumanKnowledgeModule;
import edu.utdallas.mavs.divas.core.sim.common.event.EnvEvent;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty;
import edu.utdallas.mavs.divas.core.sim.common.event.EventProperty.Sense;
import edu.utdallas.mavs.divas.core.sim.common.percept.Smellable;
import edu.utdallas.mavs.divas.core.sim.common.state.AgentState;
import edu.utdallas.mavs.divas.core.sim.common.state.EnvObjectState;

/**
 * This class describes a virtual agent's olfactory sensor.
 */
public class OlfactorySensor extends PhysicalSensor<HumanKnowledgeModule<?>, HumanPerceptionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new olfactory sensor
     * 
     * @param knowledgeModule
     *        the agent's KnowledgeModule
     * @param humanPerceptionModule
     *        the agent's perception module
     */
    public OlfactorySensor(HumanKnowledgeModule<?> knowledgeModule, HumanPerceptionModule humanPerceptionModule)
    {
        super(knowledgeModule, humanPerceptionModule);
    }

    @Override
    public void setAlgorithm()
    {
        // No seperate algorithms for smell
    }

    /**
     * Process the perception of an event
     * 
     * @param event
     *        an event to perceive
     */
    public void receive(EnvEvent event)
    {
        if(event instanceof Smellable)
        {
            AgentState self = knowledgeModule.getSelf();

            Smellable smellableEvent = (Smellable) event;

            // calculate the distance between the agent and the origin of the sound
            float distance = smellableEvent.getSmellPosition().distance(self.getPosition());

            float smellRadius = smellableEvent.getCurrentSmellRadius();

            float power = event.getIntensity() / (distance * distance);
            // if the sound can be heard from agent's current position
            if(distance < smellRadius && power > knowledgeModule.getSelf().getSmellSensitivity())
            {
                List<EventProperty> epList = event.getEventPropertiesForSense(Sense.Smell);
                for(int i = 0; i < epList.size(); i++)
                {
                    float certainty = 100;
                    EventProperty eventProperty = epList.get(i);
                    SensedData tempsd = new SensedData(knowledgeModule.getTime(), eventProperty.getType(), eventProperty.getSense(), eventProperty.getValue(), certainty, knowledgeModule.getOlfactoryTrustConstant());
                    tempsd.setDirection(event.getOrigin().subtract(self.getPosition()));
                    tempsd.setOrigin(event.getOrigin());
                    tempsd.setIntensity(event.getIntensity());
                    perceptionModule.addPerceivedData(tempsd);
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
        // cannot smell objects yet
    }

    /**
     * Process the perception of an agent
     * 
     * @param agent
     *        the agent to perceive
     */
    public void receive(AgentState agent)
    {
        // cannot smell agents yet
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
            if(event.isCurrentlySmellable())
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
