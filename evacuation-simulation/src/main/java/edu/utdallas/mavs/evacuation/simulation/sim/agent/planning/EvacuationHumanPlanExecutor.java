package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;

import edu.utdallas.mavs.divas.core.sim.agent.planning.AbstractPlanExecutor;
import edu.utdallas.mavs.divas.core.sim.agent.planning.Plan;
import edu.utdallas.mavs.divas.core.sim.agent.task.Task;
import edu.utdallas.mavs.divas.core.sim.common.stimulus.Stimuli;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangeDesiredSpeedStimulus;
import edu.utdallas.mavs.evacuation.simulation.sim.common.stimulus.ChangePostureStimulus;

public class EvacuationHumanPlanExecutor extends AbstractPlanExecutor<EvacuationHumanKnowledgeModule, HumanTaskModule> implements Serializable
{
    private static final long   serialVersionUID = 1L;

    public EvacuationHumanPlanExecutor(EvacuationHumanKnowledgeModule knowledgeModule)
    {
        super(knowledgeModule);
    }

    @Override
    public Stimuli executePlan(Plan plan)
    {
        Stimuli sm = new Stimuli();

        //System.out.println(plan.getTasks().size());
        for(Task task : plan.getTasks())
        {
            //System.out.println(task);
            if(task.isEnabled())
            {
                sm.addAll(task.execute(knowledgeModule.getId()));
            }
        }

        sm.add(new ChangeDesiredSpeedStimulus(knowledgeModule.getId(), knowledgeModule.desiredSpeed));

        sm.add(new ChangePostureStimulus(knowledgeModule.getId(), knowledgeModule.getSelf().getPosture()));

        writeDebugInfo(plan);
        
        return sm;

    }

    /**
     * Creates debug info to be displayed on agent details on the GUI. To be removed.
     */
    private void writeDebugInfo(Plan plan)
    {
        if(plan != null)
        {
            // System.out.println("Plan: " + plan.toString());
            knowledgeModule.getSelf().setPlanningDetails("State: " + knowledgeModule.getCurrentMentalState() + ", Plan: " + plan.toString());
        }
    }
}
