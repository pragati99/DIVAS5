package edu.utdallas.mavs.evacuation.simulation.sim.agent.planning;

import java.io.Serializable;
import java.util.List;

import edu.utdallas.mavs.divas.core.sim.agent.interaction.perception.data.CombinedReasonedData;
import edu.utdallas.mavs.divas.core.sim.agent.knowledge.internal.Goal;
import edu.utdallas.mavs.divas.core.sim.agent.planning.ReactiveProactivePlanningModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.knowledge.EvacuationHumanKnowledgeModule;
import edu.utdallas.mavs.evacuation.simulation.sim.agent.task.HumanTaskModule;

public class HumanPlanningModule extends ReactiveProactivePlanningModule<EvacuationHumanKnowledgeModule, HumanTaskModule, EvacuationHumanPlanExecutor, EvacuationHumanPlanGenerator, EvacuationHumanReactionModule> implements Serializable
{
    private static final long serialVersionUID = 1L;

    public HumanPlanningModule(EvacuationHumanPlanGenerator planGenerator, EvacuationHumanPlanExecutor planExecutor, EvacuationHumanKnowledgeModule knowledgeModule, EvacuationHumanReactionModule reactionModule)
    {
        super(planGenerator, planExecutor, knowledgeModule, reactionModule);
    }

    @Override
    public void addGoal(Goal newGoal)
    {
        planGenerator.addGoal(newGoal);
    }

    @Override
    public boolean isImmediateReactionRequired()
    {
        boolean ret = false;
        List<CombinedReasonedData> events = knowledgeModule.getEventsThisTick();

        for(int i = 0; i < events.size(); i++)
        {
            CombinedReasonedData thisEvent = events.get(i);

            // System.out.println(thisEvent.getPredicatedName() + " " + thisEvent.getCertaintyPercent());

            if(knowledgeModule.isEventDangerous(thisEvent.getPredicatedName()) && (thisEvent.getCertaintyPercent() > 15))
            {
                ret = true;
            }

        }

        return ret;
    }

}
